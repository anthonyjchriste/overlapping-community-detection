import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashSet;


class RemoveDups {
	public static void main(String argv[]) {
		File clustersFile			= new File(argv[0]);
		HashSet<String> pairsSet 	= new HashSet<>();
		ArrayList<String> newLines 	= new ArrayList<>();
		StringBuilder sb 			= new StringBuilder();
		HashSet<String> dups	 	= new HashSet<>();
		BufferedWriter writer;
		Scanner scan;
		String[] pairs;
		String[] splitPair;
		
		try {
			// Read in original file
			scan = new Scanner(clustersFile);
			while(scan.hasNextLine()) {
				pairs = scan.nextLine().split(" ");
				for(String pair : pairs) {
					if(!pairsSet.contains(pair)) {
						sb.append(pair + " ");
						pairsSet.add(pair);
						splitPair = pair.split(",");
						pairsSet.add(splitPair[1] + "," + splitPair[0]);
					} else {
						dups.add(pair);
					}
				}
				if(sb.length() > 0) sb.append("\n");
				newLines.add(sb.toString());
				sb = new StringBuilder();
			}
			scan.close();
			
			// Write out new file
			writer = new BufferedWriter(new FileWriter(clustersFile + ".nodups"));
			
			for(String line : newLines) {
				writer.write(line);
			}
			
			writer.flush();
			writer.close();
			writer = new BufferedWriter(new FileWriter(clustersFile + ".dups"));
			for(String dup : dups) {
				writer.write(dup);
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not open clusters file");
		} catch (IOException e) {
			System.out.println("Could not write dup free file");
		}
	}
}
