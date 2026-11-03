package io.github.fonfalleh.config;

import io.github.fonfalleh.playin.indexer.enrichers.MetadataFileSolrEnricher;
import io.github.fonfalleh.playin.indexer.enrichers.MidiSolrEnricher;
import io.github.fonfalleh.playin.indexer.enrichers.MusescoreSolrEnricher;
import io.github.fonfalleh.playin.indexer.enrichers.MxmlSolrEnricher;
import io.github.fonfalleh.playin.indexer.enrichers.SolrDocEnricher;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Enrichers {

    private static final Map<String, SolrDocEnricher> enrichers = initialize();

    private static Map<String, SolrDocEnricher> initialize() {
        Map<String, SolrDocEnricher> map = new HashMap<>();
        MxmlSolrEnricher mxmlSolrEnricher = new MxmlSolrEnricher();
        Stream.of(
                new MetadataFileSolrEnricher(),
                new MidiSolrEnricher(),
                mxmlSolrEnricher,
                new MusescoreSolrEnricher(mxmlSolrEnricher)
        ).forEach(e -> e.registerEnricher(map));
        return map;
    }

    public static Map<String, SolrDocEnricher> getEnricherMap() {
        return enrichers;
    }
}
