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

	private static double dtime = 0.001;
	private double vmax;
	private double jerkmax;
	private double amax;

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

			// Start of acceleration (Phase I)
			double da = jerkmax * dtime;
			a += da;
			if (a > amax) {
				a = amax;
			}
			double dv = a * dtime;
			v += dv;
			if (v > vmax) {
				v = vmax;
			}
			double dx = v * dtime;
			x += dx;

			// Actual derivatives
			aAct += v; // actual a
			aAct /= dtime;
			jAct += aAct; // actual j
			jAct /= dtime;

			// End of acceleration (Phase II)
			//double ts = 0.5 * (1 + Math.sqrt(1 + 8 * (vmax - v) / jerkmax));
			double jreq = 2*(vmax-v)/(t)

			// print
			try {
				String line = String.format(Locale.US, "%15f%15f%15f%15f%15f%15f\n", t, x, v, aAct, jAct, ts);
				bw.write(line); // header
			} catch (IOException e) {
				e.printStackTrace();
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
