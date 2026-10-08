package io.github.fonfalleh.formats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.fonfalleh.formats.musicxml.LyricExtractor;
import io.github.fonfalleh.formats.musicxml.PitchExtractor;
import io.github.fonfalleh.formats.musicxml.XmlMetadata;
import io.github.fonfalleh.formats.musicxml.model.MXML;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public void testXmlMetadata() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        MXML mxml = xmlMapper.readValue(this.getClass().getResourceAsStream("/musescore_musicxml/juljul_nordqvist.musicxml"), MXML.class);

        XmlMetadata metadata = XmlMetadata.extract(mxml);

        assertEquals("Jul, jul, strålande jul", metadata.getTitle());
        assertEquals("Gustaf Nordqvist", metadata.getComposers().get(0));
        assertEquals("Edvard Evers", metadata.getLyricists().get(0));
    }

    @Test
    public void testXmlPitches() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        MXML mxml = xmlMapper.readValue(this.getClass().getResourceAsStream("/musescore_musicxml/blinka_lilla.musicxml"), MXML.class);

        List<List<Integer>> notes = PitchExtractor.extract(mxml);

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

    public static int mxmlPitchToMidiPitch(String step, byte alter, byte octave) {
        MXML.Pitch pitch = new MXML.Pitch();
        pitch.step = step;
        pitch.alter = alter;
        pitch.octave = octave;
        return PitchExtractor.mxmlPitchToMidiPitch(pitch);
    }
}
