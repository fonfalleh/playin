package io.github.fonfalleh.javalin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SongCrudIT extends ITBase {

    ObjectMapper mapper = new ObjectMapper();

    private static final String FIRST_BODY = """
            {
                "id" : "1337",
                "title" : [ "leet" ],
                "source" : "http://localhost:7070",
                "editor" : [ "me" ]
            }
            """;
    private static final String PATCH_BODY = """
            {
                "title" : [ "leet revamped" ],
                "editor" : [ "Some guy" ]
            }
            """;
    private static final String PATCHED_BODY = """
            {
                "id" : "1337",
                "title" : [ "leet revamped" ],
                "source" : "http://localhost:7070",
                "editor" : [ "Some guy" ]
            }
            """;

    @Test
    @Order(1)
    void get_expectMissing() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/song/1337");
            Assertions.assertEquals(404, response.code());
        });
    }

    @Test
    @Order(2)
    void post_expectOk() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.post("/song", FIRST_BODY);
            Assertions.assertEquals(200, response.code());
        });
    }

    @Test
    @Order(3)
    void get_expectBody() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/song/1337");
            Assertions.assertEquals(200, response.code());

            JsonNode doc = mapper.readTree(response.body().string());
            ((ObjectNode) doc).remove("_version_");
            Assertions.assertEquals(mapper.readTree(FIRST_BODY), doc);
        });
    }

    @Test
    @Order(4)
    void patch_expectOk() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.patch("/song/1337", PATCH_BODY);
            Assertions.assertEquals(200, response.code());
        });
    }

    @Test
    @Order(5)
    void get_expectUpdatedBody() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/song/1337");
            Assertions.assertEquals(200, response.code());

            JsonNode doc = mapper.readTree(response.body().string());
            ((ObjectNode) doc).remove("_version_");
            Assertions.assertEquals(mapper.readTree(PATCHED_BODY), doc);
        });
    }

    @Test
    @Order(6)
    void delete_expectOk() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.delete("/song/1337");
            Assertions.assertEquals(200, response.code());
        });
    }

    @Test
    @Order(7)
    void getRemoved_expectMissing() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/song/1337");
            Assertions.assertEquals(404, response.code());
        });
    }
}
