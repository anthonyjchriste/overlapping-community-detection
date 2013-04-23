package edu.hawaii.achriste.ocdutils;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GraphMlHandler extends DefaultHandler {
    private Map<Pair, Double> mapping;
    private BufferedWriter out;
    
    public GraphMlHandler(BufferedWriter out) {
        this.out = out;
    }
    
    @Override
    public void startDocument() throws SAXException {
        mapping = new HashMap<>();
    }
    
    @Override
    public void endDocument() throws SAXException {
        System.out.println("done");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        System.out.format("[S] %s %s %s %s\n", uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        System.out.format("[S] %s %s %s\n", uri, localName, qName);
    }
}
