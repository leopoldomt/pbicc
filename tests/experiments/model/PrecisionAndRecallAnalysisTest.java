package experiments.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import experiments.utils.FileManager;
import experiments.utils.JsonUtils;

public class PrecisionAndRecallAnalysisTest {

	@Test
	public void shouldBeConsistentWithSameParameters() {
		FileManager manager = new FileManager("tests/experiments/model");
		List<String> inputs = manager.listJsonFilesWithRelatives();
		for (String input : inputs) {
			try {
				List<Entry> entries = JsonUtils.load(input);
				String result = PrecisionAndRecallAnalysis.run(entries, entries, "component");
				assertEquals("Expected result should be \"(1.00, 1.00)\"", "(1.00, 1.00)", result);

			} catch (IOException e) {
				fail("Should not have failed: " + e.getLocalizedMessage());
			}
		}
	}

	@Test
	public void shouldBeZeroWithUnrelatedSubjects() {
		List<Entry> subject1 = null;
		List<Entry> subject2 = null;
		try {
			subject1 = JsonUtils.load("test-data/json/results/abstract-art.json");
			subject2 = JsonUtils.load("test-data/json/results/adblockplus.json");

		} catch (IOException e) {
			fail("Should not have failed: " + e.getLocalizedMessage());
		}
		String result = PrecisionAndRecallAnalysis.run(subject1, subject2, "component");
		assertEquals("Expected result should be \"(0.00, 0.00)\"", "(0.00, 0.00)", result);

		result = PrecisionAndRecallAnalysis.run(subject2, subject1, "component");
		assertEquals("Expected result should be \"(0.00, 0.00)\"", "(0.00, 0.00)", result);

	}
}
