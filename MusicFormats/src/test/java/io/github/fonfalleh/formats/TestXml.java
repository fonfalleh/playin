package io.github.fonfalleh.formats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.fonfalleh.formats.musicxml.LyricExtractor;
import io.github.fonfalleh.formats.musicxml.model.MXML;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class TestXml {

    @Test
    public void testCScaleXml() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode jsonNode = xmlMapper.readTree(
                new File(this.getClass().getResource("/musescore_musicxml/c_scale.musicxml").getFile()));
        JsonNode measures = jsonNode.get("part").get("measure");
        List<Integer> pitches = new ArrayList<>();

        // TODO something more pretty
        // measures.forEach(m -> m.get("note").forEach(n -> sts.add(n.get("pitch").get("step").asText())));
        measures.forEach(measure ->
                measure.get("note").forEach(note ->
                        pitches.add(Integer.valueOf(jsonNodeNoteToPitch(note)))));

        ArrayList<String> lyricEntries = new ArrayList<>();
        measures.forEach(m ->
                m.get("note").forEach(n -> {
            if (n.has("lyric")) {
                lyricEntries.add(n.get("lyric").get("text").asText());
            }
        }));

        assertEquals(
                List.of(48, 50, 52, 53, 55, 57, 59, 60),
                pitches
        );

        // TODO more intepretation of lyric node! More viable to actually puzzle lyrics together!
        assertEquals(
                List.of("do", "re", "mi", "a", "a", "a", "a!"),
                lyricEntries
        );
         // actual do re mi a-a a_ a!
    }

    @Test
    public void testXmlLyrics() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        MXML mxml = xmlMapper.readValue(this.getClass().getResourceAsStream("/musescore_musicxml/blinka_lilla.musicxml"), MXML.class);

        List<String> actualLyrics = new LyricExtractor().extract(mxml);

        List<String> expectedLyrics = List.of(
                "Blinka, lilla stjärna där, hur jag undrar vad du är. " +
                        "Fjärran lockar du min syn, lik en diamant i skyn. " +
                        "Blinka, lilla stjärna där, hur jag undrar vad du är.",
                "Twinkle, twinkle little star, how I wonder what you are! " +
                        "Up above the world so high, Like a diamond in the sky. " +
                        "Twinkle, twinkle little star, how I wonder what you are!"
        );
        assertIterableEquals(expectedLyrics, actualLyrics);
    }

    @Test
    public void testXmlPitches() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        MXML mxml = xmlMapper.readValue(this.getClass().getResourceAsStream("/musescore_musicxml/blinka_lilla.musicxml"), MXML.class);

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
                    .map(TestXml::mxmlPitchToMidiPitch)
                    .toList();

            if (list.isEmpty()) {
                // TODO lower staff with rests...
                // Or is this nice?
                continue;
            }
            notes.add(list);
        }

        //.forEach(e -> {});

        // TODO perhaps keep empty list from rests? But not very interesting to index.
        List<List<Integer>> expectedMidiPitches = Collections.singletonList(List.of(
                60, 60, 67, 67, 69, 69, 67, 65, 65, 64, 64, 62, 62, 60,
                67, 67, 65, 65, 64, 64, 62, 67, 67, 65, 65, 64, 64, 62,
                60, 60, 67, 67, 69, 69, 67, 65, 65, 64, 64, 62, 62, 60
        ));
        assertIterableEquals(expectedMidiPitches, notes);
    }

    public static int jsonNodeNoteToPitch(JsonNode note) {
        JsonNode pitch = note.get("pitch");
        return mxmlPitchToMidiPitch(
                pitch.get("step").asText(),
                pitch.has("alter") ? (byte) pitch.get("alter").asInt() : 0,
                (byte) pitch.get("octave").asInt()
        );
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
    public static int mxmlPitchToMidiPitch(String step, byte alter, byte octave) {
        //return mxmlPitchToMidiPitch(new MXML.Pitch(step, alter, octave));
        MXML.Pitch pitch = new MXML.Pitch();
        pitch.step = step;
        pitch.alter = alter;
        pitch.octave = octave;

        return mxmlPitchToMidiPitch(pitch);
    }
}
