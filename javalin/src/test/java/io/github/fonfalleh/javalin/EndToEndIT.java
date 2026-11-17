package io.github.fonfalleh.javalin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fonfalleh.songuploader.FormUploadHelper;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import io.javalin.testtools.TestConfig;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//TODO bad name
public class EndToEndIT extends ITBase {

    private static final Logger log = LoggerFactory.getLogger(EndToEndIT.class);
    ObjectMapper mapper = new ObjectMapper();

    static void indexStoff(Javalin app) {
        JavalinTest.test(app, (server, client) -> {
            FormUploadHelper.uploadChildDirectories( // TODO doesn't listen to errors in solr, just fire and forget
                    EndToEndIT.class.getResource("/songs").getPath(),
                    FormUploadHelper.getUriForPort(server.port()));
        });
        //TODO wip - verify somehow (also probably clean up example songs)
    }

    @BeforeEach
    void ensureIndexed() {
        if (!verifyIndexedDocs()) {
            indexStoff(app); // TODO can't init app twice :(
        }
    }

    private boolean verifyIndexedDocs() {
        Set<String> wantedIds = Set.of(
                "bach_oberkeet",
                "blinka_lilla",
                "juljul_nordqvist",
                "pacius_fredman13",
                "pacius_sång_för_nordiska_ynglingar_muse",
                "pacius_studentsång",
                "pacius_vårsång",
                "prinsgustaf_vårsång"
        );
        try {
            return solrClient.getById(wantedIds).getNumFound() == wantedIds.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    void testSearch() {
        JavalinTest.test(app, (server, client) -> {

            Response response = client.get("/solrj");
            SolrResponse solrResponse = mapper.readValue(response.body().string(), SolrResponse.class);
            Assertions.assertEquals(200, response.code());
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SolrResponse(Body response) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        record Body(Long numFound, List<Map<String, Object>> docs) {
        }
    }
}
