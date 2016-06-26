package tg.main;	

import icc.data.Component;
import icc.parsing.AndroidManifestParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import tg.helper.HashGenerator;
import tg.helper.IntentJson;
import tg.main.AppResults.IntentResult;
import tg.main.AppResults.Manifest;
import tg.main.AppResults.Match;
import tg.parse.IntentForResolution;
import tg.resolution.IntentResolution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class MainResolution {

	private static final String ROOT = "tg";
	private static final String MANIFESTS = "/manifests";
	private static final String MANIFESTS_ALL = MANIFESTS + "/all";
	private static final String MANIFESTS_PACKAGES = MANIFESTS + "/packages";
	private static final String APPS = "/apps";
	private static final String RESULTS = "/results";

	public static void main(String[] args) {
		
		for(String arg : args) {
			System.out.println(arg);
		}
		
		
		
		Scanner in = new Scanner(System.in);
		String input;
		int choose = 1;
		
		int argCount = 0;
		
		StringBuilder sb;
		while (argCount < args.length && choose > 0) {
			try {
				sb = new StringBuilder();
				sb.append("Choose operation:\n")
				.append("(1) setup\n")
				.append("(2) read news manifest.xml\n")
				.append("(3) resolve 1 app\n")
				.append("(4) resolve all apps\n")
				.append("(5) slow resolve 1 app\n")
				.append("(6) slow resolve all apps\n")
				.append("(0) exit\n")
				.append(">> ");
				System.out.print(sb.toString());
				//input = in.nextLine();
				input = args[argCount];
				choose = Integer.parseInt(input);

				switch (choose) {
				case 0:
					//exit
					break;
				case 1:
					//setup();
					IncrementalResolution.setup();
					break;
				case 2:
					//parseNewManifests();
					IncrementalResolution.parseNewManifests();
					break;
				case 3:
					String fileName = chooseFile(in);
					//resolveOneApp(fileName);
					try {
						IncrementalResolution.resolveApp(fileName);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					} catch (JsonIOException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
				case 4:
					//resolveAllApps();
					try {
						IncrementalResolution.resolveAllApps();
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					} catch (JsonIOException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
				case 5:
					String fileName2 = chooseFile(in);
					try {
						IncrementalResolution.slowResolveApp(fileName2);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					} catch (JsonIOException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
				case 6:
					try {
						IncrementalResolution.slowResolveAllApps();
					} catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonIOException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
				default:
				}

				
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
			}
			argCount++;
		}

		System.out.println("end!");
	}

	private static void slowResolveAllApps() {
		
	}

	private static void slowResolveOneApp() {
		// TODO Auto-generated method stub
		
	}

	private static void resolveAllApps() {
		File appsPath = new File(ROOT+APPS);
		if (appsPath.exists()) {
			File[] files = appsPath.listFiles();
			for (File file : files) {
				try {
					resolve(file.getName());
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void resolveOneApp(String fileName) {		
		try {
			

			System.out.println("choose = "+fileName);
			
			//IncrementalResolution.resolveApp(fileName);
			resolve(fileName);

		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void resolve(String appFileName) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		IntentForResolution[] ifr = IntentJson.readForResolution(ROOT+APPS+"/"+appFileName);
		IntentsApp app = IntentsApp.createIntentsApp(ifr);
		AppResults lastAppResults = getLastAppResults(appFileName);


		AppResults appResults = resolve(app, lastAppResults);

		if(appResults != null)
			saveAppResults(appResults, appFileName);
		else 
			System.err.println("No changes and/or no new Manifest and/or no updated Manifest");

	}

	private static AppResults resolve(IntentsApp app, AppResults lastAppResults) {
		//System.out.println(">>"+lastAppResults);
		boolean appModified = false, newManifest = false, updatedManifest = false;

		if (lastAppResults == null) {
			// has no test before
			return firstResolve(app);

		}

		//0. carregar manifests
		Map<String, Integer> manifestsInfo = getManifestsInfo();
		Map<String, Integer> newManifests = new HashMap<String,Integer>();
		ArrayList<String> newManifestsForResolution = new ArrayList<String>();
		Map<String, Integer>  updatedManifests = new HashMap<String, Integer>();
		ArrayList<String> updatedManifestsForResolution = new ArrayList<String>();

		//Set<String> packs_info = manifestsInfo.keySet();

		for(Entry<String,Integer> manifest : manifestsInfo.entrySet()) {
			if(!lastAppResults.manifest_packages.containsKey(manifest.getKey())){
				//0.1 verificar novos manifests
				newManifests.put(manifest.getKey(),manifest.getValue());
				newManifestsForResolution.add(ROOT+MANIFESTS_PACKAGES+"/"+manifest.getKey()+"/v"+manifest.getValue()+".xml");
			} else {
				//0.2 verificar manifests atualizados
				if(manifest.getValue()>lastAppResults.manifest_packages.get(manifest.getKey())){
					updatedManifests.put(manifest.getKey(), manifest.getValue());
					updatedManifestsForResolution.add(ROOT+MANIFESTS_PACKAGES+"/"+manifest.getKey()+"/v"+manifest.getValue()+".xml"); 
				}
			}
		}	

		//1. testar se houve modificacao no app
		String app_hash = HashGenerator.generateBase64Hash(Arrays.toString(app.all.toArray()));			
		if (!app_hash.equals(lastAppResults.app_hash)){
			appModified = true;
			//1.1 houve mudancas no app

			//1.1.0 atualizo o app_hash
			lastAppResults.app_hash = app_hash;
			boolean contain;

			//1.1.2 seleciono as novas intents para serem testadas
			ArrayList<IntentForResolution> newIntents = new ArrayList<IntentForResolution>();

			for (IntentForResolution ifr : app.all) {
				contain = false;
				for (int i=0; i<lastAppResults.intent_results.size(); i++) {
					if (lastAppResults.intent_results.get(i).hash.equals(ifr.getHash())) {
						contain = true;
						i = lastAppResults.intent_results.size();
					}
				}
				if(!contain) {
					newIntents.add(ifr);
				}
			}
			
			//1.1.1 removo do arquivo de resultados itents deletadas
			ArrayList<IntentResult> indexToRemove = new ArrayList<IntentResult>();
			for (int j=0; j<lastAppResults.intent_results.size(); j++){
			//for (IntentResult result : lastAppResults.intent_results) {
				contain = false;
				for (int i=0; i< app.all.size(); i++) {
					if (app.all.get(i).getHash().equals(lastAppResults.intent_results.get(j).hash)) {
						contain = true;
						//indexToRemove.add(j);
						i = app.all.size();
					}
				}
				if(!contain){
					//lastAppResults.intent_results.remove(result);
					indexToRemove.add(lastAppResults.intent_results.get(j));
				}
			}
			
			for (IntentResult i : indexToRemove) {
				lastAppResults.intent_results.remove(i);
				lastAppResults.intent_hashes.remove(i.hash);
			}
			
			
			
			//1.1.3 testo as novas intetns com os manifestos antigos; e,
			//1.1.5 adicionar novas intents ao lastIntentResults como novas intentResults
			if(!newIntents.isEmpty()){

				ArrayList<String> oldmanifests = new ArrayList<String>();
				for (Entry<String, Integer> entry : lastAppResults.manifest_packages.entrySet()) {
					oldmanifests.add(ROOT+MANIFESTS_PACKAGES+"/"+entry.getKey()+"/v"+entry.getValue()+".xml");
				}

				//new intents
				ArrayList<IntentResult> newIntentResults = resolve(newIntents, oldmanifests);
				lastAppResults.addNewIntentResults(newIntentResults);

			}

			//1.1.4 testo intents antigas(+novas) com novos manifestos
			//new manifests


		} 
		//1.1 se nao houve, 
		//1.1.1 testo intents antigas com novos manifestos e manifestos atualizados.
		if (!newManifestsForResolution.isEmpty()) {	
			newManifest = true;
			ArrayList<IntentResult> intentResultsForNewManifests = resolve(app.all, newManifestsForResolution);
			lastAppResults.addNewManifestResults(intentResultsForNewManifests, newManifests);
		}

		//1.1.4 testo intents antigas(+novas) com manifestos atualizados.
		if (!updatedManifestsForResolution.isEmpty()) {
			updatedManifest = true;
			ArrayList<IntentResult> intentResultsForUpdatedManifests = resolve(app.all, updatedManifestsForResolution);
			lastAppResults.updateManifestResults(intentResultsForUpdatedManifests, updatedManifests);
		}


		//2. gravar resultados num novo arquivo de resultados
		//3. aumentar o .version

		if(appModified || newManifest || updatedManifest){
			return lastAppResults;
		} else {
			return null;
		}

	}

	private static AppResults firstResolve(IntentsApp app) {
		System.out.println("firstResolve");
		//TODO 1. test app with all last manifest versions...	
		ArrayList<String> manifestsFileName = getAllManifestFileNames();

		ArrayList<IntentResult> intentResult = resolve(app.all, manifestsFileName);
		AppResults appResults = new AppResults();
		appResults.app_hash = HashGenerator.generateBase64Hash(Arrays.toString(app.all.toArray()));
		appResults.intent_results.addAll(intentResult);
		for (IntentForResolution ifr : app.all) {
			appResults.intent_hashes.add(ifr.getHash());
		}
		File f;
		for (String manifestName: manifestsFileName) {
			f = new File(manifestName);
			appResults.manifest_packages.put(f.getParentFile().getName(), Integer.parseInt(f.getName().substring(1, 2)));	
		}
		appResults.version = 1;
		return appResults;

	}

	private static void saveAppResults(AppResults appResults, String appFileName) {
		System.out.println("saveAppResults");
		File appResultVersion = new File(ROOT+RESULTS+"/"+appFileName.substring(0,appFileName.lastIndexOf("."))+"/.version");
		File appResultFolder = appResultVersion.getParentFile();

		try {
			//atualizar a versao do result file
			updateVersion(appResultVersion, appResults.version+1);

			JsonObject appResultsJson = new JsonObject();

			//adicionar o hash relativo ao arquivo do app avaliado
			appResultsJson.addProperty("app_hash", appResults.app_hash);	

			//adicionar os packages do manifests testados como uma lista
			JsonArray manifest_packages = new JsonArray();
			JsonObject appJson;
			for(Entry<String, Integer> app :appResults.manifest_packages.entrySet()){
				appJson = new JsonObject();
				appJson.addProperty("package", app.getKey());
				appJson.addProperty("version", app.getValue());
				manifest_packages.add(appJson);
			}
			appResultsJson.add("manifest_packages", manifest_packages);

			//adiconar os hashes das intents testas
			JsonArray intent_hashes = new JsonArray();
			for (String intent_hash : appResults.intent_hashes) {
				intent_hashes.add(intent_hash);
			}
			appResultsJson.add("intent_hashes", intent_hashes);


			//Adicionar os intent_results 
			JsonArray intentResultsJson = new JsonArray();

			JsonObject json;
			for (IntentResult iResult : appResults.intent_results) {
				json = new JsonObject();
				json.addProperty("hash", iResult.hash);

				JsonArray manifestsJson = parseManifestToJson(iResult.manifests);
				json.add("manifests", manifestsJson);
				intentResultsJson.add(json);
			}
			appResultsJson.add("intent_results", intentResultsJson);

			System.out.println(appResultsJson.toString());


			Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.create();

			String jsonString = gson.toJson(appResultsJson);

			File f = new File(appResultFolder.getAbsolutePath()+"/v"+appResults.version+".json");
			FileWriter writer = new FileWriter(f);
			writer.write(jsonString);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateVersion(File versionFile, int newVersion) {
		System.out.println("Updating Version + - "+newVersion);
		FileWriter writer;
		try {
			writer = new FileWriter(versionFile);
			writer.write(""+newVersion);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private static JsonArray parseManifestToJson(Map<String, Manifest> manifests2) {

		JsonArray manifestsJson = new JsonArray();

		for(Manifest manifest : manifests2.values()){
			JsonObject json = new JsonObject();
			json.addProperty("package", manifest.pack);
			json.addProperty("version", manifest.version);

			json.add("matches", getMatchesJson(manifest.matches));

			manifestsJson.add(json);
		}

		return manifestsJson;
	}

	private static JsonArray getMatchesJson(ArrayList<Match> matches) {
		JsonArray matchesJson = new JsonArray();
		JsonObject matchJson;
		for (Match m : matches) {
			matchJson = new JsonObject();
			matchJson.addProperty("component_name", m.component_name);
			matchJson.addProperty("value", m.value);
			matchesJson.add(matchJson);
		}
		return matchesJson;
	}

	private static ArrayList<IntentResult> resolve(ArrayList<IntentForResolution> ifrs,
			ArrayList<String> manifestsFileName) {
		ArrayList<IntentResult> intentResults = new ArrayList<AppResults.IntentResult>();

		AndroidManifestParser manifestParser;
		IntentResolution.Result result;
		ArrayList<Match> matches;
		Match match = null;
		Manifest manifest = null;
		IntentResult intentResult;

		for (IntentForResolution ifr : ifrs) {
			intentResult = new IntentResult();
			for (String manifestName : manifestsFileName) {
				try {
					manifestParser = new AndroidManifestParser(manifestName);
					manifest = new Manifest();
					matches = new ArrayList<AppResults.Match>();

					for (Component comp : manifestParser.components){
						result = IntentResolution.resolve(ifr, comp);
						match = new Match();
						match.component_name = comp.name;
						match.value = result.match;
						matches.add(match);
					}
					manifest.pack = manifestParser.appPackage;
					int index = manifestName.lastIndexOf("v");
					manifest.version = Integer.parseInt(manifestName.substring(index+1, index+2));
					manifest.matches.addAll(matches);
					intentResult.manifests.put(manifest.pack, manifest);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			intentResult.hash = ifr.getHash();
			intentResults.add(intentResult);
		}

		return intentResults;
	}

	private static ArrayList<String> getAllManifestFileNames() {
		System.out.println("getAllManifestFileNames");

		ArrayList<String> manifestNames = new ArrayList<String>();
		File manifestsPaths = new File(ROOT+MANIFESTS_PACKAGES);

		if (manifestsPaths.isDirectory()) {
			System.out.println(manifestsPaths+" is directory");
			for (File f : manifestsPaths.listFiles()) {
				if (f.isDirectory()) {
					int version = getVersion(f.getAbsolutePath());//getVersionFromPath(f.getAbsolutePath());
					manifestNames.add(ROOT+MANIFESTS_PACKAGES+"/"+f.getName()+"/v"+version+".xml");
					//System.out.println(f.getAbsolutePath()+"/v"+version+".xml foi adicionado!");
				}
			}
		} else {
			System.out.println("isnt directory");
		}

		return manifestNames;
	}



	private static int getVersionFromPath(String absolutePath) {

		int v = 1;
		try {
			File version = new File(absolutePath+"/.version");

			BufferedReader reader = new BufferedReader(new FileReader(version));
			v = Integer.parseInt(reader.readLine());
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return v;
	}

	private static AppResults getLastAppResults(String appFileName) {


		//File appResultVersion = new File(ROOT+RESULTS+"/"+appFileName.substring(0,appFileName.lastIndexOf(".")));
		File appResultFolder = new File(ROOT+RESULTS+"/"+appFileName.substring(0,appFileName.lastIndexOf(".")));

		System.out.println("AppResultPath: "+appResultFolder.getAbsolutePath());
		System.out.println("AppResultPath: "+appResultFolder.isDirectory());

		int version;
		if (!appResultFolder.exists()) {
			appResultFolder.mkdirs();
			version = createVersionFile(appResultFolder.getPath());
			System.out.println("getLastAppResults.createVersionFile() ==" + version);
			return null;
		}

		AppResults appResults = null;

		try {

			appResults = new AppResults();

			version = getVersion(ROOT+RESULTS+"/"+appFileName.substring(0,appFileName.lastIndexOf(".")));			
			appResults.version = version;

			FileReader appResultsReader = new FileReader(appResultFolder.getAbsolutePath()+"/v"+(version-1)+".json");
			JsonParser parser = new JsonParser();

			JsonObject jsonResult = (JsonObject) parser.parse(appResultsReader);

			JsonArray manifest_packages = jsonResult.getAsJsonArray("manifest_packages");



			JsonArray intent_hashes = jsonResult.getAsJsonArray("intent_hashes");

			JsonArray results = jsonResult.getAsJsonArray("intent_results");
			appResults.app_hash = jsonResult.get("app_hash").getAsString();


			String pack;
			int pack_version;
			for(JsonElement manifest : manifest_packages) {
				pack = manifest.getAsJsonObject().get("package").getAsString();
				pack_version = manifest.getAsJsonObject().get("version").getAsInt();
				appResults.manifest_packages.put(pack, pack_version);
			}

			for(JsonElement hash : intent_hashes) {
				appResults.intent_hashes.add(hash.getAsString());
			}

			String hash;
			Manifest manifest;
			Match match;
			AppResults.IntentResult result;


			for (JsonElement resultJson : results) {
				result = new AppResults.IntentResult();
				hash = resultJson.getAsJsonObject().get("hash").getAsString();
				JsonArray manifests = resultJson.getAsJsonObject().getAsJsonArray("manifests");

				result.hash = hash;


				for(JsonElement manifestJson : manifests) {
					manifest = new Manifest();
					manifest.version = manifestJson.getAsJsonObject().get("version").getAsInt();
					manifest.pack = manifestJson.getAsJsonObject().get("package").getAsString();
					for (JsonElement matchesJson : manifestJson.getAsJsonObject().get("matches").getAsJsonArray()) {
						match = new Match();
						match.component_name = matchesJson.getAsJsonObject().get("component_name").getAsString();
						match.value = matchesJson.getAsJsonObject().get("value").getAsBoolean();
						manifest.matches.add(match);
					}
					result.manifests.put(manifest.pack, manifest);

				}

				appResults.intent_results.add(result);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appResults;
	}


	private static String chooseFile(Scanner in) {
		System.out.println("Chosse a App (number):");
		int index = 1;
		File apps = new File(ROOT+APPS);

		for(File app : apps .listFiles()){
			System.out.println(String.format("(%d) %s", index, app.getName()));
			index++;
		}
		int input = Integer.parseInt(in.nextLine());
		return apps.listFiles()[input-1].getName();
	}


	private static void parseNewManifests() {

		Map<String, Integer> infos = getManifestsInfo();
		File f = new File(ROOT+MANIFESTS_ALL);
		File[] newManifests;
		boolean updateInfo = false;
		if (f.isDirectory()) {
			newManifests = f.listFiles();
			AndroidManifestParser manifestParser;
			String packageName;
			int count;
			FileOutputStream outputStream;
			FileInputStream inputString;
			for (File newManifest: newManifests) {
				try {
					manifestParser = new AndroidManifestParser(newManifest.getAbsolutePath());
					packageName = manifestParser.appPackage;

					//create package folder and get next manifet_version
					File manifest_version = new File(ROOT+MANIFESTS_PACKAGES+"/"+packageName+"/.version");
					int version = 1;	
					if(manifest_version.exists()) {
						FileReader fr = new FileReader(manifest_version);
						BufferedReader reader = new BufferedReader(fr);
						version = Integer.parseInt(reader.readLine())+1;
						reader.close();
						fr.close();
					} else {
						manifest_version.getParentFile().mkdirs();
						//manifest_version.createNewFile();
					}
					FileWriter fw = new FileWriter(manifest_version);
					BufferedWriter writer = new BufferedWriter(fw);
					writer.write(""+version);
					writer.close();
					fw.close();

					outputStream = new FileOutputStream(ROOT+MANIFESTS_PACKAGES+"/"+packageName+"/v"+version+".xml");
					inputString = new FileInputStream(newManifest);
					byte[] buffer = new byte[1024];
					while((count = inputString.read(buffer))>0) {
						outputStream.write(buffer, 0, count);
					}
					inputString.close();
					outputStream.close();

					newManifest.delete();
					infos.put(packageName, version);
					updateInfo = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


			if(updateInfo) updateInfo(infos);

		}

	}


	private static void updateInfo(Map<String, Integer> infos) {
		try {
			JsonObject json = new JsonObject();

			JsonArray packages = new JsonArray();

			JsonObject pack;

			for(Entry<String, Integer> entry : infos.entrySet()) {
				pack = new JsonObject();
				pack.addProperty("name", entry.getKey());
				pack.addProperty("version", entry.getValue());
				packages.add(pack);
			}

			json.add("packages", packages);

			Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.create();

			String string = gson.toJson(json);

			File file = new File(ROOT+MANIFESTS+"/INFO.json");
			FileWriter writer;
			writer = new FileWriter(file, false);
			writer.write(string);
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static Map<String, Integer> getManifestsInfo() {
		Map<String, Integer> infos = new HashMap<String, Integer>();
		try {
			File file = new File(ROOT+MANIFESTS+"/INFO.json");

			if(file.exists()) { 

				FileReader infoFileReader = new FileReader(file);
				JsonParser parser = new JsonParser();

				JsonElement infoJson = parser.parse(infoFileReader);
				System.out.println(infoJson);

				JsonArray packages = infoJson.getAsJsonObject().getAsJsonArray("packages");
				String name;
				int version;
				for(JsonElement pack : packages){
					name = pack.getAsJsonObject().get("name").getAsString();
					version = pack.getAsJsonObject().get("version").getAsInt();
					infos.put(name, version);			
				}
			} 

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return infos;
	}

	private static void setup() {
		setupFolders();
	}


	private static boolean setupFolders() {
		boolean result = true;
		File f = new File(ROOT+MANIFESTS_ALL);
		if(!f.exists()) result = result && f.mkdirs();
		f = new File(ROOT+MANIFESTS_PACKAGES);
		if(!f.exists()) result = result && f.mkdirs();
		f = new File(ROOT+APPS);
		if(!f.exists()) result = result && f.mkdirs();
		f = new File(ROOT+"/results");
		if(!f.exists()) result = result && f.mkdirs();

		if (result) {
			System.out.println("Folders created...");
		} else {
			System.out.println("Folders not created...");
		}
		return result;
	}

	private static int getVersion (String path) {

		File versionFile = new File(path);
		if(versionFile.exists()){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(path+"/.version"));
				int result = Integer.parseInt(reader.readLine());
				reader.close();
				return result;
			} catch (FileNotFoundException e) {
				return createVersionFile(path);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return createVersionFile(path);
		}
		return -1;
	}

	private static int createVersionFile(String path) {
		try {
			FileWriter writer = new FileWriter(new File(path+"/.version"));
			writer.write(""+1);
			writer.close();
			return 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
