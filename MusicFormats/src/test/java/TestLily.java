import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class TestLily {

    @Test
    public void testCScale() {
        List<String> names = List.of("c", "d", "e", "f", "g", "a", "b", "c'");
        List<Integer> expectedPitches = List.of(48, 50, 52, 53, 55, 57, 59, 60);
        assertEquals(expectedPitches, noteNamesToMidiPitch(names));
    }
    @Test
    public void testSomePitches() {
        List<String> names = List.of("c", "deses", "bis,", "c''", "c,,");
        List<Integer> expectedPitches = List.of(48, 48, 48, 72, 24);
        assertEquals(expectedPitches, noteNamesToMidiPitch(names));
    }

    List<Integer> noteNamesToMidiPitch(List<String> notes) {
        return notes.stream()
                .map(TestLily::lilyPitchToMidiPitch)
                .collect(Collectors.toList());
    }

    /**
     * Very naive approach for lilypond to midi-pitch in absolute mode
     * @param note
     * @return
     */
    static int lilyPitchToMidiPitch(String note) {
        int pitch;
        switch (note.charAt(0)) {
                case 'c' :
                    pitch = 48;
                    break;
                case 'd' :
                    pitch = 50;
                    break;
                case 'e' :
                    pitch = 52;
                    break;
                case 'f' :
                    pitch = 53;
                    break;
                case 'g' :
                    pitch = 55;
                    break;
                case 'a' :
                    pitch = 57;
                    break;
                case 'b' :
                    pitch = 59;
                    break;
                default:
                    // TODO error handling?
                    pitch = 0;
                    break;
        }
        int index = 1;
        // this one allows things like c'is,es which is illegal in lilypond
        // technically, it's pitch[accidentals][octaves]
        while (index < note.length()) {
            switch (note.charAt(index)) {
                case 'i' :
                    pitch++;
                    index += 2;
                    break;
                case 'e' :
                    pitch--;
                    index += 2;
                    break;
                case '\'' :
                    pitch += 12 ;
                    index++;
                    break;
                case ',' :
                    pitch -= 12 ;
                    index++;
                    break;
                default:
                    index++;
                    //TODO error handling?
            }
        }
        return pitch;
    }
}
