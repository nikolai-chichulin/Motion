package runner;

import motion.Motion;
import motion.Motion2;
import motion.Motion3;

public class Runner {

	public static void main(String[] args) {
		double vmax = 20; // MM/s
		double amax = 1400; // MM/s^2
		double jmax = 1400; // MM/s^3 
		Motion motion3 = new Motion3(vmax, amax, jmax);
		Motion motion2 = new Motion2(vmax, amax);
		motion2.Run(1);
		motion3.Run(1);
	}

}
