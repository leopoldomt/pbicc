package experiments.model;

/**
 * This is the data structure to represent the output of the analysis.  
 * 
 * TODO: Consider building objects of this kind to build JSON files.   
 * 
 * @author damorim
 *
 */

public class Entry {
	String scope;
	String methodType;
	String identifier;
	String component;
	String action;
	String data;
	String mimeType;
	String extras;
	
	public Entry(String scope, String methodType) {
		super();
		this.scope = scope;
		this.methodType = methodType;
	}
	
	// consider using reflection for this...
	public String select(String column) {
		String res;
		switch (column) {
		case "scope":
			res = scope;
			break;
		case "methodType":
			res = methodType;
			break;
		case "identifier":
			res = identifier;
			break;
		case "component":
			res = component;
			break;
		case "action":
			res = action;
			break;
		case "data":
			res = data;
			break;
		case "mimeType":
			res = mimeType;
			break;
		case "extras":
			res = extras;
			break;
		default:
			throw new RuntimeException(String.format("Attribute %s not found!  Please check.", column));
		}
		return res;
	}
}