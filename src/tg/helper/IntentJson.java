package tg.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import tg.parse.IntentForResolution;
import tg.parse.IntentFromJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


public class IntentJson {

	public static IntentFromJson[] readFromJson (String path) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(IntentFromJson.class, new FirstIntentDeserializer());
		IntentFromJson[] its = gsonB.create().fromJson(new FileReader(path), IntentFromJson[].class);
		return its;
	}
	
	public static IntentForResolution[] readForResolution(String path) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(IntentForResolution.class, new SecondIntentDeserializer());
		IntentForResolution[] ifrs = gsonB.create().fromJson(new FileReader(path), IntentForResolution[].class);
		return ifrs;
	}
	
	public static void write(ArrayList<IntentForResolution> ifrs, String fileName) throws IOException {
		Gson gson = new GsonBuilder()
		.registerTypeAdapter(IntentForResolution.class, new IntentSerializer())
		.setPrettyPrinting()
		.create();
		
		JsonArray list = new JsonArray();
		
		for(int i=0; i<ifrs.size(); i++)
			list.add(gson.toJsonTree(ifrs.get(i)));
		
		String json =  gson.toJson(list);
		//System.out.println(json);
		
		File file = new File(fileName);
		FileWriter writer = new FileWriter(file);
		
		writer.write(json);
		writer.close();
		
		
	}
	
	public static void write(IntentForResolution ifr, String fileName) throws IOException {
		Gson gson = new GsonBuilder()
		.registerTypeAdapter(IntentForResolution.class, new IntentSerializer())
		.setPrettyPrinting()
		.create();
		String json =  gson.toJson(ifr);
		System.out.println(json);
		
		File file = new File(Files.ForResolution.path+fileName);
		FileWriter writer = new FileWriter(file);
		
		writer.write(json);
		writer.close();
		
		
	}
	

}
