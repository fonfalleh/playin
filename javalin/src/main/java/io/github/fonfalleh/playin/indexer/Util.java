package io.github.fonfalleh.playin.indexer;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {
    public static List<String> pitchesToString(List<List<Integer>> filePitches) {
        if (filePitches.isEmpty()) {
            return null;
        }
        return filePitches.stream()
                .filter(Predicate.not(Collection::isEmpty))
                .map(track -> track.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")))
                .toList();
    }
}
