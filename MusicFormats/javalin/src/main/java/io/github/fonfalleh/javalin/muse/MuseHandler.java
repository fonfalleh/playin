package io.github.fonfalleh.javalin.muse;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class MuseHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        // TODO actually do something. Also naming
        Path temps = Files.createTempDirectory("musetmp");
        ctx.uploadedFileMap().forEach((name, list) -> {
            UploadedFile file = list.getFirst();
            String filename = file.filename();
            System.out.println(filename);
            Path path = temps.resolve(filename);
            FileUtil.streamToFile(file.content(), path.toString());
            MuseReader.runMuse(path);
        });

    }
}
