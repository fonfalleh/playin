package io.github.fonfalleh.javalin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LilyPng {

    static byte[] lilyToPng(String lily) throws IOException {

        List<String> commands = List.of("/bin/bash", "./small_lilypond.sh", lily);
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        Process lilypond = null;
        try {
            lilypond = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(lilypond.getInputStream()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            System.out.println(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File file = new File("out.png");
        byte[] bytes = Files.readAllBytes(Path.of("out.png"));
        file.delete();

        return bytes;
    }

}
