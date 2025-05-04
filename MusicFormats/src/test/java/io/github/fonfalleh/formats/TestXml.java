package io.github.fonfalleh.formats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                        pitches.add(jsonNodeNoteToPitch(note))));

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

        //InputStream resourceAsStream = this.getClass().getResourceAsStream("/musescore_musicxml/blinka_lilla.musicxml");
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class MXML {
        // TODO see other @JacksonXml annotations
        String version;
        String encoding;

        @JsonProperty("part")
        Part part;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Part {
        @JsonProperty("id")
        String id;

        @JsonProperty("measure")
        List<Measure> measures;

    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Measure {
        @JsonProperty("number")
        String number;
        //@JacksonXmlProperty("note")
        @JsonProperty("note")
        @JacksonXmlElementWrapper(useWrapping = false)
        List<Note> notes = new ArrayList<>();

    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Note {
        @JsonProperty("pitch")
        Pitch pitch;


    }
    static class Pitch {
        @JsonProperty("step")
        String step;
        @JsonProperty("octave")
        String octave;
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
