import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KingLear {
	
	private static HashSet<String> charaNames;

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		charaNames = getCharaNames(lines);
		reader.close();
		PrintWriter writer = new PrintWriter("edited_" + args[0]);
		for (String s: lines) {
			writer.println(process(s));
		}
		writer.close();
	}

	private static Pattern pattern;
	private static Matcher matcher;
	private static String helperString;
	private static String helperArr[];
	private static HashSet<String> getCharaNames(ArrayList<String> lines) {
		HashSet<String> charaNames = new HashSet<String>();
		for (String s: lines) {
			if (s.matches(".*Enter[,]?\\s[^,]+(,\\s[^,]*)*")) {
				helperString = s.substring(s.indexOf("Enter") + 6);
				addCharas(charaNames);
			} else if (s.matches(".*Exit[,]?\\s[^,]+(,\\s[^,]*)*")) {
				helperString = s.substring(s.indexOf("Exit") + 5);
				addCharas(charaNames);
			} else if (s.matches(".*Exeunt[,]?\\s[^,]+(,\\s[^,]*)*")) {
				helperString = s.substring(s.indexOf("Exeunt") + 7);
				addCharas(charaNames);
			} else if (s.matches(".*Re-enter[,]?\\s[^,]+(,\\s[^,]*)*.*")) {
				helperString = s.substring(s.indexOf("Re-enter") + 9);
				addCharas(charaNames);
			}
		}
		System.out.println("recognized characters:");
		for (String cn: charaNames) {
			System.out.println(cn);
		}
		return charaNames;
	}

	private static void addCharas(HashSet<String> charaNames) {
		pattern = Pattern.compile("[^,]+(,\\s[^,]*)*");
		matcher = pattern.matcher(helperString);
		matcher.find();
		helperString = matcher.group();
		helperArr = helperString.split(",");
		for (String s1: helperArr) {
			if (s1.indexOf("and") != -1) {
				charaNames.add(s1.replace("and", "").trim());
			} else {
				charaNames.add(s1.trim());
			}
		}
	}

	private static String process(String line) {
		if (checkDialogue(line)) {
			return processDialogue(line);
		} else if (checkStageDir(line)) {
			return processStageDir(line);
		} else {
			return line;
		}
	}

	private static String processStageDir(String line) {
		return "<i>" + line + "</i>";
	}

	private static boolean checkStageDir(String line) {
		if (line.matches(".*Enter[,]?\\s[^,]+(,\\s[^,]*)*.*") || line.matches(".*Exit[,]?(\\s[^,]+(,\\s[^,]*)*)?.*") || line.matches(".*Exeunt[,]?(\\s[^,]+(,\\s[^,]*)*)?.*") || line.matches(".*Re-enter[,]?\\s[^,]+(,\\s[^,]*)*.*")) {
			return true;
		}
		return false;
	}

	private static String processDialogue(String line) {
		return "<b>" + line + "</b>";
	}

	private static boolean checkDialogue(String line) {
		for (String charaName: charaNames) {
			if (line.equals(charaName)) {
				return true;
			}
		}
		return false;
	}

}
