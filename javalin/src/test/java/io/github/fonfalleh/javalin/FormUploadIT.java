package io.github.fonfalleh.javalin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fonfalleh.songuploader.FormUploadHelper;
import io.javalin.Javalin;
import io.javalin.testtools.HttpClient;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.Request;
import io.javalin.testtools.Response;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

/**
 * Note: Tests that call musescore behind the scenes probably needs to explicitly set <code>museBinary</code> property
 * to absolute path of a musescore binary if not on PATH (i.e. if located in e.g. ~/.local/bin)
 */
class FormUploadIT extends ITBase {

    ObjectMapper mapper = new ObjectMapper();

    private static final String IT_MULTIFORM_BOUNDARY = "---Javalin integration tests";
    private static final Logger log = LoggerFactory.getLogger(FormUploadIT.class);

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
        HttpRequest.BodyPublisher bodyPublisher = FormUploadHelper.constructBodyPublisher(
                IT_MULTIFORM_BOUNDARY,
                List.of("target/songs/blinka_lilla/blinka_lilla.musicxml", "target/songs/blinka_lilla/blinka_lilla.musicxml"),
                "id", "blinka_xml"
        );

        JavalinTest.test(app, (server, client) -> {
            Response response = postMultiPartForm(server, client, bodyPublisher);
            Assertions.assertEquals(200, response.code());
        });
    }

    @Test
    void testNoFileUpload() {
        HttpRequest.BodyPublisher bodyPublisher = FormUploadHelper.constructBodyPublisher(
                IT_MULTIFORM_BOUNDARY,
                null,
                "id", "nofile",
                "title", "No file"
        );

        JavalinTest.test(app, (server, client) -> {
            Response response = postMultiPartForm(server, client, bodyPublisher);
            Assertions.assertEquals(200, response.code());
        });
    }

    public static Response postMultiPartForm(Javalin server, HttpClient client, HttpRequest.BodyPublisher bodyPublisher) {
        return client.request(
                new Request.Builder()
                        .url("http://localhost:" + server.port() + "/formupload")
                        .header("Content-Type", "multipart/form-data; boundary=" + IT_MULTIFORM_BOUNDARY)
                        .post(bodyPublisher)
                        .build());
    }

    @Test
    void testMidiUpload() {
        HttpRequest.BodyPublisher bodyPublisher = FormUploadHelper.constructBodyPublisher(
                IT_MULTIFORM_BOUNDARY,
                List.of("target/songs/pacius_vårsång/vårsång.mid"),
                "id", "vårsång_midi",
                "title", "Vårsång"
        );
        JavalinTest.test(app, (server, client) -> {
            Response response = postMultiPartForm(server, client, bodyPublisher);
            Assertions.assertEquals(200, response.code());
        });
    }

    @Test
    void testMusescoreUpload() throws SolrServerException, IOException {
        HttpRequest.BodyPublisher bodyPublisher = FormUploadHelper.constructBodyPublisher(
                IT_MULTIFORM_BOUNDARY,
                List.of("target/songs/pacius_sång_för_nordiska_ynglingar/Sång_foer_Nordiska_ynglingar.mscx"),
                "id", "pacius_sång_för_nordiska_ynglingar_muse"
        );
        JavalinTest.test(app, (server, client) -> {
            Response response = postMultiPartForm(server, client, bodyPublisher);
            Assertions.assertEquals(200, response.code());
        });

        ArgumentCaptor<SolrInputDocument> captor = ArgumentCaptor.forClass(SolrInputDocument.class);
        Mockito.verify(solrClient).add(captor.capture());
        SolrInputDocument value = captor.getValue();
        Approvals.verify(value.jsonStr());
    }

    //TODO perhaps don't have this here? Kinda weird architecturally.
    @Test
    void testSongUploader() throws SolrServerException, IOException {
        JavalinTest.test(app, (server, client) -> {
            URI uri = FormUploadHelper.getUriForPort(server.port());
            FormUploadHelper.uploadDirectory("target/songs/pacius_sång_för_nordiska_ynglingar", uri);
        });
        ArgumentCaptor<SolrInputDocument> captor = ArgumentCaptor.forClass(SolrInputDocument.class);
        Mockito.verify(solrClient).add(captor.capture());
        SolrInputDocument value = captor.getValue();
        Approvals.verify(value.jsonStr());
    }

}
