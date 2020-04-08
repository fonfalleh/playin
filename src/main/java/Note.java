import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Class for describing notes.
 * A note is either a note or a rest. A note has a pitch, a duration and a number of punctuations.
 */
public class Note implements Comparable<Note> {
    public short getPitch() {
        return pitch;
    }

    public Note setPitch(int pitch) {
        this.pitch = (short) pitch;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public Note setDuration(int duration) {
        this.duration = (byte) duration;
        return this;
    }

    // 48 is middle c, ~131hz
    private short pitch; // 69 is 440hz A

    // Durations, as in Lilypond they are written as 4 for quarter notes, etc.
    private byte duration;

    // Not sure how to model duration with punctuations as integers, so punctuations are separate from the duration
    private byte punctuations = 0;

    // Rests are notes for now.
    private boolean isRest;

    // Give pitch, defaults to quarter note
    public Note(short pitch) {
        this.pitch = pitch;
        this.duration = 4;
    }

    public Note(short pitch, byte duration) {
        this.pitch = pitch;
        this.duration = duration;
    }

    public Note(short pitch, byte duration, byte punctuations) {
        this.pitch = pitch;
        this.duration = duration;
        this.punctuations = punctuations;
    }

    public Note(short pitch, byte duration, byte punctuations, boolean isRest) {
        this.pitch = pitch;
        this.duration = duration;
        this.punctuations = punctuations;
        this.isRest = isRest;
    }


    public Note interval(int jump) {
        return new Note((short) (this.pitch + jump), this.duration);
    }

    private static String lilyPattern = "([A-Ha-hr])(es|is)*([',])*(\\d)*(\\.)*";
    static final int octaveSize = 12;

    /**
     * Method for creating instances frmo lilypond-style input.
     *
     * @param noteToken
     * @return
     */
    public static Note fromLily(String noteToken) {

        short pitch = 0;
        byte duration = 4; // TODO :repeated notes have same duration in lilypond
        byte punctuations = 0;

        boolean isRest = false;

        int intChar = Character.valueOf(noteToken.charAt(0));
        // TODO lol no
        // Actual: [0,2,4,5,7,9,11]....
        char baseNote = noteToken.charAt(0);
        pitch = baseNoteToShort(baseNote);
        if (baseNote == 'r')
            isRest = true;
        /*
        if (intChar >= 97 && intChar <= 103) {// Between 'a' and 'g'
            int offset = (intChar + 7) % 99; // gives 0 for
            pitch = (short) (intChar - 51); // 99 (valueOf('c')) - 48 (pitch of c) = 51 = offset
        } else if (intChar >= 65 && intChar <= 71) { // Between 'A' and 'G'
            pitch = (short) (intChar - 31); // 67 (valueOf('C')) - 36 (pitch of C) = 31 = offset
        } else if (noteToken.charAt(0) == 'r') {//rest
            isRest = true;
        } else {

        }

         */
        Scanner scanner = new Scanner(noteToken.substring(1)); // continue from second character of string after deciding pitch class

        // accidentals
        Pattern acc = Pattern.compile("[ei]s");
        while (true) {
            String accidental = scanner.findInLine(acc);
            if (accidental == null) {
                break;
            }
            if (accidental.charAt(0) == 'i') {
                pitch++;
            } else {
                pitch--;
            }
        }

        // octaves
        Pattern occ = Pattern.compile("[,']");
        while (true) {
            String octave = scanner.findInLine(occ);
            if (octave == null)
                break;
            else if (octave.equals(","))
                pitch -= octaveSize;
            else
                pitch += octaveSize;
        }
        // duration
        String durationString = scanner.findInLine("\\d+");
        if (durationString != null) {
            duration = Byte.parseByte(durationString);
        }
        // punctuation
        String punctuationString = scanner.findInLine("\\.*");
        if (punctuationString != null) {
            punctuations = (byte) punctuationString.length();
        }
        scanner.close();

        return new Note(pitch, duration, punctuations, isRest);
    }

    private static short baseNoteToShort(char baseNote) {
        short pitch;
        switch (Character.toLowerCase(baseNote)) { // octaves start at c, so a is above.
            case 'c':
                pitch = 48;
                break;
            case 'd':
                pitch = 50;
                break;
            case 'e':
                pitch = 52;
                break;
            case 'f':
                pitch = 53;
                break;
            case 'g':
                pitch = 55;
                break;
            case 'a':
                pitch = 57;
                break;
            case 'b':
                pitch = 59;
                break;
            default:
                return 0; // TODO throw exception?
        }
        if (Character.isUpperCase(baseNote)) {
            pitch -= 12;
        }
        return pitch;
    }

    public String pitchClass() {
        switch (pitch % 12) {
            case 0:
                return "c";
            case 1:
                return "cis/des";
            case 2:
                return "d";
            case 3:
                return "dis/ees";
            case 4:
                return "e";
            case 5:
                return "f";
            case 6:
                return "fis/ges";
            case 7:
                return "g";
            case 8:
                return "gis/aes";
            case 9:
                return "a";
            case 10:
                return "ais/bes";
            case 11:
                return "b";
            default:
                return "nope";
        }
    }

    public byte getPunctuations() {
        return punctuations;
    }

    public Note setPunctuations(byte punctuations) {
        this.punctuations = punctuations;
        return this;
    }

    public boolean isRest() {
        return isRest;
    }

    public Note setRest(boolean rest) {
        isRest = rest;
        return this;
    }

    public int compareTo(Note other) {
        return this.pitch - other.pitch;
    }
}
