package io.github.fonfalleh.formats.midi;

import org.apache.lucene.analysis.CharacterUtils;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class SolrTestTokenFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    protected SolrTestTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            CharacterUtils.toLowerCase(termAtt.buffer(), 0, termAtt.length());
            return true;
        } else {
            return false;
        }
    }
}
