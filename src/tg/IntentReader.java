package tg;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


public class IntentReader {
	String path;
	public IntentReader (String path) {
		this.path = path;

			try {
				readPath();
			} catch (JsonSyntaxException e) {
				System.out.println(e.getMessage());
			} catch (JsonIOException e) {
				System.out.println(e.getMessage());
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			}
	}


	private void readPath () throws JsonSyntaxException, JsonIOException, FileNotFoundException {
//		Gson gson = new Gson();
		
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(Intent.class, new IntentDeseralizer());
		Intent[] its = gsonB.create().fromJson(new FileReader(path), Intent[].class);

		for(Intent it : its){
			System.out.println(it.toString());
		}
	}

}
