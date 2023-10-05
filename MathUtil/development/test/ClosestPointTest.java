package test;

import mathutil.MathUtil;

public class ClosestPointTest implements Test {

	@Override
	public void run() {

		test1();
	}

	static void test1() {
		double[] sp = new double[] { 0, 0 };
		double[] ep = new double[] { 1, 1 };

		double[] p = new double[] { -0.01, -0.01 };

		double[] cp = MathUtil.closestPoint(sp, ep, p);

		if (cp.length != 0) {
			System.out.println(Double.toString(cp[0]) + " : " + Double.toString(cp[1]));
		} else {
			System.out.println("No closest point.");
		}
	}
}
