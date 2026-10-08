package io.github.fonfalleh.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;

public class SolrClientFactory {
    private static final String SOLR_HOST = System.getProperty("solr.host", "localhost");
    private static final String SOLR_BASE_URL = "http://" + SOLR_HOST + ":8983/solr";

    private static final SolrClient client = createClient();

    // Note: Uses javalin's jetty version for solrj
    private static HttpJettySolrClient createClient() {
        return new HttpJettySolrClient.Builder()
                .withBaseSolrUrl(SOLR_BASE_URL)
                .withDefaultCollection("playin")
                .build();
    }

    public static SolrClient getClient() {
        return client;
    }
}
