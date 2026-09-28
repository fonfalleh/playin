\version "2.22.0"
% automatically converted by musicxml2ly from Till_skogs_en_liten_fogel_floeg.mxl
\pointAndClickOff

\header {
    title =  "Till skogs en liten fogel flög"
    composer =  "Otto Lindblad"
    encodingsoftware =  "MuseScore 3.6.2"
    encodingdate =  "2021-03-23"
    subtitle =  "Efter P.D.A. Atterbom"
    }

#(set-global-staff-size 20.0)
\paper {
    
    paper-width = 21.0\cm
    paper-height = 29.7\cm
    top-margin = 1.5\cm
    bottom-margin = 1.5\cm
    left-margin = 1.5\cm
    right-margin = 1.5\cm
    indent = 1.6153846153846154\cm
    short-indent = 0.6461538461538461\cm
    }
\layout {
    \context { \Score
        autoBeaming = ##f
        }
    }
PartPOneVoiceOne =  \relative d {
    \clef "treble_8" \time 6/8 \key g \major \partial 8 \stemUp d8
    ^\markup{ \bold {Andantino} } | % 1
    \stemDown b'4 \stemDown b8 \stemUp a8. [ \stemUp b16 \stemUp a8 ] | % 2
    \stemDown d4 \stemDown d8 \stemDown d4 \stemDown c8 | % 3
    \stemDown b4 \stemDown d8 \stemDown g4 \stemDown a8 | % 4
    \stemDown a4 \stemDown cis,8 \stemDown d8 r8 \stemDown d8 | % 5
    \stemDown e4 \stemDown e8 \stemDown a,8. [ \stemDown b16 \stemDown c8
    ] \break | % 6
    \stemDown d4 \stemDown d8 \stemDown d4. | % 7
    \stemDown d8 [ \stemDown c8 \stemDown c8 ] \stemDown b4 \stemUp a8 | % 8
    \stemUp g4 \stemUp a8 \stemDown b4 r8 | % 9
    \stemDown g'4. \stemDown e4 r8 | \barNumberCheck #10
    \stemDown e4 ( \stemDown d8 ) \stemDown c4 r8 | % 11
    \stemDown a8 [ \stemDown b8 \stemDown c8 ] \stemDown d4 \stemDown c8
    | % 12
    \stemDown b4 \stemUp a8 \stemDown d4 r8 | % 13
    \stemDown g4. \stemDown e4 r8 \break | % 14
    \stemDown e4 ( \stemDown d8 ) \stemDown c4 r8 | % 15
    \stemDown a8 [ \stemDown b8 \stemDown c8 ] \stemDown e4 \stemDown d8
    | % 16
    \stemDown d4 \stemDown fis8 \stemDown g4 r8 \bar "|."
    }

PartPTwoVoiceOne =  \relative d {
    \clef "treble_8" \time 6/8 \key g \major \partial 8 \stemUp d8 | % 1
    \stemUp g4 \stemUp g8 \stemUp fis4 \stemUp fis8 | % 2
    \stemUp g4 \stemUp g8 \stemUp a4 \stemUp a8 | % 3
    \stemDown b4 \stemDown c8 \stemDown b4 \stemDown cis8 | % 4
    \stemDown d4 \stemUp a8 \stemUp a8 r8 \stemUp a8 | % 5
    \stemUp a4 \stemUp a8 \stemUp fis4 \stemUp fis8 \break | % 6
    \stemUp g4 \stemUp g8 \stemUp gis4. | % 7
    \stemUp a8 [ \stemUp a8 \stemUp fis8 ] \stemUp g4 \stemUp fis8 | % 8
    \stemUp g4 \stemUp fis8 \stemUp g4 r8 | % 9
    \stemDown e'4. \stemDown c4 r8 | \barNumberCheck #10
    \stemDown b4. \stemUp a4 r8 | % 11
    \stemUp fis8 [ \stemUp g8 \stemUp a8 ] \stemDown b4 \stemUp a8 | % 12
    \stemUp g4 \stemUp fis8 \stemUp g4 r8 | % 13
    \stemDown e'4. \stemDown c4 r8 \break | % 14
    \stemDown b4. \stemUp a4 r8 | % 15
    \stemUp fis8 [ \stemUp g8 \stemUp a8 ] \stemUp ais4 \stemDown b8 | % 16
    \stemUp a4 \stemDown c8 \stemDown b4 r8 \bar "|."
    }

