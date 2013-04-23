package edu.hawaii.achriste.ocdutils;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * This class provides a front end to handle the command line arguments
 * being passed in.
 * 
 * @author Anthony Christe
 */
public class OCDUtils {
    private static final int DATA_DIR = 1;
    private static final int DATA_SET = 0;
    private static final int UTILITY = 2;
    private static final int FIRST_ARG = 3;
    
    /**
     * Stores a key/value combination of each utility and it's description.
     */
    private static TreeMap<String, String> usageMap;
    
    /**
     * Location of data directory.
     */
    private static String dataDir;
    
    /**
     * Name of data set.
     */
    private static String dataSet;

    /**
     * Validates arguments and called appropriate utilities.
     * @param args Arg 0 is the data directory. Arg 1 is the data set name. Arg
     * 2 is the utility method. Args 3 and up are arguments to the utility 
     * methods. It should be noted that Arg 0 can also be "help" and if it is,
     * Arg 1 can be a specific utility to look up help for.
     */
    public static void main(String[] args) {
        usageMap = getUsageMap();

        // Program never runs with < 1 argument
        if(args.length < 1) {
            System.err.println("Missing arguments");
            usage();
        }
        
        // Is user requesting help?
        if (args[0].equals("help")) {
            // Specific help item
            if (args.length == 2) {
                help(args[1]);
            }
            // All help items
            help();
        }

        // If user not asking for help, at least 3 arguments needed
        if (args.length < 3) {
            System.err.println("Missing arguments");
            usage();
        }

        dataDir = args[DATA_DIR];
        dataSet = args[DATA_SET];
        String util = args[UTILITY];
        
        // Call the correct handler
        switch (util) {
            case "convert":
                convert(args[FIRST_ARG], false);
                break;
            case "convertw":
                convert(args[FIRST_ARG], true);
                break;
            case "dups":
                dups();
                break;
            case "sort":
                sort();
                break;
            default:
                System.err.println("Unknown utility " + util);
                usage();
                break;
        }
    }

    /**
     * Joins a data directory, file set name, and extension into a 
     * single String.
     * @param dataDir The data directory.
     * @param dataSet The data set name.
     * @param extension The extention (minus preceding period ".").
     * @return A combined String of the dataDir/dataSet.excention
     */
    public static String joinURL(String dataDir, String dataSet, String extension) {
        return String.format("%s/%s.%s", dataDir, dataSet, extension);
    }

    /**
     * Handles calling the correct conversion utility method.
     * @param args 
     * @param weighted 
     */
    private static void convert(String convertType, boolean weighted) {
        File graphMlFile = new File(joinURL(dataDir, dataSet, "graphml"));
        File pairsFile = new File(joinURL(dataDir, dataSet, "pairs"));

        switch (convertType) {
            // GraphML to .pairs
            case "g2p":
                Converter.convertGraphMlToPairs(graphMlFile, pairsFile, weighted);
                break;
            default:
                System.err.println("Unknown conversion type.");
                if (weighted) {
                    help("convertw");
                } else {
                    help("convert");
                }
                break;
        }
    }
    
    /**
     * Duplicate remover handler.
     */
    private static void dups() {
        File sortedClustersFile = new File(joinURL(dataDir, dataSet, "clusters.sorted"));
        File sortedNoDupsClustersFile = new File(joinURL(dataDir, dataSet, "clusters.sorted.nodups"));
        DuplicateRemover.removeDuplicates(sortedClustersFile, sortedNoDupsClustersFile);
    }

    /**
     * Community sort by size handler.
     */
    private static void sort() {
        File clustersFile = new File(joinURL(dataDir, dataSet, "clusters"));
        File sortedClustersFile = new File(joinURL(dataDir, dataSet, "clusters.sorted"));
        Sorter.sortCommunitiesByReverseSize(clustersFile, sortedClustersFile);
    }
    
    /**
     * Displays the basic usage for this program.
     */
    private static void usage() {
        System.out.println("usage:\n\tjava -jar OCDUtils.jar [data set] [data dir] [utility]");
        System.out.println("full info:\n\tOCDUtils help");
        System.out.println("utility info:\n\tOCDUtils help [utility]");
        System.out.println("available utilities:");

        for (String utility : usageMap.keySet()) {
            System.out.println("\t" + utility);
        }

        System.exit(0);
    }

    /**
     * Displays help for a single utility method.
     * @param utility The utility method to display the help for.
     */
    private static void help(String utility) {
        if (usageMap.containsKey(utility)) {
            System.out.println("utility: " + utility);
            System.out.println(usageMap.get(utility));
            System.exit(0);
        } else {
            System.out.println("Unknown utility");
            usage();
        }
    }

    /**
     * Display help for all utilities.
     */
    private static void help() {
        System.out.println("usage:\n\tjava -jar OCDUtils.jar [data set] [data dir] [utility]");
        for (String utility : usageMap.keySet()) {
            System.out.println("\nutility: " + utility);
            System.out.println(usageMap.get(utility));
        }
        System.exit(0);
    }

    /**
     * Store utility help strings in an organized way.
     * @return A mapping of all utilities to their descriptions.
     */
    private static TreeMap<String, String> getUsageMap() {
        TreeMap<String, String> usageMap = new TreeMap<>();
        
        usageMap.put("convert", "\tConvert between unweighted GraphML and .pairs files\n"
                + "Prerequisits:\n\tvalid GraphML file\n"
                + "Arguments (1-of):\n\tg2p (GraphML to .pairs)\n\tp2g (.pairs to GraphML");
        
        usageMap.put("dups", "\tRemoves duplicate edge-pairs from sorted clusters file\n"
                + "Prerequisits:\n\tsorted clusters file (.clusters.sorted)\n"
                + "Arguments:\n\tnone");
        
        usageMap.put("convertw", "\tConvert between weighted GraphML and .pairs files\n"
                + "Prerequisits:\n\tvalid GraphML file\n"
                + "Arguments (1-of):\n\tg2p (GraphML to .pairs)\n\tp2g (.pairs to GraphML)");
        
        usageMap.put("sort","\tSorts a clusters file by community size, largest to smallest\n"
                + "Prerequisits:\n\tvalid .clusters file\n"
                + "Arguments:\n\tnone");
        
        return usageMap;
    }
}
