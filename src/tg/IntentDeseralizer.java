package tg;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class IntentDeseralizer implements JsonDeserializer<Intent> {

	@Override
	public Intent deserialize(JsonElement element, Type clazz,
			JsonDeserializationContext context) throws JsonParseException {

		System.out.println(">>>> JAMPA <<<<");
		
		Intent it = new Intent();
		it.scope = element.getAsJsonObject().get("scope").getAsString();
		it.methodType = element.getAsJsonObject().get("methodType").getAsString();
		it.identifier = element.getAsJsonObject().get("identifier").getAsString();
		it.component = element.getAsJsonObject().get("component").getAsString();
		it.actions = getAttribute(element.getAsJsonObject().get("action").getAsString(), " | ");
		it.data = element.getAsJsonObject().get("data").getAsString();
		it.mimeType = element.getAsJsonObject().get("mimeType").getAsString();
		it.category = element.getAsJsonObject().get("category").getAsString();
		it.flags = element.getAsJsonObject().get("flags").getAsString();
		
		
		it.extras = getAttribute(element.getAsJsonObject().get("extras").getAsString(), ", ");

		return it;
	}

	private ArrayList<String> getAttribute(String attrString, String delimiter) {
		ArrayList<String> attrs = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(attrString, delimiter);
		
		while(st.hasMoreTokens())
			attrs.add(st.nextToken());
		
		return attrs;
	}
	
	private void setExtras(Intent it, String exts) {
		ArrayList<String> extras = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(exts, " , ");
	}

	private void setActions(Intent it, String actions) {
		ArrayList<String> acts = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(actions, " | ");
		while(st.hasMoreTokens())
			acts.add(st.nextToken());
		it.actions = acts;		
	}
	
	



}
