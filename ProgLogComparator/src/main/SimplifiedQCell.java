package main;

import java.util.Locale;

/**
 * Implements a simplified QCell
 */
public class SimplifiedQCell {

	enum DataType {
		AXIS_DATA, OTHER;
	}

	enum AxisAction {
		RAPID, LINEAR, CW, CCW;

		@Override
		public String toString() {

			String ret = "";
			switch (this) {
			case RAPID:
				ret = "RAPID";
				break;
			case LINEAR:
				ret = "LINEAR";
				break;
			case CW:
				ret = "CW";
				break;
			case CCW:
				ret = "CCW";
				break;
			default:
				break;
			}

			return ret;

		}
	}

	int cellID;
	int blockID;

	boolean eob;
	boolean exstop;

	DataType dataType;

	AxisAction axisAction;

	double length;
	double targets[];
	double xtargets[];
	double feedrate;

	public SimplifiedQCell(int id, int bid, boolean eob, boolean exstop, DataType type, AxisAction action,
			double length, double targets[], double feedrate) {
		this.cellID = id;
		this.blockID = bid;
		this.eob = eob;
		this.exstop = exstop;
		this.dataType = type;
		this.axisAction = action;
		this.length = length;
		this.targets = targets.clone();
		this.xtargets = new double[6];
		this.feedrate = feedrate;
	}

	public SimplifiedQCell(int id, int bid, boolean eob, boolean exstop, DataType type, AxisAction action,
			double length, double targets[], double xtargets[], double feedrate) {
		this.cellID = id;
		this.blockID = bid;
		this.eob = eob;
		this.exstop = exstop;
		this.dataType = type;
		this.axisAction = action;
		this.length = length;
		this.targets = targets.clone();
		this.xtargets = xtargets.clone();
		this.feedrate = feedrate;
	}

	@Override
	public String toString() {

		String ret = String.format(Locale.US, "%5d", cellID);
		ret += String.format(Locale.US, " %10d", blockID);
		ret += String.format(Locale.US, " %8s", eob ? "true" : "false");
		ret += String.format(Locale.US, " %8s", axisAction.toString());
		ret += String.format(Locale.US, " %8s", exstop ? "true" : "false");
		ret += String.format(Locale.US, " %10.4f %10.4f %10.4f %10.4f %10.4f %10.4f", targets[0], targets[1],
				targets[2], targets[3], targets[4], targets[5]);
		ret += String.format(Locale.US, " %12.4f", length);
		ret += String.format(Locale.US, " %12.4f", feedrate);
		ret += "\n";

		return ret;
	}

	/**
	 * Returns a header for the output files. Must correspond to toString() format.
	 */
	public static String getHeader() {
		return String.format(Locale.US, "%5s %10s %8s %8s %8s %10s %10s %10s %10s %10s %10s %12s %12s", "ID",
				"Block ID", "EoB", "Action", "Estop", "X", "Y", "Z", "A", "B", "C", "Length", "Feed rate") + "\n";
	}

	public boolean almostEqual(SimplifiedQCell other) {

		final double tol = 1E-4; // tolerance

		boolean isActionEqual = axisAction == other.axisAction;
		boolean isLengthEqual = Math.abs(length - other.length) < tol;

		boolean areTargetsEqual = true;
		for (int i = 0; i < targets.length; i++) {
			if (Math.abs(targets[i] - other.targets[i]) > tol) {
				areTargetsEqual = false;
				break;
			}
		}

		boolean areXTargetsEqual = true;
		for (int i = 0; i < xtargets.length; i++) {
			if (Math.abs(xtargets[i] - other.xtargets[i]) > tol) {
				areXTargetsEqual = false;
				break;
			}
		}

		boolean isEOBEqual = eob == other.eob;
		boolean isFeedRateEqual = Math.abs(feedrate - other.feedrate) < tol;

		return isActionEqual && isLengthEqual && areTargetsEqual && areXTargetsEqual && isEOBEqual && isFeedRateEqual;
	}
}
