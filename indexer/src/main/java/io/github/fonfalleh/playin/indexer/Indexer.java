package io.github.fonfalleh.playin.indexer;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * PoC class for indexing songs as metadata + midi pitches into solr.
 * One song per dir, and a <code>metadata</code> file with an id is required per song.
 */
public class Indexer {

    // Fields
    static final String COMPOSER = "composer";
    static final String LYRICIST = "lyricist";
    static final String PITCHES = "pitches";
    static final String TITLE = "title";
    // File types
    private static final String XML = "xml";
    private static final String MIDI = "midi";
    private static final String METADATA = "metadata";

    private static final Function<File, String> FILE_TYPE_CLASSIFIER = file -> {
        String baseName = file.getName().toLowerCase(Locale.ROOT);
        String extension = baseName.substring(baseName.lastIndexOf('.') + 1);
        return switch (extension) {
            case XML, "musicxml" -> XML;
            case MIDI, "mid" -> MIDI;
            case METADATA -> METADATA;
            default -> "_ignored";
        };
    };

    // TODO fix
    private static SolrClient client = new HttpJettySolrClient
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
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        client.add(docs);
        client.close();
    }

    /**
     * Produces a {@link SolrInputDocument} from a directory.
     *
     * @param directory Input directory. Needs at least a <code>metadata</code>-file.
     * @return doc A document if successful, otherwise null.
     */
    static SolrInputDocument solrDocFromDirectory(File directory) {
        SolrInputDocument doc = new SolrInputDocument();
        File[] files = directory.listFiles();
        if (files == null) {
            return null;
        }
        Map<String, List<File>> filesByType = Arrays.stream(files).collect(Collectors.groupingBy(FILE_TYPE_CLASSIFIER));

        MetadataFileSolrEnricher.enrichSolrDoc(filesByType.get(METADATA), doc);
        MxmlSolrEnricher.enrichSolrDoc(filesByType.get(XML), doc);
        MidiSolrEnricher.enrichSolrDoc(filesByType.get(MIDI), doc);

        return doc;
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

    static void sendDocumentsForIndexing(List<SolrInputDocument> docs) throws SolrServerException, IOException {
    }
}
