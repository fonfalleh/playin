import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ChordTest {

    /**
     * Tests that getNotes() returns the notes the chord contains. Note that they are sorted by pitch.
     * Not sure if this is a good idea yet, but I hope so :)
     */
    @Test
    void getNotes() {
        Note note1 = Note.fromLily("e");
        Note note2 = Note.fromLily("g");
        Note note3 = Note.fromLily("c");
        Chord c = new Chord(Arrays.asList(
                note1, note2, note3
        ));

        assertTrue(c.getNotes().equals(Arrays.asList(note3, note1, note2)));
    }

    @Test
    void majorTriad() {
        Chord c = Chord.majorTriad(Note.fromLily("c"));


    }

    /**
     * Ok, so this is laughably overfitted and will be reworked when something more clever is in place. Probably Enums?
     */
    @Test
    void chordType() throws Exception {
        String actual = Chord.chordType(Chord.majorTriad(Note.fromLily("c")));
        Assertions.assertEquals("Major triad, with root c", actual);
    }
}