package motion;

public class Motion3 extends Motion {

	enum Phase {
		phase_1, // start of acceleration
		phase_2, // end of acceleration
		phase_3, // start of deceleration
		phase_4; // end of deceleration
	}

	private static final double CORR_J = 0.9;

	private double jerkmax;
	private Phase phase;

	public Motion3(double vmax, double amax, double jmax) {
		this.vmax = vmax;
		this.amax = amax;
		this.jerkmax = jmax;
		header("motion3.out");
	}

	@Override
	public void Run(double tmax) {
		System.out.println("Motion started.");
		phase = Phase.phase_1;
		int step = 0;
		double t = 0;
		double x = 0;
		double v = 0;
		double a = 0;
		double aPrev = 0;
		double aAct = 0;
		double jAct = 0;
		while (true) {
			// next time step
			step++;
			t += dtime;
			if (t > tmax) {
				break;
			}

			aPrev = aAct;
			jAct = -aAct;
			aAct = -v;

			// transition from phase 1 to phase 2?
			if (phase == Phase.phase_1 && !Motion.isAlmostEqual(v, vmax)) {
				double acrit = Math.sqrt(2 * jerkmax * CORR_J * (vmax - v));
				if (a >= acrit) {
					phase = Phase.phase_2;
				}
			}

			// acceleration control
			switch (phase) {
			case phase_1: // acceleration tries to reach maximum, possibly amax
				a += CORR_J * jerkmax * dtime;
				if (a > amax) {
					a = amax;
				}
				break;
			case phase_2: // acceleration decreases, velocity tries to reach vmax
				// regulate the jerk at every time step
				// to avoid mesh error and come exactly to vmax
				// if use jmax, velocity will come to vmax+-delta
				if (!Motion.isAlmostEqual(v, vmax)) {
					double j = 0.5 * aPrev * aPrev / (vmax - v);
					if (j > jerkmax) {
						j = jerkmax;
					}
					a -= j * dtime;
					if (a < 0) {
						a = 0;
					}
				} else {
					phase = Phase.phase_3;
				}
				break;
			case phase_3:
				break;
			case phase_4:
				break;
			default:
				break;
			}

			// motion continues
			v += a * dtime;
			double dx = v * dtime;
			x += dx;

			// Actual derivatives
			aAct += v; // actual a
			aAct /= dtime;
			jAct += aAct; // actual j
			jAct /= dtime;

			// print
			output(t, x, v, aAct, jAct);

			if (phase == Phase.phase_3) {
				break;
			}
		}
		close();
	}
}
