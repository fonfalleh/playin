package io.github.fonfalleh.javalin.search;

import io.github.fonfalleh.javalin.Main;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.jetty.HttpJettySolrClient;
import org.apache.solr.client.solrj.request.json.JsonQueryRequest;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SongSearch {

    public static final String NO_FILTERING_FACET_VALUE = "All";

    private static final String QUERY_PARAM = "query";
    private static final String COMPOSER_PARAM = "composer";

    private static final String SOLR_HOST = System.getProperty("solr.host", "localhost");
    private static final String SOLR_BASE_URL = "http://" + SOLR_HOST + ":8983/solr";

    // Note: Uses javalin's jetty version for solrj
    private static final HttpJettySolrClient client = new HttpJettySolrClient.Builder()
            .withBaseSolrUrl(SOLR_BASE_URL)
            .withDefaultCollection("playin")
            .build();

    public static Handler searchHandler = ctx -> {
        {
            NamedList<Object> response;
            try {
                response = querySolrForSongs(ctx);
            } catch (SolrServerException | IOException e) {
                throw new RuntimeException(e);
            }

            List<Map<String, Object>> buckets = getFacetByName(response, COMPOSER_PARAM);

            HashMap<String, Object> model = new HashMap<>();
            FieldFacet composerFacet = FieldFacet.of(ctx.queryParam(COMPOSER_PARAM), buckets);

            Optional.ofNullable(ctx.queryParam(QUERY_PARAM)).ifPresent(q -> model.put(QUERY_PARAM, q));

            model.put("composerFacet", composerFacet);
            model.put("documentList", new Main.SongSearchResult(getSolrDocuments(response)));

            ctx.render("search.jte", model);
        }
    };

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getFacetByName(NamedList<Object> response, String name) {
        Map<String, Object> facets = (Map<String, Object>) response.get("facets");
        Map<String, Object> composer = (Map<String, Object>) facets.get(name);
        return (List<Map<String, Object>>) composer.get("buckets");
    }

    public static SolrDocumentList getSolrDocuments(NamedList<Object> response) {
        return (SolrDocumentList) response.get("response");
    }

    public static NamedList<Object> querySolrForSongs(Context ctx) throws SolrServerException, IOException {
        JsonQueryRequest request = buildJsonQuery(ctx);
        return client.request(request);
    }

    private static JsonQueryRequest buildJsonQuery(Context context) {
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
        if (composer == null || composer.isBlank() || NO_FILTERING_FACET_VALUE.equals(composer)) {
            return;
        }
        request.withFilter(Map.of(
                "#tag", "composer:" + quoteString(composer)
        ));
    }

    private static String quoteString(String string) {
        return "\"" + string + "\"";
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
