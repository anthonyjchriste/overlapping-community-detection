
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class COCluster {

    private double threshold;
    private int optionMask;
    private String clustersFileLoc;
    private String clusterStatsFileLoc;
    private String pairsFileLoc;
    private String jaccsFileLoc;

    public COCluster(double threshold, int optionMask, String dataDir, String dataName) {
        this.threshold      = threshold;
        this.optionMask     = optionMask;
        clustersFileLoc     = dataDir + "/" + dataName + ".clusters";
        clusterStatsFileLoc = dataDir + "/" + dataName + ".cluster_stats";
        pairsFileLoc        = dataDir + "/" + dataName + ".pairs";
        jaccsFileLoc        = dataDir + "/" + dataName + ".jaccs";

        // Computing clusters from file
        try {
            // If clustering from file
            if (!Options.isSet(Options.IN_MEMORY, optionMask)) {
                clusterFromFile();
                // Otherwise, clustering from memory
            } else {
                // TODO: Implement me
            }
        } catch (IOException e) {
            System.out.println("Error creating cluster files");
            e.printStackTrace();
        }
    }

    private void clusterFromFile() throws IOException {
        TreeMap<Integer, SortedSet<Pair>> index2cluster = new TreeMap<>();
        TreeMap<Pair, SortedSet<Pair>> edge2iter = new TreeMap<>();

        int ni      = 0;
        int nj      = 0;
        int wij     = 0;
        int index   = 0;
        int tmp     = 0;

        // First, we need to read in the pairs file
        Scanner scan    = null;
        File pairsFile  = new File(pairsFileLoc);
        String pairsLine[];

        SortedSet<Pair> tmpSet;
        
        System.out.println("Reading pairs file...");
        try {
            scan = new Scanner(pairsFile);
            while (scan.hasNextLine()) {
                pairsLine   = scan.nextLine().split(" ");
                ni          = Integer.parseInt(pairsLine[0]);
                nj          = Integer.parseInt(pairsLine[1]);

                if (ni >= nj) {
                    tmp = ni;
                    ni = nj;
                    nj = tmp;
                }

                tmpSet = new TreeSet<>();
                tmpSet.add(new Pair(ni, nj));

                index2cluster.put(index, tmpSet);
                edge2iter.put(new Pair(ni, nj), index2cluster.get(index));

                index++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Problem loading pairs file for clustering");
            e.printStackTrace();
        } finally {
            scan.close();
        }

        // Next, we need to read in the jaccs file
        File jaccsFile = new File(jaccsFileLoc);
        int i0      = 0;
        int i1      = 0;
        int j0      = 0;
        int j1      = 0;
        int idx_i   = 0;
        int idx_j   = 0;
        double jacc = 0.0;
        String jaccsLine[];

        System.out.println("Reading jaccs file and clustering...");
        try {
            scan = new Scanner(jaccsFile);
            while (scan.hasNextLine()) {
				
                jaccsLine   = scan.nextLine().split("\t");
                i0          = Integer.parseInt(jaccsLine[0]);
                i1          = Integer.parseInt(jaccsLine[1]);
                j0          = Integer.parseInt(jaccsLine[2]);
                j1          = Integer.parseInt(jaccsLine[3]);
                jacc        = Double.parseDouble(jaccsLine[4]);

                SortedSet<Pair> setI = edge2iter.get(new Pair(i0, i1));
                SortedSet<Pair> setJ = edge2iter.get(new Pair(j0, j1));

                if (jacc >= threshold) {
                    if (i0 >= i1) {
                        tmp = i0;
                        i0  = i1;
                        i1  = tmp;
                    } // Swap if needed
                    if (j0 >= j1) {
                        tmp = j0;
                        j0  = j1;
                        j1  = tmp;
                    } // Swap if needed

                    // Check to make sure sets aren't already merged
                    if (!(setI.equals(setJ))) {
                        if (setJ.size() > setI.size()) {
                            tmpSet  = setI;
                            setI    = setJ;
                            setJ    = tmpSet;
                        }

                        for (Pair pair : setJ) {
                            setI.add(pair);
                            edge2iter.put(pair, setI);
                        }

                        // Ugly slow hack from cpp port
                        // Needs work around
                        int key = -1;
                        for (Integer i : index2cluster.keySet()) {
                            if (index2cluster.get(i).equals(setJ)) {
                                key = i;
                                break;
                            }
                        }

                        if (key > -1) {
                            index2cluster.remove(key);
                        } else {
                            System.out.println("ERR: Key not found");
                        }
                    }
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("Problem loading jaccs file for clustering");
            e.printStackTrace();
        } finally {
            scan.close();
        }

        System.out.println("There were " + index2cluster.size() + " clusters at threshold " + threshold + ".");

        // Write clusters to file and calculate partition density
        BufferedWriter clustersWriter = new BufferedWriter(new FileWriter(clustersFileLoc));
        BufferedWriter clusterStatsWriter = new BufferedWriter(new FileWriter(clusterStatsFileLoc));
        SortedSet<Integer> clusterNodes = new TreeSet<>();
        int mc, nc;
        int M       = 0;
        int Mns     = 0;
        double wSum = 0.0;

        System.out.println("Writing clusters to file...");
        for (Integer i : index2cluster.keySet()) {
            clusterNodes.clear();
            for (Pair pair : index2cluster.get(i)) {
                clustersWriter.write(pair.getFirst() + "," + pair.getSecond() + " ");
                clusterNodes.add(pair.getFirst());
                clusterNodes.add(pair.getSecond());
            }
            mc = index2cluster.get(i).size();
            nc = clusterNodes.size();
            M += mc;
            if (nc != 2) {
                Mns += mc;
                wSum += mc * (mc - (nc - 1.0)) / ((nc - 2.0) * (nc - 1.0));
            }
            clustersWriter.newLine();
            clusterStatsWriter.write(mc + " " + nc);
            clusterStatsWriter.newLine();    
        }
        clustersWriter.flush();
        clusterStatsWriter.flush();
        clustersWriter.close();
        clusterStatsWriter.close();

        System.out.println("The partition density is:");
        System.out.println("\tD = " + 2.0 * wSum / M);
        System.out.println("not counting one-edge clusters:");
        System.out.println("\tD = " + 2.0 * wSum / Mns);
    }
}
