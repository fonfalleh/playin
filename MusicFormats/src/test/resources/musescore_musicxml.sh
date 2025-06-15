#!/usr/bin/env bash

find . \( -name "*.mscx" -o -name "*.mscz" \) -execdir musescore -o out.musicxml {} \;

# looping over find considered harmful...
#for FILE in $(find . -name *.mscx -o -name *.mscz); do
#    musescore -o "$FILE".musicxml "$FILE"
#done

# TODO something more cool than out.musicxml, but without too much bash string manipulation