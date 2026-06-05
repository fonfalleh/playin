package io.github.fonfalleh.playin.indexer;

import io.github.fonfalleh.formats.musicxml.model.MXML;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * PoC class for indexing songs as metadata + midi pitches into solr.
 * One song per dir, and a <code>metadata</code> file with an id is required per song.
 */
public class Indexer {

    static final String COMPOSER = "composer";
    static final String LYRICIST = "lyricist";
    static final String PITCHES = "pitches";
    static final String TITLE = "title";

    SolrClient client = new HttpJettySolrClient
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

    /**
     * Produces a {@link SolrInputDocument} from a directory.
     *
     * @param directory Input directory. Needs at least a <code>metadata</code>-file.
     * @return doc A document if successful, otherwise empty.
     */
    static Optional<SolrInputDocument> solrDocFromDirectory(File directory) {
        Optional<SolrInputDocument> maybeDoc = createDocFromMetadata(directory);
        if (maybeDoc.isEmpty()) {
            return maybeDoc;
        }
        SolrInputDocument doc = maybeDoc.get();

        // Handle midiFiles
        File[] midiFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".midi")
                || name.toLowerCase().endsWith(".mid"));
        File[] mxmlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".musicxml")
                || name.toLowerCase().endsWith(".xml"));

        MXML[] mxmls = MxmlSolrEnricher.parseXmlFiles(mxmlFiles);

        MxmlSolrEnricher.enrichSolrDoc(mxmls, doc);
        MidiSolrEnricher.enrichSolrDoc(midiFiles, doc);

        return Optional.of(doc);
    }

    private static final String metadataMultiValueSeparator = ";";

    public static Optional<SolrInputDocument> createDocFromMetadata(File directory) {
        File metaFile = new File(directory, "metadata");
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
            System.out.println("Malformed metadata file: " + directory.getPath());
            return Optional.empty();
        }
        return Optional.of(doc);
    }

    static List<String> pitchesToString(List<List<Integer>> filePitches) {
        if (filePitches.isEmpty()) {
            return null;
        }

        return filePitches.stream()
                .filter(Predicate.not(Collection::isEmpty))
                .map(track -> track.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")))
                .toList();
    }

    void sendDocumentsForIndexing(List<SolrInputDocument> docs) throws SolrServerException, IOException {
        client.add(docs);
    }
}
