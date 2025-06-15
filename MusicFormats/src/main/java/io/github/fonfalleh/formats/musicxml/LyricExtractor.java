package io.github.fonfalleh.formats.musicxml;

import io.github.fonfalleh.formats.musicxml.model.MXML;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;

public class LyricExtractor {
    Map<Integer, StringBuilder> stringBuilders = new TreeMap<>();

    // TODO loop over parts
    public List<String> extract(MXML mxml) {

        List<MXML.Lyric> lyricElements = mxml.parts.get(0).measures.stream()
                .flatMap(MXML.Measure::notes)
                .flatMap(MXML.Note::lyrics)
                .filter(Predicate.not(Objects::isNull))
                .toList();

        StringBuilder sb;
        for (MXML.Lyric l : lyricElements) {
            sb = getStringBuilder(l.number);
            switch (l.syllabic) {
                case begin, middle -> sb.append(l.text);
                case single, end -> {
                    sb.append(l.text);
                    sb.append(' ');
                }
            }
        }
        return stringBuilders.values().stream()
                .map(b -> b.deleteCharAt(b.length() - 1))
                .map(StringBuilder::toString)
                .toList();
    }

    StringBuilder getStringBuilder(int i) {
        if (!stringBuilders.containsKey(i)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilders.put(i, stringBuilder);
            return stringBuilder;
        } else {
            return stringBuilders.get(i);
        }
    }
}
