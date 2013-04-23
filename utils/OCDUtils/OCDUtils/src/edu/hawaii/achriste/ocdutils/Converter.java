package edu.hawaii.achriste.ocdutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author anthony
 */
public class Converter {
    public static void convertGraphMlToPairs(File graphMlFile, File pairsFile, boolean weighted) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(pairsFile));
            Map<Pair, Double> mapping = new HashMap<>();
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            DefaultHandler handler = new GraphMlHandler(out, weighted);
            saxParser.parse(graphMlFile, handler);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.err.println("Problem occured with conversion");
            System.err.println(ex.getMessage());
        }
    }
}
