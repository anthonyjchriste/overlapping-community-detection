package edu.hawaii.achriste.ocdutils;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author anthony
 */
public class OCDUtils {

    private static final int DATA_DIR = 0;
    private static final int DATA_SET = 1;
    private static final int UTILITY = 2;
    private static final int FIRST_ARG = 3;
    private static HashMap<String, String> usageMap;
    private static String dataDir;
    private static String dataSet;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        usageMap = getUsageMap();

        if(args.length < 1) {
            System.err.println("Missing arguments");
            usage();
        }
        
        if (args[0].equals("help")) {
            if (args.length == 2) {
                help(args[1]);
            }
            help();
        }

        if (args.length < 3) {
            System.err.println("Missing arguments");
            usage();
        }

        dataDir = args[DATA_DIR];
        dataSet = args[DATA_SET];

        String util = args[UTILITY];

        switch (util) {
            case "convert":
                convert(args, false);
                break;
            case "convertw":
                convert(args, true);
                break;
            default:
                System.err.println("Unknown utility");
                usage();
                break;
        }
    }

    public static String joinURL(String dataDir, String dataSet, String extension) {
        return String.format("%s/%s.%s", dataDir, dataSet, extension);
    }

    private static void convert(String[] args, boolean weighted) {
        if (args.length != 4) {
            System.err.println("Missing argument");
            if (weighted) {
                help("convertw");
            } else {
                help("convert");
            }
        }

        String convertType = args[FIRST_ARG];
        File graphMlFile = new File(joinURL(dataDir, dataSet, "graphml"));
        File pairsFile = new File(joinURL(dataDir, dataSet, "pairs"));

        switch (convertType) {
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

    private static void usage() {
        System.out.println("usage:\n\tOCDUtils [data dir] [data set] [utility]");
        System.out.println("full info:\n\tOCDUtils help");
        System.out.println("utility info:\n\tOCDUtils help [utility]");
        System.out.println("available utilities:");

        for (String utility : usageMap.keySet()) {
            System.out.println("\t" + utility);
        }

        System.exit(0);
    }

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

    private static void help() {
        System.out.println("usage:\n\tOCDUtils [data dir] [data set] [utility]");
        for (String utility : usageMap.keySet()) {
            System.out.println("\nutility: " + utility);
            System.out.println(usageMap.get(utility));
        }
        System.exit(0);
    }

    private static HashMap<String, String> getUsageMap() {
        HashMap<String, String> usageMap = new HashMap<>();
        usageMap.put("convert", "\tConvert between unweighted GraphML and .pairs files\n"
                + "Arguments (1-of) :\n\tg2p (GraphML to .pairs)\n\tp2g (.pairs to GraphML");
        usageMap.put("convertw", "\tConvert between weighted GraphML and .pairs files\n"
                + "Arguments (1-of) :\n\tg2p (GraphML to .pairs)\n\tp2g (.pairs to GraphML)");
        return usageMap;
    }
}
