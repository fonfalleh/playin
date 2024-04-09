package io.github.fonfalleh.formats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
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
        assertEquals(
                List.of(48, 50, 52, 53, 55, 57, 59, 60),
                pitches
        );
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
        int pitch;
        switch (step) {
            case "C" :
                pitch = 0;
                break;
            case "D" :
                pitch = 2;
                break;
            case "E" :
                pitch = 4;
                break;
            case "F" :
                pitch = 5;
                break;
            case "G" :
                pitch = 7;
                break;
            case "A" :
                pitch = 9;
                break;
            case "B" :
                pitch = 11;
                break;
            default:
                // TODO error handling?
                pitch = 0;
                break;
        }
        // accidentals
        pitch += alter;
        // c3 -> 48 = 3 * 12 + 12
        pitch += (octave + 1) * 12;
        return pitch;
    }
}
