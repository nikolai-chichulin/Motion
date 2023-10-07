package motion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Motion {

	enum Phase {
		phase_1, // start of acceleration
		phase_2, // end of acceleration
		phase_3, // start of deceleration
		phase_4; // end of deceleration
	}

	private static double dtime = 0.001;
	private double vmax;
	private double jerkmax;
	private double amax;
	private double err_v = 0.001;
	private Phase phase;

	private BufferedWriter bw = null;

	public Motion(double vmax, double amax, double jmax) {
		this.vmax = vmax;
		this.amax = amax;
		this.jerkmax = jmax;
		try {
			Charset cset = StandardCharsets.UTF_8;
			Path pw = Paths.get("motion.out");
			bw = Files.newBufferedWriter(pw, cset);
			String line = String.format(Locale.US, "%15s%15s%15s%15s%15s\n", "time", "X", "V", "a", "Jerk");
			bw.write(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Motion created.");
	}

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
			double acrit = Math.sqrt(2 * jerkmax * (vmax - v));
			if (a >= acrit) {
				phase = Phase.phase_2;
			}

			// acceleration control
			switch (phase) {
			case phase_1: // acceleration tries to increase up to amax
				a += jerkmax * dtime;
				if (a > amax) {
					a = amax;
				}
				v += a * dtime;
				break;
			case phase_2: // acceleration decreases to zero
				a -= jerkmax * dtime;
				if (a < 0) {
					a = 0;
				}
				v += a * dtime;
				break;
			case phase_3:
				break;
			case phase_4:
				break;
			default:
				break;
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
			try {
				String line = String.format(Locale.US, "%15f%15f%15f%15f%15f\n", t, x, v, aAct, jAct);
				bw.write(line); // header
			} catch (IOException e) {
				e.printStackTrace();
			}

			// stop?
			if (Math.abs(v - vmax) < err_v) {
				break;
			}
		}
		try {
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Motion finished.");
	}
}
