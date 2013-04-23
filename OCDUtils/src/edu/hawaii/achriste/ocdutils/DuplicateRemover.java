package edu.hawaii.achriste.ocdutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Removes duplicates from sorted clusters file.
 * @author Anthony Christe
 */
public class DuplicateRemover {

    /**
     * Removes duplicates from sorted clusters file.
     * @param sortedClustersFile Sorted clusters file for input.
     * @param sortedNoDupsClustersFile Sorted no duplicate clusters file for output.
     */
    public static void removeDuplicates(File sortedClustersFile, File sortedNoDupsClustersFile) {
        HashSet<String> pairsSet = new HashSet<>();
        ArrayList<String> newLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        BufferedWriter writer;
        Scanner in;
        String[] pairs;
        String[] splitPair;

        try {
            // Read in original file
            in = new Scanner(sortedClustersFile);
            System.out.println("Reading file");
            while (in.hasNextLine()) {
                pairs = in.nextLine().split(" ");
                
                // Add each pair to a set if the set doesn't contain it
                // StringBuilder builds new line without duplicates
                for (String pair : pairs) {
                    if (!pairsSet.contains(pair)) {
                        sb.append(pair + " ");
                        pairsSet.add(pair);
                        splitPair = pair.split(",");
                        pairsSet.add(splitPair[1] + "," + splitPair[0]);
                    }
                }
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                newLines.add(sb.toString());
                sb = new StringBuilder();
            }
            in.close();

            // Write out new file
            writer = new BufferedWriter(new FileWriter(sortedNoDupsClustersFile));
            System.out.println("Writing file");
            for (String line : newLines) {
                writer.write(line);
            }

            writer.flush();
            writer.close();
            System.out.println("Done");
        } catch (FileNotFoundException e) {
            System.out.println("Could not open clusters file " + sortedClustersFile);
        } catch (IOException e) {
            System.out.println("Could not write dup free file " + sortedNoDupsClustersFile);
        }
    }
}
