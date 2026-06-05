package io.github.fonfalleh.playin.indexer;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.fonfalleh.formats.musicxml.LyricExtractor;
import io.github.fonfalleh.formats.musicxml.PitchExtractor;
import io.github.fonfalleh.formats.musicxml.XmlMetadata;
import io.github.fonfalleh.formats.musicxml.model.MXML;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MxmlSolrEnricher {

    public static void enrichSolrDoc(MXML[] mxmls, SolrInputDocument doc) {
        if (mxmls == null || mxmls.length == 0)
            return;
        for (MXML mxml : mxmls) {
            addMetadata(doc, mxml);
            addLyrics(doc, mxml);
            addPitches(doc, mxml);
        }
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
        doc.addField(Indexer.PITCHES, Indexer.pitchesToString(PitchExtractor.extract(mxml)));
    }

    static MXML[] parseXmlFiles(File[] files) {
        XmlMapper xmlMapper = new XmlMapper();
        return (MXML[]) Arrays.stream(files).map(file -> {
                    try {
                        return xmlMapper.readValue(file, MXML.class);
                    } catch (IOException e) {
                        System.out.println("Failed parsing file: " + file.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray();
    }
}
