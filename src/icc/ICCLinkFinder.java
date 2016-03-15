package icc;

import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import icc.data.ICCLinkFindingResults;
import icc.visitors.ActivityVisitor;
import icc.visitors.ContentResolverVisitor;
import icc.visitors.ScopeAwareVisitor;
import icc.visitors.ServiceVisitor;
import icc.visitors.StaticBroadcastVisitor;
import icc.visitors.SymbolTableVisitor;

/**
 * This entity is the main entry-point for the ICC analysis. It is responsible
 * for detecting ICC links according to a given compilation unit and a set of
 * visitors.
 */
public class ICCLinkFinder {
	/**
	 * Finds inter-component communication links for a compilation unit.
	 *
	 * @param cu
	 *            The given compilation unit
	 * @return Analysis results represented as a {@link ICCLinkFindingResults}
	 */
	public static ICCLinkFindingResults findICCLinks(CompilationUnit cu) {
		ICCLinkFindingResults results = new ICCLinkFindingResults();

		Set<ScopeAwareVisitor> visitors = new HashSet<>();
		visitors.add(new SymbolTableVisitor(results));
		visitors.add(new ActivityVisitor(results));
		visitors.add(new ServiceVisitor(results));
		visitors.add(new StaticBroadcastVisitor(results));
		visitors.add(new ContentResolverVisitor(results));

		for (ScopeAwareVisitor visitor : visitors) {
			visitor.visit(cu, null);
		}
		results.accessStats();

		return results;
	}
}
