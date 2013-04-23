package edu.hawaii.achriste.ocdutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sorter {
    
    public static void sortCommunitiesByReverseSize(File clustersFile, File sortedClustersFile) {
        String[] pairs;
        List<Set<String>> list = new LinkedList<>();
        
        try {
            Scanner in = new Scanner(clustersFile);
            while(in.hasNextLine()) {
                pairs = in.nextLine().split(" ");
                list.add(new HashSet(Arrays.asList(pairs)));
            }
            
            Comparator<Set<String>> comparator = new Comparator() {
                @Override
                public int compare(Object setA, Object setB) {
                    return Integer.valueOf(((Set) setB).size()).compareTo(((Set) setA).size());
                }
            };
            
            Collections.sort(list, comparator);
            
            BufferedWriter out = new BufferedWriter(new FileWriter(sortedClustersFile));
            String line;
            for(Set<String> set : list) {
                line = set.toString();
                line = line.substring(1, line.length() - 1);
                out.append(line + "\n");
            }
            out.close();
        }
        catch(FileNotFoundException e) {
            System.err.println("Could not find file " + clustersFile);
        } catch (IOException ex) {
            System.err.println("Could not write file" + sortedClustersFile);
        }
    }
}
