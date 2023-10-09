package motion;

public final class Motion2 extends Motion {

	enum Phase {
		phase_1, // acceleration, a = amax = Const > 0
		phase_2; // deceleration, a = -amax = Const < 0
	}

	private Phase phase;

	public Motion2(double vmax, double amax) {
		this.vmax = vmax;
		this.amax = amax;
		header("motion2.out");
	}

	@Override
	public void Run(double tmax) {
		System.out.println("Motion started.");
		phase = Phase.phase_1;
		double t = 0;
		double x = 0;
		double v = 0;
		double a = 0;
		double aAct = 0;
		double jAct = 0;
		while (true) {
			// next time step
			t += dtime;
			if (t > tmax) {
				break;
			}

			jAct = -aAct;
			aAct = -v;

			// phase definition
			if (t >= 0.15) {
				phase = Phase.phase_2;
			}

			// acceleration control
			switch (phase) {
			case phase_1: // acceleration tries to increase up to amax
				a = amax;
				v += a * dtime;
				break;
			case phase_2: // acceleration decreases to zero
				a = -amax;
				v += a * dtime;
				break;
			default:
				break;
			}

			if (v < 0) {
				v = 0;
			}
			if (v > vmax) {
				v = vmax;
			}

			// motion continues
			double dx = v * dtime;
			x += dx;

			// Actual derivatives
			aAct += v; // actual a
			aAct /= dtime;
			jAct += aAct; // actual j
			jAct /= dtime;

			// print
			output(t, x, v, aAct, jAct);

			// stop?
		}
		close();
	}

}
