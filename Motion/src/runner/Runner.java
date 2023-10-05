package runner;

import motion.Motion;

public class Runner {

	public static void main(String[] args) {
		Motion motion = new Motion(10, 50, 50);
		motion.Run(2);
	}

}
