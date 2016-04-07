package experiments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import experiments.model.Entry;
import experiments.model.PrecisionAndRecallAnalysis;
import experiments.utils.FileManager;
import experiments.utils.JsonUtils;

public class Main {

	static final String GROUND_TRUTH_DIR = "test-data/json/ground-truth";
	static final String RESULT_DIR = "test-data/json/results";

	public static void main(String[] args) {
		FileManager manager = new FileManager(GROUND_TRUTH_DIR);
		List<String> groundTruthFilePaths = manager.listJsonFilesWithRelatives();
		List<String> fileNames = manager.listJsonFileNames();

		manager = new FileManager(RESULT_DIR);
		List<String> resultFilePaths = manager.listJsonFilesWithRelatives();

		Report fullResults = new Report();
		for (int i = 0; i < groundTruthFilePaths.size(); i++) {
			String[] attributes = { "action", "component", "data", "mimeType", "extras" };

			List<Entry> golden = null;
			List<Entry> output = null;
			try {
				golden = JsonUtils.load(groundTruthFilePaths.get(i));
				output = JsonUtils.load(resultFilePaths.get(i));
			} catch (Exception e) {
				System.err.printf("Bad JSON. Skipping %s...\n", fileNames.get(i));
				continue;
			}

			for (String attribute : attributes) {
				String result = PrecisionAndRecallAnalysis.run(golden, output, attribute);
				fullResults.writeCells(fileNames.get(i), attribute, result);
			}
		}
		fullResults.showOnConsole();
	}

	static class Report {

		static final String[] HEADER_COLS = { "file", "attribute", "precision-recall" };
		static final String LINE_SEP = System.getProperty("line.separator");
		static final String COL_SEP = ";";

		private StringBuilder builder;

		public Report() {
			this.builder = new StringBuilder();
			buildReportHeader();
		}

		public void showOnConsole() {
			System.out.println(this.builder.toString());
		}

		public void writeCells(String... values) {
			for (int i = 0; i < values.length - 1; i++) {
				builder.append(values[i]).append(COL_SEP);
			}
			builder.append(values[values.length - 1]);
			builder.append(LINE_SEP);
		}

		private void buildReportHeader() {
			for (int i = 0; i < HEADER_COLS.length - 1; i++) {
				builder.append(HEADER_COLS[i]).append(COL_SEP);
			}
			builder.append(HEADER_COLS[HEADER_COLS.length - 1]);
			builder.append(LINE_SEP);
		}
	}
}
