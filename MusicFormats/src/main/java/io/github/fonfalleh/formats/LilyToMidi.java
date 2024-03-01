package io.github.fonfalleh.formats;

public class LilyToMidi {
    //TODO potentially make this the main method and let the String-call convert to char[]
    public static int lilyPitchToMidiPitch(char[] note) {
        return lilyPitchToMidiPitch(String.valueOf(note));
    }

    /**
     * Very naive approach for lilypond to midi-pitch in absolute mode
     * @param note
     * @return
     */
    public static int lilyPitchToMidiPitch(String note) {
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
