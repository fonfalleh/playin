package io.github.fonfalleh.playin.indexer;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.fonfalleh.formats.midi.MidiNoteExtractor;
import io.github.fonfalleh.formats.musicxml.LyricExtractor;
import io.github.fonfalleh.formats.musicxml.PitchExtractor;
import io.github.fonfalleh.formats.musicxml.XmlMetadata;
import io.github.fonfalleh.formats.musicxml.model.MXML;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;

import javax.sound.midi.InvalidMidiDataException;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * PoC class for indexing songs as metadata + midi pitches into solr.
 * One song per dir, and a <code>metadata</code> file with an id is required per song.
 */
public class Indexer {

    SolrClient client = new Http2SolrClient
            .Builder("http://localhost:8983/solr")
                    .withDefaultCollection("playin")
                    .build();

    public static void main(String[] args) throws SolrServerException, IOException {
        if (args.length < 1) {
            indexDirsFromPath(Indexer.class.getResource("/songs").getPath());
        } else {
            indexDirsFromPath(args[0]);
        }
    }

    public static void indexDirsFromPath(String path) throws SolrServerException, IOException {
        File f = new File(path);

        List<SolrInputDocument> docs = Arrays.stream(f.listFiles())
                .filter(File::isDirectory)
                .map(Indexer::solrDocFromDirectory)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());


        Indexer indexer = new Indexer();
        indexer.sendDocumentsForIndexing(docs);
        indexer.client.close();
    }

    /*
    Plan:
    find files
    per folder with things,
    create solr document for indexing, add to queue
     */

    /**
     * Produces a {@link SolrInputDocument} from a directory.
     * @param file Input directory. Needs at least a <code>metadata</code>-file.
     * @return doc A document if successful, otherwise empty.
     */
    static Optional<SolrInputDocument> solrDocFromDirectory(File file) {
        Optional<SolrInputDocument> maybeDoc = createDocFromMetadata(file);
        if (maybeDoc.isEmpty()) {
            return maybeDoc;
        }
        SolrInputDocument doc = maybeDoc.get();

        // Handle midiFiles
        File[] midiFiles = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".midi")
                || name.toLowerCase().endsWith(".mid"));
        File[] mxmlFiles = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".musicxml"));

        List<MXML> mxmls = parseXml(mxmlFiles);

        for (MXML mxml : mxmls) {
            addMetadata(doc, mxml);
            addLyrics(doc, mxml);
            addPitches(doc, mxml);
        }
        //TODO idea:
        // Have interface for "extracting" music, create map of (file endings -> extractors)

        // Extract pitches
        List<String> pitches = Arrays.stream(midiFiles)
                .flatMap(Indexer::extractPitchesFromMidi)
                .toList();

        // TODO constants, where are fields defined?
        doc.addField("pitches", pitches);

        return Optional.of(doc);
    }

    private static void addPitches(SolrInputDocument doc, MXML mxml) {
        // TODO verify adding lists...
        // Also constants
        doc.addField("pitches", pitchesToString(PitchExtractor.extract(mxml)).toList());
    }

    private static void addLyrics(SolrInputDocument doc, MXML mxml) {
        List<String> lyrics = new LyricExtractor().extract(mxml);
        //TODO verify
        // TODO constants
        if (!lyrics.isEmpty()) doc.addField("lyrics", lyrics);
    }

    private static void addMetadata(SolrInputDocument doc, MXML mxml) {
        XmlMetadata metadata = XmlMetadata.extract(mxml);
        if (metadata.getTitle() != null) doc.addField("title", metadata.getTitle());
        if (metadata.getComposers() != null) doc.addField("composer", metadata.getComposers());
        if (metadata.getLyricists() != null) doc.addField("lyricist", metadata.getLyricists());
    }

    private static List<MXML> parseXml(File[] files) {
        XmlMapper xmlMapper = new XmlMapper();
        return Arrays.stream(files).map(file -> {
                    try {
                        return xmlMapper.readValue(file, MXML.class);
                    } catch (IOException e) {
                        System.out.println("Failed parsing file: " + file.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    static Stream<String> extractPitchesFromMidi(File midiFile) {
        List<List<Integer>> filePitches;
        try {
            filePitches = MidiNoteExtractor.extractPitchTracksFromFile(midiFile);
        } catch (InvalidMidiDataException | IOException e) {
            System.out.println("Error when trying to extract tracks from midi file: " + midiFile.getPath());
            return Stream.empty();
        }
        return pitchesToString(filePitches);
    }

    static Stream<String> pitchesToString(List<List<Integer>> filePitches) {
        if (filePitches.isEmpty()) {
            return Stream.empty();
        }

        return filePitches.stream()
                .filter(Predicate.not(Collection::isEmpty))
                .map(track -> track.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")));
    }

    private static final String metadataMultiValueSeparator = ";";
    public static Optional<SolrInputDocument> createDocFromMetadata(File file) {
        File metaFile = new File(file, "metadata");
        if (!metaFile.canRead()) {
            return Optional.empty();
        }
        SolrInputDocument doc = new SolrInputDocument();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(metaFile));
            //TODO do cleaning here? Or moderate metadata. Or have util/app/page for creating metadata.
            reader.lines().forEach(s -> {
                String[] split = s.split(":", 2);
                Arrays.stream(split[1].split(metadataMultiValueSeparator)).forEach(
                        m -> doc.addField(split[0], m.trim()));
            });
        } catch (FileNotFoundException e) {
            System.out.println("No metadata file found");
            return Optional.empty();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Malformed metadata file: " + file.getPath());
            return Optional.empty();
        }
        return Optional.of(doc);
    }

    void sendDocumentsForIndexing(List<SolrInputDocument> docs) throws SolrServerException, IOException {
        client.add(docs);
    }
}
