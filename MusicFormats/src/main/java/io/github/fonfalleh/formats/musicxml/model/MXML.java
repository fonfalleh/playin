package io.github.fonfalleh.formats.musicxml.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MXML {

    // TODO see other @JacksonXml annotations

    @JacksonXmlProperty(localName = "version")
    String version;

    @JacksonXmlProperty(localName = "part")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Part> parts;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        @JacksonXmlProperty(localName = "id")
        String id;

        @JacksonXmlProperty(localName = "measure")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Measure> measures;

    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Measure {
        @JsonProperty("number")
        int number;

        @JacksonXmlProperty(localName = "note")
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonMerge // This fixed mystery of missing notes, if all listed elements are not together
        public List<Note> notes = new ArrayList<>();

        public Stream<Note> notes() {
            return notes.stream();
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Note {
        @JacksonXmlProperty(localName = "default-x", isAttribute = true)
        String defaultX;

        @JsonProperty("pitch")
        Pitch pitch;

        @JacksonXmlProperty(localName = "duration")
        int duration;

        @JacksonXmlProperty(localName = "voice")
        int voice;

        @JacksonXmlProperty(localName = "type")
        String type; // TODO enum

        @JacksonXmlProperty(localName = "staff")
        int staff;

        @JacksonXmlProperty(localName = "lyric")
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonMerge // Perhaps not needed here?
        List<Lyric> lyric;

        public Stream<Lyric> lyrics() {
            return lyric == null ? null :lyric.stream();
        }
    }

    public static class Pitch {
        // Both these work somehow
        @JacksonXmlProperty(localName = "step")
        String step;
        @JsonProperty("octave")
        String octave;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lyric {
        @JacksonXmlProperty(localName = "syllabic")
        public Syllabic syllabic;
        @JacksonXmlProperty(localName = "number", isAttribute = true)
        public int number;
        @JsonProperty("text")
        public String text;
    }
    // https://w3c.github.io/musicxml/musicxml-reference/data-types/syllabic/
    public enum Syllabic {
        begin,
        end,
        middle,
        single
    }
}
