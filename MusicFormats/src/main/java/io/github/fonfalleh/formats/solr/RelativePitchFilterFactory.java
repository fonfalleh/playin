//TODO verify things, just copypasted this from the example from earlier

package io.github.fonfalleh.formats.solr;

import io.github.fonfalleh.formats.midi.SolrTestTokenFilter;
import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

import java.util.Map;

/**
 * Factory for {@link SolrTestTokenFilter}.
 *
 * //TODO example for using this with analysis
 * <pre class="prettyprint">
 * &lt;fieldType name="text_lwrcase" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;
 *     &lt;filter class="solr.LowerCaseFilterFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 *
 * @since 3.1 //TODO
 * @lucene.spi {@value #NAME} //probably doesn't work as is?
 */
public class RelativePitchFilterFactory extends TokenFilterFactory {

    /** SPI name */ //TODO name as in for creating new types without specifying class?
    //TODO learn about SPI?
    public static final String NAME = "relativepitch"; // TODO tmp

    private boolean skipRepeats = false;

    // Basically just replace everything with my own names for testing
    public RelativePitchFilterFactory(Map<String, String> args) {
        super(args);
        this.skipRepeats = getBoolean(args, "skipRepeats", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    /** Default ctor for compatibility with SPI */ // TODO no idea what this means
    public RelativePitchFilterFactory() {
        throw defaultCtorException();
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new RelativePitchFilter(input, skipRepeats);
    }

    @Override
    public TokenStream normalize(TokenStream input) {
        return create(input);
    }
}
