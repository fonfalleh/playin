import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
public class TestXml {

    @Test
    public void foo() throws IOException {
        System.out.println("asd");
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode jsonNode = xmlMapper.readTree(
                new File(this.getClass().getResource("/musescore_musicxml/c_scale.musicxml").getFile()));
        JsonNode measures = jsonNode.get("part").get("measure");
        List<Integer> pitches = new ArrayList<>();
        //for ()
//measures.forEach(n -> n.get("note").forEach());
        measures.forEach(m -> m.get("note").forEach(n -> n.get("pitch").get("step").asText()));
        List<String> sts = new ArrayList<>();
        // TODO something more pretty
        measures.forEach(m -> m.get("note").forEach(n -> sts.add(n.get("pitch").get("step").asText())));
    }
}
