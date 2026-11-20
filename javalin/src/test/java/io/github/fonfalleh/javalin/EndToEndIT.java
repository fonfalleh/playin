package io.github.fonfalleh.javalin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fonfalleh.javalin.search.SongSearchHandler;
import io.github.fonfalleh.songuploader.FormUploadHelper;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import io.javalin.testtools.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

//TODO bad name
public class EndToEndIT extends ITBase {

    private static final Logger log = LoggerFactory.getLogger(EndToEndIT.class);
    ObjectMapper mapper = new ObjectMapper();

    void indexStoff() {

        //Ugly workaround to avoid double init of Javalin.
        SongSearchHandler songSearchHandler = new SongSearchHandler(solrClient);
        Javalin app2 = Main.javalinApp(songSearchHandler, indexer, Main.createTemplateEngine(), solrClient);

        JavalinTest.test(app2, (server, client) -> {
            FormUploadHelper.uploadChildDirectories( // TODO doesn't listen to errors in solr, just fire and forget
                    EndToEndIT.class.getResource("/songs").getPath(),
                    FormUploadHelper.getUriForPort(server.port()));
        });
        //TODO wip - verify somehow (also probably clean up example songs)
    }

    @BeforeEach
    void ensureIndexed() {
        if (!verifyIndexedDocs()) {
            indexStoff();
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

    @Test
    // TODO param query + id? @ParameterizedTest
    void testTitle() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/solrj?query=Blinka+lilla+stjärna");
            ResponseBody body = response.body();
            SolrResponse solrResponse = mapper.readValue(body.string(), SolrResponse.class);
            Map<String, Object> first = solrResponse.response().docs().getFirst();
            Assertions.assertEquals(200, response.code());
            Assertions.assertEquals("blinka_lilla", first.get("id"));
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SolrResponse(Body response) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        record Body(Long numFound, List<Map<String, Object>> docs) {
        }
    }
}
