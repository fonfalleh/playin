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

# scratchpad

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

Chords and simultaneous sounds like a research project on it's own, but perhaps cool to investigate different ways to accomplish this.
We either have timestamps or beats that can be used to at least figure out which things start at the same time relatively easily.

TODO Can synonyms or graphs be used to handle tricky situations and/or chords? Practically only usable at query time though...

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

----
Started looking at xml. Many options, and many outdated.

Already tried using musicxml xsd schema to generate classes, but it would be nice to not have to do that.
Just do some manual extraction of notes, without bothering with 

First, lazy approach to just use Jackson? Actually a json lib... well...  
Yeah, sure, for initial demoing?

Consider streaming otherwise?:
https://docs.oracle.com/javase/tutorial/jaxp/stax/why.html

Seems like it's more efficient and would perhaps be better for using in a plugin

----

About input formats vs displaying results...

Midi/pitches very easy and straightforward to index, it's just a string of integers (/bytes)  
It is a lossy conversion though, and c# and db are indistinguishable with this approach. Also, displaying what matched is not very pretty.
Idea: Start trivial as POC, do some post-search magic to invoke soure data after match has been found?

----

Keeping it simple in the beginning:

repeats, ties, slurs, etc might become complicated. Don't overcomplicate from the start, but remember that it might get tricky.

repeats:
```
    <measure number="2">
      <barline location="left">
        <bar-style>heavy-light</bar-style>
        <repeat direction="forward"/>
        </barline>
...
      <barline location="right">
        <bar-style>light-heavy</bar-style>
        <repeat direction="backward"/>
        </barline>
      </measure>
```

Seems simple enough? Let's see when codas and segnos are introduced...

On the other hand... what's the use case here? Maybe just unroll midi?

----
# Solr stoff!

Really interesting field format! :D
https://solr.apache.org/guide/solr/9_4/indexing-guide/external-files-processes.html#the-preanalyzedfield-type

Actually really, really cool! Why didn't I know about it earlier?

Idea: With this, is it possible to do highlighting? That would make it possible to map query to actual score. ALso measures etc.

This is actually awesome :D

for higlighting: indexed, stored, termVectors, termPositions

TODO for cool stuff!
- create util for mapping source to `SimplePreAnalyzedParser` syntax along with tokens
- create solr core for demoing
- create demo schema with appropriate fields
 - searchable, both json and simple fields
 - appropriate query analysis
  - multiple formats as well (in one field)? (can they be combined?) (passthrough for midi, translate for abc) 
