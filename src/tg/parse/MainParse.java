package tg.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import tg.helper.Files;
import tg.helper.FirstIntentDeserializer;
import tg.helper.IntentJson;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MainParse {

	static String fileFromJsonName = Files.FromJson.file2;
	
	static String fileForResolutionName = fileFromJsonName.substring(fileFromJsonName.lastIndexOf("/")+1);

	public static void before () {
		System.out.println(fileFromJsonName);
		System.out.println(">>> START <<<");
		setIntentsFromJson(fileFromJsonName);
		setIntetsForResolution();		
	}

	
	public static void it () {
		int i = 1;
		try {
			IntentJson.write(intentsForResolution, fileForResolutionName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*for(IntentForResolution ifr : intentsForResolution){
			try {
				IntentParser.parseToFile(ifr, i+".json");
				i++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}

	public static void after () {
		System.out.println(">>> END <<<");
	}



	public static void main(String[] args) {
		before();
		it();
		after();
	}
	
	static ArrayList<IntentForResolution> intentsForResolution = new ArrayList<IntentForResolution>();
	static void setIntetsForResolution() {
		if(ifjs != null){
			int sbe = 0;
			int saf = 0;
			int index = 1;
			for(IntentFromJson it : ifjs){
				sbe = intentsForResolution.size();
				intentsForResolution.addAll(IntentParser.parseToResolution(it, fileForResolutionName+"_"+index));
				saf = intentsForResolution.size();
				
				System.out.println("1 --> "+(saf-sbe));
				index++;
			}
		}
	}

	static IntentFromJson[] ifjs;
	static void setIntentsFromJson(String file) {
		try {
			ifjs = IntentJson.readFromJson(file);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
