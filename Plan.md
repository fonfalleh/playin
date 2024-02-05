Make system/language for importing, exporting, manipulating and analyzing "sheet music"

Idea: Compare to LLVM IR, and other sheet music software like lilypond.

Some goals:
Read music from different formats, mainly lilypond and midi files?

Export to various formats? Low priority.

Playback? Could be nice, but sounds like a lot of work.

Have own format for keeping info? Better to stick with some variant of lilypond...

A song consists of different voices, which

Use cases:
from a specific measure beat, give info on chord / notes
 From beat, get measure. From measure, get voices / notes at that point.
 Format? M3B3? getStuff(base.Measure m, Beat b)
 How to deal with offbeat?

from part of song, get details on note progressions, chord progressions, more info?
 highest / lowest notes (range)
 metadata ?
 other fun vectors

from collection of



From midi file, get help turning it to a nice formatted sheet? (This is probably already part of midi2ly? http://lilypond.org/doc/v2.19/Documentation/usage/invoking-midi2ly)
This might be a better case for musesecore ...

Progression:
Split up import into separate interface
Same with import

Lily:
 Model (import) one monophonic song - a single voice, simple.
 polyphonic song with all voices all the time
 polyphonic song with sporadic voices that come and go. (look at voice name? Have a new voice each time? )


Idea:
Analysis pass like lucene or LLVM

----
# Indexing in Solr

- Java 11 required for solr 9 (not necessary for plugins, but assume jdk 11 ok)

- Have base format as input as reference (abc? lily?)
 - good for both indexing and searching
- Have different kinds of analysis WITH example configs
  - strip octave?
  - key-agnostic - relative pitches
  - with or without rests and repeats
- Indexing vs Querying
 - index many formats (midi, xml, lily), query by abc/lily

- Indexing midi
 - Channel by channel? No "standard form"... 

Must know
- how to turn notation into indexed tokens ("light lilypond parsing")
 - first step is just to take a list (whitespace separated string) of notes rather than implementing a complete parser? 
- c in lilypond represents what pitch?
- how to write a nice analysis step for solr. See solr source code for examples.

parsing lilypond/other notation requires state: key signature, relative pitch, etc.

Probably much easier to import MIDI rather than parse other formats.
 - Have some testsuite for guaranteed compatibility from/to MIDI from musescore and lilypond
 - have simple suite for generating from/to different formats
# Format cross-compatibility

Good to have some basis for how to convert from/to equivalent parts in formats.

Structure and such will probably be lost, but part/voice-data and metadata can probably be kept from/to formats.

## Musescore
```
Usage: musescore [options] [scorefile]
...
  -o, --export-to <file>                  Export to 'file'. Format depends on
                                          file's extension
...
  --score-meta                            Export score metadata to JSON
                                          document and print it to stdout
  --score-parts                           Generate parts data for the given
                                          score and save them to separate mscz
                                          files
  --score-parts-pdf                       Generate parts data for the given
                                          score and export the data to a single
                                          JSON file, print it to stdout
  --score-transpose <options>             Transpose the given score and export
                                          the data to a single JSON file, print
                                          it to stdout

```

Interesting!

## Lilypond

see `midi2ly` and `musicxml2ly`.

Not great as I remember, but always something.

Good as query language but perhaps not as indexing language?

!!! Can only import xml, not export! (official)

## Musicxml
Can be exported to from most formats, probably makes most sense to do "partwise".
(Lilypont can _not_ export musicxml)

Can be parsed in Java. (See schema in some tests)

## MIDI

Has official java package. See documentation...

Also some snippets https://stackoverflow.com/questions/3850688/reading-midi-files-in-java

----

### scratchpad

midi debugging c_scale.midi:

first impressions:
.ly has generated 2 tracks.
Should contain 8 notes.

track[0] seems like metadata, ignore for now?
track[1] has 18 events - seems like metadata, 8 start + 8 stop events + end of track

tick size... sequence has "resolution" = 384, seems to be the lenght of a quarter note.

Almost everything here is lilypond defaults, possibly version specific (although I've omitted version string, so that might change?)

// TODO Idea: Have another test for checking resolution and the like?

Hmm. A sequence has tracks, tracks has list of messages. Messages can be notes on/off.

((FastShortMessage) sequence.tracks.get(1).eventsList.get(1).message).getData1() -> 48
pitch of first NOTE_ON event 48 = c in lilypond. Yay!

Also, ((FastShortMessage) sequence.getTracks()[1].get(1).getMessage()).getData1()

The subsequent events seem to increase in data1(pitch) as expected accoring to the c scale.
Diatonic major: HHhHHHh  
[0,2,4,5,7,9,11,12]

----

Musescore sheets and exports done for c_scale and repeats.
Musescore has lots of settings for export, took some defaults.

TODO do tests, and do something for musicxml.

Musicxml nice because pitch names are present

TODO check enharmonic equivalences, such as cis/des to see if that info is stored.

mxl unusable, it seems, as it's compressed and can't be read

TODO later: multiple parts/voices, and chords

----

Seems like lilypond with repeats don't work well for midi output? Strange.
TODO investigate later? Seems easier with musescore for now...  
Oh, because of https://lilypond.org/doc/v2.22/Documentation/notation/using-repeats-with-midi

musescore midi does not have the same metadata track on track[0] as lilypond... So song info is all in track[0] (at least for one voice)

seems like there is 25 ticks between note_off and note_on, unlike on in lilypond
...
Something is weird with notes, both in lilypond and in musescore.
```
Index: 16, Command: 144, Data1: 48
Index: 17, Command: 144, Data1: 48
Index: 18, Command: 144, Data1: 50
Index: 19, Command: 144, Data1: 50
```
Command 144 = NOTE_ON, i.e start of note. Two ONs but no OFFs?  
Ah, because Data2 (velocity) is 0 on the second of these. Added condition for Data2 > 0.