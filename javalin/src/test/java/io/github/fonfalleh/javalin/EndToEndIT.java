package io.github.fonfalleh.javalin;

import io.github.fonfalleh.songuploader.FormUploadHelper;
import io.javalin.testtools.JavalinTest;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

//TODO bad name
public class EndToEndIT extends ITBase {

    @Test
    void foo() throws SolrServerException, IOException {
        JavalinTest.test(app, (server, client) -> {
            FormUploadHelper.uploadChildDirectories(
                    EndToEndIT.class.getResource("/songs").getPath(),
                    FormUploadHelper.getUriForPort(server.port()));
        });
        //TODO wip - verify somehow (also probably clean up example songs)
    }
}
