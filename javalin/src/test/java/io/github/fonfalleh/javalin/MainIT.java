package io.github.fonfalleh.javalin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fonfalleh.config.Enrichers;
import io.github.fonfalleh.config.SolrClientFactory;
import io.github.fonfalleh.javalin.search.SongSearchHandler;
import io.github.fonfalleh.playin.indexer.Indexer;
import io.javalin.Javalin;
import io.javalin.testtools.HttpClient;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Request;
import io.javalin.testtools.Response;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.http.HttpRequest;

class MainIT {

    private static final String MULTIFORM_BOUNDARY = "---Javalin integration tests";
    private static final Logger log = LoggerFactory.getLogger(MainIT.class);
    ObjectMapper mapper = new ObjectMapper();

    static Javalin app;

    @BeforeEach
    void setup() {
        // TODO minimal setup? Or full E2E?
        SolrClient solrClient = SolrClientFactory.getClient();

        SongSearchHandler songSearchHandler = new SongSearchHandler(solrClient);
        Indexer indexer = new Indexer(Enrichers.getEnricherMap(), solrClient);

        app = Main.javalinApp(songSearchHandler, indexer, Main.createTemplateEngine());
    }

    @Test
    void testClient() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/solrj");

            String body = response.getBody().string();
            JsonNode jsonNode = mapper.readTree(body);
            int numFound = jsonNode.get("response")
                    .get("numFound").asInt();

            //TODO better test. Don't care for if this is true, just wanted to verify connection works
            Assertions.assertTrue(numFound >= 0);
        });
    }

    @Test
    void testMusicXmlUpload() {
        HttpRequest.BodyPublisher bodyPublisher = constructBodyPublisher(
                "songs/blinka_lilla/blinka_lilla.musicxml",
                "id", "blinka_xml"
        );

        JavalinTest.test(app, (server, client) -> {
            Response response = postMultiPartForm(server, client, bodyPublisher);
            Assertions.assertEquals(200, response.code());
        });
    }

    @Test
    void testNoFileUpload() {
        HttpRequest.BodyPublisher bodyPublisher = constructBodyPublisher(
                null,
                "id", "nofile"
        );

        JavalinTest.test(app, (server, client) -> {
            Response response = postMultiPartForm(server, client, bodyPublisher);
            Assertions.assertEquals(200, response.code());
        });
    }

    public static Response postMultiPartForm(Javalin server, HttpClient client, HttpRequest.BodyPublisher bodyPublisher) {
        return client.request(
                new Request.Builder()
                        .url("http://localhost:" + server.port() + "/fileupload")
                        .header("Content-Type", "multipart/form-data; boundary=" + MULTIFORM_BOUNDARY)
                        .post(bodyPublisher)
                        .build());
    }

    @Test
    void testMidiUpload() {
        HttpRequest.BodyPublisher bodyPublisher = constructBodyPublisher(
                "songs/pacius_vårsång/vårsång.mid",
                "id", "vårsång_midi"
        );
        JavalinTest.test(app, (server, client) -> {
            Response response = postMultiPartForm(server, client, bodyPublisher);
            Assertions.assertEquals(200, response.code());
        });
    }

    private static HttpRequest.BodyPublisher constructBodyPublisher(String filePathString, String... formNameAndValue) {
        try {
            StringBuilder builder = new StringBuilder();

            builder.append("--" + MULTIFORM_BOUNDARY + "\r\n");
            for (int i = 0; i < formNameAndValue.length; i += 2) {
                builder.append("Content-Disposition: form-data; name=\"").append(formNameAndValue[i]).append("\"\r\n\r\n")
                        .append(formNameAndValue[i + 1]);

                if (i + 2 == formNameAndValue.length && filePathString == null) {
                    // Skip separating boundary if last input and no file specified
                    break;
                }
                builder.append("\r\n--" + MULTIFORM_BOUNDARY + "\r\n");
            }

            if (filePathString == null) {
                builder.append("\r\n--" + MULTIFORM_BOUNDARY + "--");
                return HttpRequest.BodyPublishers.ofString(builder.toString());
            }

            // A little ugly, but seems to do the trick. Resources are copied into target during build. Couldn't get getResource() to work.
            File file = new File("target/" + filePathString);
            builder.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
            builder.append("Content-Type: application/octet-stream\r\n\r\n"); //Octet stream seems to be generic binary, so seems ok

            return HttpRequest.BodyPublishers.concat(
                    HttpRequest.BodyPublishers.ofString(builder.toString()),
                    HttpRequest.BodyPublishers.ofFile(file.toPath()),
                    HttpRequest.BodyPublishers.ofString("\r\n--" + MULTIFORM_BOUNDARY + "--")
            );
        } catch (Exception e) {
            log.error("Exception thrown during body construction", e);
            throw new RuntimeException(e);
        }

    }
}
