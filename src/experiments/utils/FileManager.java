package experiments.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FileManager {

	private File currentDir;

	public FileManager(String rootDir) {
		if (rootDir == null)
			throw new UnknownPathDirectoryException("Null path");

		File currentDir = new File(rootDir);
		if (!currentDir.exists() || !currentDir.isDirectory()) {
			throw new UnknownPathDirectoryException("Path does not exist or it is not a directory");
		}
		this.currentDir = currentDir;
	}

	/**
	 * Lists all children node names from the current directory.
	 * 
	 * @return A list of children node names
	 */
	public List<String> listAllFileNames() {
		return Arrays.asList(currentDir.list());
	}

	/**
	 * Lists all JSON file names from the current directory.
	 * 
	 * @return A list of JSON file names
	 */
	public List<String> listJsonFileNames() {
		return Arrays.asList(currentDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return Pattern.matches("(.)+\\.json", name);
			}
		}));
	}

	/**
	 * Similar to {@link #listJsonFileNames()} but names are prefixed with the
	 * root directory from this instance.
	 * 
	 * @return A list of JSON file names with relative paths.
	 */
	public List<String> listJsonFilesWithRelatives() {
		List<String> jsonFiles = this.listJsonFileNames();
		for (int i = 0; i < jsonFiles.size(); i++) {
			String element = this.currentDir.getPath() + File.separator + jsonFiles.get(i);
			jsonFiles.set(i, element);
		}
		return jsonFiles;
	}

}
