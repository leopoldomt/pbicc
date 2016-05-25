package icc.intent;

import java.util.ArrayList;

import icc.data.IntentFilter;

public class IntentForResolution {

	private String componentName;
	private String action;
	private ArrayList<String> categories;
	private IntentFilter.Data data;

	public IntentForResolution() {
		this.categories = new ArrayList<String>();
		//this.datas = new ArrayList<IntentFilter.Data>();
	}

	public IntentForResolution(String compName) {
		this();
		this.componentName = compName;
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

	public IntentFilter.Data getData() {
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

	public void setData(IntentFilter.Data data) {
		this.data = data;
	}

}
