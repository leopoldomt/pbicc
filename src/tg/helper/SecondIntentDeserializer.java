package tg.helper;

import java.lang.reflect.Type;

import tg.parse.DataURI;
import tg.parse.IntentForResolution;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SecondIntentDeserializer implements
		JsonDeserializer<IntentForResolution> {

	@Override
	public IntentForResolution deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		IntentForResolution ifr = new IntentForResolution();
		
		String parentId = json.getAsJsonObject().get("parentId").getAsString();
		String action = json.getAsJsonObject().get("action").getAsString();
		String methodType = json.getAsJsonObject().get("methodType").getAsString();
		String componentName = json.getAsJsonObject().get("componentName").getAsString();
		String mimeType = json.getAsJsonObject().get("mimetype").getAsString();
		String sData = json.getAsJsonObject().get("data").getAsString();
		JsonArray sCategories = json.getAsJsonObject().get("categories").getAsJsonArray(); 
		
		ifr.setParentId(parentId);
		ifr.setAction(action.isEmpty()?null:action);
		ifr.setMethodType(methodType);
		ifr.setComponentName(componentName.isEmpty()?null:componentName);
		
		
		DataURI data = new DataURI(sData);
		data.setType(mimeType);
		ifr.setData(data);
		
		for(JsonElement e : sCategories){
			ifr.addCategory(e.getAsString());
		}
		
		return ifr;
	}

}
