package tg;

import java.util.ArrayList;

public class IntentFromJson {

	public String scope;
	public String methodType;
	public String identifier;
	public ArrayList<String> components;
	public ArrayList<String> actions;
	public ArrayList<String> datas;
	public ArrayList<String> mimeTypes;
	public ArrayList<String> categories;
	public String flags;
	public ArrayList<String> extras;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("#scope: %s\n", scope));
		sb.append(String.format("#methodType: %s\n", methodType));
		sb.append(String.format("#identifier: %s\n", identifier));
		sb.append(String.format("#component: %s\n", components));
		
		for(String a : actions )
			sb.append(String.format("#action: %s\n", a));
		
		for(String d : datas)
			sb.append(String.format("#data: %s\n", d));
		for(String m : mimeTypes)
			sb.append(String.format("#mimeType: %s\n", m));
		
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

	public ArrayList<String> getComponents() {
		return components;
	}

	public String getAction() {
		return actions.get(0);
	}

	public ArrayList<String> getDatas() {
		return datas;
	}

	public ArrayList<String> getMimeTypes() {
		return mimeTypes;
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
