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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import tg.helper.HashGenerator;
import tg.helper.IntentJson;
import tg.main.AppResults.IntentResult;
import tg.main.AppResults.Manifest;
import tg.main.AppResults.Match;
import tg.parse.IntentForResolution;
import tg.resolution.IntentResolution;

public class IncrementalResolution {
	private static final String ROOT = "tg";
	private static final String MANIFESTS = "/manifests";
	private static final String MANIFESTS_ALL = MANIFESTS + "/all";
	private static final String MANIFESTS_PACKAGES = MANIFESTS + "/packages";
	private static final String APPS = "/apps";
	private static final String RESULTS = "/results";


	public static void slowResolveAllApps() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		File appsPath = new File(ROOT+APPS);
		if (appsPath.exists()) {
			File[] files = appsPath.listFiles();
			for (File file : files) {
				slowResolveApp(file.getName());
			}
		}
	}

	public static void slowResolveApp(String appName) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		IntentForResolution[] ifr = IntentJson.readForResolution(ROOT+APPS+"/"+appName);
		IntentsApp app = IntentsApp.createIntentsApp(ifr);
		ArrayList<String> manifests = getAllManifestFileNames();
		
		AppResults appResults = resolve(app, manifests);
		
		File appResultFolder = new File(ROOT+RESULTS+"/"+appName.substring(0,appName.lastIndexOf(".")));

		
		if (!appResultFolder.exists()) {
			appResultFolder.mkdirs();
			appResults.version = createVersionFile(appResultFolder.getPath());
		} else {
			appResults.version = getVersion(appResultFolder.getAbsolutePath());
		}
		saveOrUpdateResults(appResults, appName);
	}

	public static void resolveAllApps() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		File appsPath = new File(ROOT+APPS);
		if (appsPath.exists()) {
			File[] files = appsPath.listFiles();
			for (File file : files) {
				resolveApp(file.getName());
			}
		}
	}

	public static void resolveApp(String appName) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		IntentForResolution[] ifr = IntentJson.readForResolution(ROOT+APPS+"/"+appName);
		IntentsApp app = IntentsApp.createIntentsApp(ifr);
		AppResults lastAppResults = getLastAppResultsFromJson(appName);
		ArrayList<String> manifests = getAllManifestFileNames();

		AppResults appResults;
		if (lastAppResults != null){
			appResults = resolve(app, lastAppResults, manifests);
		} else {
			appResults = resolve(app, manifests);
		}

		if (appResults != null) {
			saveOrUpdateResults(appResults, appName);
		} else {
			System.err.println("No changes and/or no new Manifest and/or no updated Manifest");

		}
	}

	// First resolution: resolve without lastResults; 
	private static AppResults resolve(IntentsApp app, ArrayList<String> manifests) {
		ArrayList<IntentResult> intentResult = runIntentResolution(app.all, manifests);	
		AppResults appResults = new AppResults();
		appResults.app_hash = HashGenerator.generateBase64Hash(Arrays.toString(app.all.toArray()));
		appResults.intent_results.addAll(intentResult);
		for (IntentForResolution ifr : app.all) {
			appResults.intent_hashes.add(ifr.getHash());
		}
		File f;
		for (String manifestName: manifests) {
			f = new File(manifestName);
			appResults.manifest_packages.put(f.getParentFile().getName(), Integer.parseInt(f.getName().substring(1, 2)));	
		}
		appResults.version = 1;
		return appResults;
	}

	// resolve with all paramans.
	private static AppResults resolve(IntentsApp app, AppResults lastAppResults, ArrayList<String> manifests) {

		boolean appWasModified = false;
		boolean hasNewManifest = false;
		boolean hasUpdatedManifest = false;

		Map<String, Integer> newManifests = new HashMap<String,Integer>();
		ArrayList<String> newManifestsForResolution = new ArrayList<String>();
		Map<String, Integer>  updatedManifests = new HashMap<String, Integer>();
		ArrayList<String> updatedManifestsForResolution = new ArrayList<String>();

		setNewAndUpdatedManifests(newManifests, newManifestsForResolution, updatedManifests, updatedManifestsForResolution,lastAppResults);

		String app_hash = HashGenerator.generateBase64Hash(Arrays.toString(app.all.toArray()));			

		//verifying if  app was modified
		if (!app_hash.equals(lastAppResults.app_hash)) {
			//appWasModified = true;

			//att app_hash
			lastAppResults.app_hash = app_hash;

			ArrayList<IntentForResolution> newIntents = getNewIntentsFromApp(app, lastAppResults);

			appWasModified = deleteRemovedIntents(app, lastAppResults);

			if(!newIntents.isEmpty()){
				appWasModified = true;
				ArrayList<String> oldmanifests = getOldManifestsFromLastAppResults(lastAppResults);
				//Run intentResolution to newIntents with oldManifests...
				ArrayList<IntentResult> newIntentResults = runIntentResolution(newIntents, oldmanifests);
				lastAppResults.addNewIntentResults(newIntentResults);
			}
		}

		if (!newManifestsForResolution.isEmpty()) {
			hasNewManifest = true;
			ArrayList<IntentResult> intentResultsForNewManifests = runIntentResolution(app.all, newManifestsForResolution);
			lastAppResults.addNewManifestResults(intentResultsForNewManifests, newManifests);
		}

		if (!updatedManifestsForResolution.isEmpty()) {
			hasUpdatedManifest = true;
			ArrayList<IntentResult> intentResultsForUpdatedManifests = runIntentResolution(app.all, updatedManifestsForResolution);
			lastAppResults.updateManifestResults(intentResultsForUpdatedManifests, updatedManifests);
		}


		if (appWasModified || hasNewManifest || hasUpdatedManifest) {
			return lastAppResults;
		} 
		return null;
	}

	private static ArrayList<String> getOldManifestsFromLastAppResults(AppResults lastAppResults) {
		ArrayList<String> oldmanifests = new ArrayList<String>();
		for (Entry<String, Integer> entry : lastAppResults.manifest_packages.entrySet()) {
			oldmanifests.add(ROOT+MANIFESTS_PACKAGES+"/"+entry.getKey()+"/v"+entry.getValue()+".xml");
		}
		return oldmanifests;
	}


	private static boolean deleteRemovedIntents(IntentsApp app, AppResults lastAppResults) {
		ArrayList<IntentResult> indexToRemove = new ArrayList<IntentResult>();
		boolean contain;
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

		if (indexToRemove.isEmpty()) {
			return false;
		}
		return true;		
	}


	private static ArrayList<IntentForResolution> getNewIntentsFromApp(
			IntentsApp app, AppResults lastAppResults) {
		ArrayList<IntentForResolution> newIntents = new ArrayList<IntentForResolution>();
		boolean contain;
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
		return newIntents;
	}


	private static ArrayList<IntentResult> runIntentResolution(ArrayList<IntentForResolution> ifrs,
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


	private static void setNewAndUpdatedManifests(
			Map<String, Integer> newManifests,
			ArrayList<String> newManifestsForResolution,
			Map<String, Integer> updatedManifests,
			ArrayList<String> updatedManifestsForResolution,
			AppResults lastAppResults) {
		Map<String, Integer> manifestsInfo = getManifestsInfo();

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
			e.printStackTrace();
		}
		return infos;
	}

	private static void saveOrUpdateResults(AppResults appResults,String appName) {		
		File appResultVersion = new File(ROOT+RESULTS+"/"+appName.substring(0,appName.lastIndexOf("."))+"/.version");
		File appResultFolder = appResultVersion.getParentFile();

		updateAppResultVersion(appResultVersion, appResults.version+1);
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

		try {
			File f = new File(appResultFolder.getAbsolutePath()+"/v"+appResults.version+".json");
			FileWriter writer;
			writer = new FileWriter(f);
			writer.write(jsonString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static JsonArray parseManifestToJson(Map<String, Manifest> manifests) {
		JsonArray manifestsJson = new JsonArray();
		for(Manifest manifest : manifests.values()){
			JsonObject json = new JsonObject();
			json.addProperty("package", manifest.pack);
			json.addProperty("version", manifest.version);
			json.add("matches", getMatchesJson(manifest.matches));
			manifestsJson.add(json);
		}
		return manifestsJson;
	}

	private static JsonElement getMatchesJson(ArrayList<Match> matches) {
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

	private static void updateAppResultVersion(File appResultVersion, int newVersion) {
		//System.out.println("Updating Version + - "+newVersion);
		FileWriter writer;
		try {
			writer = new FileWriter(appResultVersion);
			writer.write(""+newVersion);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private static ArrayList<String> getAllManifestFileNames() {
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


	private static int getVersion(String path) {
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
			e.printStackTrace();
		}
		return -1;
	}

	private static AppResults getLastAppResultsFromJson(String appName) {
		File appResultFolder = new File(ROOT+RESULTS+"/"+appName.substring(0,appName.lastIndexOf(".")));
		AppResults appResults = null;
		int version;

		if (!appResultFolder.exists()) {
			appResultFolder.mkdirs();
			version = createVersionFile(appResultFolder.getPath());
			return appResults;
		} 


		try {

			appResults = new AppResults();

			version = getVersion(ROOT+RESULTS+"/"+appName.substring(0,appName.lastIndexOf(".")));			
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
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return appResults;
	}

	public static void parseNewManifests() {

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

	public static void setup() {
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


}
