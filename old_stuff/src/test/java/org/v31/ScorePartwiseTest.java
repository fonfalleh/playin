package org.v31;

import org.junit.jupiter.api.Test;
import org.musicxml.v31.ScorePartwise;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScorePartwiseTest {

    @Test
    public void testFile() {

        try {

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("Mer_hahn_en_neue_Oberkeet.musicxml").getFile());
            JAXBContext jaxbContext = JAXBContext.newInstance(ScorePartwise.class);

            System.setProperty("javax.xml.accessExternalDTD", "all");
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ScorePartwise score = (ScorePartwise) jaxbUnmarshaller.unmarshal(file);

            List<ScorePartwise.Part> parts = score.getPart();

            for (ScorePartwise.Part part : parts) {
                System.out.println(part.getId());
            }

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

}