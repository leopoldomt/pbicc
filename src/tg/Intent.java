package tg;

import java.util.ArrayList;

public class Intent {

	String scope;
	String methodType;
	String identifier;
	String component;
	ArrayList<String> actions;
	String data;
	String mimeType;
	String category;
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
		sb.append(String.format("#category: %s\n", category));
		sb.append(String.format("#flags: %s\n", flags));
		
		sb.append(String.format("#extras: "));
		for(String ext : extras)
			sb.append(String.format("%s, ", ext));
		sb.append("\n");
		
		return sb.toString();
	}

}
