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
		double accDist = getAccelerationDistance();
		double decDist = getDecelerationDistance();
//		System.out.println("Sacc = " + Double.toString(accDist));
//		System.out.println("Sdec = " + Double.toString(decDist));
		header("motion3.out");
		System.out.println("Motion 3 created.");
	}

	/**
	 * Returns duration of a third order motion.
	 * 
	 * @param as starting acceleration
	 * @param ae ending acceleration
	 * @param j  jerk
	 * @return duration of the motion
	 */
	private double getTime3Order(double as, double ae, double j) {
		if (Motion.isAlmostZero(j)) {
			return Double.NaN;
		}
		return (ae - as) / j;
	}

	/**
	 * Returns the ending velocity in a second order motion.
	 * 
	 * @param Vs starting velocity
	 * @param a  acceleration
	 * @param t  time
	 * @return the ending velocity
	 */
	private double getSpeed2Order(double Vs, double a, double t) {
		return Vs + a * t;
	}

	/**
	 * Returns the ending velocity in a third order motion.
	 * 
	 * @param Vs starting velocity
	 * @param as starting acceleration
	 * @param ae ending acceleration
	 * @param j  jerk
	 * @return the ending velocity
	 */
	private double getSpeed3Order(double Vs, double as, double ae, double j) {
		if (Motion.isAlmostZero(j)) {
			return Double.NaN;
		}
		return Vs + (ae * ae - as * as) / (2 * j);
	}

	/**
	 * Returns distance traveled in a second order motion.
	 * 
	 * @param Vs starting velocity
	 * @param a  acceleration
	 * @param t  time
	 * @return
	 */
	private double getDistance2Order(double Vs, double a, double t) {
		return Vs * t + 0.5 * a * t * t;
	}

	/**
	 * Returns distance traveled in a third order motion.
	 * 
	 * @param Vs starting velocity
	 * @param as starting acceleration
	 * @param ae ending acceleration
	 * @param j  jerk
	 * @return distance traveled
	 */
	private double getDistance3Order(double Vs, double as, double ae, double j) {
		if (Motion.isAlmostZero(j)) {
			return Double.NaN;
		}
		return Vs * (ae - as) / j + (ae - as) * (ae - as) * (ae + 2 * as) / (6 * j * j);
	}

	/**
	 * Estimates the ramping up phases.
	 */
	private double getAccelerationDistance() {

		// actual jerk
		double j = jerkmax * CORR_J;

		// starting values
		double vs = 0;
		double as = 0;

		// Phase 2 time estimate
		double t2 = (vmax - vs) / amax - amax / j + (as * as) / (2 * amax * j);
		double a1 = 0;
		if (t2 > 0) {
			a1 = amax;
			System.out.println("t2 > 0, Phase 2 exists");
		} else {
			t2 = 0;
			a1 = Math.sqrt((vmax - vs) * j + as * as / 2);
			System.out.println("t2 = 0, no Phase 2");
		}
		System.out.println("Phase 1 acceleration = " + Double.toString(a1));

		// Phase 1 acceleration grows
		double t1 = getTime3Order(as, a1, j);
		double v1 = getSpeed3Order(vs, as, a1, j);
		double s1 = getDistance3Order(vs, as, a1, j);
		// Phase 2 constant acceleration
		double a2 = a1;
		double v2 = getSpeed2Order(v1, a2, t2);
		double s2 = getDistance2Order(v1, a2, t2);
		// Phase 3 acceleration ramping down
		double a3 = 0;
		double t3 = getTime3Order(a2, a3, -j);
		double v3 = getSpeed3Order(v2, a2, a3, -j);
		double s3 = getDistance3Order(v2, a2, a3, -j);
		double tacc = t1 + t2 + t3;
		double sacc = s1 + s2 + s3;
//		System.out.println("Acceleration phases:");
//		System.out.println("t1 = " + Double.toString(t1));
//		System.out.println("t2 = " + Double.toString(t2));
//		System.out.println("t3 = " + Double.toString(t3));
//		System.out.println("V1 = " + Double.toString(v1));
//		System.out.println("V2 = " + Double.toString(v2));
//		System.out.println("V3 = " + Double.toString(v3));
//		System.out.println("S1 = " + Double.toString(s1));
//		System.out.println("S2 = " + Double.toString(s2));
//		System.out.println("S3 = " + Double.toString(s3));
		System.out.println("Tacc = " + Double.toString(tacc));
		System.out.println("Sacc = " + Double.toString(sacc));
		System.out.println("Vfin = " + Double.toString(v3));
		return sacc;
	}

	/**
	 * Estimates the ramping up phases.
	 */
	private double getDecelerationDistance() {

		// actual jerk
		double j = jerkmax * CORR_J;

		// starting values
		double v4 = vmax;
		double a4 = 0;

		// given residue values
		double vres = 0;
		double ares = 0;

		// Phase 6 time estimate
		double t6 = -(vres - v4 + (amax * amax) / j - (a4 * a4 + ares * ares) / (2 * j)) / amax;
		double a5 = 0;
		if (t6 > 0) {
			a5 = -amax;
			System.out.println("t6 > 0, Phase 6 exists");
		} else {
			t6 = 0;
			a5 = -Math.sqrt((v4 - vres) * j + (a4 * a4 + ares * ares) / 2);
			System.out.println("t6 = 0, no Phase 6");
		}
		System.out.println("Phase 5 acceleration = " + Double.toString(a5));

		// Phase 5 deceleration grows
		double t5 = getTime3Order(a4, a5, -j);
		double v5 = getSpeed3Order(v4, a4, a5, -j);
		double s5 = getDistance3Order(v4, a4, a5, -j);
		// Phase 6 constant deceleration
		double a6 = a5;
		double v6 = getSpeed2Order(v5, a6, t6);
		double s6 = getDistance2Order(v5, a6, t6);
		// Phase 7 deceleration ramping down
		double a7 = ares;
		double t7 = getTime3Order(a6, a7, j);
		double v7 = getSpeed3Order(v6, a6, a7, j);
		double s7 = getDistance3Order(v6, a6, a7, j);

		double tdec = t5 + t6 + t7;
		double sdec = s5 + s6 + s7;
//		System.out.println("Deceleration phases:");
//		System.out.println("t5 = " + Double.toString(t5));
//		System.out.println("t6 = " + Double.toString(t6));
//		System.out.println("t7 = " + Double.toString(t7));
//		System.out.println("V5 = " + Double.toString(v5));
//		System.out.println("V6 = " + Double.toString(v6));
//		System.out.println("V7 = " + Double.toString(v7));
//		System.out.println("S5 = " + Double.toString(s5));
//		System.out.println("S6 = " + Double.toString(s6));
//		System.out.println("S7 = " + Double.toString(s7));
		System.out.println("Tdec = " + Double.toString(tdec));
		System.out.println("Sdec = " + Double.toString(sdec));
		System.out.println("Vfin = " + Double.toString(v7));
		return sdec;
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
				System.out.println("Phase 2 started at t = " + Double.toString(time2));
			}
		case phase_2: // acceleration remains constant and positive
			// check for the transition to phase 3
			if (!Motion.isAlmostEqual(v, vmax)) {
				if (a * a >= (2 * jerkmax * CORR_J * (vmax - v))) {
					phase = Phase.phase_3;
					time3 = t;
					System.out.println("Phase 3 started at t = " + Double.toString(time3));
				}
			} else {
				phase = Phase.phase_3;
				time3 = t;
				System.out.println("Phase 3 started at t = " + Double.toString(time3));
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
				System.out.println("Phase 4 started at t = " + Double.toString(time4));
			}
			break;
		case phase_4: // constant velocity, zero acceleration
			aNext = 0;
			if (t - time4 > 1) {
				phase = Phase.phase_5;
				time5 = t;
				System.out.println("Phase 5 started at t = " + Double.toString(time5));
			}
			break;
		case phase_5: // the first deceleration phase, acceleration tends to -amax
			jerk = -CORR_J * jerkmax; // jerk < 0
			aNext += jerk * dtime; // a < 0
			if (aNext <= -amax) {
				aNext = -amax;
				phase = Phase.phase_6;
				time6 = t;
				System.out.println("Phase 6 started at t = " + Double.toString(time6));
			}
		case phase_6: // acceleration remains constant and negative
			// check for the transition to phase 7
			if (!Motion.isAlmostZero(v)) {
				if (a * a >= (2 * CORR_J * jerkmax * v)) {
					phase = Phase.phase_7;
					time7 = t;
					System.out.println("Phase 7 started at t = " + Double.toString(time7));
				}
			} else {
				phase = Phase.phase_7;
				time7 = t;
				System.out.println("Phase 7 started at t = " + Double.toString(time7));
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
				System.out.println("Phase 8 started at t = " + Double.toString(time8));
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

			// print
			output(t, x, v, a, jAct);
		}
		close();
		System.out.println("Motion 3 finished.");
	}
}
