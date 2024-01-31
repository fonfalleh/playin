import org.junit.jupiter.api.Test;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class TestMidi {

    @Test
    public void testVeryHardcodedCScaleContents() throws Exception {
        String testFile = this.getClass().getResource("/ly_midi/c_scale.midi").getFile();
        Sequence sequence = MidiSystem.getSequence(new File(testFile));

        MidiMessage message = sequence.getTracks()[1].get(1).getMessage();
        assertInstanceOf(ShortMessage.class, message);

        int firstPitch = ((ShortMessage) sequence.getTracks()[1].get(1).getMessage()).getData1();
        assertEquals(48, firstPitch, "First pitch is 48, c in the small octave");
    }


    // TODO load midi file, check contents
    // For example, pitch of notes and check if repeats are unrolled

    // Also later, make sure that files generated from lilypond/musescore (see maven-exec shell) are correct

    // Also write tests for converting midi to indexable songs (must be implemented first though :D )
}
