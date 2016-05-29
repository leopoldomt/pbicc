package tg.helper;

import java.lang.reflect.Type;

import tg.parse.IntentForResolution;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class IntentSerializer implements JsonSerializer<IntentForResolution> {

	@Override
	public JsonElement serialize(IntentForResolution ifr, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.addProperty("parentId", ifr.getParentId());
		result.addProperty("methodType", ifr.getMethodType());
		result.addProperty("componentName", null==ifr.getComponentName()?"":ifr.getComponentName());
		result.addProperty("action", null==ifr.getAction()?"":ifr.getAction());
		result.addProperty("data", ifr.getData().toString());
		result.addProperty("mimetype", null==ifr.getData().getType()?"":ifr.getData().getType());
		
		JsonArray categories = new JsonArray();
		for(int i=0; i<ifr.getCategories().size(); i++){
			categories.add(ifr.getCategories().get(i));
		}
		
		result.add("categories", categories);
		return result;
	}

}
