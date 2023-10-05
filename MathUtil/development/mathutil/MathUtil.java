package mathutil;

public class MathUtil {

	private MathUtil() {
	}

	// Public API

	/**
	 * Finds and returns the point of intersection of the two linear segments [sp1;
	 * ep1] and [sp2; ep2] with respect to given tolerance. Returns null if no
	 * intersection exists.
	 * 
	 * @param sp1 Segment 1 start point
	 * @param ep1 Segment 1 end point
	 * @param sp2 Segment 2 start point
	 * @param ep2 Segment 2 end point
	 * @param tol the tolerance. If two points are within the tolerance square
	 *            (+-tol; +-tol), we do not distinguish these.
	 */
	public static double[] intersect(double[] sp1, double[] ep1, double[] sp2, double[] ep2, double tol) {
		double dx1 = ep1[0] - sp1[0];
		double dy1 = ep1[1] - sp1[1];
		double dx2 = ep2[0] - sp2[0];
		double dy2 = ep2[1] - sp2[1];
		double dxs = sp2[0] - sp1[0];
		double dys = sp2[1] - sp1[1];

		double mainDet = det(dx1, -dx2, dy1, -dy2);
		double detOne = det(dxs, -dx2, dys, -dy2);
		double detTwo = det(dx1, dxs, dy1, dys);

		double fi1 = safeDivide(detOne, mainDet);
		double fi2 = safeDivide(detTwo, mainDet);

		// 1) If fi1 and fi2 are NaN, the lines are parallel. In this case the segments
		// can intersect if only they lie on one line. Try 4 points test.
		// 2) If f11 and fi2 are within the [0; 1] range => we've found the intersection
		// point!
		// 3) If f11 or fi2 are outside the [0; 1] range => the intersection point is
		// outside the segment bounds. Try 4 points test.
		// "4 points test" means calculate closest distances from Segment 1 to the
		// start/end points of Segment 2 and vice versa.
		// The shortest of the four will be the "almost intersection point" with the
		// specified tolerance.

		if (!Double.isNaN(fi1) && !Double.isNaN(fi2) && Double.isFinite(fi1) && Double.isFinite(fi2) && fi1 >= 0
				&& fi1 <= 1 && fi2 >= 0 && fi2 <= 1) {
			double x = sp1[0] + fi1 * dx1;
			double y = sp1[1] + fi1 * dy1;
			return new double[] { x, y };
		} else {
			return fourPointsTestPoint(sp1, ep1, sp2, ep2, tol);
		}

	}

	/**
	 * Returns the shortest distance between the line [sp; ep] and the point p.
	 * 
	 * @param sp the start point of the line
	 * @param ep the end point of the line
	 * @param p  the point
	 * @return the shortest distance between the line and the point
	 */
	public static double distance(double[] sp, double[] ep, double[] p) {
		return distance(sp[0], sp[1], ep[0], ep[1], p[0], p[1]);
	}

	/**
	 * Returns determinant of the given (2x2) matrix in the standard form:<br>
	 * |a11, a12|<br>
	 * |a21, a22|
	 * 
	 * @param a11 matrix element a11
	 * @param a12 matrix element a12
	 * @param a21 matrix element a21
	 * @param a22 matrix element a22
	 * @return determinant of the matrix
	 */
	public static double det(double a11, double a12, double a21, double a22) {
		return (a11 * a22 - a21 * a12);
	}

	// distance between the two points
	public static double distance(double xs, double ys, double xe, double ye) {
		return Math.hypot(xe - xs, ye - ys);
	}

	/**
	 * Returns the point on the line which is closest to a specified point.
	 * 
	 * @param sp
	 * @param ep
	 * @param p
	 * @return
	 */
	public static double[] closestPoint(double[] sp, double[] ep, double[] p) {
		return closestPoint(sp[0], sp[1], ep[0], ep[1], p[0], p[1]);
	}

	// Private helpers

	// returns the point on the line which is closest to a specified point
	private static double[] closestPoint(double xs, double ys, double xe, double ye, double x0, double y0) {
		double fi = getClosestFi(xs, ys, xe, ye, x0, y0);
		double x = xs + fi * (xe - xs);
		double y = ys + fi * (ye - ys);
		return new double[] { x, y };
	}

	// returns the parameter value for the point on the line which is closest to a
	// specified point
	private static double getClosestFi(double xs, double ys, double xe, double ye, double x0, double y0) {
		final double tol = 1E-12;
		double ret = 0;
		double l2 = (xe - xs) * (xe - xs) + (ye - ys) * (ye - ys); // the line length squared
		if (l2 > tol) {
			ret = -(xs - x0) * (xe - xs) - (ys - y0) * (ye - ys);
			ret /= l2;
			if (ret < 0) {
				ret = 0;
			} else if (ret > 1) {
				ret = 1;
			}
		}
		return ret;
	}

	// shortest distance between line and point
	private static double distance(double xs, double ys, double xe, double ye, double x0, double y0) {
		double[] cp = closestPoint(xs, ys, xe, ye, x0, y0); // line1/sp2
		return distance(cp[0], cp[1], x0, y0);
	}

	// returns one point out of the four
	private static double[] fourPointsTestPoint(double[] sp1, double[] ep1, double[] sp2, double[] ep2, double tol) {
		// Try 4 closest points
		double mindist = fourPointsTestDistance(sp1, ep1, sp2, ep2);
		if (mindist < tol) {
			if (distance(sp1, ep1, sp2) == mindist) {
				return sp2.clone(); // line1/sp2 => return sp2
			}
			if (distance(sp1, ep1, ep2) == mindist) {
				return ep2.clone(); // line1/ep2 => return ep2
			}
			if (distance(sp2, ep2, sp1) == mindist) {
				return sp1.clone(); // line2/sp1 => return sp1
			}
			return ep1.clone(); // line2/ep1 => return ep1
		}
		return new double[0];
	}

	// returns minimal distance out of the four
	private static double fourPointsTestDistance(double[] sp1, double[] ep1, double[] sp2, double[] ep2) {
		// Try 4 closest points
		double distL1S2 = distance(sp1, ep1, sp2); // distance line1-sp2
		double distL1E2 = distance(sp1, ep1, ep2); // distance line1-ep2
		double distL2S1 = distance(sp2, ep2, sp1); // distance line2-sp1
		double distL2E1 = distance(sp2, ep2, ep1); // distance line2-ep1
		return Math.min(Math.min(distL1S2, distL1E2), Math.min(distL2S1, distL2E1)); // return minimal distance
	}

	/**
	 * Simple safe division function. Only returns NaN if the denominator is equal
	 * zero.
	 * 
	 * @param one the numerator
	 * @param two the denominator
	 * @return the quotient
	 */
	public static double safeDivide(double one, double two) {
		double ret = 0;
		if (two == 0) {
			ret = Double.NaN;
		} else {
			ret = one / two;
		}
		return ret;
	}
}
