package io.github.fonfalleh.javalin;

import io.github.fonfalleh.config.Enrichers;
import io.github.fonfalleh.config.SolrClientFactory;
import io.github.fonfalleh.javalin.search.SongSearchHandler;
import io.github.fonfalleh.playin.indexer.Indexer;
import io.javalin.Javalin;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public abstract class ITBase {

    Javalin app;
    Indexer indexer;
    SolrClient solrClient;

    @BeforeEach
    void setup() {
        // TODO minimal setup? Or full E2E?
        solrClient = Mockito.spy(SolrClientFactory.getClient());

        SongSearchHandler songSearchHandler = new SongSearchHandler(solrClient);
        indexer = new Indexer(Enrichers.getEnricherMap(), solrClient);

        app = Main.javalinApp(songSearchHandler, indexer, Main.createTemplateEngine(), solrClient);
    }
}
