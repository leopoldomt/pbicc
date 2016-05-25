package icc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import icc.data.ICCLinkFindingResults;
import icc.parsing.AndroidManifestParser;

/**
 * Singleton implementation of state
 *
 * @author damorim
 *
 */
public class State {

	/**
	 * elements of state
	 */
	private Map<String/* classname */, PutsAndGets> pgMap = new HashMap<String, PutsAndGets>();
	private Map<String/* filename */, CompilationUnit> astMap = new HashMap<String, CompilationUnit>();

	// TODO remove this map, since it should no longer be necessary
	private Map<String/* filename */, ICCLinkFindingResults> resultsMap = new HashMap<String, ICCLinkFindingResults>();

	private ICCLinkFindingResults iccResults = new ICCLinkFindingResults();
	private AndroidManifestParser manifestParser;

	/**
	 * singleton implementation
	 */
	private static State instance;

	private State() {
	} // private constructor

	public static State getInstance() {
		if (instance == null) {
			instance = new State(); // only call to constructor and called at
									// most once
		}
		return instance;
	}

	/**
	 * getter functions
	 *
	 */
	public Map<String/* classname */, PutsAndGets> pgMap() {
		return pgMap;
	}

	public Map<String, ICCLinkFindingResults> resultsMap() {
		return resultsMap;
	}

	public ICCLinkFindingResults iccResults() {
		return iccResults;
	}

	public void setICCResults(ICCLinkFindingResults results) {
		this.iccResults = results;
	}

	public Map<String, CompilationUnit> astMap() {
		return astMap;
	}

	public Set<String> getFiles() {
		return Collections.unmodifiableSet(pgMap.keySet());
	}

	public AndroidManifestParser getManifestParser() {
		return manifestParser;
	}

	public void setManifestParser(AndroidManifestParser manifestParser) {
		this.manifestParser = manifestParser;
	}

}
