package io.github.fonfalleh.playin.indexer;

import io.github.fonfalleh.formats.midi.MidiNoteExtractor;
import org.apache.solr.common.SolrInputDocument;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MidiSolrEnricher {


    public static void enrichSolrDoc(File[] midiFiles, SolrInputDocument doc) {
        if (midiFiles == null || midiFiles.length == 0)
            return;
        for (File midiFile : midiFiles) {
            List<String> pitches = extractPitchesFromMidi(midiFile);
            doc.addField(Indexer.PITCHES, pitches);
        }
    }

    static List<String> extractPitchesFromMidi(File midiFile) {
        List<List<Integer>> filePitches;
        try {
            filePitches = MidiNoteExtractor.extractPitchTracksFromFile(midiFile);
        } catch (InvalidMidiDataException | IOException e) {
            System.out.println("Error when trying to extract tracks from midi file: " + midiFile.getPath());
            return null;
        }
        return Indexer.pitchesToString(filePitches);
    }
}
