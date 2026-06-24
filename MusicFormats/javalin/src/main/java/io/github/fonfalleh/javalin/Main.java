package io.github.fonfalleh.javalin;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinJte;
import io.javalin.util.FileUtil;
import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;
import org.apache.solr.client.solrj.request.json.JsonQueryRequest;
import org.apache.solr.common.util.NamedList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {

    // Note: Uses javalin's jetty version for solrj
    static final HttpJettySolrClient client = new HttpJettySolrClient.Builder()
            .withBaseSolrUrl("http://localhost:8983/solr")
            .withDefaultCollection("playin")
            .build();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost); // Eh.
            });
            config.routes.get("/solrj", ctx ->
                    {
                        JsonQueryRequest request = buildJsonQuery(ctx);
                        NamedList<Object> response = client.request(request);
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
            config.routes.post("/muse", ctx -> {
                ctx.formParamMap();


                UploadedFile foo = ctx.uploadedFile("foo");
                Path temps = Files.createTempDirectory("temps");
                String filepath;
                ctx.uploadedFileMap().forEach((name, list) -> {
                    System.out.println(name);
                    Path path = temps.resolve(name);
                    FileUtil.streamToFile(list.getFirst().content(), path.toString());
                    MuseReader.runMuse(path);
                });


                //FileUtil.streamToFile(foo.content(), filepath);
                //MuseReader.runMuse(filepath);
            });

            config.fileRenderer(new JavalinJte());
            config.routes.get("/jte", ctx -> {
                String foo = ctx.queryParam("foo");
                ctx.render("test.jte", Map.of("foo", foo != null ? foo : "foo"));
            });
        }).start(7070);
    }

    static JsonQueryRequest buildJsonQuery(Context context) {
        String query = context.queryParam("query");
        String composer = context.queryParam("composer");

        JsonQueryRequest request = new JsonQueryRequest()
                .withParam("hl", true)
                .withFacet("composer", Map.of(
                        "type", "terms",
                        "field", "composer_facet",
                        "domain", Map.of("query", "*:*")
                ));

        setQuery(query, request);
        setFilter(composer, request);
        return request;
    }

    private static void setFilter(String composer, JsonQueryRequest request) {
        if (composer != null)
            request.withFilter(Map.of(
                    "#tag", "composer:" + composer
            ));
    }

    private static void setQuery(String queryParam, JsonQueryRequest request) {
        if (queryParam == null) {
            request.setQuery("*:*");
        } else {
            Map<String, Object> queryMap = Map.of("bool", Map.of(
                    "should", List.of(
                            Map.of(
                                    "edismax", Map.of(
                                            "query", queryParam,
                                            "qf", List.of(
                                                    "title",
                                                    "composer",
                                                    "lyrics",
                                                    "lyricist")
                                    )),
                            Map.of(
                                    "edismax", Map.of(
                                            "query", '"' + queryParam + '"',
                                            "qf", List.of(
                                                    "pitches",
                                                    "pitches_relative")
                                    ))
                    )
            ));
            request.setQuery(queryMap);
        }
    }
}
