/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hawaii.achriste.ocdutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author anthony
 */
public class Converter {
    public static void convertGraphMlToPairs(File graphMlFile, File pairsFile) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(pairsFile));
            Map<Pair, Double> mapping = new HashMap<>();
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            DefaultHandler handler = new GraphMlHandler(out);
            saxParser.parse(graphMlFile, handler);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        convertGraphMlToPairs(new File("test.graphml"), new File("test.pairs"));
                
    }
    
}
