import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;


public class NoLeadingLineBreaks {

	public static void main(String[] args) throws IOException {
		File file[] = new File("./").listFiles();
		String fname;
		ArrayList<String> temp = new ArrayList<String>();
		Iterator<String> it;
		String line;
		boolean noLeadingLB;
		BufferedReader reader;
		PrintWriter writer;
		for (File f: file) {
			fname = f.getName();
			if (fname.length() >= 5 && fname.substring(fname.length() - 4).equals(".txt")) {
				noLeadingLB = false;
				reader = new BufferedReader(new FileReader(fname));
				while ((line = reader.readLine()) != null) {
					if (!noLeadingLB && line.length() == 0) {}
					else if (!noLeadingLB && line.length() > 0) {
						temp.add(line);
						noLeadingLB = true;
					} else if (noLeadingLB) {
						temp.add(line);
					}
				}
				reader.close();
				writer = new PrintWriter(f.getName());
				it = temp.iterator();
				while (it.hasNext()) {
					writer.println(it.next());
				}
				writer.flush();
				writer.close();
				temp.clear();
				System.out.println(fname + " Done.");
			}
		}
	}

}
