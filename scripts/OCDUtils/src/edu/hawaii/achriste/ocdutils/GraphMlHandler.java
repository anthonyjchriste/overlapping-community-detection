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
        System.out.println("Starting");
    }

    /**
     * Scans all tags specifically searching for edges and data within the edges.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        int source;
        int target;
     
        // Entering an edge-tag
        if(qName.equals("edge")) {
            this.inEdge = true;
            source = (int) Double.parseDouble(attributes.getValue("source"));
            target = (int) Double.parseDouble(attributes.getValue("target"));
            this.edgePair = new Pair<>(source, target);
            
            // If this isn't weighted, write out the results not instead of waiting for the weight
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
        
        // Entering a data tag which should be inside of the edge tag
        if(qName.equals("data") && attributes.getValue("key").equals("weight") && this.inEdge) {
            this.inData = true;
        }
    }
    
    /**
     * Reads the characters between the tags.
     * @param chars Characters between tags.
     * @param start Starting location of the characters.
     * @param length Length of the characters.
     */
    @Override
    public void characters(char[] chars, int start, int length) {
        String line;
        
        // Make sure we're only recording characters between <edge><data>...</data></edge>
        if(inEdge && inData && weighted) {
            // Creates a line with the format vertex vertex weight.
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

    /**
     * This method fires at every end tag.
     * 
     * This is used to determine when we've reached the end of a data or edge tag.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals("data") && this.inData) {
            this.inData = false;
        }
        if(qName.equals("edge") && this.inEdge) {
            this.inEdge = false;
        }
    }
    
    /**
     * Reached the end of the XML document, flush and close streams.
     */
    @Override
    public void endDocument() throws SAXException {
        try {
            out.flush();
            out.close();
            System.out.println("Done");
        }
        catch (IOException e) {
            System.err.println("Problem closing output stream");
            e.printStackTrace();
        }
    }
}
