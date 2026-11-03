package io.github.fonfalleh.playin.indexer.enrichers;

import io.github.fonfalleh.javalin.muse.MuseReader;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class MusescoreSolrEnricher implements SolrDocEnricher {

    private final MxmlSolrEnricher mxmlSolrEnricher;

    public MusescoreSolrEnricher(MxmlSolrEnricher mxmlSolrEnricher) {
        this.mxmlSolrEnricher = mxmlSolrEnricher;
    }

    @Override
    public List<String> supportedExtensions() {
        return List.of("mscz", "mscx");
    }

    @Override
    public void enrichSolrDoc(File file, SolrInputDocument doc) {
        Path path = file.toPath();
        MuseReader.runMuse(path);

        File tmpOut = path.resolveSibling("out.xml").toFile();
        mxmlSolrEnricher.enrichSolrDoc(tmpOut, doc);
        tmpOut.delete();
    }
}
