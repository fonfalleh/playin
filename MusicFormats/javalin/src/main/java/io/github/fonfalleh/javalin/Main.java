package io.github.fonfalleh.javalin;

import gg.jte.Content;
import gg.jte.TemplateOutput;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinJte;
import io.javalin.util.FileUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;
import org.apache.solr.client.solrj.request.json.JsonQueryRequest;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                        NamedList<Object> response = querySolr(ctx);
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
                NamedList<Object> response = querySolr(ctx);
                SolrDocumentList docs = getSolrDocuments(response);




                /* TODO build response:
                docs
                facets - with selection -> options
                 */
                Map<String, Object> facets = (Map<String, Object>) response.get("facets");
                Map<String, Object> composer =  (Map<String, Object>) facets.get("composer");
                List<Map<String, Object>> buckets =  (List<Map<String, Object>>) composer.get("buckets");
                HashMap<String, Object> params = new HashMap<>();
                Optional.ofNullable(ctx.queryParam("q")).ifPresent(q -> params.put("q", q));
                FacetList facetList = FacetList.of(ctx.queryParam("composer"), buckets);
                params.put("facetList", facetList);
                ctx.render("test.jte", params);
            });

            config.staticFiles.add("static");
        }).start(7070);
    }

    public record FacetList(List<FacetOption> options) {
        public static FacetList of(String param, List<Map<String, Object>> buckets) {
            ArrayList<FacetOption> list = new ArrayList<>();
            for (Map<String, Object> bucket : buckets) {
                String value = (String) bucket.get("val");
                long count = (Long) bucket.get("count");
                boolean selected = value.equals(param);
                list.add(new FacetOption(value, count, selected));
            }
            return new FacetList(list);
        }
    }
    public record FacetOption(String name, long count, boolean selected){}

    public static SolrDocumentList getSolrDocuments(NamedList<Object> response) {
        return (SolrDocumentList) response.get("response");
    }

    private static NamedList<Object> querySolr(Context ctx) throws SolrServerException, IOException {
        JsonQueryRequest request = buildJsonQuery(ctx);
        return client.request(request);
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
        setComposerFilter(composer, request);
        return request;
    }

    private static void setComposerFilter(String composer, JsonQueryRequest request) {
        if (composer == null || composer.isBlank() || "All".equals(composer)) { // TODO All... kinda hard coupling
            return;
        }
        request.withFilter(Map.of(
                "#tag", "composer:" + composer
        ));
    }

    private static void setQuery(String queryParam, JsonQueryRequest request) {
        if (queryParam == null || queryParam.isBlank()) {
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
