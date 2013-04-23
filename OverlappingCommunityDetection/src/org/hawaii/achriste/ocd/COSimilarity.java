
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

public class COSimilarity {

    private HashMap<Pair, Double> edgeToWeight = new HashMap<>();
    private int optionsMask;
    private int numNodes = -1;
    private String pairsFileLoc;
    private String jaccsFileLoc;
    private ArrayList<SortedSet<Integer>> neighbors;

    public COSimilarity(int optionsMask, String dataDir, String dataName) {
        this.optionsMask    = optionsMask;
        pairsFileLoc        = dataDir + "/" + dataName + ".pairs";
        jaccsFileLoc        = dataDir + "/" + dataName + ".jaccs";
        neighbors           = readPairsFile();

        // Writing similarities to file
        if (!Options.isSet(Options.IN_MEMORY, optionsMask)) {
            try {
                writeSimilarities();
            } catch (IOException e) {
                System.out.println("Error writing to jaccs file");
                e.printStackTrace();
            }
        // Computing similarities in memory
        } else {
            // TODO: Implement me
        }
    }

    /**
     * Reads in the .pairs file
     * @return Sets of all the neighbors for all edge pairs
     */
    private ArrayList<SortedSet<Integer>> readPairsFile() {
        File pairsFile = new File(pairsFileLoc);
        Scanner scan = null;
        ArrayList<SortedSet<Integer>> neighbors = null;
        int i = -1;
        int j = -1;
        int maxNode = -1;
        String pairsLine[];

        System.out.println("Reading pairs file " + pairsFileLoc);

        try {
            scan = new Scanner(pairsFile);
            while (scan.hasNextLine()) {
                pairsLine = scan.nextLine().split(" ");
                i = Integer.parseInt(pairsLine[0]);
                j = Integer.parseInt(pairsLine[1]);
                if (Options.isSet(Options.WEIGHTED, optionsMask)) {
                    // TODO: Order the pairs correctly in weighted sim so we can 
                    // get rid of having send the same edge  in reverse. Though,
                    // this may be faster.
                    //if(i <= j)
                    //	edgeToWeight.put(new Pair(i, j), Double.parseDouble(pairsLine[2]));
                    //else
                    //	edgeToWeight.put(new Pair(j, i), Double.parseDouble(pairsLine[2]));
                    edgeToWeight.put(new Pair(i, j), Double.parseDouble(pairsLine[2]));
                    edgeToWeight.put(new Pair(j, i), Double.parseDouble(pairsLine[2]));
                }
                if (i > maxNode) {
                    maxNode = i;
                }
                if (j > maxNode) {
                    maxNode = j;
                }
            }

            numNodes = maxNode + 1;
            neighbors = new ArrayList<>(numNodes);

            for (int k = 0; k < numNodes; k++) {
                neighbors.add(new TreeSet<Integer>());
            }

            scan.close();

            scan = new Scanner(pairsFile);
            while (scan.hasNextLine()) {
                pairsLine = scan.nextLine().split(" ");
                i = Integer.parseInt(pairsLine[0]);
                j = Integer.parseInt(pairsLine[1]);
                neighbors.get(i).add(j);
                neighbors.get(j).add(i);
            }

            if (!Options.isSet(Options.MULTI_PARTITE, optionsMask)) {
                for (int k = 0; k < numNodes; k++) {
                    neighbors.get(k).add(k);
                }
            }

            scan.close();
            return neighbors;
        } catch (FileNotFoundException e) {
            System.out.println("Could not open pairs file " + pairsFileLoc);
            return null;
        }
    }

