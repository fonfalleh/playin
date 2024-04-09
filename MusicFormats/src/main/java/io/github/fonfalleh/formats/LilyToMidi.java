package io.github.fonfalleh.formats;

public class LilyToMidi {
    public static int lilyPitchToMidiPitch(String note) {
        return lilyPitchToMidiPitch(note.toCharArray());
    }

    /**
     * Very naive approach for lilypond to midi-pitch in absolute mode
     * @param note
     * @return
     */
    public static int lilyPitchToMidiPitch(char[] note) {
        int pitch;
        switch (note[0]) {
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
                    return -1;
        }
        int index = 1;
        // this one allows things like c'is,es which is illegal in lilypond
        // technically, it's pitch[accidentals][octaves]
        while (index < note.length) {
            switch (note[index]) {
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
                    return -1;
            }
        }
        return pitch;
    }
}
