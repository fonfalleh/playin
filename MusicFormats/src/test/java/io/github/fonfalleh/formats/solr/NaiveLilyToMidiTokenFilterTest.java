package io.github.fonfalleh.formats.solr;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.junit.Test; // lucene testsuite doesn't work for jupiter, using junit4

public class NaiveLilyToMidiTokenFilterTest extends BaseTokenStreamTestCase {

    @Test
    public void testSomeNotes() throws Exception {
        String input = "c cis des d dis ees e";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new NaiveLilyToMidiTokenFilter(stream, true);
        assertTokenStreamContents(stream, new String[]{"48", "49", "49", "50", "51", "51", "52"});
    }

    @Test
    public void testVariousInputs() throws Exception {
        String input = "c 1 c foo";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new NaiveLilyToMidiTokenFilter(stream, true);
        assertTokenStreamContents(stream, new String[]{"48", "1", "48"});
    }

    @Test
    public void testVariousInputsRemoved() throws Exception {
        String input = "c 1 c foo";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new NaiveLilyToMidiTokenFilter(stream, false);
        assertTokenStreamContents(stream, new String[]{"48", "48"});
    }

    @Test
    public void testOnlyBadInputsNoOutput() throws Exception {
        String input = "foo bar baz";
        TokenStream stream = whitespaceMockTokenizer(input);
        stream = new NaiveLilyToMidiTokenFilter(stream, false);
        assertTokenStreamContents(stream, new String[]{});
    }
}