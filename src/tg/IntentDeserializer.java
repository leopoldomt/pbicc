package tg;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class IntentDeserializer implements JsonDeserializer<IntentFromJson> {

	@Override
	public IntentFromJson deserialize(JsonElement element, Type clazz,
			JsonDeserializationContext context) throws JsonParseException {		
		IntentFromJson it = new IntentFromJson();
		it.scope = element.getAsJsonObject().get("scope").getAsString();
		it.identifier = element.getAsJsonObject().get("identifier").getAsString();
		it.component = element.getAsJsonObject().get("component").getAsString();
		it.actions = getAttribute(element.getAsJsonObject().get("action").getAsString(), " | ");
		it.data = element.getAsJsonObject().get("data").getAsString();
		it.mimeType = element.getAsJsonObject().get("mimeType").getAsString();
		it.categories = getAttribute(element.getAsJsonObject().get("category").getAsString(), " | ");
		it.flags = element.getAsJsonObject().get("flags").getAsString();
		it.extras = getAttribute(element.getAsJsonObject().get("extras").getAsString(), ", ");

		it.methodType = element.getAsJsonObject().get("methodType").getAsString();

		if(it.methodType.equals("startActivity") || it.methodType.equals("startActivityForResult")){
			it.categories.add(Intent.CATEGORY_DEFAULT);
		}

		return it;
	}

	private ArrayList<String> getAttribute(String attrString, String delimiter) {
		ArrayList<String> attrs = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(attrString, delimiter);

		String token;
		while(st.hasMoreTokens()){
			token = st.nextToken();
			if(!token.equals("-"))
				attrs.add(token);
		}

		return attrs;
	}
}
