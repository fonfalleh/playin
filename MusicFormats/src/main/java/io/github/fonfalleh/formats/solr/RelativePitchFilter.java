package io.github.fonfalleh.formats.solr;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class RelativePitchFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    protected RelativePitchFilter(TokenStream input, boolean skipRepeats) {
        super(input);
        this.skipRepeats = skipRepeats;
    }

    private int previousPitch = -1; // TODO verify this is unique per TokenStream

    private boolean skipRepeats = false;

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            //TODO graceful error handling
            int pitch = Integer.parseInt(new String(termAtt.buffer(), 0, termAtt.length()));

            if (previousPitch == -1) {
                previousPitch = pitch;
                termAtt.setEmpty(); // TODO verify. We ignore the first token.
                //TODO perhaps check other filters if this is not the way to do it
                return true; // TODO fix
            }

            int pitchDiff = pitch - previousPitch;
            previousPitch = pitch;

            // Possibly skip tokens to ignore repeated notes
            if (skipRepeats && pitchDiff == 0) {
                termAtt.setEmpty(); // TODO verify. We ignore the first token.
                //TODO perhaps check other filters if this is not the way to do it
                return true;
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
