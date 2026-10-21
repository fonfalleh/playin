package io.github.fonfalleh.javalin.fileupload;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Fields {

    // TODO Have these as source of truth for schema? generate xml? Via rest?

    ID("id"),
    TITLE("title"),
    COMPOSER("composer"),
    EDITOR("editor"),
    SOURCE("source"),
    ;

    Fields(String name) {
        this.name = name;
    }

    private final String name;

    static Set<String> fieldnames() {
        return Arrays.stream(Fields.values())
                .map(Fields::getName)
                .collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

}
