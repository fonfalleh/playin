package io.github.fonfalleh.javalin;

import io.javalin.testtools.JavalinTest;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class EndToEndIT extends ITBase {

    @Test
    void foo() throws SolrServerException, IOException {
        indexer.indexDirsFromPath(EndToEndIT.class.getResource("/songs").getPath());

        //JavalinTest.test();
    }
}
