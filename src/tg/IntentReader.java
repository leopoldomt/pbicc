package tg;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


public class IntentReader {

	public static Intent[] fromJsonFile (String path) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(Intent.class, new IntentDeserializer());
		Intent[] its = gsonB.create().fromJson(new FileReader(path), Intent[].class);
		return its;
	}

}
