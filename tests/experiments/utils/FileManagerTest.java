package experiments.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
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
		// IMPORTAN: ORDER IS OS DEPENDENT!
		//
		// Files are based on ROOT_DIR
		List<String> expectedChildren = Arrays.asList("FileManagerTest.java", "dummy-1.json");
		ensureSameElements(manager.listAllFileNames(), expectedChildren);
	}

	@Test
	public void shouldListJsonFileNames() {
		// files are based on ROOT_DIR
		List<String> expectedChildren = Arrays.asList("dummy-1.json");
		ensureSameElements(manager.listJsonFileNames(), expectedChildren);
	}

	@Test
	public void shouldSupportRelativePaths() {
		File f = new File("tests/experiments/utils/dummy-1.json");
	
		// Because path separators are OS dependent, the File object must be
		// used. Other option would be concatenate several File.separator
		List<String> expectedChildren = Arrays.asList(f.getPath());
		ensureSameElements(manager.listJsonFilesWithRelatives(), expectedChildren);

		String extraSeparator = File.separator;
		manager = new FileManager(ROOT_DIR + extraSeparator + extraSeparator);
		ensureSameElements(manager.listJsonFilesWithRelatives(), expectedChildren);
	}

	private <T> void ensureSameElements(Collection<T> bagA, Collection<T> bagB) {
		assertEquals(bagB.size(), bagA.size());
		assertTrue(bagA.containsAll(bagB));
		assertTrue(bagB.containsAll(bagA));
	}
}
