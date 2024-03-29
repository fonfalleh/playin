import io.github.fonfalleh.formats.MidiNoteExtractor;
import org.junit.jupiter.api.Test;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class TestMidi {

    @Test
    public void testVeryHardcodedCScaleContents() throws Exception {
        Sequence sequence = loadMidiSequenceFromResource("/ly_midi/c_scale.midi");

        MidiMessage message = sequence.getTracks()[1].get(1).getMessage();
        assertInstanceOf(ShortMessage.class, message);

        int firstPitch = ((ShortMessage) sequence.getTracks()[1].get(1).getMessage()).getData1();
        assertEquals(48, firstPitch, "First pitch is 48, c in the small octave");
    }

    /**
     * c d e f
     * g a b c
     * g a b c
     * c b a g
     */
    static List<Integer> REPEATS_EXPECTED_PITCHES = List.of(
            48, 50, 52, 53,
            55, 57, 59, 60,
            55, 57, 59, 60,
            60, 59, 57, 55);

    @Test
    public void testMSRepeatsMidi() throws InvalidMidiDataException, IOException {
        Sequence sequence = loadMidiSequenceFromResource("/musescore_midi/repeats.mid");
        List<Integer> pitchesFromTrack = MidiNoteExtractor.getPitchesFromTrack(sequence.getTracks()[0]);
        assertEquals(REPEATS_EXPECTED_PITCHES, pitchesFromTrack);
    }

    @Test
    public void testLilyRepeatsMidi() throws InvalidMidiDataException, IOException {
        Sequence sequence = loadMidiSequenceFromResource("/ly_midi/repeats.midi");
        List<Integer> pitchesFromTrack = MidiNoteExtractor.getPitchesFromTrack(sequence.getTracks()[1]);
        assertEquals(REPEATS_EXPECTED_PITCHES, pitchesFromTrack);
    }

    @Test
    public void testLilyPitchesMidi() throws InvalidMidiDataException, IOException {
        Sequence sequence = loadMidiSequenceFromResource("/ly_midi/pitches.midi");
        List<Integer> pitchesFromTrack = MidiNoteExtractor.getPitchesFromTrack(sequence.getTracks()[1]);
        assertEquals(List.of(48, 48, 48, 72, 24), pitchesFromTrack);
    }

    public static Sequence loadMidiSequenceFromResource(String path) throws InvalidMidiDataException, IOException {
        String testFile = TestMidi.class.getResource(path).getFile();
        return MidiSystem.getSequence(new File(testFile));
    }


    // TODO load midi file, check contents
    // For example, pitch of notes and check if repeats are unrolled

    // Also later, make sure that files generated from lilypond/musescore (see maven-exec shell) are correct

    // Also write tests for converting midi to indexable songs (must be implemented first though :D )
}
