package base;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    // Oh my, https://en.wikipedia.org/wiki/List_of_chords, quite a few chords.
    // Start basic with triads and add some of the more common classes later
    public static String chordType(Chord c) throws Exception {
        int lowest = c.getNotes().get(0).getPitch();

        List<Short> pitches =
                c.getNotes()
                        .stream()
                        .map(n -> n.getPitch())
                        .collect(Collectors.toList());


        // TODO WIP
        // start basic, check interval size between notes
        // check for thirds / triads
        if (pitches.get(1) - pitches.get(0) == 4 && pitches.get(2) - pitches.get(0) == 7)
            return "Major triad, with root " + c.getNotes().get(0).pitchClass();
        throw new Exception();

        // return null;
    }

    public static List<Short> getPitches(Chord c) {
        return c.getNotes().stream().map(Note::getPitch).collect(Collectors.toList());
    }

    public static List<Short> getOffsets(Chord c) {
        List<Short> pitches = getPitches(c);
        short lowest = pitches.get(0);
        return pitches.stream()
                .map(p -> (short) (p - lowest)) // So, type conversions turned out to be more annoying that expected.
                .collect(Collectors.toList());
    }

    public Optional<ChordType> getChordType(List<Short> notes) {
        if (notes.size() < 2)
            return null;
        switch (notes.get(1)) {
            case 4:

        }

        // 0 4 7 Major // TODO: What about 0 4 7 12? For current idea, they are equivalent, which is not true.
        // 0 4 7 9 Major sixth
        // 0 4 7 9 2 Major sixth ninth (6 add 9) TODO think about modeling larger than one octave. Simply add another octave? Or is this equivalent to 0 2 4 7 9? (No)
        // Idea : Instead of modeling from 0 and looking at relative piches, look at intervals.
        
        return Optional.empty();
    }
}

