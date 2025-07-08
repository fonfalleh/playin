package io.github.fonfalleh.formats.solr;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.junit.Test;

public class RelativePitchFilterTest extends BaseTokenStreamTestCase {

    @Test
    public void testSomeNotes() throws Exception {
        String input = "48 49 49 50 51 51 52";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new RelativePitchFilter(stream, false);
        assertTokenStreamContents(stream, new String[]{"1", "0", "1", "1", "0", "1"});
    }

    @Test
    public void testSomeNotesSkippingRepeats() throws Exception {
        String input = "48 49 49 50 51 51 52";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new RelativePitchFilter(stream, true);
        assertTokenStreamContents(stream, new String[]{"1", "1", "1", "1"});
    }

    @Test
    public void upAndDown() throws Exception {
        String input = "1 2 3 4 3 2 1";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new RelativePitchFilter(stream, false);
        assertTokenStreamContents(stream, new String[]{"1", "1", "1", "-1", "-1", "-1"});
    }

    @Test
    public void testSingle() throws Exception {
        String input = "1";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new RelativePitchFilter(stream, false);
        assertTokenStreamContents(stream, new String[]{});
    }

    @Test
    public void testSingleRepeat() throws Exception {
        String input = "1 1";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new RelativePitchFilter(stream, true);
        assertTokenStreamContents(stream, new String[]{});
    }
}