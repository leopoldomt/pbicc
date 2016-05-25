package icc.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ICCLinkFindingResults {

	public Map<String, String> literalStrings = new HashMap<String, String>();
	public Map<String, String> referencedStrings = new HashMap<String, String>();
	public Map<String, String> mapStrings = new HashMap<String, String>();

	
	public SymbolTable<VarInfo> varsST;
	public SymbolTable<IntentInfo> intentsST;
	public List<ICCLinkInfo<IntentInfo>> iccLinks;
	public IntentStats stats;

	public ICCLinkFindingResults() {
		this(true);
	}

	public ICCLinkFindingResults(boolean init) {
		if (init) {
			this.varsST = new SymbolTable<VarInfo>();
			this.intentsST = new SymbolTable<IntentInfo>();
			this.iccLinks = new ArrayList<ICCLinkInfo<IntentInfo>>();
			this.stats = new IntentStats();
		}
	}

	public void accessStats() {
		stats.iccLinks.value = iccLinks.size();
		stats.intentCount.value = intentsST.getMap().size() + getAnonymousIntentsCount();
		stats.explicitICCLinks.value = getExplicitLinksCount();
		stats.implicitICCLinks.value = getImplicitLinksCount();
		stats.explicitIntents.value = getExplicitIntentsCount();
		stats.implicitIntents.value = getImplicitIntentsCount();
	}
	
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int x = 1;
		int total = iccLinks.size();
		for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks) {
			sb.append(linkInfo.toJSON());
			if (x<total) {
				sb.append(",");
			}
			x++;
		}
		sb.append("]");
		return sb.toString();
	}
	

	private int getAnonymousIntentsCount() {
		int result = 0;

		for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks) {
			IntentInfo intentInfo = linkInfo.getTarget();

			// the intent is anonymous
			if (!intentsST.getMap().values().contains(intentInfo)) {
				result++;
			}
		}

		return result;
	}

	private int getExplicitLinksCount() {
		int result = 0;

		for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks) {
			IntentInfo intentInfo = linkInfo.getTarget();

			// the intent is anonymous
			if (intentInfo.isExplicit()) {
				result++;
			}
		}

		return result;
	}

	private int getImplicitLinksCount() {
		int result = 0;

		for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks) {
			IntentInfo intentInfo = linkInfo.getTarget();

			// the intent is anonymous
			if (!intentInfo.isExplicit()) {
				result++;
			}
		}

		return result;
	}

	private int getExplicitIntentsCount() {
		int result = 0;

		// all named intents
		for (Map.Entry<String, IntentInfo> entry : this.intentsST.getMap()
				.entrySet()) {
			if (entry.getValue().isExplicit()) {
				result++;
			}
		}

		// all anonymous intents
		for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks) {
			IntentInfo intentInfo = linkInfo.getTarget();

			if (!intentsST.getMap().values().contains(intentInfo)) {
				if (intentInfo.isExplicit()) {
					result++;
				}
			}
		}

		return result;
	}

	private int getImplicitIntentsCount() {
		int result = 0;

		// all named intents
		for (Map.Entry<String, IntentInfo> entry : this.intentsST.getMap()
				.entrySet()) {
			if (!entry.getValue().isExplicit()) {
				result++;
			}
		}

		// all anonymous intents
		for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks) {
			IntentInfo intentInfo = linkInfo.getTarget();

			if (!intentsST.getMap().values().contains(intentInfo)) {
				if (!intentInfo.isExplicit()) {
					result++;
				}
			}
		}

		return result;
	}
	
	public Map<String, String> propagate() {
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
						String tmp2 = findWithSuffixMatch(part);
						sb.append((tmp2!=null)?tmp2:part);
						sb.append(" ");
					}
					tmp = sb.toString();
				} else {
					// case 2: not a compound string...look for a string with the same suffix...
					tmp = findWithSuffixMatch(val);
					if (tmp == null) {
						//System.err.println("MISSED THIS CASE " + entry);
						continue;
					}
				}
			}
			referencedStrings.remove(entry);
			literalStrings.put(key, tmp);
		}
		return Collections.unmodifiableMap(literalStrings);
	}

	public String findWithSuffixMatch(String tmp) {
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
