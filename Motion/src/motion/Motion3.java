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
	private double xend;

	private int duration_8 = 0;

	// Moments when the the phases begin
	private double time1 = 0;
	private double time2 = 0;
	private double time3 = 0;
	private double time4 = 0;
	private double time5 = 0;
	private double time6 = 0;
	private double time7 = 0;
	private double time8 = 0;

	public Motion3(double vmax, double amax, double jmax) {
		this.phase = Phase.phase_1;
		this.vmax = vmax;
		this.amax = amax;
		this.jerkmax = jmax;
		estimate_acceleration();
		header("motion3.out");
		System.out.println("Motion 3 created.");
	}

	private double getTime(double as, double ae, double j) {
		if (Motion.isAlmostZero(j)) {
			return Double.NaN;
		}
		return (ae - as) / j;
	}

	private double getSpeed(double Vs, double as, double ae, double j) {
		if (Motion.isAlmostZero(j)) {
			return Double.NaN;
		}
		return Vs + (ae * ae - as * as) / (2 * j);
	}

	private double getDistance(double Vs, double as, double ae, double j) {
		if (Motion.isAlmostZero(j)) {
			return Double.NaN;
		}
		return Vs * (ae - as) / j + (ae - as) * (ae - as) * (ae + 2 * as) / (6 * j * j);
	}

	private void estimate_acceleration() {
		double j = jerkmax * CORR_J;
		double Vs = 0;
		double as = 0;
		double t1 = 0;
		double t2 = (vmax - Vs) / amax - amax / j + as * as / (2 * amax * j);
		double t3 = 0;
		double S1 = 0;
		double S2 = 0;
		double S3 = 0;
		double V1 = 0;
		double V2 = 0;
		double V3 = 0;

		if (t2 > 0) {
			System.out.println("t2 > 0");
			t1 = (amax - as) / j;
			t3 = amax / j;
			// S1 = Vs * (amax - as) / j + (amax - as) * (amax - as) * (amax - 2 * as) / //
			// (6 * j * j);
			S1 = getDistance(Vs, as, amax, j);
			// V1 = Vs + (amax * amax - as * as) / (2 * j);
			V1 = getSpeed(Vs, as, amax, j);
			S2 = V1 * t2 + amax * t2 * t2 / 2;
			V2 = V1 + amax * t2;
			// S3 = V2 * amax / j + 2 * amax * amax * amax / (6 * j * j);
			S3 = getDistance(Vs, amax, 0, j);
		} else {
			System.out.println("t2 = 0");
			t2 = 0;
			double ap = Math.sqrt((vmax - Vs) * j + as * as / 2);
			System.out.println("Apeak = " + Double.toString(ap));
			t1 = (Math.sqrt((vmax - Vs) * j + as * as / 2) - as) / j;
			V1 = Vs + (ap * ap - as * as) / (2 * j);
			t3 = Math.sqrt((vmax - Vs) * j + as * as / 2) / j;
			S1 = Vs * (ap - as) / j + (ap - as) * (ap - as) * (ap - 2 * as) / (6 * j * j);
			S2 = 0;
			// S = Vs * (ae - as) / j + (ae - as)^2 * (ae + 2as) / (6j^2)
			// S1 = Vs * (a1 - as) / j1 + (a1 - as)^2 * (a1 - 2as) / (6j1^2)
			// S3 = -V2 * a2 / j3 - 2a2^3 / (6j3^2)
			S3 = V1 * ap / j + 2 * ap * ap * ap / (6 * j * j);
		}
		double Sacc = S1 + S2 + S3;
		System.out.println("t1 = " + Double.toString(t1));
		System.out.println("t2 = " + Double.toString(t2));
		System.out.println("t3 = " + Double.toString(t3));
		System.out.println("V1 = " + Double.toString(V1));
		System.out.println("V2 = " + Double.toString(V2));
		System.out.println("V3 = " + Double.toString(V3));
		System.out.println("S1 = " + Double.toString(S1));
		System.out.println("S2 = " + Double.toString(S2));
		System.out.println("S3 = " + Double.toString(S3));
		System.out.println("Sacc = " + Double.toString(Sacc));
	}

	private double getAcceleration(double t, double v, double a) {
		double aNext = a;
		double jerk = 0;
		switch (phase) {
		case phase_1: // the first phase of acceleration, acceleration tends to maximum
			jerk = CORR_J * jerkmax; // jerk > 0
			aNext += jerk * dtime; // a > 0
			if (aNext >= amax) {
				aNext = amax;
				phase = Phase.phase_2;
				time2 = t;
			}
		case phase_2: // acceleration remains constant and positive
			// check for the transition to phase 3
			if (!Motion.isAlmostEqual(v, vmax)) {
				if (a * a >= (2 * jerkmax * CORR_J * (vmax - v))) {
					phase = Phase.phase_3;
					time3 = t;
				}
			} else {
				phase = Phase.phase_3;
				time3 = t;
			}
			break;
		case phase_3: // acceleration tends to zero, velocity tends to Vmax
			// we need to regulate the jerk at every time step
			// to avoid mesh error and come exactly to Vmax
			// otherwise, (if use j = Jmax), velocity will come to Vmax +- delta
			if (!Motion.isAlmostEqual(v, vmax)) {
				jerk = -0.5 * a * a / (vmax - v); // jerk < 0
				if (-jerk > jerkmax) {
					jerk = -jerkmax;
				}
				aNext += jerk * dtime;
				if (aNext < 0) {
					aNext = 0;
				}
			} else {
				phase = Phase.phase_4;
				time4 = t;
			}
			break;
		case phase_4: // constant velocity, zero acceleration
			aNext = 0;
			if (t > 1) {
				phase = Phase.phase_5;
				time5 = t;
			}
			break;
		case phase_5: // the first deceleration phase, acceleration tends to -amax
			jerk = -CORR_J * jerkmax; // jerk < 0
			aNext += jerk * dtime; // a < 0
			if (aNext <= -amax) {
				aNext = -amax;
				phase = Phase.phase_6;
				time6 = t;
			}
		case phase_6: // acceleration remains constant and negative
			// check for the transition to phase 7
			if (!Motion.isAlmostZero(v)) {
				if (a * a >= (2 * CORR_J * jerkmax * v)) {
					phase = Phase.phase_7;
					time7 = t;
				}
			} else {
				phase = Phase.phase_7;
				time7 = t;
			}
			break;
		case phase_7: // acceleration tends to zero
			jerk = 0.5 * a * a / v; // jerk > 0
			if (jerk > jerkmax) {
				jerk = jerkmax;
			}
			aNext += jerk * dtime; // a < 0
			if (Motion.isAlmostZero(aNext) || Motion.isAlmostZero(v)) {
				aNext = 0;
				phase = Phase.phase_8;
				time8 = t;
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
	public void Run(double tmax, double ep) {
		System.out.println("Motion 3 started.");
		this.xend = ep;
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
			x += v * dtime;

			// actual jerk
			jAct += a;
			jAct /= dtime;

			// time to start decelerating phase 5?
			double duration_5 = (a + amax) / jerkmax;
			double distance_5 = v * duration_5 + 0.5 * a * duration_5 * duration_5
					- jerkmax * duration_5 * duration_5 * duration_5 / 6;
			double distance_6 = 0;
			double distance_7 = amax * vmax / jerkmax + amax * amax * amax / (3 * jerkmax * jerkmax);
			double distance_decel = distance_5 + distance_6 + distance_7;
			double distance_to_go = xend - x;
//			if (distance_to_go < distance_decel) {
//				phase = Phase.phase_5;
//			}

			// print
			output(t, x, v, a, jAct);
		}
		close();
		System.out.println("Motion 3 finished.");
	}
}
