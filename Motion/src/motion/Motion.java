package motion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public abstract class Motion {
	protected static double dtime = 0.001;
	protected double vmax;
	protected double amax;
	private BufferedWriter bw = null;
	private static final double TOL = 1E-10;

	abstract public void Run(double tmax);

	protected void header(String fname) {
		try {
			Charset cset = StandardCharsets.UTF_8;
			Path pw = Paths.get(fname);
			bw = Files.newBufferedWriter(pw, cset);
			String line = String.format(Locale.US, "%15s%15s%15s%15s%15s\n", "time", "X", "V", "a", "Jerk");
			bw.write(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Motion created.");
	}

	protected void output(double t, double x, double v, double a, double j) {
		try {
			if (j > 100000) {
				j = 100000;
			}
			if (j < -100000) {
				j = -100000;
			}
			String line = String.format(Locale.US, "%15f%15f%15f%15f%15f\n", t, x, v, a, j);
			bw.write(line); // header
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void close() {
		try {
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Motion finished.");
	}

	public static boolean isAlmostEqual(double a, double b, double tol) {
		return Math.abs(a - b) <= tol;
	}

	public static boolean isAlmostEqual(double a, double b) {
		return Math.abs(a - b) <= TOL;
	}
}
