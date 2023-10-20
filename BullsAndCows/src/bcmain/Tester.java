package bcmain;

import java.util.Locale;

public class Tester {

	public static void main(String[] args) {
		boolean output = true;
		Solver solver = new Solver();
		String secret = "5021";
		int res = solver.test(secret, output);
		if (res > 0) {
			System.out.println(String.format(Locale.US, "%s: wins in %d turns.", secret, res));
		} else if (res == -1) {
			System.out.println(String.format(Locale.US, "Alarm: %s is not valid.", secret));
		} else {
			System.out.println(String.format(Locale.US, "Unknown alarm at %s.", secret));
		}
	}

}
