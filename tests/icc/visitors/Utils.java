package icc.visitors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class Utils {

	// TODO: This could be really reused in the main function.

	/**
	 * Generates a {@link CompilationUnit} based on the given file and its
	 * relative parent path.
	 * <p>
	 * Example: <blockquote>
	 * <code>CompilationUnit cu = generateCompilationUnit("Apg.java", "test-data/k9/src");</code>
	 * </blockquote>
	 * <p>
	 * A Runtime Exception will halt the execution if:
	 * <ol>
	 * <li>file is not well-formed (ie can't be correctly parsed) or</li>
	 * <li>file doesn't exists due to unreachable path</li>
	 * </ol>
	 * 
	 * @param fileName
	 *            The file name
	 * @param parentDir
	 *            Relative path to the source code
	 * @return A parsed compilation unit.
	 * 
	 */
	public static CompilationUnit generateCompilationUnit(String fileName, String parentDir) {
		CompilationUnit compilationUnit = null;
		try {
			FileInputStream toBeParsed = new FileInputStream(new File(parentDir, fileName));
			compilationUnit = JavaParser.parse(toBeParsed);

		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("File \"%s/%s\" couldn't be found!", parentDir, fileName));
		} catch (ParseException e) {
			throw new RuntimeException(String.format("File named \"%s\" is not well-formed!", fileName));
		}
		return compilationUnit;
	}
}
