package io.github.fonfalleh.formats;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MidiNoteExtractor {
    public static List<Integer> getPitchesFromTrack(Track track) {
        List<Integer> pitches = new ArrayList<>();
        for (int i = 0; i < track.size(); i++) {
            MidiMessage message = track.get(i).getMessage();
            if (message instanceof ShortMessage) {
                ShortMessage sm = (ShortMessage) message;
                //System.out.printf("Index: %s, Command: %s, Data1: %s, Data2: %s%n", i, sm.getCommand(), sm.getData1(), sm.getData2());
                // Some notes have velocity (Data2) = 0 instead of NOTE_OFF...
                if (sm.getCommand() == ShortMessage.NOTE_ON
                        && sm.getData2() > 0) {
                    pitches.add(sm.getData1());
                }
            }
        }
        return pitches;
    }

    public static List<List<Integer>> extractPitchTracksFromFile(File f) throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(f);
        return Arrays.stream(sequence.getTracks())
                .map(MidiNoteExtractor::getPitchesFromTrack)
                .filter(Predicate.not(Collection::isEmpty))
                .collect(Collectors.toList());
    }
}
