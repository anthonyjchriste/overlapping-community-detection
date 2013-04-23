import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Scanner;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

public class clusters2graphml {
	private static String clustersFile;
	private static String tieDataFile;
	private static String nodeDataFile;
	private static String nodePropertiesFile;
	
	private static String graphName;
	private static HashMap<Integer, HashSet<String>> communityToNodes;
	private static HashMap<String, Integer> edgeToCommunity;
	private static HashMap<String, Double> edgeToWeight;
	private static HashMap<String, TreeSet<Integer>> nodeToCommunities;
	private static HashMap<String, String> nodeToEntity;
	private static HashMap<String, String> nodeToActor;
	private static HashMap<String, String> nodeToRoom;
	private static HashMap<String, String> nodeToType;
	private static HashMap<String, String> nodeToTenantOrGroup;
	private static HashMap<String, Double> nodeToX;
	private static HashMap<String, Double> nodeToY;
	private static HashMap<String, Double> nodeToSize;
	private static HashMap<String, Double> nodeToColor;
	private static HashMap<String, String> nodeToShortLabel;
	
	private static final String E_DELIM = ":";
	private static final String V_DELIM = ",";
	
	private static void usage() {
		System.out.println("java clusters2graphml [clusters file] [tieData] [nodeData] [nodeProperties] [graph name]");
	}
	
	public static void main(String[] args) {
		if(args.length < 5) {
			usage();
			System.exit(1);
		}
		
		Document xml		= null;
		clustersFile 		= args[0];
		tieDataFile			= args[1];
		nodeDataFile		= args[2];
		nodePropertiesFile	= args[3];
		graphName			= args[4];
		communityToNodes	= new HashMap<>();
		edgeToCommunity 	= new HashMap<>();
		edgeToWeight		= new HashMap<>();
		nodeToCommunities 	= new HashMap<>();
		nodeToEntity		= new HashMap<>();
		nodeToActor			= new HashMap<>();
		nodeToRoom			= new HashMap<>();
		nodeToType			= new HashMap<>();
		nodeToTenantOrGroup	= new HashMap<>();
		nodeToX				= new HashMap<>();
		nodeToY				= new HashMap<>();
		nodeToSize			= new HashMap<>();
		nodeToColor			= new HashMap<>();
		nodeToShortLabel	= new HashMap<>();
		
		try {
			parseClustersFile();
			parseTieData();
			parseNodeData();
			parseNodeProperties();
			xml = createXML();
		} catch (FileNotFoundException e) {
			System.out.println("Can not open clusters file " + clustersFile);
		} catch (ParserConfigurationException e) {
			System.out.println("Problem with creating XML");
			e.printStackTrace();
		}
		
		writeXML(xml);
	}
	
