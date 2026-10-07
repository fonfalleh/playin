package io.github.fonfalleh.javalin.search;

import gg.jte.Content;
import gg.jte.TemplateOutput;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;

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
