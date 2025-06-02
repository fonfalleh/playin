package io.github.fonfalleh.formats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public void testXmlMapper() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        MXML mxml = xmlMapper.readValue(this.getClass().getResourceAsStream("/musescore_musicxml/blinka_lilla.musicxml"), MXML.class);

        List<Lyric> lyricElements = mxml.parts.get(0).measures.stream()
                .flatMap(Measure::notes)
                .flatMap(Note::lyrics)
                .filter(Predicate.not(Objects::isNull))
                .toList();

        StringBuilder sb;
        for (Lyric l: lyricElements) {
            sb = getStringBuilder(l.number);
            switch (l.syllabic) {
                case begin, middle -> sb.append(l.text);
                case single, end -> {
                    sb.append(l.text);
                    sb.append(' ');
                }
            }
        }
        List<String> actualLyrics = stringBuilders.values().stream()
                .map(b -> b.deleteCharAt(b.length() - 1))
                .map(StringBuilder::toString)
                .toList();

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

    static Map<Integer, StringBuilder> stringBuilders = new TreeMap<>();
    static StringBuilder getStringBuilder(int i) {
        if (!stringBuilders.containsKey(i)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilders.put(i, stringBuilder);
            return stringBuilder;
        } else {
            return stringBuilders.get(i);
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class MXML {
        // TODO see other @JacksonXml annotations

        @JacksonXmlProperty(localName = "version")
        String version;

        @JacksonXmlProperty(localName = "part")
        @JacksonXmlElementWrapper(useWrapping = false)
        List<Part> parts;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Part {
        @JacksonXmlProperty(localName = "id")
        String id;

        @JacksonXmlProperty(localName = "measure")
        @JacksonXmlElementWrapper(useWrapping = false)
        List<Measure> measures;

    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Measure {
        @JsonProperty("number")
        int number;

        @JacksonXmlProperty(localName = "note")
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonMerge // This fixed mystery of missing notes, if all listed elements are not together
        public List<Note> notes = new ArrayList<>();

        public Stream<Note> notes() {
            return notes.stream();
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Note {
        @JacksonXmlProperty(localName = "default-x", isAttribute = true)
        String defaultX;

        @JsonProperty("pitch")
        Pitch pitch;

        @JacksonXmlProperty(localName = "duration")
        int duration;

        @JacksonXmlProperty(localName = "voice")
        int voice;

        @JacksonXmlProperty(localName = "type")
        String type; // TODO enum

        @JacksonXmlProperty(localName = "staff")
        int staff;

        @JacksonXmlProperty(localName = "lyric")
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonMerge // Perhaps not needed here?
        List<Lyric> lyric;

        public Stream<Lyric> lyrics() {
            return lyric == null ? null :lyric.stream();
        }
    }

    static class Pitch {
        // Both these work somehow
        @JacksonXmlProperty(localName = "step")
        String step;
        @JsonProperty("octave")
        String octave;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Lyric {
        @JacksonXmlProperty(localName = "syllabic")
        Syllabic syllabic;
        @JacksonXmlProperty(localName = "number", isAttribute = true)
        int number;
        @JsonProperty("text")
        String text;
    }
    // https://w3c.github.io/musicxml/musicxml-reference/data-types/syllabic/
    enum Syllabic {
        begin,
        end,
        middle,
        single
    }

    public static int jsonNodeNoteToPitch(JsonNode note) {
        JsonNode pitch = note.get("pitch");
        return mxmlPitchToMidiPitch(
                pitch.get("step").asText(),
                pitch.has("alter") ? (byte) pitch.get("alter").asInt() : 0,
                (byte) pitch.get("octave").asInt()
        );
    }

    public static int mxmlPitchToMidiPitch(String step, byte alter, byte octave) {
        int pitch = switch (step) {
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
        pitch += alter;
        // c3 -> 48 = 3 * 12 + 12
        pitch += (octave + 1) * 12;
        return pitch;
    }
}
