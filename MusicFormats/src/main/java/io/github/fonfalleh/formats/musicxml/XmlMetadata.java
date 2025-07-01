package io.github.fonfalleh.formats.musicxml;

import io.github.fonfalleh.formats.musicxml.model.MXML;

import java.util.ArrayList;
import java.util.List;

public class XmlMetadata {

    String title;
    List<String> composers = new ArrayList<>();
    List<String> lyricists = new ArrayList<>();

    private XmlMetadata() {
    }

    public static XmlMetadata extract(MXML mxml) {

        XmlMetadata data = new XmlMetadata();
        if (mxml.work != null) {
            data.title = mxml.work.workTitle;
        }

        if (mxml.identification != null && mxml.identification.creators != null) {
            mxml.identification.creators
                    .forEach(data::addCreatorMetadata);
        }
        return data;
    }

    void addCreatorMetadata(MXML.Creator c) {
        switch (c.getType()) {
            case "composer" -> composers.add(c.text);
            case "lyricist" -> lyricists.add(c.text);
            default -> {
            }
        }
        ;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getComposers() {
        return composers;
    }

    public List<String> getLyricists() {
        return lyricists;
    }
}