- create util to manage structure for collection of songs with different formats and metadata, and join for indexing
- something like postman that isn't postman
  - [Bruno](https://www.usebruno.com/)!

More concretely:
- (actually implement stuff and not just testfiles for establishing standards)
- DONE "midi to relative midi"-step as lib for querytime relative searches
 - Rather, int to relative int step... tokenfilter with memory of last token
- create a minimal solr core config for use in docker compose (use )
- see how techproducts load in data in solr examples, create sample dataset
 - also some kind of util for taking files and indexing them?
- ??? simple js-page for actually searching in solr when done?
- very simple lilypond-to-midi-pitch converter (no interpretation, straight transform)
 - start with absolute, then think about relative stoff?
 - for relative, have some interface with different modes?
 - future work: actually interpret lilypond? Research project :P

----
# Testing PreAnalyzedField with simple parser
Creating a field type:
```
{
    "add-field-type": {
        "name": "preanalyzed",
        "class": "solr.PreAnalyzedField",
        "parserImpl": "org.apache.solr.schema.SimplePreAnalyzedParser",
        "queryAnalyzer": {
            "tokenizer": {
                "class": "solr.WhitespaceTokenizerFactory"
            }
        }
    }
}
```

resulting entry in schema:
```
  <fieldType name="preanalyzed" class="solr.PreAnalyzedField" parserImpl="solr.SimplePreAnalyzedParser">
    <analyzer type="query">
      <tokenizer class="solr.WhitespaceTokenizerFactory"/>
    </analyzer>
  </fieldType>
```
create field simplepre in schema in GUI

create testdoc:
```
{
    "id":"test",
    "simplepre": "1 =a b c= 1,s=0,e=1 2,s=2,e=5"
}
```
stored: "a b c", indexed tokens "1" "2". 2 has offset 2 to 5, so match on 2 should highlight "b c".

See syntax in doc. Probably more clean to implement using json, but much easier to write simple syntax by hand for testing.

`q=simplepre:2&hl=true&hl.fl=simplepre`

http://localhost:8983/solr/gettingstarted/select?hl.fl=simplepre&hl=true&indent=true&q=simplepre%3A2

```
[...]
"highlighting":{
    "test":{
        "simplepre":["a <em>b c</em>"]
    }
[...]
```
<3<3<3
Exactly what I wanted!

Also, phrase search seems to work!
`fq=simplepre:"1 2"` matches!

----
# 2024-02-21

This is not intended as a diary, just making notes on progress in order to write something clever later.

Created naive translator that can be used for translating queries in (very restricted subset of) lilypond. 
Added some more examples with tests.  
Still need to actually put some code into a package and not just tests :P 

started looking at maven archetype for building libs for including in solr token filter  
see `mvn archetype:generate`

Though, perhaps just have the code in the solr plugin? But then there will be duplication if that's wanted in more places... decisions, decisions!

Also looked at examples on how to implement token filters - `LowerCaseFilter` looks simple enough!
DONE next: try to actually create a TokenFilter and include the code


----
# 2024-02-22

https://solr.apache.org/guide/8_4/libs.html#lib-directories

https://cwiki.apache.org/confluence/display/solr/SolrPlugins

Really lacking in doc... as is tradition.

Schema api could use a lot more examples ()

https://solr.cool - cool solr stoff?

searched github for solr tokenfilter, found the following project + pom:

https://github.com/edmeister/solr-classical-greek-tokenfilter/blob/master/pom.xml
```

    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.apache.lucene/lucene-test-framework -->
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-test-framework</artifactId>
            <version>4.10.2</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.lucene/lucene-core -->
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-core</artifactId>
            <version>4.10.2</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.lucene/lucene-analyzers-common -->
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-analyzers-common</artifactId>
            <version>4.10.2</version>
        </dependency>

    </dependencies>
```

quite old but should match our expectations.

Time to start locking down versions!
solr 9.5 released recently... let's go!
...which depends on lucene 9.9.2

Note: Got an error, looked at lucene source, and fixed that `lucene-analyzers-common` has changed name to `lucene-analysis-common`.

Last commit on branch 9_5? No, tag `releases/solr/9.5.0` probably better idea.

hmm. many shenanigans!

Created test tokenfilter (which is just LowerCaseFilter with a new name)

Also pasted maven archetype stoff so a jar is produced with mvn install.

Added as field type!
```
{
  "add-field-type": {
    "name": "testTokenFilter",
    "class": "solr.TextField",
    "queryAnalyzer": {
      "filters": [
        {
          "class": "io.github.fonfalleh.formats.midi.SolrTestTokenFilterFactory"
        }
      ],
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    }
  }
}
```

Tested with solr 9.5.0 in docker.
Some annoyances with paths, and SPI (which I don't know anything about, and I didn't get it to work), but it worked with class in the end!

one default lib dir: `<solr_home>/lib/`. `solr.solr.home=/var/solr/data` in some cases, such as the dockerfile

Had to do `docker cp <jar> <container>:/var/solr/data/lib` and then docker restart before it could work? Not sure.
Potentially, mount a volume with `/var/solr/data/lib` or create own docker image which fixes stuff beforehand.
Something nice in a compose-file.

DONE next: actually create token filter which translates lily to midi? Maybe!
DONE Also docker niceties.
TODO also fix maven plugins as wanted, and package in nice fashion.


```
{
  "add-field-type": {
    "name": "naiveMidiPreanalyzed",
    "class": "solr.PreAnalyzedField",
    "parserImpl": "org.apache.solr.schema.SimplePreAnalyzedParser",
    "queryAnalyzer": {
      "filters": [
        {
          "class": "io.github.fonfalleh.formats.solr.NaiveLilyToMidiTokenFilterFactory"
        }
      ],
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    }
  }
}
```

Ok, this calls for ~~celebration~~ debugging!

...

Ok, now time to celebrate!

(query analysis)
```
WT      c   cis d   dis ees
NLTMTF  48  49  50  51  51
```

**This is insanely cool! (if you're like me)**

```
// NOTE: The returned buffer may be larger than the valid length().
public char[] buffer();
```

TODO cleanup, tests :) 

DONE setup maven project alongside compose.yml which makes an easy launch of solr with plugins possible :)

For docker-compose
```
solr-precreate: args
# Create a core on disk
# arguments are: corename configdir
```


`docker cp 92c4b6122321:/var/solr/data/testcore testcore`

