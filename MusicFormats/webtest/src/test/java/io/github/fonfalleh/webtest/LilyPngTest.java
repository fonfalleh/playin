package io.github.fonfalleh.webtest;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LilyPngTest {

    @Test
    void testfOO() throws IOException {

        ArrayList<String> commands = new ArrayList<>(3);

        commands.add("/bin/bash");
        commands.add("./small_lilypond.sh");


        //Process lilypond = new ProcessBuilder("lilypond", "-h").start();
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        commands.add("c d' e");
        Process lilypond = processBuilder.start();

        File file = new File("out.png");
        byte[] bytes = Files.readAllBytes(Path.of("out.png"));
        file.delete();

        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(lilypond.getInputStream()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            System.out.println(out);
        }
    }

}
