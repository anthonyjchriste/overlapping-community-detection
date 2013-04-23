package edu.hawaii.achriste.ocdutils;

import java.io.BufferedWriter;
import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles how GraphML files are parsed into .pairs files.
 * 
 * @author Anthony Christe
 */
public class GraphMlHandler extends DefaultHandler {
    /**
     * Output stream that points to the .pairs file.
     */
    private BufferedWriter out;
    
    /**
     * Marks whether network is weighted or not
     */
    private boolean weighted;
    
    /**
     * Determines if cursor is currently between an opening and closing edge tag.
     */
    private boolean inEdge = false;
    
    /**
     * Determines if cursor is currently between an opening and closing data tag.
     * This should only happen if inEdge is also true.
     */
    private boolean inData = false;
    
    /**
     * Stores the last edge-pair read after hitting an opening edge tag.
     */
    private Pair<Integer, Integer> edgePair;
    
    /**
     * Creates a new Handler object.
     * @param out Reference to output stream.
     * @param weighted Is the graph weighted or non-weighted
     */
    public GraphMlHandler(BufferedWriter out, boolean weighted) {
        super();
        this.out = out;
        this.weighted = weighted;
    }
    
    /**
     * Currently does nothing.
     * @throws SAXException
     */
    @Override
    public void startDocument() throws SAXException {
        
    }

    /**
     * Scans all tags searching for edges and data within the edges.
     * @throws SAXException 
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        int source;
        int target;
     
        if(qName.equals("edge")) {
            this.inEdge = true;
            source = (int) Double.parseDouble(attributes.getValue("source"));
            target = (int) Double.parseDouble(attributes.getValue("target"));
            this.edgePair = new Pair<>(source, target);
            
            if(!this.weighted) {
                try {
                    out.append(this.edgePair.toString () + "\n");
                }
                catch(IOException e) {
                    System.err.println("Problem writing pairs file");
                    e.printStackTrace();
                }
            }
        }
        
        if(qName.equals("data") && attributes.getValue("key").equals("weight") && this.inEdge) {
            this.inData = true;
        }
    }
    
    @Override
    public void characters(char[] chars, int start, int length) {
        double weight;
        String line;
        if(inEdge && inData && weighted) {
            line = String.format("%s %s\n", edgePair.toString(), new String(chars, start, length));
            
            try {
                out.append(line);
            }
            catch(IOException e) {
                System.err.println("Could not write to .pairs file");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals("data") && this.inData) {
            this.inData = false;
        }
        if(qName.equals("edge") && this.inEdge) {
            this.inEdge = false;
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("Problem closing output stream");
            e.printStackTrace();
        }
    }
}
