package io.github.fonfalleh.javalin.formupload;

import io.github.fonfalleh.javalin.muse.MuseReader;
import io.github.fonfalleh.playin.indexer.Fields;
import io.github.fonfalleh.playin.indexer.Indexer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import io.javalin.util.FileUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FormUploadHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(FormUploadHandler.class);
    private final Indexer indexer;

    public FormUploadHandler(Indexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        SolrInputDocument doc = new SolrInputDocument();

        enrichWithFileIfPresent(doc, ctx);

        for (String field : Fields.fieldnames()) {
            String formValue = ctx.formParam(field);
            if (formValue != null && !formValue.isBlank()) {
                doc.addField(field, formValue);
            }
        }
        try {
            indexer.indexDocument(doc);
        } catch (SolrServerException | IOException e) {
            log.error("Exception thrown while indexing song.", e);
            throw new RuntimeException(e);
        }
        //TODO : Maybe add redirect to form + "song with id <id> was indexed successfully" as header?
        ctx.result("Song indexed successfully.");
    }

    public void enrichWithFileIfPresent(SolrInputDocument doc, Context ctx) throws IOException {
        UploadedFile file = ctx.uploadedFile("file");
        if (file == null) {
            return;
        }
        Path temps = Files.createTempDirectory("formupload");

        String filename = file.filename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);

        Path path = temps.resolve("input." + extension);
        FileUtil.streamToFile(file.content(), path.toString());

        enrichWithFile(doc, extension, path, temps);
    }

    private void enrichWithFile(SolrInputDocument doc, String extension, Path path, Path temps) {
        if ("mscz".equals(extension) || "mscx".equals(extension)) {
            MuseReader.runMuse(path);
            // On success, creates out.xml
            path = temps.resolve("out.xml");
        }
        // TODO Shouldn't have to know about solr, wrong abstraction layer
        indexer.enrichDocWithFile(path.toFile(), doc);
    }

}
