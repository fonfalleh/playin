package io.github.fonfalleh.playin.indexer.enrichers;

import org.apache.solr.common.SolrInputDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class MetadataFileSolrEnricher implements SolrDocEnricher {

    private static final String metadataMultiValueSeparator = ";";

    @Override
    public List<String> supportedExtensions() {
        return List.of("metadata");
    }

    @Override
    public void enrichSolrDoc(File file, SolrInputDocument doc) {
        if (!file.canRead()) {
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            //TODO do cleaning here? Or moderate metadata. Or have util/app/page for creating metadata.
            reader.lines().forEach(s -> {
                String[] split = s.split(":", 2);
                Arrays.stream(split[1].split(metadataMultiValueSeparator)).forEach(
                        m -> doc.addField(split[0], m.trim()));
            });
        } catch (FileNotFoundException e) {
            System.out.println("No metadata file found");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Malformed metadata file: " + file.getPath());
        }
    }

}
