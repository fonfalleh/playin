\version "2.20.0"

global = {
  \key c \major
  \time 4/4
}

trumpetC = \relative c'' {
  \global
  % Music follows here.
  c8 c c4 e g 
  c4 \fermata
  
}

\score {
  \new Staff \with {
    instrumentName = "Trumpet in C"
    midiInstrument = "trumpet"
  } \trumpetC
  \layout { }
  \midi {
    \tempo 4=100
  }
}