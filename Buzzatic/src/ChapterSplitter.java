import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;


public class ChapterSplitter {

	/**
	 * @param args first arg is complete file name, second is specified filename w/o .txt
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				throw new Exception("Wrong number of args");
			}
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			//File file = new File(args[1]);
			File file;
			//PrintWriter writer = new PrintWriter(new FileWriter(file+".txt"));
			PrintWriter writer=null;
			String str=new String();
			int firstnum1=0;
			while((str=reader.readLine())!=null){
				if(str.length()!=0){
					//if(!str.matches(".*\\d.*")){
					if(!str.trim().matches("\\d")){
						if (writer==null) continue;
						writer.print(str);
						writer.println();
						writer.flush();
						continue;
					} else {
						if (writer!=null) writer.close();
						firstnum1=((Number)NumberFormat.getInstance().parse(str)).intValue();
						writer = new PrintWriter(new FileWriter(args[1]+(""+firstnum1)+".txt"));
						continue;
					}
				} else {
					if (writer!=null) writer.println();
					//continue;
				}
			}
		} catch (Exception e) {
			printException(e);
		}
	}

	private static void printException(Exception e) {
		System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		e.printStackTrace();
	}

}
