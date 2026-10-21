package io.github.fonfalleh.javalin.fileupload;

import io.github.fonfalleh.javalin.muse.MuseReader;
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

public class FileUploadHandler implements Handler {

    private final Indexer indexer;

    public FileUploadHandler(Indexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Path temps = Files.createTempDirectory("fileupload");
        UploadedFile file = ctx.uploadedFile("file");

        String filename = file.filename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);

        Path path = temps.resolve("input." + extension);
        FileUtil.streamToFile(file.content(), path.toString());

        SolrInputDocument doc = solrDocFromFile(extension, path, temps);

        for (String field : Fields.fieldnames()) {
            String formValue = ctx.formParam(field);
            if (formValue != null && !formValue.isBlank()) {
                doc.addField(field, formValue);
            }
        }

        // TODO freeform parameters... or do we actually want that?

        try {
            indexer.indexDocument(doc);
        } catch (SolrServerException | IOException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    private SolrInputDocument solrDocFromFile(String extension, Path path, Path temps) {
        if ("mscz".equals(extension) || "mscx".equals(extension)) {
            MuseReader.runMuse(path);
            // On success, creates out.xml
            path = temps.resolve("out.xml");
        }
        // TODO Shouldn't have to know about solr, wrong abstraction layer
        SolrInputDocument doc = new SolrInputDocument();
        indexer.enrichDocWithFile(path.toFile(), doc);
        return doc;
    }

}
