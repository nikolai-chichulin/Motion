package bcmain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Solver {

	class Four {
		Four(int a, int b, int c, int d) {
			i1 = a;
			i2 = b;
			i3 = c;
			i4 = d;
		}

		@Override
		public String toString() {
			String s = String.format(Locale.US, "%d%d%d%d", i1, i2, i3, i4);
			return s;
		}

		int i1;
		int i2;
		int i3;
		int i4;
	}

	private boolean[] mask = new boolean[10001];
	private List<Four> variants = new ArrayList<>();

	public Solver() {
		for (int i = 0; i < mask.length; i++) {
			mask[i] = true;
		}
	}

	public int scan(String ins, int bullsExp, int cowsExp) {
		boolean output = false;
		variants.clear();
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
							variants.add(new Four(i1, i2, i3, i4));
							ret++;
							if (output)
								System.out.println(Integer.toString(i1) + "." + Integer.toString(i2) + "."
										+ Integer.toString(i3) + "." + Integer.toString(i4));
						} else {
							mask[n] = false;
						}
					}
				}
			}
		}
		if (output)
			System.out.println("Number of variants = " + ret);
		return ret;
	}

	/**
	 * Does the next turns based on the current variants.
	 * 
	 * @param turn number of turn
	 * @return the next turn as a four of integers
	 */
	Four nextTurn(int turn) {
		if (turn == 1) {
			return new Four(0, 1, 2, 3);
		} else if (turn == 2) {
			return new Four(4, 5, 6, 7);
		}
		Four ret = variants.get(0);
		return ret;
	}

	public void help() {
		int r = 10 * 9 * 8 * 7;
		System.out.println("Initial number of variants = " + r);

		try (Scanner in = new Scanner(System.in)) {
			while (r > 1) {
				System.out.println("Your move: ");
				String ins = in.next();
				System.out.println("Bulls cows: ");
				int bulls = in.nextInt();
				int cows = in.nextInt();
				System.out.println(String.format(Locale.US, "Check: number: %s bulls: %d cows: %d ", ins, bulls, cows));
				r = scan(ins, bulls, cows);
			}
			System.out.println("Win!");
		}
	}

	public void solve() {
		int r = 10 * 9 * 8 * 7;
		System.out.println("Initial number of variants = " + r);
		Four nextTurn = new Four(0, 0, 0, 0);

		int i = 0;
		try (Scanner in = new Scanner(System.in)) {
			while (r > 0) {
				i++;
				nextTurn = nextTurn(i);
				System.out.println("Move " + i + ": " + nextTurn);
				System.out.println("Reaction (bulls cows): ");
				int bulls = in.nextInt();
				int cows = in.nextInt();
				if (bulls == 4 && cows == 0) {
					r = 0;
				} else {
					r = scan(nextTurn.toString(), bulls, cows);
					System.out.println("...there are still " + r + " variants...");
				}
			}
			System.out.println(nextTurn + " wins in " + i + " turns!");
		}
	}
}
