\version "2.20.0"

oneNotes   = { c\ff   d^\markup{ }  e      f }
twoNotes   = { c^\ff  d ^\markup{ } e^"e"  f }
threeNotes = { c-\ff  d-\markup{ }  e-"e"  f }
fourNotes  = { c _\ff d _\markup{ } e _"e" f }
fiveNotes  = { c_\ff  d_\markup{ }  e_"e"  f }

\score {
  <<
    \new Staff \new Voice = "one"   \oneNotes
    \new Staff \new Voice = "two"   \twoNotes
    \new Staff \new Voice = "three" \threeNotes
    \new Staff \new Voice = "four"  \fourNotes
    \new Staff \new Voice = "five"  \fiveNotes
  >>
}