package bcmain;

import java.util.Locale;

public class Tester {

	public static void main(String[] args) {
		// tests();
		test("7280");
	}

	public static void tests() {
		int resmax = 0;
		int smax = 0;
		for (int s = 1000; s < 10000; s++) {
			int res = test(Integer.toString(s));
			if (res > resmax) {
				resmax = res;
				smax = s;
			}
		}
		System.out.println(
				String.format(Locale.US, "The hardest secret is %d that was solved in %d turns.", smax, resmax));
	}

	public static int test(String secret) {
		boolean output = true;
		Solver solver = new Solver();
		int res = solver.test(secret, output);
		if (res > 0) {
			System.out.println(String.format(Locale.US, "%s: wins in %d turns.", secret, res));
		} else if (res == -1) {
			System.out.println(String.format(Locale.US, "Alarm: %s is not valid.", secret));
		} else {
			System.out.println(String.format(Locale.US, "Unknown alarm at %s.", secret));
		}
		return res;
	}

}
