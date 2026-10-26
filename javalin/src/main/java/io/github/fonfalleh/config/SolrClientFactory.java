package io.github.fonfalleh.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;

public class SolrClientFactory {
    private static final String SOLR_HOST = System.getProperty("solr.host", "localhost");
    private static final String SOLR_PORT = System.getProperty("solr.port", "8983");
    private static final String SOLR_BASE_URL = "http://" + SOLR_HOST + ":" + SOLR_PORT +"/solr";

    private static SolrClient client;

    // Note: Uses javalin's jetty version for solrj
    private static HttpJettySolrClient createClient() {
        return new HttpJettySolrClient.Builder()
                .withBaseSolrUrl(SOLR_BASE_URL)
                .withDefaultCollection("playin")
                .build();
    }

    public static SolrClient getClient() {
        if (client == null) {
            client = createClient();
        }
        return client;
    }
}
