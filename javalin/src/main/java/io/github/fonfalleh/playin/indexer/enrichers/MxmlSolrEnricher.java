package io.github.fonfalleh.playin.indexer.enrichers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.fonfalleh.formats.musicxml.LyricExtractor;
import io.github.fonfalleh.formats.musicxml.PitchExtractor;
import io.github.fonfalleh.formats.musicxml.XmlMetadata;
import io.github.fonfalleh.formats.musicxml.model.MXML;
import io.github.fonfalleh.playin.indexer.Fields;
import io.github.fonfalleh.playin.indexer.Util;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MxmlSolrEnricher implements SolrDocEnricher {

    private final XmlMapper xmlMapper = new XmlMapper();
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
        if (metadata.getTitle() != null) doc.addField(Fields.TITLE.getName(), metadata.getTitle());
        if (metadata.getComposers() != null) doc.addField(Fields.COMPOSER.getName(), metadata.getComposers());
        if (metadata.getLyricists() != null) doc.addField(Fields.LYRICIST.getName(), metadata.getLyricists());
    }

    private static void addLyrics(SolrInputDocument doc, MXML mxml) {
        List<String> lyrics = new LyricExtractor().extract(mxml);
        if (!lyrics.isEmpty()) doc.addField(Fields.LYRICS.getName(), lyrics);
    }

    private static void addPitches(SolrInputDocument doc, MXML mxml) {
        // TODO verify adding lists...
        doc.addField(Fields.PITCHES.getName(), Util.pitchesToString(PitchExtractor.extract(mxml)));
    }

    private MXML parseXmlFile(File file) {
        try {
            return xmlMapper.readValue(file, MXML.class);
        } catch (IOException e) {
            log.warn("Failed parsing file: {}, skipping enrichment", file.getName());
            return null;
        }
    }
}
