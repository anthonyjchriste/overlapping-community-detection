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
 * This class contains methods that convert between GraphML and .pairs files.
 * @author Anthony Christe
 */
public class Converter {
    
    /**
     * Converts GraphML files to .pairs files using a streaming parser.
     * @param graphMlFile The GraphML file being converted.
     * @param pairsFile The resulting pairs file name.
     * @param weighted true if the GraphML file is weighted, false otherwise.
     */
    public static void convertGraphMlToPairs(File graphMlFile, File pairsFile, boolean weighted) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(pairsFile));
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            DefaultHandler handler = new GraphMlHandler(out, weighted);
            saxParser.parse(graphMlFile, handler);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.err.println("Problem occured with conversion");
            System.err.println(ex.getMessage());
        }
    }
}
