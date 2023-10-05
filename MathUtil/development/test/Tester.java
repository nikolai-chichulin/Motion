package test;

import java.util.ArrayList;
import java.util.List;

public class Tester {

	static List<Test> tests;

	public static void main(String[] args) {

		tests = new ArrayList<>();
		prepareTests();
		runTests();

	}

	static void prepareTests() {
		tests.add(new IntersectTest());
		tests.add(new ClosestPointTest());
	}

	private static void runTests() {
		for (Test test : tests) {
			test.run();
		}
	}
}
