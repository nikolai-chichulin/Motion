package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import main.SimplifiedQCell.AxisAction;

public class Util {

	public static List<SimplifiedQCell> getMotionCells(BufferedReader br) {

		if (br == null) {
			return Collections.emptyList();
		}

		List<SimplifiedQCell> ret = new ArrayList<>();

		String line = null;
		boolean loop = true;

		while (loop) {
			try {
				line = br.readLine();
				if (line == null) {
					break;
				}

				String target = "ID:"; // New block starts with "ID:" at the line's beginning
				int targetIndex = line.indexOf(target);
				String sub = Util.getSubString(line, target);
				if (targetIndex == 0 && sub != null) {
					// Read the block
					int id = Integer.valueOf(sub);

					line = br.readLine();
					target = "Block ID:";
					int blockid = getInteger(line, target);

					// line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					target = "End of Block:";
					boolean eob = getBoolean(line, target);

					line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					target = "Data Type:";
					sub = Util.getSubString(line, target);
					if (sub == null || !sub.equals("AXIS_DATA")) {
						continue;
					}

					line = br.readLine();
					line = br.readLine();
					target = "	AxisAction:";
					sub = Util.getSubString(line, target);
					AxisAction action = AxisAction.RAPID;
					if (sub.equals("LINEARACTION")) {
						action = SimplifiedQCell.AxisAction.LINEAR;
					} else if (sub.equals("CWACTION")) {
						action = SimplifiedQCell.AxisAction.CW;
					} else if (sub.equals("CCWACTION")) {
						action = SimplifiedQCell.AxisAction.CCW;
					}

					line = br.readLine();
					line = br.readLine();
					target = "	Stroke Length:";
					double length = getDouble(line, target);

					line = br.readLine();
					target = "	Axis targets:";
					double targets[] = Arrays.copyOf(getDoubles(line, target), 6);

					line = br.readLine();
					target = "	Transformed axis targets when G234 is active:";
					double xtargets[] = getDoubles(line, target);

					line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					target = "	Feed rate MM/Sec:";
					double feedrate = getDouble(line, target);

					line = br.readLine();
					line = br.readLine();
					line = br.readLine();
					target = "	Exact Stop:";
					boolean exstop = getBoolean(line, target);

					SimplifiedQCell cell = new SimplifiedQCell(id, blockid, eob, exstop,
							SimplifiedQCell.DataType.AXIS_DATA, action, length, targets, xtargets, feedrate);
					ret.add(cell);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;

	}

	/**
	 * Only motion cells!
	 */
	public static List<SimplifiedQCell> getDifference(List<SimplifiedQCell> cells1, List<SimplifiedQCell> cells2) {

		List<SimplifiedQCell> ret = new ArrayList<>();
		for (SimplifiedQCell cell1 : cells1) {
			boolean single = true;
			for (SimplifiedQCell cell2 : cells2) {
				if (cell1.almostEqual(cell2)) {
					single = false;
					break;
				}
			}
			if (single) {
				ret.add(cell1);
			}
		}
		return ret;
	}

	public static void print(BufferedWriter bw, List<SimplifiedQCell> dcells) {
		try {
			String line = String.format("%5s%s", "", SimplifiedQCell.getHeader());
			bw.write(line); // header
			int i = 0;
			for (SimplifiedQCell cell : dcells) {
				line = String.format("%5d%s", i, cell.toString());
				bw.write(line); // cells
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getSubString(String line, String target) {
		String ret = null;
		if (line != null && target != null) {
			int k = line.indexOf(target);
			if (k > -1) {
				int shift = target.length();
				ret = line.substring(k + 1 + shift, line.length()).trim();
			}
		}
		return ret;
	}

	private static boolean getBoolean(String line, String target) {
		String sub = getSubString(line, target);
		if (sub != null) {
			return sub.equals("true");
		}
		return false;
	}

	private static int getInteger(String line, String target) {
		String sub = getSubString(line, target);
		if (sub != null) {
			return Integer.valueOf(sub);
		}
		return -1;
	}

	private static double getDouble(String line, String target) {
		String sub = getSubString(line, target);
		if (sub != null) {
			return Double.valueOf(sub);
		}
		return -1;
	}

	private static double[] getDoubles(String line, String target) {
		String sub = getSubString(line, target);
		if (sub != null) {
			String stargets[] = sub.split(" ");
			return getDoubles(stargets);
		}
		return null;
	}

	private static double[] getDoubles(String sdoubles[]) {
		if (sdoubles == null) {
			return null;
		}

		double ret[] = new double[sdoubles.length];
		int i = 0;
		for (String word : sdoubles) {
			ret[i] = Double.valueOf(word).doubleValue();
			i++;
		}
		return ret;
	}
}