	private static void writeXML(Document xml) {
		if(xml == null) {
			System.err.println("Error loading document object");
			System.exit(1);
		}
		try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
			
			
            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(new File(clustersFile + ".graphml"));
            DOMSource source = new DOMSource(xml);
            trans.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Document createXML() throws ParserConfigurationException {
		Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element node;
		
		Element graphml = xml.createElement("graphml");
		graphml.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		graphml.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		graphml.setAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		xml.appendChild(graphml);
		
		// Edge to weight
		graphml.appendChild(createKey("eweight", "double", "edge", xml));
		// Edge to community
		graphml.appendChild(createKey("ecomm", "integer", "edge", xml));
		
		// Node data
		graphml.appendChild(createKey("entity", "string", "node", xml));
		graphml.appendChild(createKey("actor", "string", "node", xml));
		graphml.appendChild(createKey("room", "string", "node", xml));
		graphml.appendChild(createKey("type", "string", "node", xml));
		graphml.appendChild(createKey("tenantorgroup", "string", "node", xml));
		graphml.appendChild(createKey("x", "double", "node", xml));
		graphml.appendChild(createKey("y", "double", "node", xml));
		graphml.appendChild(createKey("size", "double", "node", xml));
		graphml.appendChild(createKey("ncomm", "integer", "node", xml));
		graphml.appendChild(createKey("commlist", "listint", "node", xml));
		
		Element graph = xml.createElement("graph");
		graph.setAttribute("id", graphName);
		graph.setAttribute("edgedefault", "directed");
		graphml.appendChild(graph);
		
		// Declare community vertices
		for(Integer i : communityToNodes.keySet()) {
			node = xml.createElement("node");
			node.setAttribute("id", "C_" + i.toString());
			node.appendChild(createData("entity", "COMM", xml));
			graph.appendChild(node);
		}
		
		// Declare verticies
		for(String n : nodeToCommunities.keySet()) {
			node = xml.createElement("node");
			node.setAttribute("id", n);
			node.appendChild(createData("ncomm", Integer.toString(nodeToCommunities.get(n).size()), xml)); 
			node.appendChild(createData("commlist", getCommStr(n), xml));
			node.appendChild(createData("entity", nodeToEntity.get(n), xml));
			node.appendChild(createData("actor", nodeToActor.get(n), xml));
			node.appendChild(createData("room", nodeToRoom.get(n), xml));
			node.appendChild(createData("type", nodeToType.get(n), xml));
			node.appendChild(createData("tenantorgroup", nodeToTenantOrGroup.get(n), xml));
			if(nodeToX.containsKey(n)) node.appendChild(createData("x", nodeToX.get(n).toString(), xml));
			if(nodeToY.containsKey(n)) node.appendChild(createData("y", nodeToY.get(n).toString(), xml));
			if(nodeToSize.containsKey(n)) node.appendChild(createData("size", nodeToSize.get(n).toString(), xml));
			
			graph.appendChild(node);
		}
		
		// We can reduce some memory usage before we do the edges
		nodeToActor.clear();
		nodeToColor.clear();
		nodeToCommunities.clear();
		nodeToEntity.clear();
		nodeToRoom.clear();
		nodeToShortLabel.clear();
		nodeToSize.clear();
		nodeToTenantOrGroup.clear();
		nodeToType.clear();
		nodeToX.clear();
		nodeToY.clear();
		
		// Declare community edges
		for(Integer i : communityToNodes.keySet()) {
			for(String n : communityToNodes.get(i)) {
				node = xml.createElement("edge");
				node.setAttribute("id", "C_" + i.toString() + "," + n);
				node.setAttribute("source", "C_" + i.toString());
				node.setAttribute("target", n);
				graph.appendChild(node);
			}
		}
		
		// Declare edges
		for(String edge : edgeToCommunity.keySet()) {
			node = xml.createElement("edge");
			node.setAttribute("id", edge);
			node.setAttribute("source", edge.split(",")[0]);
			node.setAttribute("target", edge.split(",")[1]);
			node.appendChild(createData("eweight", edgeToWeight.get(edge).toString(), xml));
			node.appendChild(createData("ecomm", edgeToCommunity.get(edge).toString(), xml));
			graph.appendChild(node);
		}
		
		// Finish clearing up the memory
		edgeToCommunity.clear();
		edgeToWeight.clear();
		
		return xml;
	}
	
	private static Element createKey(String name, String type, String nodeOrEdge, Document xml) {
		Element element = xml.createElement("key");
		element.setAttribute("id", name);
		element.setAttribute("for", nodeOrEdge);
		element.setAttribute("attr.name", name);
		element.setAttribute("attr.type", type);
		return element;
	}
	
	private static Element createData(String key, String text, Document xml) {
		Element element = xml.createElement("data");
		Text elementTxt = xml.createTextNode(text);
		element.setAttribute("key", key);
		element.appendChild(elementTxt);
		return element;
	}
	
	private static String getCommStr(String n) {
		String result = "";
		for(Integer i : nodeToCommunities.get(n)) {
			result = result + i.toString() + ",";
		}
		result = result.substring(0, result.length() - 1);
		return result;
	}
	
	private static void parseClustersFile() throws FileNotFoundException {
		Scanner scan = new Scanner(new File(clustersFile));
		String line;
		String[] edges, nodes;
		int comm = 0;
		
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			edges = line.trim().split(E_DELIM);
			communityToNodes.put(comm, new HashSet<String>());
			
			for(String edge : edges) {
				edgeToCommunity.put(edge, comm);
				nodes = edge.trim().split(V_DELIM);
				
				if(!communityToNodes.get(comm).contains(nodes[0]))
					communityToNodes.get(comm).add(nodes[0]);
					
				if(!communityToNodes.get(comm).contains(nodes[1]))
					communityToNodes.get(comm).add(nodes[1]);
				
				if(!nodeToCommunities.containsKey(nodes[0]))
					nodeToCommunities.put(nodes[0], new TreeSet<Integer>());
				
				if(!nodeToCommunities.containsKey(nodes[1]))
					nodeToCommunities.put(nodes[1], new TreeSet<Integer>());
					
				nodeToCommunities.get(nodes[0]).add(comm);
				nodeToCommunities.get(nodes[1]).add(comm);
			}
			comm++;
		}
		scan.close();
	}
	
	public static void parseTieData() throws FileNotFoundException {
		Scanner scan = new Scanner(new File(tieDataFile));
		String line;
		String[] splitLine;
		String edge, reverse;
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(",");
			edge = splitLine[0] + "," + splitLine[1];
			reverse = splitLine[1] + "," + splitLine[0];
			edgeToWeight.put(edge, Double.parseDouble(splitLine[2]));
			edgeToWeight.put(reverse, Double.parseDouble(splitLine[2]));
		}
	}
	
	public static void parseNodeData() throws FileNotFoundException {
		Scanner scan = new Scanner(new File(nodeDataFile));
		String line;
		String[] splitLine;
		String node;
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(",");
			node = splitLine[0];
			nodeToEntity.put(node, splitLine[1]);
			nodeToActor.put(node, splitLine[2]);
			nodeToRoom.put(node, splitLine[3]);
			nodeToType.put(node, splitLine[4]);
			try {
				nodeToTenantOrGroup.put(node, splitLine[5]);
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				nodeToTenantOrGroup.put(node, "");
			}
		}
	}
	
	public static void parseNodeProperties() throws FileNotFoundException {
		Scanner scan = new Scanner(new File(nodePropertiesFile));
		String line;
		String[] splitLine;
		String node;
		int startIdx;
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			splitLine = line.split(",");
			if(splitLine[0].equals("ROOM")) {
				node = splitLine[0] + " " + splitLine[1];
				startIdx = 2;
			} else {
				startIdx = 1;
				node = splitLine[0];
			}
			
			nodeToX.put(node, Double.parseDouble(splitLine[startIdx]));
			nodeToY.put(node, Double.parseDouble(splitLine[startIdx + 1]));
			nodeToSize.put(node, Double.parseDouble(splitLine[startIdx + 2]));
			nodeToColor.put(node, Double.parseDouble(splitLine[startIdx + 3]));
			nodeToShortLabel.put(node, splitLine[startIdx + 4]);
		}
	}
}