----
# 2024-03-21+

More hacking and trying things out in lucene. I must say the semantics of some things are ... not quite obvious, to say the least.
Perhaps there is info in some documentation somewhere, but googling and looking at source code has been my way forward, which is a path filled with trial and error.

Thought about making a filter that makes use of lookahead, but that was... not trivial.
Looked at WordDelimiter, which uses lookahead, but it was way to much overhead and not part of lucene core, so I ignored lookahead for now.

Dropping/removing tokens was not obvious (I produced some empty tokens instead), but looking at StopFilter, it seems like one can just increment input to drop a token.
returning false on `incrementToken()` aborts the filtering, and setting buffer to empty produces empty tokens.


Anyway, a naive implementation of calculating relative pitch is implemented, but also a little broken in this wip commit...

```
{
  "add-field-type": {
    "name": "relativePitch2",
    "class": "solr.TextField",
    "queryAnalyzer": {
      "filters": [
        {
          "class": "io.github.fonfalleh.formats.solr.RelativePitchFilterFactory",
          "skipRepeats": true
        }
      ],
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    }
  }
}
```

It works!

TODO nice package with examples...


Perhaps consider asciidoc for documentation? Can work with both graphviz and lilypond
https://docs.asciidoctor.org/diagram-extension/latest/diagram_types/graphviz/  
https://docs.asciidoctor.org/diagram-extension/latest/diagram_types/lilypond/  


```
{
  "add-field-type": {
    "name": "relativeLilyField",
    "class": "solr.TextField",
    "analyzer": {
      "filters": [
        {
          "class": "io.github.fonfalleh.formats.solr.NaiveLilyToMidiTokenFilterFactory"
        },
        {
          "class": "io.github.fonfalleh.formats.solr.RelativePitchFilterFactory",
          "skipRepeats": false
        }
      ],
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    }
  }
}
```


woo! 
"a b c'" matches "c d ees"!!  
Coolio!
-----

TODO probably change package name  
TODO eventually structure things so everything isn't in one fat jar (it's not that fat yet though), just the analysis stuff with dependencies  
TODO test with elasticsearch? Current plugins are pure lucene, so they should work in elastic as well? But needs packaging.

Cool demo PoC:
- DONE Have demo structure of some song
  - one dir per song with input formats + metadata in plaintext
- Have songs (go with public domain)
  1. Vårsång (glad sås)
- DONE Have indexing thing ready
  - DONE SolrJ
  - DONE Document structure: midi as tracks, other fields from metadata?
- DONE Have appropriate fields in solr (kinda done?)
- Have some way to query things if not comfortable with Solr syntax?
  - bruno template?
  - learn javascript? :'(
    - https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Client-side_web_APIs/Fetching_data Byotiful
  - create a solr GUI?
  - create a search request handler with predefined params for edismax search?
    - Could be nice. 
- DONE Docker compose

----
# 2024-03-29

Moved some things around, removed some things, and started on an Indexer-tool.
Quite WIP, but seems to construct a correct solr document. Need to create appropriate fields in solr demo and save config as deployable.

id
composer : string
editor : string
source : string
pitches : multiValued midi

https://maven.apache.org/shared/maven-filtering/index.html

https://github.com/br4chu/docker-compose-maven-plugin

----

```
{
  "add-field-type": {
    "name": "midiWithLilyQuery",
    "class": "solr.TextField",
    "indexAnalyzer": {
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    },
    "queryAnalyzer": {
      "filters": [
        {
          "class": "io.github.fonfalleh.formats.solr.NaiveLilyToMidiTokenFilterFactory"
        }
      ],
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    }
  }
}
```

pitches_relative:
```
{
  "add-field-type": {
    "name": "relativeMidiWithLilyQuery",
    "class": "solr.TextField",
    "indexAnalyzer": {
      "filters": [
        {
          "class": "io.github.fonfalleh.formats.solr.RelativePitchFilterFactory",
          "skipRepeats": false
        }
      ],
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    },
    "queryAnalyzer": {
      "filters": [
        {
          "class": "io.github.fonfalleh.formats.solr.NaiveLilyToMidiTokenFilterFactory"
        },
        {
          "class": "io.github.fonfalleh.formats.solr.RelativePitchFilterFactory",
          "skipRepeats": false
        }
      ],
      "tokenizer": {
        "class": "solr.WhitespaceTokenizerFactory"
      }
    }
  }
}
```

TODO consider highlighting options, consider not storing relative fields when they are copyfields.


