package io.github.fonfalleh.playin.indexer.enrichers;

import io.github.fonfalleh.formats.midi.MidiNoteExtractor;
import io.github.fonfalleh.playin.indexer.Indexer;
import io.github.fonfalleh.playin.indexer.Util;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MidiSolrEnricher implements SolrDocEnricher {
    private static final Logger log = LoggerFactory.getLogger(MidiSolrEnricher.class);

    @Override
    public List<String> supportedExtensions() {
        return List.of("midi", "mid");
    }

    @Override
    public void enrichSolrDoc(File file, SolrInputDocument doc) {
        List<String> pitches = extractPitchesFromMidi(file);
        doc.addField(Indexer.PITCHES, pitches);
    }

    static List<String> extractPitchesFromMidi(File midiFile) {
        List<List<Integer>> filePitches;
        try {
            filePitches = MidiNoteExtractor.extractPitchTracksFromFile(midiFile);
        } catch (InvalidMidiDataException | IOException e) {
            log.warn("Error when trying to extract tracks from midi file: {}", midiFile.getPath());
            return null;
        }
        return Util.pitchesToString(filePitches);
    }
}
