package tg.parse;

import java.util.ArrayList;

import icc.data.IntentFilter;

public class IntentForResolution {

	private String hash;
	private String parentId;
	private String methodType;
	private String componentName;
	private String action;
	private ArrayList<String> categories;
	private DataURI data;

	public IntentForResolution() {
		this.categories = new ArrayList<String>();
		//this.datas = new ArrayList<IntentFilter.Data>();
	}
	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setParentId(String pid) {
		parentId = pid;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setComponentName(String name) {
		componentName = name;
	}
	
	public String getComponentName() {
		return componentName;
	}

	public String getAction() {
		return action;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public DataURI getData() {
		return data;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setCategories(ArrayList<String> ctgs) {
		categories = ctgs;
	}
	public void addCategory(String category) {
		categories.add(category);
	}

	public void setData(DataURI data) {
		this.data = data;
	}
	
	public String getMethodType() {
		return methodType;
	}
	
	public void setMethodType(String method) {
		methodType = method;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("#hash: %s\n", this.hash));
		sb.append(String.format("#parentId: %s\n", this.parentId));
		sb.append(String.format("#methodType: %s\n", this.methodType));
		sb.append(String.format("#componentName: %s\n", this.componentName));
		sb.append(String.format("#action: %s\n", this.action));
		sb.append(String.format("#data: %s\n", this.data.toString()));
		sb.append(String.format("#mimeType: %s\n", this.data.getType()));
		sb.append(String.format("#categories: %s\n", this.categories.toString()));
		
		
		return sb.toString();
	}

	public String toStringLine() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("_parentId_%s_", this.parentId));
		sb.append(String.format("_methodType_%s_", this.methodType));
		sb.append(String.format("_componentName_%s_", this.componentName));
		sb.append(String.format("_action_%s_", this.action));
		sb.append(String.format("_data_%s_", this.data.toString()));
		sb.append(String.format("_mimeType_%s_", this.data.getType()));
		sb.append(String.format("_categories_%s_", this.categories.toString()));
		return sb.toString();
	}

}
