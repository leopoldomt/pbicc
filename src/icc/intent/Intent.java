package icc.intent;

import java.util.ArrayList;

import icc.data.IntentFilter;

public class Intent {

	private String componentName;
	private String action;
	private ArrayList<String> categories;
	private IntentFilter.Data data;

	public Intent() {
		this.categories = new ArrayList<String>();
		//this.datas = new ArrayList<IntentFilter.Data>();
	}

	public Intent(String compName) {
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

	public void addCategory(String category) {
		categories.add(category);
	}

	public void setData(IntentFilter.Data data) {
		this.data = data;
	}

}
