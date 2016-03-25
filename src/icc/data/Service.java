package icc.data;

public class Service extends Component {

	
	public boolean isolatedProcess = false;
	
	public Service() {
		super();
	}

	@Override
	public String toStringExclusiveAttributes() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("IsolatedProcess: %s\n", isolatedProcess));
		return sb.toString();
	}

}

