package io.github.fonfalleh.javalin;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SongCrudHandler implements CrudHandler {

    private static final Logger log = LoggerFactory.getLogger(SongCrudHandler.class);
    private final SolrClient solrClient;

    public SongCrudHandler(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    @Override
    public void getAll(@NotNull Context context) {
        // Nah. Don't wanna.
        context.status(404);
    }

    @Override
    public void getOne(@NotNull Context context, @NotNull String id) {
        try {
            SolrDocument doc = solrClient.getById(id);
            if (doc != null) {
                context.json(doc.jsonStr());
                return;
            }
        } catch (SolrServerException | IOException e) {
            context.status(500);
            return;
        }
        context.json("{\"msg\" :\"No document found with id " + id + "\"}");
        context.status(404);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void create(@NotNull Context context) {
        Map<String, Object> map = context.bodyAsClass(Map.class);
        SolrInputDocument doc = new SolrInputDocument();
        map.forEach(doc::addField);

        try {
            solrClient.add(doc);
            context.status(200);

        } catch (SolrServerException | IOException e) {
            log.warn("Error when indexing document.", e);
            context.status(500);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(@NotNull Context context, @NotNull String id) {
        Map<String, Object> patchBody = context.bodyAsClass(Map.class);
        // Don't allow id from body, as that may create copies of doc with new id instead of updating
        patchBody.put("id", id);
        try {
            SolrDocument oldDoc = solrClient.getById(id);
            SolrInputDocument updatedDoc = new SolrInputDocument();
            Map<String, Object> fieldsToInclude;
            fieldsToInclude = new LinkedHashMap<>();
            if (oldDoc != null) {
                fieldsToInclude.putAll(oldDoc);
            }
            fieldsToInclude.putAll(patchBody);
            fieldsToInclude.forEach(updatedDoc::addField);
            solrClient.add(updatedDoc);
            context.status(200);

        } catch (SolrServerException | IOException e) {
            log.warn("Failed patching document {}", id, e);
            context.status(500);
        }
    }

    @Override
    public void delete(@NotNull Context context, @NotNull String id) {
        try {
            solrClient.deleteById(id);
            context.status(200);
        } catch (SolrServerException | IOException e) {
            context.status(500);
        }
    }
}
