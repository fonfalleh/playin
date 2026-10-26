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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static void main() {
        
        TemplateEngine templateEngine = createTemplateEngine();

        Map<String, SolrDocEnricher> enricherMap = Enrichers.getEnricherMap();
        SolrClient solrClient = SolrClientFactory.getClient();
        Indexer indexer = new Indexer(enricherMap, solrClient);
        SongSearchHandler songSearchHandler = new SongSearchHandler(solrClient);

        javalinApp(songSearchHandler, indexer, templateEngine).start(7070);
    }

    public static Javalin javalinApp(SongSearchHandler songSearchHandler, Indexer indexer, TemplateEngine templateEngine) {
        return Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost); // TODO Eh. Fix sometime?
            });

            config.routes.get("/", songSearchHandler);
            config.routes.get("/lily", new LilyHandler());
            config.routes.get("/solrj", new SolrJSearchHandler(songSearchHandler));
            config.routes.get("/fileupload", ctx -> ctx.render("fileupload.jte"));
            config.routes.post("/fileupload", new FileUploadHandler(indexer)); // TODO add redirect?

            config.fileRenderer(new JavalinJte(templateEngine));

            config.staticFiles.add("static");
        });
    }

    public static TemplateEngine createTemplateEngine() {
        boolean usePrecompiledTemplates = Boolean.parseBoolean(System.getProperty("precompiled.templates", "false"));
        Path templateClassDirectory = Path.of("jte-classes").toAbsolutePath();
        if (usePrecompiledTemplates) {
            log.info("Using precompiled jte templates, expecting classes to be located in {}", templateClassDirectory);
            return TemplateEngine.createPrecompiled(templateClassDirectory, gg.jte.ContentType.Html);
        } else {
            log.info("Not using precompiled jte templates, compiled templates to be put in {}", templateClassDirectory);
            Path moduleRelativeTemplatePath = Path.of("src", "main", "resources", "templates");
            Path relativeTemplatePath;
            String moduleDir = "javalin";
            if (System.getProperty("user.dir").endsWith(moduleDir)) {
                relativeTemplatePath = moduleRelativeTemplatePath;
            } else {
                String projectDir = "playin";
                if (System.getProperty("user.dir").endsWith(projectDir)) {
                    relativeTemplatePath = Path.of(moduleDir).resolve(moduleRelativeTemplatePath);
                } else {
                    throw new RuntimeException("Must be run in " + moduleDir + " or " + projectDir + " directory in order to use dynamically compiled templates.");
                }
            }

            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(relativeTemplatePath);
            return TemplateEngine.create(codeResolver, templateClassDirectory, gg.jte.ContentType.Html);
        }
    }

}