DONE take schema from testcore (fields are added), add to git repo with docker compose and examples  
DONE rename core to ... playin?

Some fighting with the docker setup...

Problem lies in mounting a lib dir on /var/solr/data/lib

/var/solr/data/ is populated on first startup, and mounting /var/solr/data/lib

Some progress with putting docker in resources and use templating and resource moving

TODO don't structure solr config under docker config, combine them using maven resources.

----
# 2024-04-04+

Why does `docker run -dit --name my-apache-app -p 8080:80 -v "$PWD":/usr/local/apache2/htdocs/ httpd:2.4` but compose doesn't work?!?!?

Ok, I have no idea why, but launching the container via intellij's docker compose plugin somehow works.  
Good enough for now.

Something about it needing to be detached as well? But `docker compose run -d apache` didn't work either. Maybe `... up -d`?

So, I've added docker compose config for apache server, and included an example webpage from mdn to serve as playground for GUI https://github.com/mdn/learning-area/tree/main/javascript/apis/fetching-data/can-store  
Also, I've removed the images to avoid bloating the git repo.

# 2024-04-07

TODO have wildcard-field for defining metadata fields without having to update the schema?  
TODO copyfield from composer, author, lyricist, etc. to "creators?"  
TODO have facet fields metadata-fields? (All of them?)  
TODO consider lyrics of midifiles/other source, or separate lyrics file? Probably undecidable, there's just no standard.  
TODO standardize things more. Standard fields, standard source for info, etc.  
TODO how to deal with arrangements, like Pacius/Bellman? arranger-copyfield to author (along with composer)?
TODO multivalue-separator for metadata?

TODO Dreaming big: Have score with lyrics mapped to notes (somehow), searches for lyrics should highlight notes. Probably not that hard if deciding on a standard procedure on how to produce output, but probably impossible to generate from a great majority of sources.  
Unless! write "parser" for lyrics with milisms and other ties of words.

However, from recreating some scores in musescore (mainly Pacius), I've had to do some silly workarounds that would probably render the lyrics very broken if parsed from start to finish.


Anyway, added more songs, example response with json, and modified website files so that "results" are displayed! It looks terrible :D

DONE version control schema changes.  
Well, turns out I had only added parts and never actually copied the whole schema, so there's a lot more diffs than expected.
Wait... the order is deterministic, right? RIGHT? TODO verify or ... abandon sanity

TODO TODO did the wildcard string field break multivalued=false where unspecified? Doesn't that work for managed schema?  
The example response uses multivalued for fields that maybe shouldn't. Oh well, easy to see when/if it breaks.

DONE can/should facets be stored=false and docValues=true? Can't remember.  
yes

TODO website: play around with css so it's not broken :) 

DONE? plugins: passthrough of unexpected tokens/don't crash bad queries  
NaiveLily lets integers through (configurable), removes all other tokens.

WIP tests. Lucene testsuite requires junit4... Eh.

TODO TODO check junit4 vs junit5... ANnoying. mvn works for junit4 tests, 5 are not run. Did they do that earlier? Or is it the naming of the classes that hinders execution?  
Oh, I was just using an old version of surfire plugin. ... but mvn generated it, is everything out ot date?

# 2024-04-11

Testing some solr json queries.

fun with query expansion!
POST http://localhost:8983/solr/playin/select?facetfield=title
```json
{
  "query": "*:*",
  "facet": {
    "composer": {
      "type": "terms",
      "field": "${facetfield:composer_facet}"
    }
  }
}
```

TODO add bruno files

Playing around with "macros", can solr queries can reference querystring from json body :D i.e. perhaps a static json query can be used and just change url params to modify query?
```
{
  "query": {
    "edismax": {
      "query": "${QUERY:*}",
      "qf": [
        "title",
        "composer",
        "pitches",
        "pitches_relative"
      ]
    }
  },
  "facet": {
    "composer": {
      "type": "terms",
      "field": "composer_facet",
      "domain": { "query" : "*:*"}
    }
  },
  "filter": {
    "#tag": "composer:${COMOPSER:*}"
  },
  "params": {
    //"debug": "true",
    "hl": "true"
  }
}
```

Bah, cors...

experimented with apache, but hasn't worked yet...  
Try in solr: https://github.com/docker-solr/docker-solr/issues/182#issuecomment-699559615

Finally made it work! Very unsafe for production, but that's ok!
Followed this one:
http://marianoguerra.org/posts/enable-cors-in-apache-solr.html

