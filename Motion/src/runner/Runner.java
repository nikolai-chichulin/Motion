package runner;

import motion.Motion;

public class Runner {

	public static void main(String[] args) {
		double vmax = 200;
		double amax = 1500;
		double jmax = amax;
		Motion motion = new Motion(vmax, amax, jmax);
		motion.Run(10);
	}

}