PartPThreeVoiceOne =  \relative d {
    \clef "bass" \time 6/8 \key g \major \partial 8 \stemDown d8 | % 1
    \stemDown d4 \stemDown d8 \stemDown d4 \stemDown d8 | % 2
    \stemDown d4 \stemDown d8 \stemDown d4 \stemDown d8 | % 3
    \stemDown d4 \stemDown a'8 \stemDown g4 \stemDown g8 | % 4
    \stemDown fis4 \stemDown g8 \stemDown fis8 r8 \stemDown fis8 | % 5
    \stemDown fis4 \stemDown fis8 \stemDown d4 \stemDown d8 \break | % 6
    \stemDown d4 \stemDown d8 \stemDown e4. | % 7
    \stemDown e8 [ \stemDown e8 \stemDown es8 ] \stemDown d4 \stemDown d8
    | % 8
    \stemDown d4 \stemDown d8 \stemDown d4 r8 | % 9
    \stemDown c'4. \stemDown g4 r8 | \barNumberCheck #10
    \stemDown gis4. \stemDown a4 r8 | % 11
    \stemDown d,8 [ \stemDown d8 \stemDown d8 ] \stemDown d4 \stemDown d8
    | % 12
    \stemDown d4 \stemDown d8 \stemDown d4 r8 | % 13
    \stemDown c'4. \stemDown g4 r8 \break | % 14
    \stemDown gis4. \stemDown a4 r8 | % 15
    \stemDown d,8 [ \stemDown d8 \stemDown d8 ] \stemUp cis4 \stemDown d8
    | % 16
    \stemDown d4 \stemDown d8 \stemDown d4 r8 \bar "|."
    }

PartPFourVoiceOne =  \relative d {
    \clef "bass" \time 6/8 \key g \major \partial 8 \stemDown d8 | % 1
    \stemUp g,4 \stemUp g8 \stemUp c4 \stemUp c8 | % 2
    \stemUp b4 \stemUp b8 \stemUp fis4 \stemUp fis8 | % 3
    \stemUp g8 [ \stemUp g'8 \stemUp fis8 ] \stemDown e4 \stemDown e8 | % 4
    \stemDown d4 \stemDown e8 \stemDown d8 r8 \stemDown d8 | % 5
    \stemUp c4 \stemUp c8 \stemUp c4 \stemUp a8 \break | % 6
    \stemUp b4 \stemUp b8 \stemUp b4. | % 7
    \stemUp a8 [ \stemUp a8 \stemUp a8 ] \stemDown d4 \stemUp c8 | % 8
    \stemUp b4 \stemUp a8 \stemUp g4 r8 | % 9
    \stemUp c4. \stemUp c4 r8 | \barNumberCheck #10
    \stemDown e4. \stemUp a,4 r8 | % 11
    \stemUp c8 [ \stemUp b8 \stemUp a8 ] \stemUp g4 \stemUp fis8 | % 12
    \stemUp g4 \stemUp c8 \stemUp b4 r8 | % 13
    \stemUp c4. \stemUp c4 r8 \break | % 14
    \stemDown e4. \stemUp a,4 r8 | % 15
    \stemUp c8 [ \stemUp b8 \stemUp a8 ] \stemUp g4 \stemUp g8 | % 16
    \stemUp fis4 \stemUp a8 \stemUp g4 r8 \bar "|."
    }


% The score definition
\score {
    <<
        
        \new StaffGroup
        <<
            \new StaffGroup \with { }
            
            <<
                \new Staff
                <<
                    \set Staff.instrumentName = "Tenor"
                    \set Staff.shortInstrumentName = "T."
                    
                    \context Staff << 
                        \mergeDifferentlyDottedOn\mergeDifferentlyHeadedOn
                        \context Voice = "PartPOneVoiceOne" {  \PartPOneVoiceOne }
                        >>
                    >>
                \new Staff
                <<
                    \set Staff.instrumentName = "Tenor"
                    \set Staff.shortInstrumentName = "T."
                    
                    \context Staff << 
                        \mergeDifferentlyDottedOn\mergeDifferentlyHeadedOn
                        \context Voice = "PartPTwoVoiceOne" {  \PartPTwoVoiceOne }
                        >>
                    >>
                
                >>
            \new StaffGroup \with { }
            
            <<
                \new Staff
                <<
                    \set Staff.instrumentName = "Bass"
                    \set Staff.shortInstrumentName = "B."
                    
                    \context Staff << 
                        \mergeDifferentlyDottedOn\mergeDifferentlyHeadedOn
                        \context Voice = "PartPThreeVoiceOne" {  \PartPThreeVoiceOne }
                        >>
                    >>
                \new Staff
                <<
                    \set Staff.instrumentName = "Bass"
                    \set Staff.shortInstrumentName = "B."
                    
                    \context Staff << 
                        \mergeDifferentlyDottedOn\mergeDifferentlyHeadedOn
                        \context Voice = "PartPFourVoiceOne" {  \PartPFourVoiceOne }
                        >>
                    >>
                
                >>
            
            >>
        
        >>
    \layout {}
    % To create MIDI output, uncomment the following line:
    %  \midi {\tempo 4 = 94 }
    }

