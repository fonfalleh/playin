package io.github.fonfalleh.javalin.search;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.solr.common.util.NamedList;
import org.jetbrains.annotations.NotNull;

public class SolrJSearchHandler implements Handler {

    private final SongSearchHandler songSearchHandler;

    public SolrJSearchHandler(SongSearchHandler songSearchHandler) {
        this.songSearchHandler = songSearchHandler;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
            NamedList<Object> response = songSearchHandler.querySolrForSongs(ctx);
            ctx.json(response.jsonStr());
    }
}
