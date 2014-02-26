import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KingLear {

	private static HashSet<String> sceneHeaders;
	private static HashSet<String> charaNames;
	private static HashSet<String> stageDirs;
	private static ArrayList<String> lines;

	public static void main(String[] args) throws IOException {
		// Directory path here
		String path = ".";
		String files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> filelist=new ArrayList<File>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".txt") || files.endsWith(".TXT")) {
					filelist.add(listOfFiles[i]);
				}
			}
		}

		for (File f: filelist) {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			lines = new ArrayList<String>();
			//fill up lines
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			//print out current act/scene
			System.out.println("\n" + lines.get(0));
			//initialize line banks
			sceneHeaders = new HashSet<String>();
			charaNames = new HashSet<String>();
			stageDirs = new HashSet<String>();
			//fill up line banks
			getHeaders();
			getCharaNames();
			getStageDirs();
			PrintWriter writer = new PrintWriter(f);
			for (String s: lines) {
				//process and print
				writer.println(process(s));
			}
			writer.close();
		}
	}

	private static void getHeaders() {
		for (String line: lines) {
			if (line.indexOf("ACT ") != -1 || line.indexOf("SCENE ") != -1) {
				sceneHeaders.add(line);
			}
		}
	}

	private static Pattern pattern;
	private static Matcher matcher;
	private static String helperString;
	private static String helperArr[];
	private static void getCharaNames() {
		for (String s: lines) {
			if (s.matches(".*Enter[,]?\\s[^,]+(,\\s[^,]*)*")) { // Enter
				helperString = s.substring(s.indexOf("Enter") + 6);
				addCharas();
			} else if (s.matches(".*Exit[,]?\\s[^,]+(,\\s[^,]*)*")) { // Exit
				helperString = s.substring(s.indexOf("Exit") + 5);
				addCharas();
			} else if (s.matches(".*Exeunt[,]?\\s[^,]+(,\\s[^,]*)*")) { // Exeunt
				helperString = s.substring(s.indexOf("Exeunt") + 7);
				addCharas();
			} else if (s.matches(".*Re-enter[,]?\\s[^,]+(,\\s[^,]*)*.*")) { // Re-enter
				helperString = s.substring(s.indexOf("Re-enter") + 9);
				addCharas();
			} else if (s.matches("[A-Z]+(\\s[A-Z]+)*")) {
				helperString = s.trim();
				addCharas();
			}
		}
		System.out.println("recognized characters:");
		for (String cn: charaNames) {
			System.out.println(cn);
		}
	}

	/**
	 * basically tries to add to the character bank using whatever's in helperString to the best of its ability
	 */
	private static void addCharas() {
		//find cluster of comma-separated values
		pattern = Pattern.compile("[^,]+(,\\s[^,]*)*");
		matcher = pattern.matcher(helperString);
		matcher.find();
		helperString = matcher.group();
		helperArr = helperString.split(",");
		for (String s1: helperArr) {
			s1 = s1.trim();
			for (String s2: s1.split("and")) {
				s2 = s2.trim();
				if (!s2.equals("")) {
					if (s2.matches("[a-zA-Z][a-z]*(\\s[a-zA-Z][a-z]+)*")) { //anything not all caps, like "Fool" / "a Fool" / "some elephants"
						if (s2.split("\\s")[s2.split("\\s").length - 1].trim().matches("[a-zA-Z]+[s]")) { //plural - get rid of the s
							charaNames.add(s2.split("\\s")[s2.split("\\s").length - 1].trim().substring(0, s2.split("\\s")[s2.split("\\s").length - 1].trim().length() - 1));
						} else { // not plural
							charaNames.add(s2.split("\\s")[s2.split("\\s").length - 1].trim());
						}
					} else { //all caps - meaning significant character
						charaNames.add(s2.trim());
					}
				}
			}
		}
	}

	private static int lineIndex;
	private static boolean nextLineIsBlank;
	private static char testLinePunct;
	private static void getStageDirs() {
		for (String line: lines) {
			//the usual entering / exiting / re-entering stage directions
			if (line.matches(".*Enter[,]?\\s[^,]+(,\\s[^,]*)*.*") || line.matches(".*Exit[,]?(\\s[^,]+(,\\s[^,]*)*)?.*") || line.matches(".*Exeunt[,]?(\\s[^,]+(,\\s[^,]*)*)?.*") || line.matches(".*Re-enter[,]?\\s[^,]+(,\\s[^,]*)*.*")) {
				stageDirs.add(line);
			}
		}
		nextLineIsBlank = false;
		//I realized that the stage directions usually come before a blank line, so I iterate from end to start. Doesn't process the first line because that's the header
		for (lineIndex = lines.size() - 1; lineIndex >= 1; lineIndex--) {
			if (lines.get(lineIndex).length() == 0) {
				nextLineIsBlank = true;
			} else {
				if (nextLineIsBlank) {
					testLinePunct = lines.get(lineIndex).charAt(lines.get(lineIndex).length() - 1);
					//stage directions don't end with a punctuation, so I check if the last character is part of the alphabet
					if (testLinePunct >= 'a' && testLinePunct <= 'z' || testLinePunct >= 'A' && testLinePunct <= 'Z') {
						stageDirs.add(lines.get(lineIndex));
					}
				}
				nextLineIsBlank = false;
			}
		}
		System.out.println("Recognized stage directions:");
		for (String stageDir: stageDirs) {
			System.out.println(stageDir);
		}
	}

	/**
	 * check if the line is a header, character before dialogue, or stage direction and process it
	 * @param line
	 * @return the processed line, if it isn't already processed
	 */
	private static String process(String line) {
		if (checkHeader(line)) {
			return processHeader(line);
		} else if (checkDialogue(line)) {
			return processDialogue(line);
		} else if (checkStageDir(line)) {
			return processStageDir(line);
		} else {
			return line;
		}
	}

	private static boolean checkHeader(String line) {
		for (String header: sceneHeaders) {
			if (line.equals(header)) {
				return true;
			}
		}
		return false;
	}

	private static String processHeader(String line) {
		if (line.indexOf("<b>") == -1) {
			return "<b>" + line + "</b>";
		} else {
			return line;
		}
	}

	private static boolean checkStageDir(String line) {
		for (String stageDir: stageDirs) {
			if (line.equals(stageDir)) {
				return true;
			}
		}
		return false;
	}

	private static String processStageDir(String line) {
		if (line.indexOf("<i>") == -1) {
			return "<i>" + line + "</i>";
		} else {
			return line;
		}
	}

	private static boolean checkDialogue(String line) {
		for (String charaName: charaNames) {
			if (line.equals(charaName)) {
				return true;
			}
		}
		return false;
	}

	private static String processDialogue(String line) {
		if (line.indexOf("<b>") == -1) {
			return "<b>" + line + "</b>";
		} else {
			return line;
		}
	}

}
