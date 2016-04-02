package icc.data;

import java.util.HashMap;
import java.util.Map;

public class IntentInfo {
	public final String NOT_SET = "-";

	public String identifier = NOT_SET;
	public FieldList category = new FieldList();
	public FieldList className = new FieldList();
	public FieldList packageName = new FieldList();
	public FieldList type = new FieldList();
	public FieldList action = new FieldList();
	public FieldList data = new FieldList();
	public Map<String, String> extras;

	public IntentInfo target = null;

	public IntentInfo() {
		extras = new HashMap<String, String>();
	}

	// TODO: fix implementation when we have more than one possibility for a
	// field
	public String toCSV() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s,", identifier));
		builder.append(String.format("%s,", category));
		String component = getComponent();
		builder.append(String.format("%s,", component));
		builder.append(String.format("%s,", type));
		builder.append(String.format("%s,", action));
		builder.append(String.format("%s,", data));
		for (Map.Entry<String, String> entry : extras.entrySet()) {
			builder.append(String.format("%s: %s; ", entry.getKey(), entry.getValue()));
		}
		return builder.toString();
	}

	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("\n \"identifier\" : \"%s\"", identifier));
		builder.append(String.format("\n \"component\" : \"%s\"", getComponent()));
		builder.append(String.format("\n \"action\" : \"%s\"", action));
		builder.append(String.format("\n \"data\" : \"%s\"", data));
		builder.append(String.format("\n \"mimeType\" : \"%s\"", type));
		String extrasJson;
		if (extras.size() > 0) {
			extrasJson = String.join(", ", extras.keySet());
		} else {
			extrasJson = "-";
		}
		builder.append(String.format("\n \"extras\" : \"%s\"", extrasJson));
		return builder.toString();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("Identifier: %s\n", identifier));
		builder.append(String.format("Category: %s\n", category));
		String component = getComponent();
		builder.append(String.format("Component: %s\n", component));
		builder.append(String.format("Type: %s\n", type));
		builder.append(String.format("Action: %s\n", action));
		builder.append(String.format("Data: %s\n", data));
		builder.append("Extras: \n");
		for (Map.Entry<String, String> entry : extras.entrySet()) {
			builder.append(String.format("\t%s: %s\n", entry.getKey(), entry.getValue()));
		}
		return builder.toString();
	}

	public String getComponent() {
		String component = NOT_SET;
		if (!className.isEmpty() && !packageName.isEmpty()) {
			int numC = className.size();
			int numP = packageName.size();
			if (numC == numP) {
				component = "";
				for (int i = 0; i < numC; i++) {
					if (className.get(i).startsWith(packageName.get(i))) {
						component += String.format("%s", className.get(i));
					} else {
						component += String.format("%s.%s", packageName.get(i), className.get(i));
					}
				}
			} 
			else {
				throw new RuntimeException("Mismatch between size of packageName and className elements");
			}
		} 
		else if (!className.isEmpty()) {
			component = "";
			for (String c : className) {
				component += c + "; ";
			}
		} 
		else if (!packageName.equals(NOT_SET)) {
			component = "";
			for (String c : packageName) {
				component += String.format("%s.?", c) + "; ";
			}
		}

		if (!component.equals(NOT_SET)) {
			component = component.replace("\"", "");
		}

		return component;
	}

	public boolean isExplicit() {
		return !className.isEmpty() || !packageName.isEmpty();
	}

	public boolean isChooser() {
		return this.target != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((NOT_SET == null) ? 0 : NOT_SET.hashCode());
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((extras == null) ? 0 : extras.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntentInfo other = (IntentInfo) obj;
		if (NOT_SET == null) {
			if (other.NOT_SET != null)
				return false;
		} else if (!NOT_SET.equals(other.NOT_SET))
			return false;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (extras == null) {
			if (other.extras != null)
				return false;
		} else if (!extras.equals(other.extras))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String diff(IntentInfo i) {
		StringBuilder builder = new StringBuilder();
		if (this.equals(i)) {
			builder.append("No differences");
		} 
		else {
			if (!this.getComponent().equals(i.getComponent())) {
				builder.append(String.format("Component diff: %s, expected %s\n", this.getComponent(), i.getComponent()));
			}
			if (!this.category.equals(i.category)) {
				builder.append(String.format("Category diff: %s, expected %s\n", this.category, i.category));
			}
			if (!this.type.equals(i.type)) {
				builder.append(String.format("MimeType diff: %s, expected %s\n", this.type, i.type));
			}
			if (!this.action.equals(i.action)) {
				builder.append(String.format("Action diff: %s, expected %s\n", this.action, i.action));
			}
			if (!this.data.equals(i.data)) {
				builder.append(String.format("Data diff: %s, expected %s\n", this.data, i.data));
			}
			for (Map.Entry<String, String> entry : extras.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();
				String iValue = i.extras.get(k);
				if (iValue == null) {
					builder.append(String.format("Key %s is unexpected\n", k));
				} else {
					if (!v.equals(iValue)) {
						builder.append(String.format("Key %s diff:%s, expected %s\n", k, v, iValue));
					}
				}
			}
		}
		return builder.toString();
	}
}