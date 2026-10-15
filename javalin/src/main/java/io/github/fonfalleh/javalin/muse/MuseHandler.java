package io.github.fonfalleh.javalin.muse;

import io.github.fonfalleh.playin.indexer.Indexer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO more like FileHandler? Or uploadHandler? If not for explicitly calling musehandler this could handle all files?
public class MuseHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Path temps = Files.createTempDirectory("musetmp");
        ctx.uploadedFileMap().forEach((name, list) -> {
            UploadedFile file = list.getFirst();
            String filename = file.filename();
            System.out.println(filename);
            Path path = tmpFilePath(filename, temps);
            FileUtil.streamToFile(file.content(), path.toString());

            MuseReader.runMuse(path);
            // On success, creates out.xml
            Path outPath = temps.resolve("out.xml");

            // TODO Shouldn't have to know about solr
            SolrInputDocument doc = new SolrInputDocument();
            // TODO create MuseEnricher? But... not really the same. Wrap logic? Chain?
            Indexer.enrichDocWithFile(outPath.toFile(), doc);
            // TODO other metadata using formParams. Class/record/enum? Don't want to specify things everywhere.

            //TODO not static access to stuff. Actual DI with explicit init.
            // Or maybe upload to other endpoints? But then must handle http clients everywhere. Hmm.

            try {
                Indexer.indexDocument(doc);
            } catch (SolrServerException | IOException e) {
                // TODO
                throw new RuntimeException(e);
            }

        });

    }

    Path tmpFilePath(String filename, Path basePath) {
        String extension = filename.substring(filename.lastIndexOf("."));
        if (".mscz".equals(extension) || ".mscx".equals(extension))
            return basePath.resolve("input" + extension);
        else throw new IllegalArgumentException("Uploaded file isn't .mscz or .mscx:" + filename);
    }
}
