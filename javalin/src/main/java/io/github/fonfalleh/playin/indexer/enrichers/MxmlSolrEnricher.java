package io.github.fonfalleh.playin.indexer.enrichers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.fonfalleh.formats.musicxml.LyricExtractor;
import io.github.fonfalleh.formats.musicxml.PitchExtractor;
import io.github.fonfalleh.formats.musicxml.XmlMetadata;
import io.github.fonfalleh.formats.musicxml.model.MXML;
import io.github.fonfalleh.playin.indexer.Indexer;
import io.github.fonfalleh.playin.indexer.Util;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MxmlSolrEnricher implements SolrDocEnricher {

    private static final Logger log = LoggerFactory.getLogger(MxmlSolrEnricher.class);

    @Override
    public List<String> supportedExtensions() {
        return List.of("xml", "musicxml");
    }

    @Override
    public void enrichSolrDoc(File file, SolrInputDocument doc) {
        MXML mxml = parseXmlFile(file);
        addMetadata(doc, mxml);
        addLyrics(doc, mxml);
        addPitches(doc, mxml);
    }

    private static void addMetadata(SolrInputDocument doc, MXML mxml) {
        XmlMetadata metadata = XmlMetadata.extract(mxml);
        if (metadata.getTitle() != null) doc.addField(Indexer.TITLE, metadata.getTitle());
        if (metadata.getComposers() != null) doc.addField(Indexer.COMPOSER, metadata.getComposers());
        if (metadata.getLyricists() != null) doc.addField(Indexer.LYRICIST, metadata.getLyricists());
    }

    private static void addLyrics(SolrInputDocument doc, MXML mxml) {
        List<String> lyrics = new LyricExtractor().extract(mxml);
        //TODO verify
        // TODO constants
        if (!lyrics.isEmpty()) doc.addField("lyrics", lyrics);
    }

    private static void addPitches(SolrInputDocument doc, MXML mxml) {
        // TODO verify adding lists...
        // Also constants
        doc.addField(Indexer.PITCHES, Util.pitchesToString(PitchExtractor.extract(mxml)));
    }

    static MXML parseXmlFile(File file) {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            return xmlMapper.readValue(file, MXML.class);
        } catch (IOException e) {
            log.warn("Failed parsing file: {}, skipping enrichment", file.getName());
            return null;
        }
    }
}
