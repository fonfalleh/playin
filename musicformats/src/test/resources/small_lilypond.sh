#!/usr/bin/env bash

lilypond --pdf -o out - <<<"#(ly:set-option 'crop #t) { $1 }"
rm out.pdf out.cropped.pdf
mv out.cropped.png out.png
