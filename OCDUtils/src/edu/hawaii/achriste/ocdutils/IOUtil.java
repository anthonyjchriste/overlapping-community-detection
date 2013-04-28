/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hawaii.achriste.ocdutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anthony
 */
public class IOUtil {
    public static List<String> getFileAsLines(File file) {
        List<String> lines = new LinkedList<>();
        
        try {
            Scanner in = new Scanner(file);
            while(in.hasNextLine()) {
                lines.add(in.nextLine());
            }
            in.close();
        } catch (FileNotFoundException ex) {
            System.err.format("Could not read file %s\n", file);
        }
        
        return lines;
    }
}