solr 9.5.0 has jetty 10.0.19, so:  
https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-servlets/10.0.19  
https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-util/10.0.19  

DONE download those with maven?

Now things sorta works!
Also, somehow I forgot that no web server is needed for this to work. Well then.

TODO have profiles for local, unsafe development?

TODO reorganize files so that everything is not under docker, and so things are more "separation of concerns"-y

DONE Make CORS fixes (web.xml) work in docker. Create own dockerfile? It's getting silly with all volumes...

see `solr/etc/webdefault.xml`? (Before extraction? Find out...) 

----

# 2024-05-03~

Finally solved cors errors and incorporated it in build process

To build and run solr in docker with cors fix from scratch:
(From MusicFormats):  
```
mvn clean package
cd docker
docker compose build solr
docker compose build solr_cors
docker compose up solr_cors
```

(Also, run the main method of MusicFormats/src/main/java/io/github/fonfalleh/playin/indexer/Indexer.java to index the example songs into solr)

New Dockerfiles added to be more in line with how to actually use docker + compose
Cool beans!

# 2024-05-22~

Kinda basic demo, "easily" launchable available!
What to focus on next? ...

TODO:  
- Overview
  - Some documentation about what this is, and quickstart-notes?
  - Some actual definition of what this is. What is this?
  - Some light illustrations to explain concepts and link them to code or such
  - Explain limitations
  - Explain challenges / future work
