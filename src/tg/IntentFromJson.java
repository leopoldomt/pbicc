package tg;

import java.util.ArrayList;

public class IntentFromJson {

	String scope;
	String methodType;
	String identifier;
	String component;
	ArrayList<String> actions;
	String data;
	String mimeType;
	ArrayList<String> categories;
	String flags;
	ArrayList<String> extras;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("#scope: %s\n", scope));
		sb.append(String.format("#methodType: %s\n", methodType));
		sb.append(String.format("#identifier: %s\n", identifier));
		sb.append(String.format("#component: %s\n", component));
		
		for(String a : actions )
			sb.append(String.format("#action: %s\n", a));
		
		sb.append(String.format("#data: %s\n", data));
		sb.append(String.format("#mimeType: %s\n", mimeType));
		
		for(String c : categories)
			sb.append(String.format("#category: %s\n", c));
		
		sb.append(String.format("#flags: %s\n", flags));
		
		sb.append(String.format("#extras: "));
		for(String ext : extras)
			sb.append(String.format("%s, ", ext));
		sb.append("\n");
		
		return sb.toString();
	}
	

	public String getScope() {
		return scope;
	}

	public String getMethodType() {
		return methodType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getComponent() {
		return component;
	}

	public String getAction() {
		return actions.get(0);
	}

	public String getData() {
		return data;
	}

	public String getMimeType() {
		return mimeType;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public String getFlags() {
		return flags;
	}

	public ArrayList<String> getExtras() {
		return extras;
	}
	
}
