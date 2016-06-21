package tg.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AppResults {

	public int version;
	public String app_hash;
	public Map<String,Integer> manifest_packages = new HashMap<String, Integer>();
	public ArrayList<String> intent_hashes = new ArrayList<String>();
	public ArrayList<IntentResult> intent_results = new ArrayList<IntentResult>();
	
	
	public static class IntentResult {
		public String hash;
		public ArrayList<Manifest> manifests = new ArrayList<Manifest>();
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Hash: %s\n", hash))
			.append(String.format("Manifests: %s\n", Arrays.toString(manifests.toArray())));
			
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
}
