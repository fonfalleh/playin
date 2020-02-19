import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Describes a chord.
 */
public class Chord {
    public List<Note> getNotes() {
        return notes;
    }

    public Chord setNotes(List<Note> notes) {
        this.notes = notes;
        return this;
    }

    List<Note> notes;

    // Make sure that notes are always in order
    public Chord(List<Note> notes) {
        Collections.sort(notes); // TODO make copy first?
        this.notes = notes;
    }

    public static Chord majorTriad(Note root) {
        return new Chord(Arrays.asList(
                root,
                root.interval(4),
                root.interval(7)
        ));
    }

    public static String chordType(Chord c) throws Exception {
        int lowest = c.getNotes().get(0).getPitch();

        List<Integer> pitches =
                c.getNotes()
                        .stream()
                        .map(n -> n.getPitch())
                        .collect(Collectors.toList());


        // TODO WIP
        // start basic, check interval size between notes
        // check for thirds / triads
        throw new Exception();

        // return null;
    }
}
