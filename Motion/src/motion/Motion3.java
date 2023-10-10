package motion;

/**
 * A class containing the classical third order model of 1D motion.
 * 
 * @author Nikolai Chichulin
 */
public class Motion3 extends Motion {

	enum Phase {
		phase_1, // acceleration ramp up to the desired maximum with a constant positive jerk
		phase_2, // constant acceleration, zero jerk
		phase_3, // acceleration ramp down to zero, approaching the desired velocity
		phase_4, // constant velocity
		phase_5, // deceleration ramp up to the desired maximum with a constant negative jerk
		phase_6, // constant deceleration, zero jerk
		phase_7, // deceleration ramp down, approaching the desired end point
		phase_8; // inertia, constant velocity
	}

	private static final double CORR_J = 0.9; // a jerk reduction factor

	private Phase phase;
	private final double jerkmax; // upper limitation of the jerk

	private int duration_8 = 0;

	public Motion3(double vmax, double amax, double jmax) {
		this.phase = Phase.phase_1;
		this.vmax = vmax;
		this.amax = amax;
		this.jerkmax = jmax;
		header("motion3.out");
		System.out.println("Motion 3 created.");
	}

	private double getAcceleration(double t, double v, double a) {
		double aNext = 0;
		switch (phase) {
		case phase_1: // the first phase of acceleration, acceleration tends to maximum
			aNext = a + CORR_J * jerkmax * dtime; // a > 0
			if (aNext >= amax) {
				aNext = amax;
				phase = Phase.phase_2;
			}
		case phase_2: // acceleration remains constant and positive
			// check for the transition to phase 3
			if (!Motion.isAlmostEqual(v, vmax)) {
				if (a * a >= (2 * jerkmax * CORR_J * (vmax - v))) {
					phase = Phase.phase_3;
				}
			} else {
				phase = Phase.phase_3;
			}
			break;
		case phase_3: // acceleration tends to zero, velocity tends to Vmax
			// we need to regulate the jerk at every time step
			// to avoid mesh error and come exactly to Vmax
			// otherwise, (if use j = Jmax), velocity will come to Vmax +- delta
			if (!Motion.isAlmostEqual(v, vmax)) {
				double j = 0.5 * a * a / (vmax - v);
				if (j > jerkmax) {
					j = jerkmax;
				}
				aNext = a - j * dtime;
				if (aNext < 0) {
					aNext = 0;
				}
			} else {
				phase = Phase.phase_4;
			}
			break;
		case phase_4: // constant velocity, zero acceleration
			aNext = 0;
			if (t > 1) {
				phase = Phase.phase_5;
			}
			break;
		case phase_5: // the first deceleration phase, acceleration tends to -amax
			aNext = a - CORR_J * jerkmax * dtime; // a < 0
			if (aNext <= -amax) {
				aNext = amax;
				phase = Phase.phase_6;
			}
		case phase_6: // acceleration remains constant and negative
			// check for the transition to phase 7
			if (!Motion.isAlmostZero(v)) {
				if (a * a >= (2 * CORR_J * jerkmax * v)) {
					phase = Phase.phase_7;
				}
			} else {
				phase = Phase.phase_7;
			}
			break;
		case phase_7: // acceleration tends to zero
			// aNext = a + CORR_J * jerkmax * dtime; // a < 0
			double j = 0.5 * a * a / v;
			if (j > jerkmax) {
				j = jerkmax;
			}
			aNext = a + j * dtime;
			if (Motion.isAlmostZero(aNext) || Motion.isAlmostZero(v)) {
				aNext = 0;
				phase = Phase.phase_8;
			}
			break;
		case phase_8: // nothing special
			duration_8++;
		default:
			break;
		}
		return aNext;
	}

	boolean stop(Phase phase, double t) {
		return duration_8 == 100;
	}

	@Override
	public void Run(double tmax) {
		System.out.println("Motion 3 started.");
		double t = 0;
		double x = 0;
		double v = 0;
		double a = 0;
		double jAct = 0;
		int step = 0;
		while (!stop(phase, t)) {
			// next time step
			t = (step++) * dtime;
			jAct = -a; // actual jerk

			// do phase and acceleration control
			a = getAcceleration(t, v, a);

			// motion continues
			v += a * dtime;
			double dx = v * dtime;
			x += dx;

			// Actual jerk
			jAct += a;
			jAct /= dtime;

			// print
			output(t, x, v, a, jAct);
		}
		close();
		System.out.println("Motion 3 finished.");
	}
}
