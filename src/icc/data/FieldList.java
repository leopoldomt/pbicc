package icc.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FieldList extends ArrayList<String> {

	private static final long serialVersionUID = 2949554359338631287L;

	@Override
	public String toString() {
		Iterator<String> it = iterator();
		if (!it.hasNext())
			return "-";

		StringBuilder sb = new StringBuilder();
		for (;;) {
			String e = it.next();
			sb.append(e);
			if (!it.hasNext())
				return sb.toString();
			sb.append(';').append(' ');
		}
	}
	
	public String sanitize(String s) {
		//TODO consider doing more stuff here, if-need-be
		return s.replace("\"", "\\\"");
	}

	public String toJSON() {
		Iterator<String> it = iterator();
		if (!it.hasNext()) {
			return "\"-\"";
		}
		else {
			StringBuilder sb = new StringBuilder();
			boolean moreThanOne = (this.size() > 1); 
			if (moreThanOne) {
				sb.append("[");				
			}
			for (;;) {
				String e = it.next();
				sb.append("\"" + e + "\"");
				if (!it.hasNext())
					break;
				sb.append(',').append(' ');
			}
			if (moreThanOne) {
				sb.append("]");				
			}
			return sb.toString();
		}
	}

}
