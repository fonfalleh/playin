package io.github.fonfalleh.javalin.muse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class MuseReader {

    static final String museBinary;

    static {
        museBinary = System.getProperty("museBinary", "mscore");
    }

    static void runMuse(Path museFile) {
        String escapedMuseFile = museFile.toString().replace(" ", "\\ ");
        // "-platform offscreen" needed for minimal musescore without graphics
        // -- needed because of cli parse bug in musescore https://github.com/musescore/MuseScore/issues/17247
        List<String> commands = List.of(museBinary, "-o", "out.xml", escapedMuseFile, "--", "-platform", "offscreen");

        commands.forEach(System.out::println);

        ProcessBuilder processBuilder = new ProcessBuilder(commands).directory(new File(museFile.getParent().toString()));

        Process process = null;
        try {
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            System.out.println(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
