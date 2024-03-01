package io.github.fonfalleh.formats.solr;

import io.github.fonfalleh.formats.LilyToMidi;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.Arrays;

public class NaiveLilyToMidiTokenFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    /**
     * Construct a token stream filtering the given input.
     *
     * @param input
     */
    protected NaiveLilyToMidiTokenFilter(TokenStream input) {
        super(input);
    }

    //TODO rework methods to be efficient if needed? "Measure first", don't guess. There are already too many guesses going on :)
    //TODO how to test? See lucene tests for inspiration later?
    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            //termAtt.buffer() might return garbage data after buffer[length]...
            char[] buffer = Arrays.copyOf(termAtt.buffer(), termAtt.length());
            int pitch = LilyToMidi.lilyPitchToMidiPitch(buffer);
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
