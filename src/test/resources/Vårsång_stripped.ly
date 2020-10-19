% From http://www1.cpdl.org/wiki/index.php/V%C3%A5rs%C3%A5ng_(Prins_Gustaf)
% Stripped of most layout info for developing the parser
\version "2.18.2"

global = {
    
    \key g \major
    \time 4/4
    \autoBeamOff
}

sopranoNotes = \relative g' {
    \global
% Vårsång t1
\voiceOne
\dynamicUp
 d4 \p ^"Lätt och lifligt"  d8 d d4 d8. c16 |  b4 b b b |
 c4 c8. c16 b4 b8. b16 |  a4 c8. b16 a2 |  b4 b8. b16
 d4\cresc d8. d16 |  g4 g8. g16 g4 fis |  e\f e8. e16 e4 e8. e16 |
 fis4 e8. e16 d2 |  fis4 e8. e16 d2 |  d4\p d8 d d4 d8 d |
 d4 d8 d d2 |  d4 d8 d d2 |  cis4\< cis8. cis16\! d2\laissezVibrer
 ^\markup{ }|
 \pageBreak

 d4\p d8. d16 d4 d8. c16 |  b4 b8 b b4 b |
 c4\cresc c8. c16 b4 b8. b16 |  a4 a8. a16 a4 ais8. ais16 |
 b4 b8 b d4\! d |  g4 g8.\f g16 g2^\markup { } |
 g4. g8 d4 d8 d |  d4 e8 fis g2 |  d4 e8 fis
 g4 r |  r2 r4 d8\ff g |  b2 a4. g8 |  g1\fermata


    \bar "|."
}

altoNotes = \relative c' {
    \global
    \clef "G_8"
    \voiceTwo
% Vårsång t2
 \repeat volta 2 {
   b4 b8 b a4 a8. a16 |  g4 g g g |
 g4 g8. g16 g4 g8. g16 |  fis4 a8. g16 fis2 |  g4 g8. g16
 a4 a8. a16 |  cis4 e8. e16 e4 d |  d d8. d16 d4 d8. d16 }
 \alternative {
   { d4 cis8. cis16 d4(\> c)\! }{  d4 cis8. cis16 d2 }
 } | 
 \repeat volta 2 {
   fis,4 g8 a bes4 a8 bes |
 c4 bes8 bes a2 |  fis4 g8 a bes2 |  bes4 bes8. bes16 a2\laissezVibrer |

 b4 b8. b16 a4 a8. a16 |  g4 g8 g g4 g |
 g4 g8. g16 g4 g8. g16 |  fis4 fis8. fis16 fis4 fis8. fis16 |
 g4 g8 g fis4 a |  d b8. b16 cis2 |
 d4. d8 b4 b8 b | } \alternative { { c4 c8 d d2 | }{  c4 c8 d d4 r |}}
 r2 r4 b8 d |  g2 fis4. d8 |  d1
}

tenorNotes = \relative g {
    \global
    \voiceOne
% Vårsång baritone
 g4 g8 g fis4 fis8. fis16 |  e4 e d d |
 e4 e8. e16 d4 d8. d16 |  d4 d8. d16 d2 |  g4 g8. g16
 fis4 fis8. fis16 |  a4 a8. a16 a4 a |  b4 b8. b16 b4 b8. b16 |
 a4 g8. g16 fis2 |  a4 g8. g16 fis2 |  d4 e8 fis g4 fis8 g |
 a4 g8 g fis2 |  d4 e8 fis g2 |  g4 g8. g16 fis2\laissezVibrer |

 g4 g8. g16 fis4 fis8. fis16 |  e4 e8 e d4 d |
 e4 e8. e16 d4 d8. d16 |  d4 d8. d16 d4 d8. d16 |
 d4 d8 d d4 fis |  g g8. g16 g2 |
 b4. b8 g4 g8 g |  a4 a8 c b2 |  a4 a8 c
 b4 d,8^\ff d |  b4 g8 b d2 |  d'( c4.) b8 |  b1\fermata
}

bassNotes = \relative c' {
    \global
    \clef bass
    \voiceTwo
% Vårsång bass
 g4 \p g8 g fis4 fis8. fis16 |  e4 e d d |
 c c8. c16 g4 b8. b16 |  d4 d8. d16 d2 |  g4 g8. g16
 fis4 fis8. fis16 |  e4 cis8. cis16 d4 d |  g4 g8. g16 gis4 gis8. gis16 |
 a4 a,8. a16 d2 |  a'4 a,8. a16 d2 |  d4\p d8 d d4 d8 d |
 d4 d8 d d2 |  d4 d8 d d2 |  ees4\< ees8. ees16\! d2\laissezVibrer |
 g4\p g8. g16 fis4 fis8. fis16 |  e4 e8 e d4 d |
 c4\cresc c8. c16 g4 b8. b16 |  d4 d8. d16 d4 d8. d16 |
 g,4 g8 g d'4\! c |  b e8.\f e16 ees2_\markup{\italic "ten."} |
 d4. d8 d4 d8 d |  d4 d8 d g,2 |  d'4 d8 d
 g,4  << { d'8 d |  b4 g8 b d2 |  d~ d4. g,8 |  g1 }
              \new NullVoice = "bassSolo" {
                d'8 d |  b4 g8 b d2 |  d~ d4. g,8 |  g1 } >>
}

\score {
    \new ChoirStaff <<
        \new Staff <<
            \new Voice = "soprano" \sopranoNotes
            \new Voice = "alto" \altoNotes
        >>
        \new Staff = "bassStaff" <<
            \new Voice = "tenor" \tenorNotes
            \new Voice = "bass" \bassNotes
        >>
    >>
}
