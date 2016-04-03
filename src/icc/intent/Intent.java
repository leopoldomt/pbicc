package icc.intent;

import java.util.ArrayList;

public class Intent {

	private String componentName;
	private String action;
	private ArrayList<String> categories;
	private ArrayList<String> datas;

	public Intent() {
		this.categories = new ArrayList<String>();
		this.datas = new ArrayList<String>();
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

	public ArrayList<String> getDatas() {
		return datas;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void addCategory(String category) {
		categories.add(category);
	}

	public void addData(String data) {
		datas.add(data);
	}

}
