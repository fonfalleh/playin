package io.github.fonfalleh.formats;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

    static List<Integer> noteNamesToMidiPitch(List<String> notes) {
        return notes.stream()
                .map(LilyToMidi::lilyPitchToMidiPitch)
                .collect(Collectors.toList());
    }

    // TODO
    @Test
    public void testThing() throws Exception {
        //ProcessBuilder builder = new ProcessBuilder("echo \"#(ly:set-option 'crop #t) { c d e } \" | lilypond --svg -o lols -");
        ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "echo \"#(ly:set-option 'crop #t) { c d e } \" | lilypond --pdf -o lols -");
        //ProcessBuilder builder = new ProcessBuilder("pwd");
        //ProcessBuilder builder = new ProcessBuilder("echo", "\"#(ly:set-option 'crop #t) { c d e }\"", "|", "lilypond", "--svg", "-" );
        Process process = builder.start();

        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            System.out.println(out);
        }
    }

}
