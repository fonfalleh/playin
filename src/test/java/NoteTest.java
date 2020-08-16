import base.Note;
import org.junit.jupiter.api.Assertions;

class NoteTest {

    @org.junit.jupiter.api.Test
    void fromLily() {
        Note n = Note.fromLily("aeses,8.");

        // a has pitch 57;
        // eses -> -2;
        // , -> -12
        // pitch should be 57 - 2 - 12 = 43

        // duration should be equal
        Assertions.assertEquals(43, n.getPitch());
        Assertions.assertEquals(8, n.getDuration());
        Assertions.assertEquals(1, n.getPunctuations());
        Assertions.assertEquals(false, n.isRest());
    }

    @org.junit.jupiter.api.Test
    void fromLilyMiddleC() {
        Note n = Note.fromLily("c");

        Assertions.assertEquals(48, n.getPitch());
    }

    @org.junit.jupiter.api.Test
    void fromLilyRest() {
        Note n = Note.fromLily("r2");

        Assertions.assertEquals(2, n.getDuration());
        Assertions.assertEquals(0, n.getPunctuations());
        Assertions.assertEquals(true, n.isRest());
    }

    @org.junit.jupiter.api.Test
    void fromLilyOctaves() {
        Note higher = Note.fromLily("f");
        Note lower = Note.fromLily("F'");

        Assertions.assertEquals(lower.getPitch(), higher.getPitch());
    }

    @org.junit.jupiter.api.Test
    void pitchClassFromLily() {
        Note note = Note.fromLily("a");
        Assertions.assertEquals("a", note.pitchClass());

        note = Note.fromLily("B");
        Assertions.assertEquals("b", note.pitchClass());

        note = Note.fromLily("aes");
        Assertions.assertEquals("gis/aes", note.pitchClass());
    }
}