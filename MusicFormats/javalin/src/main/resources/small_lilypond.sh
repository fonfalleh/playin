#!/usr/bin/env bash

lilypond --pdf -o out - <<<"#(ly:set-option 'crop #t) { $1 }" &> /dev/null
rm out.pdf out.cropped.pdf
mv out.cropped.png out.png


# TODO remove barlines?
# https://lilypond.org/doc/v2.23/Documentation/notation/visibility-of-objects
# https://music.stackexchange.com/questions/124739/removing-entire-barline-in-lilypond

#\key c \major for other keys
#also other params to script/controller
