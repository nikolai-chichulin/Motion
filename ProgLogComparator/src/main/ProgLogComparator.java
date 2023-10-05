package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ProgLogComparator {

	public static void run(String fname1, String fname2) {
		BufferedReader br1, br2;
		BufferedWriter bw1, bw2;

		String fname3 = "diff1.dat";
		String fname4 = "diff2.dat";

		try {
			// Open the files
			Path pw = Paths.get(fname1);
			Charset cset = StandardCharsets.UTF_8;
			br1 = Files.newBufferedReader(pw, cset);
			pw = Paths.get(fname2);
			br2 = Files.newBufferedReader(pw, cset);
			pw = Paths.get(fname3);
			bw1 = Files.newBufferedWriter(pw, cset);
			pw = Paths.get(fname4);
			bw2 = Files.newBufferedWriter(pw, cset);

			bw1.write("This is a list of the unique QCells found in the first file and not found in the second:\n\n");
			bw2.write("This is a list of the unique QCells found in the second file and not found in the first:\n\n");

			List<SimplifiedQCell> cells1 = Util.getMotionCells(br1);
			List<SimplifiedQCell> cells2 = Util.getMotionCells(br2);
			List<SimplifiedQCell> dcells1 = Util.getDifference(cells1, cells2);
			List<SimplifiedQCell> dcells2 = Util.getDifference(cells2, cells1);
			Util.print(bw1, dcells1);
			Util.print(bw2, dcells2);

			br1.close();
			br2.close();
			bw1.close();
			bw2.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void run() {
		String fname1 = "ProgramOut.clean";
		String fname2 = "ProgramOut.fixed";
		run(fname1, fname2);

	}

	public static void run(String fname1) {
		BufferedReader br1;
		BufferedWriter bw1;

		String fname3 = "motioncells.dat";

		try {
			// Open the files
			Path pw = Paths.get(fname1);
			Charset cset = StandardCharsets.UTF_8;
			br1 = Files.newBufferedReader(pw, cset);
			pw = Paths.get(fname3);
			bw1 = Files.newBufferedWriter(pw, cset);

			List<SimplifiedQCell> cells1 = Util.getMotionCells(br1);
			Util.print(bw1, cells1);

			br1.close();
			bw1.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
