public class CommunityOverlap {

    public static void main(String[] args) {
        COSimilarity cos    = null;
        COCluster coc       = null;
        String dataName     = null;
        String dataDir      = null;
        double threshold    = -1;
        int optionMask      = 0;

        if (args.length < 3) {
            usage();
        }

        // Get and setup arguments 
        dataName    = args[0];
        dataDir     = args[1];

        // Store selected options on bitset
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                switch (args[i]) {
                    case "-mp":
                        optionMask |= (1 << Options.MULTI_PARTITE);
                        break;
                    case "-d":
                        optionMask |= (1 << Options.DIRECTED);
                        break;
                    case "-w":
                        optionMask |= (1 << Options.WEIGHTED);
                        break;
                    case "-s":
                        optionMask |= (1 << Options.COMPUTE_SIMILARITY);
                        break;
                    case "-c":
                        optionMask |= (1 << Options.COMPUTE_CLUSTERS);
                        break;
                    case "-m":
                        optionMask |= (1 << Options.IN_MEMORY);
                        break;
                    case "-t":
                        threshold = Double.parseDouble(args[i + 1]);
                        break;
                    case "-h": halt();
                        break;
                    default:
                        continue;
                }
            }
        }

        // Some last minute sanity checking
        // If clustering, make sure threshold was given
        if (Options.isSet(Options.COMPUTE_CLUSTERS, optionMask) && threshold == -1) {
            System.out.println("\nMust supply thresholds if computing clusters");
            usage();
        }

        // If we're computing results in memory, then similarities and clustering must be turned on
        if (Options.isSet(Options.IN_MEMORY, optionMask)) {
            if (!(Options.isSet(Options.COMPUTE_SIMILARITY, optionMask) && Options.isSet(Options.COMPUTE_CLUSTERS, optionMask))) {
                System.out.println("\nMust compute similarities first if running in memory");
                usage();
            }
        }

        if (Options.isSet(Options.COMPUTE_SIMILARITY, optionMask)) {
            cos = new COSimilarity(optionMask, dataDir, dataName);
        }

        if (Options.isSet(Options.COMPUTE_CLUSTERS, optionMask)) {
            coc = new COCluster(threshold, optionMask, dataDir, dataName);
        }

    }

    public static void usage() {
        System.out.println("\nusage:\tjava CommunityOverlap [data set name] [data dir]");
        System.out.println("options:\n\t-mp\tmultipartite\n\t-d\tdirected\n\t-w\tweighted\n\t-s\tcalculate similarities\n\t-c\tperform clustering\n\t-t [#]\tthreshold\n\t-m\tperform calculations in memoery");
        System.exit(1);
    }
    
    // Allows the program to be halted and continue on keypress.
    // This allows our profiler to see the program and start tracking it
    // before the algorithm takes off. 
    private static void halt() {
        java.util.Scanner scan = new java.util.Scanner(System.in);
        System.out.print("-- Press Enter --");
        scan.nextLine();
    }
}
