package tg;

import icc.data.IntentFilter.Data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class IntentDeserializer implements JsonDeserializer<Intent> {

	@Override
	public Intent deserialize(JsonElement element, Type clazz,
			JsonDeserializationContext context) throws JsonParseException {		
		Intent it = new Intent();
		it.scope = element.getAsJsonObject().get("scope").getAsString();
		it.methodType = element.getAsJsonObject().get("methodType").getAsString();
		it.identifier = element.getAsJsonObject().get("identifier").getAsString();
		it.component = element.getAsJsonObject().get("component").getAsString();
		it.actions = getAttribute(element.getAsJsonObject().get("action").getAsString(), " | ");
		it.data = getData(element.getAsJsonObject().get("data").getAsString());
		it.mimeType = element.getAsJsonObject().get("mimeType").getAsString();
		it.categories = getAttribute(element.getAsJsonObject().get("category").getAsString(), " | ");
		it.flags = element.getAsJsonObject().get("flags").getAsString();
		it.extras = getAttribute(element.getAsJsonObject().get("extras").getAsString(), ", ");
		return it;
	}

	private Data getData(String data) {
		Data dt = new Data();
		int index = data.lastIndexOf("://");		
		if(index > 0){
			//<schema>://
			dt.scheme = data.substring(0, index);
			int i = data.indexOf(":", index+2);
			if(i>0){
				// <schema>://<host>:<port>
				dt.host = data.substring(index+3,i);
				int j = data.indexOf("/", i+1);
				if(j > 0) {
					// <schema>://<host>:<port>/<path...>
					dt.port = data.substring(i+1, j);
					dt.path = data.substring(j);

				} else {
					// <schema>://<host>:<port>
					dt.port = data.substring(i+1);
				}
			} else {
				// <schema>://<host>
				dt.host = data.substring(index+3);
			}
		}	
		return dt;
	}

	private ArrayList<String> getAttribute(String attrString, String delimiter) {
		ArrayList<String> attrs = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(attrString, delimiter);

		while(st.hasMoreTokens())
			attrs.add(st.nextToken());

		return attrs;
	}
}
