package io.github.fonfalleh.javalin;

import gg.jte.Content;
import gg.jte.TemplateOutput;
import io.github.fonfalleh.javalin.muse.MuseHandler;
import io.github.fonfalleh.javalin.search.SongSearch;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinJte;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost); // Eh.
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

            config.fileRenderer(new JavalinJte());
            config.routes.get("/", SongSearch.searchHandler);

            config.staticFiles.add("static");
        }).start(7070);
    }

    public record SongSearchResult(SolrDocumentList docs) implements Content {

        @Override
        @SuppressWarnings("unchecked")
        public void writeTo(TemplateOutput output) {
            if (docs == null || docs.isEmpty()) {
                output.writeContent("<p>No results to display!</p>");
                return;
            }
            docs.forEach(d -> {
                output.writeContent("<section class=\"song\">");
                List<String> titles = (List<String>) d.get("title");
                output.writeContent("<h2>" +
                        String.join("<br>", titles) + "</h2>");

                List<String> composers = (List<String>) d.get("composer");
                if (composers != null) {
                    output.writeContent("<p>" +
                            String.join(", ", composers) + "</p>");
                    output.writeContent("</section>");
                }
            });
        }
    }

}
