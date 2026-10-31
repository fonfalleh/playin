package io.github.fonfalleh.playin.indexer;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Fields {

    // TODO Have these as source of truth for schema? generate xml? Via rest?

    ID("id"),
    COMPOSER("composer"),
    EDITOR("editor"),
    LYRICIST("lyricist"),
    LYRICS("lyrics"),
    PITCHES("pitches"),
    SOURCE("source"),
    TITLE("title"),
    ;

    Fields(String name) {
        this.name = name;
    }

    private final String name;

    public static Set<String> fieldnames() {
        return Arrays.stream(Fields.values())
                .map(Fields::getName)
                .collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

}
