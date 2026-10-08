package io.github.fonfalleh.playin.indexer;

import io.github.fonfalleh.config.Enrichers;
import io.github.fonfalleh.config.SolrClientFactory;
import io.github.fonfalleh.playin.indexer.enrichers.SolrDocEnricher;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PoC class for indexing songs as metadata + midi pitches into solr.
 * One song per dir, and a <code>metadata</code> file with an id is required per song.
 */
public class Indexer {

    // Fields
    // TODO shared constants
    public static final String COMPOSER = "composer";
    public static final String LYRICIST = "lyricist";
    public static final String PITCHES = "pitches";
    public static final String TITLE = "title";

    private static final Map<String, SolrDocEnricher> enricherMap = Enrichers.getEnricherMap();

    private static final Logger log = LoggerFactory.getLogger(Indexer.class);

    public static String getExtension(File file) {
        String baseName = file.getName().toLowerCase(Locale.ROOT);
        return baseName.substring(baseName.lastIndexOf('.') + 1);
    }

    private static final SolrClient client = SolrClientFactory.getClient();

    static void main(String[] args) throws SolrServerException, IOException {
        if (args.length < 1) {
            indexDirsFromPath(Indexer.class.getResource("/songs").getPath());
        } else {
            indexDirsFromPath(args[0]);
        }
        //TODO  for running in isolation. Don't close the shared client.
        client.close();
    }

    public static void indexDirsFromPath(String path) throws SolrServerException, IOException {
        File f = new File(path);

        List<SolrInputDocument> docs = Arrays.stream(f.listFiles())
                .filter(File::isDirectory)
                .map(Indexer::solrDocFromDirectory)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        client.add(docs);
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
        for (File file : files) {
            enrichDocWithFile(file, doc);
        }
        return doc;
    }

    public static void enrichDocWithFile(File file, SolrInputDocument doc) {
        SolrDocEnricher solrDocEnricher = enricherMap.get(getExtension(file));
        if (solrDocEnricher == null) {
            log.warn("Unexpected extension, skipping file {}", file.getAbsolutePath());
            return;
        }
        solrDocEnricher.enrichSolrDoc(file, doc);
    }
}
