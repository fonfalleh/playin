package io.github.fonfalleh.javalin;

import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.fonfalleh.config.Enrichers;
import io.github.fonfalleh.config.SolrClientFactory;
import io.github.fonfalleh.javalin.lily.LilyHandler;
import io.github.fonfalleh.javalin.fileupload.FileUploadHandler;
import io.github.fonfalleh.javalin.search.SolrJSearchHandler;
import io.github.fonfalleh.javalin.search.SongSearchHandler;
import io.github.fonfalleh.playin.indexer.Indexer;
import io.github.fonfalleh.playin.indexer.enrichers.SolrDocEnricher;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinJte;
import org.apache.solr.client.solrj.SolrClient;

import java.nio.file.Path;
import java.util.Map;

public class Main {

    static void main() {
        Map<String, SolrDocEnricher> enricherMap = Enrichers.getEnricherMap();
        SolrClient solrClient = SolrClientFactory.getClient();
        Indexer indexer = new Indexer(enricherMap, solrClient);
        SongSearchHandler songSearchHandler = new SongSearchHandler(solrClient);

        Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost); // TODO Eh. Fix sometime?
            });

            config.routes.get("/", songSearchHandler);
            config.routes.get("/lily", new LilyHandler());
            config.routes.get("/solrj", new SolrJSearchHandler(songSearchHandler));
            config.routes.get("/fileupload", ctx -> ctx.render("fileupload.jte")); // TODO intellij run uses parent as workdir by default, missing this. Find out more/why
            config.routes.post("/fileupload", new FileUploadHandler(indexer));

            config.fileRenderer(new JavalinJte(createTemplateEngine()));

            config.staticFiles.add("static");
        }).start(7070);
    }

    private static TemplateEngine createTemplateEngine() {
        boolean usePrecompiledTemplates = Boolean.parseBoolean(System.getProperty("precompiled.templates", "false"));
        if (usePrecompiledTemplates) {
            return TemplateEngine.createPrecompiled(Path.of("jte-classes"), gg.jte.ContentType.Html);
        } else {
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src", "main", "resources", "templates"));
            // TODO override jte-classes in create method below.
            return TemplateEngine.create(codeResolver, gg.jte.ContentType.Html);
        }
    }

}
