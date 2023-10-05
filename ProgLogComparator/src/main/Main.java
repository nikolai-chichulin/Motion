package main;

public class Main {

	public static void main(String[] args) {
		if (args != null && args.length == 2) {
			ProgLogComparator.run(args[0], args[1]);
		} else if (args != null && args.length == 1) {
			ProgLogComparator.run(args[0]);
		} else {
			ProgLogComparator.run();
		}
		System.out.println("Done.");
	}
}