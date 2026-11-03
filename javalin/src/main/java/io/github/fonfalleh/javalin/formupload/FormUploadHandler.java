package io.github.fonfalleh.javalin.formupload;

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
import java.util.List;

public class FormUploadHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(FormUploadHandler.class);
    private final Indexer indexer;

    public FormUploadHandler(Indexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // TODO Shouldn't have to know about solr, wrong abstraction layer
        SolrInputDocument doc = new SolrInputDocument();

        enrichWithFilesIfPresent(doc, ctx);

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

    public void enrichWithFilesIfPresent(SolrInputDocument doc, Context ctx) throws IOException {
        List<UploadedFile> files = ctx.uploadedFiles("file");
        if (files.isEmpty()) {
            return;
        }
        Path temps = Files.createTempDirectory("formupload");
        for (UploadedFile file : files) {
            String filename = file.filename();
            String extension = filename.substring(filename.lastIndexOf(".") + 1);

            Path path = temps.resolve("input." + extension);
            FileUtil.streamToFile(file.content(), path.toString());

            indexer.enrichDocWithFile(path.toFile(), doc);
        }
    }

}
