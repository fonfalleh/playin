package io.github.fonfalleh.formats.musicxml;

import io.github.fonfalleh.formats.musicxml.model.MXML;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PitchExtractor {

    public static List<List<Integer>> extract(MXML mxml) {

        // TODO voices / systems

        // TODO parts
        Map<Integer, List<MXML.Note>> notesByVoice = mxml.parts.get(0).measures.stream()
                .flatMap(MXML.Measure::notes)
                .collect(Collectors.groupingBy(b -> b.voice));

        // TODO order of keys. Treemap?
        List<List<Integer>> notes = new ArrayList<>();
        for (Map.Entry<Integer, List<MXML.Note>> voiceNotes : notesByVoice.entrySet()) {
            List<Integer> list = voiceNotes.getValue().stream()
                    .map(n -> n.pitch)
                    .filter(Predicate.not(Objects::isNull)) // rest notes
                    .map(PitchExtractor::mxmlPitchToMidiPitch)
                    .toList();

            if (list.isEmpty()) {
                // TODO lower staff with rests...
                // Or is this nice?
                continue;
            }
            notes.add(list);
        }
        return notes;
    }

    public static int mxmlPitchToMidiPitch(MXML.Pitch p) {
        int pitch = switch (p.step) {
            case "C" -> 0;
            case "D" -> 2;
            case "E" -> 4;
            case "F" -> 5;
            case "G" -> 7;
            case "A" -> 9;
            case "B" -> 11;
            default ->
                // TODO error handling?
                    0;
        };
        // accidentals
        pitch += p.alter;
        // c3 -> 48 = 3 * 12 + 12
        pitch += (p.octave + 1) * 12;
        return pitch;

    }
}
