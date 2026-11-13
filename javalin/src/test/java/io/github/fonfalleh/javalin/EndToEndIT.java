package io.github.fonfalleh.javalin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fonfalleh.songuploader.FormUploadHelper;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import io.javalin.testtools.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;

//TODO bad name
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EndToEndIT extends ITBase {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    @Order(1) // TODO turn into a beforeach instead? "app" has lifetime per method so should be shared?
    void indexStoff() {
        JavalinTest.test(app, (server, client) -> {
            FormUploadHelper.uploadChildDirectories( // TODO doesn't listen to errors in solr, just fire and forget
                    EndToEndIT.class.getResource("/songs").getPath(),
                    FormUploadHelper.getUriForPort(server.port()));
        });
        //TODO wip - verify somehow (also probably clean up example songs)
    }

    @Test
    @Order(2)
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
