package io.github.fonfalleh.playin.indexer;

import io.github.fonfalleh.formats.MidiNoteExtractor;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;

import javax.sound.midi.InvalidMidiDataException;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    static Optional<SolrInputDocument> solrDocFromDirectory(File file) {
        File metaFile = new File(file, "metadata");
        if (!metaFile.canRead()) {
            return Optional.empty();
        }
        SolrInputDocument doc = new SolrInputDocument();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(metaFile));
            reader.lines().forEach(s -> {
                String[] split = s.split(":", 2);
                doc.addField(split[0], split[1]);
            });
        } catch (FileNotFoundException e) {
            System.out.println("No metadata file found");
            return Optional.empty();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Malformed metadata file: " + file.getPath());
            return Optional.empty();
        }

        // Handle midifiles
        File[] midifiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".midi")
                        || name.toLowerCase().endsWith(".mid");
            }
        });

        //TODO idea:
        // Have interface for "extracting" music, create map of (file endings -> extractors)

        // Extract midifiles
        List<String> pitches = new ArrayList<>();
        for (File midifile: midifiles) {
            List<List<Integer>> filePitches;
            try {
                filePitches = MidiNoteExtractor.extractPitchTracksFromFile(midifile);
            } catch (InvalidMidiDataException | IOException e) {
                System.out.println("Error when trying to extract tracks from midi file: " + midifile.getPath());
                continue;
            }
            if (!filePitches.isEmpty()) {
                pitches.addAll(
                filePitches.stream()
                        .filter(Predicate.not(Collection::isEmpty))
                        .map(track -> track.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(" ")))
                        .collect(Collectors.toList()));

            }
        }
        doc.addField("pitches", pitches);

        return Optional.of(doc);
    }

    void sendDocumentsForIndexing(List<SolrInputDocument> docs) throws SolrServerException, IOException {
        client.add(docs);
    }
}