    /**
     * Calculates similarities and writes the jaccs file
     * @throws IOException 
     */
    private void writeSimilarities() throws IOException {
        int n_i, n_j, keystone, len;
        double curJacc                      = 0.0;
        double dotProduct                   = 0.0;
        ArrayList<String> similarities      = new ArrayList<>();
        HashMap<Integer, Double> n2a_sqrd   = new HashMap<>();
        SortedSet<Integer> ni_neighbors     = null;
        SortedSet<Integer> neighborsI       = null;
        SortedSet<Integer> neighborsJ       = null;
        BufferedWriter writer               = new BufferedWriter(new FileWriter(jaccsFileLoc));
        int percCnt                         = 1; // Percentage done counter
        double percDone                     = 0; // Percentage done

        System.out.print("Calculating similarities and writing jaccs file... \r");

        // (Weighted only) Builds vector squared values, creates a non-inclusive neighbor set,
        // and stores the edge to weight values.
        if (Options.isSet(Options.WEIGHTED, optionsMask)) {
            for (keystone = 0; keystone < numNodes; keystone++) {
                ni_neighbors = new TreeSet<>(neighbors.get(keystone));
                ni_neighbors.remove(keystone);
                edgeToWeight.put(new Pair(keystone, keystone), sumOfWeights(getWeights(keystone, ni_neighbors)) / ni_neighbors.size());
                //System.out.println("n2a_sqrd: " + keystone + " " + sumOfWeights(getWeightsSqrd(keystone, neighbors.get(keystone))));
                n2a_sqrd.put(keystone, sumOfWeights(getWeightsSqrd(keystone, neighbors.get(keystone))));
            }
        }

        // For each keystone
        for (keystone = 0; keystone < numNodes; keystone++) {
            // Keep track of how much is done
            percDone = (double)((keystone * 100)/numNodes);
            if(percDone > percCnt) {
                percCnt = (int) percDone;
                System.out.print("Calculating similarities and writing jaccs file... " + percCnt++ + "%\r");
            }
            
            // For all combinations of pairs of neighbors to the keystone
            for (Integer node_i : neighbors.get(keystone)) {
                n_i = node_i;
                if (n_i == keystone) {
                    continue;
                }

                for (Integer node_j : neighbors.get(keystone)) {
                    n_j = node_j;
                    if (n_j == keystone || n_i >= n_j) {
                        continue;
                    }

                    if (neighbors.get(keystone).size() > 1) {
                        // Calculate weighted similatiries
                        if (Options.isSet(Options.WEIGHTED, optionsMask)) {
                            neighborsI = new TreeSet<>(neighbors.get(n_i));
                            neighborsJ = new TreeSet<>(neighbors.get(n_j));

                            for (Integer x : intersection(neighborsI, neighborsJ)) {
								System.out.println("mult " + x  + "," + n_i + " * " + x + "," + n_j);
                                dotProduct += edgeToWeight.get(new Pair(x, n_i)) * edgeToWeight.get(new Pair(x, n_j));
                            }
							
							System.out.println("DP:" + dotProduct);
							
                            curJacc = dotProduct / (n2a_sqrd.get(n_i) + n2a_sqrd.get(n_j) - dotProduct);
                            System.out.println("n_i=" + n_i + " n_j=" + n_j + " " + n2a_sqrd.get(n_i) + " " + n2a_sqrd.get(n_j) + " " + ((n2a_sqrd.get(n_i) + n2a_sqrd.get(n_j))));
                            dotProduct = 0;
                        // Calculated non-weighted similarities
                        } else {
                            len = intersectionSize(neighbors.get(n_i), neighbors.get(n_j));
                            curJacc = len / (double) (neighbors.get(n_i).size() + neighbors.get(n_j).size() - len);
                        }

                        if (keystone < n_i && keystone < n_j) {
                            writer.write(keystone + "\t" + n_i + "\t" + keystone + "\t" + n_j + "\t" + curJacc);
                        } else if (keystone < n_i && keystone > n_j) {
                            writer.write(keystone + "\t" + n_i + "\t" + n_j + "\t" + keystone + "\t" + curJacc);
                        } else if (keystone > n_i && keystone < n_j) {
                            writer.write(n_i + "\t" + keystone + "\t" + keystone + "\t" + n_j + "\t" + curJacc);
                        } else {
                            writer.write(n_i + "\t" + keystone + "\t" + n_j + "\t" + keystone + "\t" + curJacc);
                        }

                        writer.newLine();
                    }
                }
            }
        }
        System.out.println(); // New line from perctange counter
        writer.flush();
        writer.close();
    }

    /**
     * Return a vector of weights between a node and each of its neighbors
     * @param node single node that connects to multiple neighbors
     * @param neighbors all neighbors of node
     * @return a vector of weights from node to each of its neighbors
     */
    private ArrayList<Double> getWeights(int node, SortedSet<Integer> neighbors) {
        ArrayList<Double> weights   = new ArrayList<>();
        Double weight               = 0.0;
        for (Integer neighbor : neighbors) {
            weight = edgeToWeight.get(new Pair(node, neighbor));
            if (weight > 0) {
                weights.add(weight);
            }
        }
        return weights;
    }

    /**
     * Squares all weights shared between a node and its neighbors
     * @param node single node that connects to multiple neighbors
     * @param neighbors all neighbors of node
     * @return a vector of weights squared from node to each of its neighbors
     */

    private ArrayList<Double> getWeightsSqrd(int node, SortedSet<Integer> neighbors) {
        ArrayList<Double> weights   = new ArrayList<>();
        Double weight               = 0.0;
        for (Integer neighbor : neighbors) {
            weight = edgeToWeight.get(new Pair(node, neighbor));
            if (weight > 0) {
                weights.add(weight * weight);
            }
            //System.out.println("node:" + node + " neigh:" + neighbor + " w:" + weight + " w^2:" + (weight * weight));
        }
        return weights;
    }

    /**
     * Performs summation over a vector of weights
     * @param weights vector of weights
     * @return summation over weights
     */
    private double sumOfWeights(ArrayList<Double> weights) {
        double sum = 0;
        for (Double weight : weights) {
            sum += weight;
        }
        return sum;
    }

    /**
     * Calculate the size of the intersection of two sets
     * @param a the first set
     * @param b the second set
     * @return the size of the intersection of set a and b
     */
    private int intersectionSize(SortedSet<Integer> a, SortedSet<Integer> b) {
        return intersection(a, b).size();
    }
    
    /**
     * Calculates the intersection of two sets. Should be O(n) or O(n lg n).
     * @param a the first set
     * @param b the second set
     * @return the intersection of set a and b
     */
    private SortedSet<Integer> intersection(SortedSet<Integer> a, SortedSet<Integer> b) {
        SortedSet<Integer> results = new TreeSet<>();
        Object[] aInts = a.toArray();
        Object[] bInts = b.toArray();
        int aIdx = 0;
        int bIdx = 0;
        int aVal, bVal;
        
        while(aIdx < aInts.length && bIdx < bInts.length) {
            aVal = (int) aInts[aIdx];
            bVal = (int) bInts[bIdx];
            if(aVal == bVal) {
                results.add(aVal);
                aIdx++;
                bIdx++;
            } else if(aVal < bVal) {
                aIdx++;
            } else {
                bIdx++;
            }
        }
        
        return results;
    }
}
