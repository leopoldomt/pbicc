package tg;

import icc.data.Component;
import icc.parsing.AndroidManifestParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import tg.helper.Files;
import tg.helper.IntentJson;
import tg.parse.IntentForResolution;
import tg.parse.IntentFromJson;
import tg.parse.IntentParser;
import tg.resolution.IntentResolution;
import tg.resolution.IntentResolution.Result;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


public class TgMain {
	static final String inputPath = "/home/jpttrindade/Mega/CIN/TCC/inputs/";
	static final String ifrPath = "/home/jpttrindade/Mega/CIN/TCC/ifr/";

	static File inputFolder = new File(inputPath);
	static File ifrFolder = new File(ifrPath);
	static File manifestFolder = new File(inputFolder.getAbsolutePath()+"/manifests");	

	static Scanner in = new Scanner(System.in);
	static IntentForResolution[] ifrs;


	public static void main(String[] args) {
		System.out.println("### Start TgMain ###");

		convertJsonToIntentForResolution();
		getIntentsForResolutionFromFile();
		resolve();

		//first();
		//second();
		System.out.println("### End TgMain ###");
	}


	private static void resolve() {
		if(ifrs != null) {
			try {
				System.out.println("Chosse a AndroidManifest file (number):");
				int index = 1;
				for(File manifest : manifestFolder.listFiles()){
					System.out.println(String.format("(%d) %s", index, manifest.getName()));
					index++;
				}
				System.out.print(">>> ");
				int input = in.nextInt();
				String choose = manifestFolder.listFiles()[input-1].getAbsolutePath();

				AndroidManifestParser androidManifestParser = new AndroidManifestParser(choose);
				Result result;
				int component;
				index = 1;
				int matches = 0;
				for(IntentForResolution ifr : ifrs){
					System.out.println("\n#Intent"+(index));
					component = 1;
					for(Component c : androidManifestParser.components){
						result = IntentResolution.resolve(ifr, c);
						System.out.print("##Component"+component+": "+result.match);
						System.out.println((result.match==false ? " -> "+result.reason : ""));

						if(result.match) matches++;

						component++;						
					}
					index++;
				}
				System.out.printf("\n#########Total de Matches = %d\n", matches);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}


	private static void convertJsonToIntentForResolution() {
		System.out.println("> Iniciando conversão de Json para IntentResolution... ");
		for(File file : inputFolder.listFiles()){
			if(file.isFile()){
				//convertJsonToIntentForResolution(file);
				ArrayList<IntentForResolution> forResolutions = new ArrayList<IntentForResolution>();
				try {
					IntentFromJson[] fromJsons = IntentJson.readFromJson(file.getAbsolutePath());
					int i = 1;
					for(IntentFromJson ifj : fromJsons) {
						forResolutions.addAll(IntentParser
								.parseToResolution(ifj, file.getName().substring(0, file.getName().lastIndexOf(".json"))+"_"+i));
						i++;
					}

					String fileName = ifrPath+"ifr_"+file.getName();
					IntentJson.write(forResolutions, fileName);
				} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
		System.out.println(">> Finalizando conversão de Json para IntentResolution. ");
	}

	private static void getIntentsForResolutionFromFile() {
		try {
			System.out.println("Choose a file (number):");
			int index = 1;
			for(File file : ifrFolder.listFiles()) {
				System.out.println(String.format("(%d) %s", index, file.getName()));
				index++;
			}
			System.out.print(">>> ");
			int input = in.nextInt();
			String choose = ifrFolder.listFiles()[input-1].getAbsolutePath();
			ifrs = IntentJson.readForResolution(choose); 
			for(IntentForResolution ifr:ifrs){
				System.out.println(ifr.toString());
			}			
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void second() {
		try {

			String forResolutionFile = "test00.json";
			AndroidManifestParser manifestParser = new AndroidManifestParser(Files.FromJson.manifestPath01);
			IntentForResolution[] forResolutions = IntentJson.readForResolution(Files.ForResolution.path+forResolutionFile);
			int index = 1;
			Result result;
			int matches = 0;
			for(IntentForResolution ifr : forResolutions){
				System.out.println("\n#Intent"+(index));
				System.out.println(ifr);
				int component = 1;
				for(Component c : manifestParser.components){
					result = IntentResolution.resolve(ifr, c);
					System.out.print("##Component"+component+": "+result.match);
					System.out.println((result.match==false ? " -> "+result.reason : ""));

					if(result.match) matches++;

					component++;
				}
				index++;
			}
			System.out.printf("\n#########Total de Matches = %d\n", matches);

		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void first() {		

		try {
			IntentFromJson[] its = IntentJson.readFromJson(Files.FromJson.file00);

			AndroidManifestParser manifestParser = new AndroidManifestParser(Files.FromJson.manifestPath01);

			ArrayList<IntentForResolution> intentForResolutions = new ArrayList<IntentForResolution>();
			int i = 0;
			for(IntentFromJson it : its){
				intentForResolutions.addAll(IntentParser.parseToResolution(it, i+""));
			}

			IntentsApp itsApp = IntentsApp.createIntentsApp(intentForResolutions);

			System.out.printf(">> The app has %dexp/%dimp intents.\n", itsApp.explicits.size(), itsApp.implicits.size());



			System.out.printf(">> The manifest has %d components\n", manifestParser.components.size());

			int index = 1;
			int component;
			IntentResolution.Result result;


			int matches = 0;
			for(IntentForResolution ifr : itsApp.all){
				System.out.println("\n#Intent"+(index));
				component = 1;
				//if(i == 5) System.out.println(ifr.getData());
				//if(index == 2) System.out.println(ifr.getData().getScheme());
				for(Component c : manifestParser.components){
					//if(component == 3) System.out.println(c.intentFilters.get(0).data);
					//System.out.println(ifr.getData().getType());
					result = IntentResolution.resolve(ifr, c);
					System.out.print("##Component"+component+": "+result.match);
					System.out.println((result.match==false ? " -> "+result.reason : ""));

					if(result.match) matches++;

					component++;
				}
				index++;
			}

			System.out.printf("\n#########Total de Matches = %d\n", matches);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
