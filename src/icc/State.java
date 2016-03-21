package icc;

import icc.data.ICCLinkFindingResults;
import icc.parsing.AndroidManifestParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

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
	private Map<String/* filename */, ICCLinkFindingResults> resultsMap = new HashMap<String, ICCLinkFindingResults>();
	private AndroidManifestParser manifestParser;

	// TODO consider removing this - leopoldo
	/*
	private Map<String, String> literalStrings = new HashMap<String, String>();
	private Map<String, String> referencedStrings = new HashMap<String, String>();
	
	public Map<String, String> literalStrings() {
		return literalStrings;
	}

	public Map<String, String> referencedStrings() {
		return referencedStrings;
	}
	/**/
	private Map<String, String> mapStrings = new HashMap<String, String>();

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
	
	
	public Map<String, String> propagate(Map<String,String> literalStrings, Map<String,String> referencedStrings) {
		/*
		 * Please, test more thoroughly case like this:
		 *   A { static String x1 = "Hello"; }
		 *   B { static String x2 = A.x1; } 
		 *   C { static String x3 = B.x2; }
		 *   
		 * Although the example I show does not show problems,
		 * in general, this code should **not** handle such 
		 * cases as the for loop assumes one specific ordering 
		 * of string constants to resolve.
		 * 
		 * To address this problem, I suggest to use a working
		 * list as opposed to this for loop.  -Marcelo 
		 */
		
		for (Map.Entry<String, String> entry : referencedStrings.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			// look for this value elsewhere...
			String tmp = literalStrings.get(val);
			if (tmp == null) { // We could not find an exact match!  
				// Applying some heuristic...
				String[] arr = val.split(" ");
				if (arr.length > 1) {
					// case 1: is this a compound string?
					StringBuffer sb = new StringBuffer();
					for (String part : arr) {
						String tmp2 = findWithSuffixMatch(part,literalStrings);
						sb.append((tmp2!=null)?tmp2:part);
						sb.append(" ");
					}
					tmp = sb.toString();
				} else {
					// case 2: not a compound string...look for a string with the same suffix...
					tmp = findWithSuffixMatch(val, literalStrings);
					if (tmp == null) {
						System.err.println("MISSED THIS CASE " + entry);
						continue;
					}
				}
			}
			referencedStrings.remove(entry);
			literalStrings.put(key, tmp);
		}
		mapStrings = Collections.unmodifiableMap(literalStrings);
		return mapStrings;
	}

	public String findWithSuffixMatch(String tmp, Map<String,String> literalStrings) {
		List<String> res = new ArrayList<String>();
		for (String s: literalStrings.keySet()) {
			if (s.endsWith(tmp)) {
				res.add(literalStrings.get(s));
				break;
			}
		}
		// null if could not find substring or it is ambiguous
		return (res.size() == 1) ? res.get(0) : null;
	}

}
