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
        Path temps = Files.createTempDirectory("musetmp");
        ctx.uploadedFileMap().forEach((name, list) -> {
            UploadedFile file = list.getFirst();
            String filename = file.filename();
            System.out.println(filename);
            Path path = tmpFilePath(filename,temps);
            FileUtil.streamToFile(file.content(), path.toString());

            MuseReader.runMuse(path);
            // On success, creates out.xml
            // TODO need to work out dependencies so this can send things to solr
            temps.resolve("out.xml");

        });

    }
    Path tmpFilePath(String filename, Path basePath) {
        String extension = filename.substring(filename.lastIndexOf("."));
        if (".mscz".equals(extension) || ".mscx".equals(extension))
            return basePath.resolve("input" + extension);
        else throw new IllegalArgumentException("Uploaded file isn't .mscz or .mscx:" + filename);
    }
}
