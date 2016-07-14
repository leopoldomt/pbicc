package tg.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import tg.main.AppResults.IntentResult;

public class AppResults {

	public int version;
	public String app_hash;
	public Map<String,Integer> manifest_packages = new HashMap<String, Integer>();
	public ArrayList<String> intent_hashes = new ArrayList<String>();
	public ArrayList<IntentResult> intent_results = new ArrayList<IntentResult>();


	public static class IntentResult {
		public String hash;
		public Map<String, Manifest> manifests = new HashMap<String, Manifest>();
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Hash: %s\n", hash))
			.append(String.format("Manifests: \n"));
			for(Manifest m : manifests.values()){
				sb.append(m).append("_");
			}
			return sb.toString();
		}
	}

	public static class Manifest {

		public String pack;
		public int version;
		public ArrayList<Match> matches = new ArrayList<Match>();
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Package: %s\n: ", pack))
			.append(String.format("Package: %d\nMatches: ", version))
			.append(Arrays.toString(matches.toArray()));

			return sb.toString();
		}

	}

	public static class Match{
		public String component_name;
		public boolean value;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format(", <%s, %b>", component_name, value));
			return sb.toString();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("app_hash: %s", app_hash));
		sb.append(String.format("apps_testados: %s\n", Arrays.toString(manifest_packages.keySet().toArray())));
		sb.append(String.format("intent_hashes: %s\n", Arrays.toString(intent_hashes.toArray())));
		sb.append(String.format("results: %s", Arrays.toString(intent_results.toArray())));	
		return sb.toString();
	}

	public void addNewIntentResults(ArrayList<IntentResult> newIntentResults) {
		for (IntentResult ir : newIntentResults) {
			intent_results.add(ir);
			intent_hashes.add(ir.hash);
		}
	}

	public void addNewManifestResults(
			ArrayList<IntentResult> intentResultsForNewManifests, Map<String, Integer> newManifests) {
		////System.out.println(">>> addNewManifestResults");
		manifest_packages.putAll(newManifests);
		
		
		for (IntentResult ir : intent_results) {
		////System.out.println(">>> addNewManifestResults.ir = "+ir.hash);
			for (int i=0; i<intentResultsForNewManifests.size(); i++) {
				////System.out.println(">>> addNewManifestResults.irfnm = "+intentResultsForNewManifests.get(i).hash);
				if (ir.hash.equals(intentResultsForNewManifests.get(i).hash)) {
					////System.out.println("ir.manifests.size b= "+ir.manifests.size());
					ir.manifests.putAll(intentResultsForNewManifests.get(i).manifests);
					i = intentResultsForNewManifests.size();
					////System.out.println("ir.manifests.size a= "+ir.manifests.size());
				}
			}
		}
	}

	public void updateManifestResults(
			ArrayList<IntentResult> intentResultsForUpdatedManifests,
			Map<String, Integer> updatedManifests) {
		manifest_packages.putAll(updatedManifests);
		for (IntentResult ir : intent_results) {
			for (int i=0; i<intentResultsForUpdatedManifests.size(); i++) {
				if (ir.hash.equals(intentResultsForUpdatedManifests.get(i).hash)) {
					for (Entry<String, Manifest> entry : intentResultsForUpdatedManifests.get(i).manifests.entrySet()) {
						ir.manifests.put(entry.getKey(), entry.getValue());
					}
					i = intentResultsForUpdatedManifests.size();
				}
			}
		}
		
	}
}
;