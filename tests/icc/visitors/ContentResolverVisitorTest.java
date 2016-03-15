package icc.visitors;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;

import icc.data.ICCLinkFindingResults;

public class ContentResolverVisitorTest {

	private static final CompilationUnit compilationUnit = Utils.generateCompilationUnit("Apg.java",
			"test-data/k9/src");

	@Test
	public void test() {
		ICCLinkFindingResults r = new ICCLinkFindingResults();
		ContentResolverVisitor visitor = new ContentResolverVisitor(r);
		visitor.visit(compilationUnit, null);
		fail("Not yet implemented");
	}

}
