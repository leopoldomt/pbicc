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

}
