package io.github.fonfalleh.javalin.muse;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class MuseHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        // TODO actually do something. Also naming
        Path temps = Files.createTempDirectory("temps");
        String filepath;
        ctx.uploadedFileMap().forEach((name, list) -> {
            System.out.println(name);
            Path path = temps.resolve(name);
            FileUtil.streamToFile(list.getFirst().content(), path.toString());
            MuseReader.runMuse(path);
        });

    }
}
