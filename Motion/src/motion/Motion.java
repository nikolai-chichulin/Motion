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
	private static final double TOL = 1E-10; // numerical precision

	abstract public void Run(double tmax, double ep);

	protected void header(String fname) {
		try {
			Charset cset = StandardCharsets.UTF_8;
			Path pw = Paths.get(fname);
			bw = Files.newBufferedWriter(pw, cset);
			String line = String.format(Locale.US, "%20s%20s%20s%20s%20s\n", "time", "X", "V", "a", "Jerk");
			bw.write(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("File opened.");
	}

	protected void output(double t, double x, double v, double a, double j) {
		try {
			String line = String.format(Locale.US, "%20f%20f%20f%20f%20f\n", t, x, v, a, j);
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
		System.out.println("File closed.");
	}

	public static boolean isAlmostEqual(double a, double b, double tol) {
		return Math.abs(a - b) <= tol;
	}

	public static boolean isAlmostEqual(double a, double b) {
		return Math.abs(a - b) <= TOL;
	}

	public static boolean isAlmostZero(double a) {
		return Math.abs(a) <= TOL;
	}
}
