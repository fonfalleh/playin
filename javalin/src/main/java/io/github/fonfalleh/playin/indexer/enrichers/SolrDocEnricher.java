package io.github.fonfalleh.playin.indexer.enrichers;

import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface SolrDocEnricher {

    List<String> supportedExtensions();

    void enrichSolrDoc(File file, SolrInputDocument doc);

    default void registerEnricher(Map<String, SolrDocEnricher> map) {
        supportedExtensions().forEach(s -> map.put(s, this));
    }
}
