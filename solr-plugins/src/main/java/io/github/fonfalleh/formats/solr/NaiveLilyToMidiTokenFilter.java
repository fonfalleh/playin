package io.github.fonfalleh.formats.solr;

import io.github.fonfalleh.formats.LilyToMidi;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.Arrays;

public class NaiveLilyToMidiTokenFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final boolean allowIntegerTokens;

    /**
     * Construct a token stream filtering the given input.
     *
     * @param input
     */
    protected NaiveLilyToMidiTokenFilter(TokenStream input, boolean allowIntegerTokens) {
        super(input);
        this.allowIntegerTokens = allowIntegerTokens;
    }

    //TODO rework methods to be efficient if needed? "Measure first", don't guess. There are already too many guesses going on :)
    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            int pitch;
            //termAtt.buffer() might return garbage data after buffer[length]...
            while (true) {
                char[] buffer = Arrays.copyOf(termAtt.buffer(), termAtt.length());
                pitch = LilyToMidi.lilyPitchToMidiPitch(buffer);
                if (pitch != -1) {
                    break;
                }
                // Not a lilypond token. Keep it as is or remove it, then continue with next token.
                if (allowIntegerTokens) {
                    boolean isInteger = true;
                    for (char c : buffer) {
                        if (!Character.isDigit(c)) {
                            isInteger = false;
                            break;
                        }
                    }
                    if (isInteger)
                    {
                        return true;
                    }
                }
                if (!input.incrementToken()) {
                    return false;
                }
            }

            String pitchToken = Integer.toString(pitch);

            // TODO revise. resizeBuffer "Grows the termBuffer to at least size newSize." No guarantees on exactness?
            if (termAtt.length() != pitchToken.length()) {
                termAtt.resizeBuffer(pitchToken.length());
                termAtt.setLength(pitchToken.length());
            }
            termAtt.copyBuffer(pitchToken.toCharArray(), 0, pitchToken.length());
            return true;
        } else {
            return false;
        }
    }
}
