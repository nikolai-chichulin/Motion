package runner;

import motion.Motion;

public class Runner {

	public static void main(String[] args) {
		double vmax = 100;
		double amax = 50;
		double jmax = 50;
		Motion motion = new Motion(vmax, amax, jmax);
		motion.Run(3);
	}

}
