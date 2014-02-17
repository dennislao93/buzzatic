
public class Test {
	
	public static void main(String[] args) {
		String test = "agaegea geagea";
		String[] test2 = test.split("and");
		for (String s: test2) {
			System.out.println(s + ";");
		}
	}

}
