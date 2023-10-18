package solver;

import java.util.Locale;
import java.util.Scanner;

public class Solver {

	private int[][] p = new int[4][10];
	private boolean[] mask = new boolean[10001];

	public Solver() {
		for (int i = 0; i < p.length; i++) {
			for (int j = 0; j < p[i].length; j++) {
				p[i][j] = 1;
			}
		}
		for (int i = 0; i < mask.length; i++) {
			mask[i] = true;
		}
	}

	public int rescan1() {
		int ret = 0;
		for (int i1 = 0; i1 < 10; i1++) {
			if (p[0][i1] == 0) {
				continue;
			}
			for (int i2 = 0; i2 < 10; i2++) {
				if (i2 == i1 || p[1][i2] == 0) {
					continue;
				}
				for (int i3 = 0; i3 < 10; i3++) {
					if (i3 == i2 || i3 == i1 || p[2][i3] == 0) {
						continue;
					}
					for (int i4 = 0; i4 < 10; i4++) {
						if (i4 == i3 || i4 == i2 || i4 == i1 || p[3][i4] == 0) {
							continue;
						}
						ret++;
//						System.out.println(Integer.toString(i1) + "." + Integer.toString(i2) + "."
//								+ Integer.toString(i3) + "." + Integer.toString(i4));
					}
				}
			}
		}
		return ret;
	}

	public int rescan2(String ins, int bullsExp, int cowsExp) {
		int ret = 0;
		for (int i1 = 0; i1 < 10; i1++) {
			for (int i2 = 0; i2 < 10; i2++) {
				if (i2 == i1) {
					continue;
				}
				for (int i3 = 0; i3 < 10; i3++) {
					if (i3 == i2 || i3 == i1) {
						continue;
					}
					for (int i4 = 0; i4 < 10; i4++) {
						if (i4 == i3 || i4 == i2 || i4 == i1) {
							continue;
						}

						int n = i1 * 1000 + i2 * 100 + i3 * 10 + i4;
						if (!mask[n]) {
							continue;
						}

						int bullsAct = 0;
						int cowsAct = 0;
						for (int i = 0; i < ins.length(); i++) {
							// Bulls the cows
							boolean bull1 = (i == 0) && (ins.charAt(i) - '0' == i1);
							boolean bull2 = (i == 1) && (ins.charAt(i) - '0' == i2);
							boolean bull3 = (i == 2) && (ins.charAt(i) - '0' == i3);
							boolean bull4 = (i == 3) && (ins.charAt(i) - '0' == i4);

							if (bull1 || bull2 || bull3 || bull4) {
								bullsAct++;
							} else {
								if (ins.charAt(i) - '0' == i1 || ins.charAt(i) - '0' == i2 || ins.charAt(i) - '0' == i3
										|| ins.charAt(i) - '0' == i4) {
									cowsAct++;
								}
							}
						}

						if (bullsAct == bullsExp && cowsAct == cowsExp) {
							ret++;
							System.out.println(Integer.toString(i1) + "." + Integer.toString(i2) + "."
									+ Integer.toString(i3) + "." + Integer.toString(i4));
						} else {
							mask[n] = false;
						}
					}
				}
			}
		}
		System.out.println("Number of variants = " + ret);
		return ret;
	}

	public void run() {
		int r = rescan1();
		System.out.println("Number of variants = " + r);

		try (Scanner in = new Scanner(System.in)) {
			while (r > 1) {
				System.out.println("Your move: ");
				String ins = in.next();
				System.out.println("Bulls cows: ");
				int bulls = in.nextInt();
				int cows = in.nextInt();
				System.out.println(String.format(Locale.US, "Check: number: %s bulls: %d cows: %d ", ins, bulls, cows));
				r = rescan2(ins, bulls, cows);
			}
			System.out.println("Win!");
		}
	}
}
