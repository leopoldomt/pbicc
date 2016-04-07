package experiments.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FileManagerTest {

	private static final String ROOT_DIR = "tests/experiments/utils";
	private FileManager manager;

	@Before
	public void setup() {
		this.manager = new FileManager(ROOT_DIR);
	}

	@Test
	public void shouldFailFastOnInvalidPath() {
		String[] invalidPaths = { null, "asdasdads", ROOT_DIR + "/dummy-1.json" };
		for (String invalidPath : invalidPaths) {
			try {
				new FileManager(invalidPath);
				fail(String.format("Should not be allowed: \"%s\"", invalidPath));
			} catch (UnknownPathDirectoryException e) {
				// Do nothing
			} catch (Exception e) {
				fail("Should have thrown UnknownPathDirectory");
			}
		}
	}

	@Test
	public void shouldListAllFileNames() {
		// Order and files are based on ROOT_DIR
		List<String> expectedChildren = Arrays.asList("FileManagerTest.java", "dummy-1.json");
		ensureSameList(manager.listAllFileNames(), expectedChildren);
	}

	@Test
	public void shouldListJsonFileNames() {
		// Order and files are based on ROOT_DIR
		List<String> expectedChildren = Arrays.asList("dummy-1.json");
		ensureSameList(manager.listJsonFileNames(), expectedChildren);
	}

	@Test
	public void shouldSupportRelativePaths() {
		// Order and files are based on ROOT_DIR
		List<String> expectedChildren = Arrays.asList("tests/experiments/utils/dummy-1.json");
		ensureSameList(manager.listJsonFilesWithRelatives(), expectedChildren);

		String extraSeparator = File.separator;
		manager = new FileManager(ROOT_DIR + extraSeparator + extraSeparator);
		ensureSameList(manager.listJsonFilesWithRelatives(), expectedChildren);
	}

	private <T> void ensureSameList(List<T> listA, List<T> listB) {
		assertEquals(listB.size(), listA.size());
		for (int i = 0; i < listB.size(); i++) {
			assertEquals(listB.get(i), listA.get(i));
		}
	}
}
