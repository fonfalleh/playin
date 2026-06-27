package io.github.fonfalleh.javalin.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record FieldFacet(List<FacetOption> options) {
    public static FieldFacet of(String param, List<Map<String, Object>> buckets) {
        ArrayList<FacetOption> list = new ArrayList<>();
        list.add(new FacetOption(SongSearch.NO_FILTERING_FACET_VALUE, 0, SongSearch.NO_FILTERING_FACET_VALUE.equals(param)));
        for (Map<String, Object> bucket : buckets) {
            String value = (String) bucket.get("val");
            long count = (Long) bucket.get("count");
            boolean selected = value.equals(param);
            list.add(new FacetOption(value, count, selected));
        }
        return new FieldFacet(list);
    }
}
