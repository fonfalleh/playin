package io.github.fonfalleh.formats.solr;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class RelativePitchFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    //TODO position increment? Do we need to implement better for phrase search?
    //TODO perhaps a naive new incremental (destructive) order?

    protected RelativePitchFilter(TokenStream input, boolean skipRepeats) {
        super(input);
        this.skipRepeats = skipRepeats;
    }

    private int previousPitch = -1; // TODO verify this is unique per TokenStream

    private boolean skipRepeats = false;

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            if (previousPitch == -1) {
                //TODO graceful error handling
                previousPitch = Integer.parseInt(new String(termAtt.buffer(), 0, termAtt.length()));;
                // From my understanding (looking at StopFilter), we can just increment input to not output the first token.
                if (!input.incrementToken()) {
                    // work on next token instead, abort if we reach end of stream (only one token in stream)
                    return false;
                }
            }
            int pitchDiff;
            while (true) {
                //TODO graceful error handling
                int pitch = Integer.parseInt(new String(termAtt.buffer(), 0, termAtt.length()));

                pitchDiff = pitch - previousPitch;
                previousPitch = pitch;

                // Possibly skip tokens by looping increments to ignore repeated notes
                if (skipRepeats && pitchDiff == 0) {
                    if (!input.incrementToken()) {
                        return false;
                    } else {
                        // duplicate found and there is a next token, keep checking diff
                        continue;
                    }
                } else {
                    break;
                }
            }

            String diffToken = Integer.toString(pitchDiff);

            // TODO revise. resizeBuffer "Grows the termBuffer to at least size newSize." No guarantees on exactness?
            // TODO is a buffer _ever_ smaller than 8 chars? (size, not length)
            if (termAtt.length() != diffToken.length()) {
                termAtt.resizeBuffer(diffToken.length());
                termAtt.setLength(diffToken.length());
            }

            termAtt.copyBuffer(diffToken.toCharArray(), 0, diffToken.length());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void reset() throws IOException {
        input.reset();
        previousPitch = -1;
    }

}
