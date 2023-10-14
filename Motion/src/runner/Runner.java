package runner;

import motion.Motion;
import motion.Motion3;

public class Runner {

	public static void main(String[] args) {
		double vmax = 200; // MM/s
		double amax = 150; // MM/s^2
		double jmax = 5000; // MM/s^3
		Motion motion3 = new Motion3(vmax, amax, jmax);
		double tmax = 300;
		double xend = 5;
		motion3.Run(tmax, xend);
	}

}
