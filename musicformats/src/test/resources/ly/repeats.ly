\version "2.22.1"
\score {
  \unfoldRepeats {
    { c d e f |
      \repeat volta 2 {
        g a b c'} |
      c' b a g
    }
  }
  \layout {}
  \midi {}
}