- Example data
  - More songs
  - Make it easy to have more songs (It sort of is, but it's manual. Perhaps some light crawling of resources?)
  - Have example with musicXML?
  - Index more things than songs? Composers, etc.
- Web 
  - Security + all that jazz. Not have everything fully exposed, etc. Proper webapp? "Middleware"?
    - Don't have query visible in source code?
    - Cors set inside solr for now
  - more metadata visible, searchable, clickable, filterable
  - somehow visualize results. Can be ~~hard~~ impossible without more data + context...
- Project structure and features
  - refactor and publish to maven central? solr.cool?
  - extract metadata from formats
  - derived metadata, highest-lowest note etc.
  - way to "run passes" (such as for deriving metadata or analyzing something) on different parts 

# 2025-02-24

Added lyrics in musescore example to see how they looked in musicxml. 

Thinking of how to search for lyrics and show which measure. Perhaps both keep plaintext lyrics and do some best effort to search in s-plit wo-rds, etc.

# 2025-05-22
A PoC of piecing together lyrics from musicxml is in place! Woop woop!

# 2025-06-19+
Thought: set up suite for testing conversions between formats + regressions? Not sure if it makes sense at this stage, also neither apt, snap or docker has musescore 4.x?

Docker?

https://github.com/musescore/MuseScore/releases Eh.

...

Anyhoo, PoC now also covers extracting midi pitches from musicxml!

What more fun can be had?

- Extract more interesting metadata!
  - Composer
  - Lyricist
  - Key?
  - Instruments / Voices
  - Range of voices
- Treat musicxml as basis for "IR" for analyzing + transforming music as imagined earlier? (See older notes and some comments in this file (Oh hey, some ideas from at least 2020 (actually earlier)))
- More accurate extraction with multiple voices
    - handle repeats
    - Handle change of instruments/voices? (Is it even decidable?)
      - Perhaps through chunking? 
      - Perhaps depends on source format?
- Make it so Indexer takes all metadata from musicxml (or musescore + export to xml) file?
    - This is very nice, because it "simplifies" data entry and management. Single source => less hassle. There are probably articles written about this. If not, perhaps I should write something?
- ... export metadata + notes from musescore? (But they say they give no guarantees on stability of structure)
- Now that we have note value + lyric - cross reference, notes give highlight on lyrics and vice versa
    - Fun and interesting challenge!
- Write more about concepts and as a introduction to what this is in general?
- Make use of existing music... cpdl.org?
  - Write crawler? Not very nice...

In any case, need a more complex file for 

The way of reading or extracting notes and lyrics from sheet music probably needs another approach in order to work for more than the most basic songs.
Perhaps treat it more like a graph, build structure, and extract from there? (i.e. no more simple `measures.stream()`)

Note: Things might not play out in the same way in different notation programmes.
For various reasons, I'm mostly working with musescore as basis for sheet music, and I don't intend to start using other things more in the near future, but still good to think about how output from different programs might look. Can patterns be found when exporting musicxml from other programs?
For example, Sibelius is huge. Finale used to be huge? Dorico? Don't want to overfit this solution, but if anything, musescore seems like the sanest system to relate to.

Some examples are in cpdl, perhaps? Again, perhaps have suite for comparing integrity between conversions... lossy conversions are probably not possible to avoid, but perhaps can be anticipated?

I've hinted at it earlier, but many "challenges" are not decidable with just the source file. It's up for interpretation.
Also, such a simple thing as which voice is singing the melody might mess up the structure of lyrics. Perhaps some annotation or special case handling of things in source file can help.

TODO write about this? Gather examples.


----
# 2025-07-05

Worked on xml indexing, refactored, jotted down lyrics for testing.

TODO :
DONE fix solr schema, add fields
DONE update queryFields in script
tidy
make prettier
make sure lyrics are correct in juljul
DONE make sure hl works in lyrics
make sure changes work in solr
DONE update solr + deps: (from solr/versions.lock)
DONE write tests for relative pitch filter


solr + deps: (from solr/versions.lock)
solr: 9.8.1
lucene.version: 9.11.1
jetty: 10.0.22

----
# 2025-07-08
Did some fixes above...

Think about splitting up concerns in order to get a better overview, make it easier to think about things
- Solr plugin
- Indexer
- Webapp

What are the use cases?
- Find song by melody
- Cool search app with exploratory search
- ... hmm

----
# 2025-07-22

Did some research on the possibility of using something like PreAnalyzedField for Elastic or OpenSearch.
Not really supported natively, but might be possible through plugins. (Creating your own, that is.)

Some discussion on elastic (Shay commenting) https://discuss.elastic.co/t/tokenstream-implementation-in-elasticsearch/7055

Interesting thing that seems supported both in ES and OS : https://www.elastic.co/docs/reference/elasticsearch/plugins/mapper-annotated-text
Not sure how it will look when querying (can't find examples), but might be interesting to look into sometime.
----

Also, did some thinking on how to make it possible to do "highlighting" on sheets, e.g. when mathing the notes of a song, display the measure(s) with matching notes.

Some initial thoughts were to do cool preprocessing and clever stuff with indexing a LOT of metadata (like the whole musicxml with annotations), but a more practical (boring) solution would probably be to just keep the measure count somehow, and do extra lookups after matching, if highlighting is present.
I would have preferred if this was easy to do as part of clever transforms 

One interesting challenge is that sheet music is context dependent - a notable (heh, notable) example, key signature. Even if a "matched" measure were to be displayed, without the context of what key the measure is in, (which might not be present in the specific measure), we're missing important context.

Another interesting feature would be searching for lyrics and getting measures printed as results. But basically, should not be harder than keeping track of which measure to use than regular notes, as they both live inside measure nodes in mxml. 

Anyway, how would one do this?
Need to
- know WHERE the match is - keep offset somehow (discard token?)
- make sure to get interesting offset when querying (highlight? more complicated stuff?)
- display sheet from snippet. (Generate from lilypond?) (I know I have notes or a link for engraving just one measure as png or something like that)

So it is MUCH easier if these things are done outside of the search engine... which is a little boring, but such is life sometimes.
That could kinda sidestep the whole problem of trying to outsmart analysis just to get pretty output and interesting tokens (á la PreAnalyzedField), but then complexity grows... Maybe have some service that queries solr and does pretty display? Sounds very fullstacky (not to mention growing complexity).

// Other idea: use lilypond note names instead of midi pitches for stored content in indexed songs? Easy to convert, and much easier to read, and conversion is not as lossy (except when using midi pitches as source)

----
# 2025-07-29

Looked up how to generate small image of some notes in lilypond.

https://lists.nongnu.org/archive/html/lilypond-user/2010-11/msg00396.html  
https://www.reddit.com/r/lilypond/comments/121w3gz/generate_notwholepage_png/

Perhaps see more on: https://music.stackexchange.com/questions/15544/lilypond-how-to-control-the-paper-size-to-create-images or https://superuser.com/questions/96970/lilypond-is-there-a-way-to-auto-crop-the-paper

Checked possibility of doing this in java (by calling lilypond.) Perhaps silly, but is doable (see TestLily.java) . Might be possible to make small util that takes a small piece of lilypond and produces
https://stackoverflow.com/questions/13991007/execute-external-program  
// TODO revise  

So... now we need to be able to export lilypond snippets from our representation of music... TODO :)

Also, new solr version 9.9.0 released, updated just for funsies and because it's so easy.