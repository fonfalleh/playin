package io.github.fonfalleh.javalin.muse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class MuseReader {

    static void runMuse(Path path) {

        // Wow!
        //TODO make it make sense sometime
        List<String> commands = List.of("/home/jacob/.local/bin/musescore", "-o", "outASDASD.xml", path.toString());
        //List<String> commands = List.of("musescore", lily);
        ProcessBuilder processBuilder = new ProcessBuilder(commands).directory(new File(path.getParent().toString()));

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
