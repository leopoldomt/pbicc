package experiments.precisionrecall;

import java.util.List;

import experiments.utils.FileManager;

public class Main {

	public static void main(String[] args) {
		FileManager manager = new FileManager("test-data/ground-truth");

		List<String> groundTruthOrderedPaths = manager.listJsonFilesWithRelatives();

		// FIXME: point to tool output directory
		// List<String> resultsOrderedPaths = new ArrayList<String>();

	}

}
