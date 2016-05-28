package tg.resolution;

import java.util.ArrayList;

import tg.DataURI;
import icc.data.IntentFilter;

public class IntentForResolution {

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

}
