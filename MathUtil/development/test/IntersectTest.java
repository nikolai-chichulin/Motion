package test;

import mathutil.MathUtil;

class IntersectTest implements Test {

	@Override
	public void run() {

		test1();
		test2();
	}

	static void test1() {
		double[] sp1 = new double[] { 0, 0 };
		double[] ep1 = new double[] { 1, 1 };

		double[] sp2 = new double[] { 0.001, 0 };
		double[] ep2 = new double[] { -1, -2 };

		double[] is = MathUtil.intersect(sp1, ep1, sp2, ep2, 1E-3);

		if (is.length != 0) {
			System.out.println(Double.toString(is[0]) + " : " + Double.toString(is[1]));
		} else {
			System.out.println("No intersection.");
		}
	}

	static void test2() {
		double[] sp1 = new double[] { 0, 0 };
		double[] ep1 = new double[] { 1, 1 };

		double[] sp2 = new double[] { 0.0011, 0 };
		double[] ep2 = new double[] { -1, -2 };

		double[] is = MathUtil.intersect(sp1, ep1, sp2, ep2, 1E-3);

		if (is.length != 0) {
			System.out.println(Double.toString(is[0]) + " : " + Double.toString(is[1]));
		} else {
			System.out.println("No intersection.");
		}
	}
}
