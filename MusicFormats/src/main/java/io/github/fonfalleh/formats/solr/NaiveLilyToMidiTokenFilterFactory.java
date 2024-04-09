package io.github.fonfalleh.formats.solr;

import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

import java.util.Map;

/**
 * Factory for {@link NaiveLilyToMidiTokenFilter}.
 *
 * //TODO example for using this with query time analysis
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
public class NaiveLilyToMidiTokenFilterFactory extends TokenFilterFactory {

    /** SPI name */ //TODO name as in for creating new types without specifying class?
            //TODO learn about SPI?
    public static final String NAME = "naivelily"; // TODO tmp
    private final boolean allowIntegerTokens;

    public NaiveLilyToMidiTokenFilterFactory(Map<String, String> args) {
        super(args);
        this.allowIntegerTokens = getBoolean(args, "allowIntegerTokens", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    /** Default ctor for compatibility with SPI */ // TODO no idea what this means
    public NaiveLilyToMidiTokenFilterFactory() {
        throw defaultCtorException();
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new NaiveLilyToMidiTokenFilter(input, allowIntegerTokens);
    }

    @Override
    public TokenStream normalize(TokenStream input) {
        return create(input);
    }
}
