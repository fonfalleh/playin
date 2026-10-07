package io.github.fonfalleh.javalin;

import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.github.fonfalleh.javalin.muse.MuseHandler;
import io.github.fonfalleh.javalin.search.SongSearch;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinJte;
import org.apache.solr.common.util.NamedList;

import java.nio.file.Path;

public class Main {

    static void main() {
        Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost); // TODO Eh. Fix sometime?
            });
            config.routes.get("/solrj", ctx ->
                    {
                        NamedList<Object> response = SongSearch.querySolrForSongs(ctx);
                        ctx.json(response.jsonStr());
                    }
            );
            config.routes.get("/lily", ctx -> {
                String notes = ctx.queryParam("notes");
                if (notes != null) {
                    byte[] bytes = LilyPng.lilyToPng(notes);
                    ctx.contentType(ContentType.IMAGE_PNG);
                    ctx.result(bytes);
                } else {
                    ctx.result("Nope");
                }
            });

            config.routes.post("/muse", new MuseHandler());

            config.fileRenderer(new JavalinJte(createTemplateEngine()));
            config.routes.get("/", SongSearch.searchHandler);

            config.staticFiles.add("static");
        }).start(7070);
    }

    private static TemplateEngine createTemplateEngine() {
        boolean usePrecompiledTemplates = Boolean.parseBoolean(System.getProperty("precompiled.templates", "false"));
        if (usePrecompiledTemplates) {
            return TemplateEngine.createPrecompiled(Path.of("jte-classes"), gg.jte.ContentType.Html);
        } else {
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src", "main", "resources", "templates"));
            return TemplateEngine.create(codeResolver, gg.jte.ContentType.Html);
        }
    }

}
