package outputparser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {
	
	public static void main(String[] args) throws IOException {
		//put name of file
		String subject = "abstract-art";
		String dir = "test-data/json/";
		// ground truth 
		//String s = "src/outputparser/ground-truth-primitive-ftp-d.json";
		String s = dir+"ground-truth/"+subject+".json";
		List<Entry> golden = load(s);
		
		// results
		//s = "src/outputparser/fake-output-primitive-ftp-d.json";
		s = dir+"results/"+subject+".json";
		List<Entry> output = load(s);
		
		String[] attributes = new String[] {"action", "component", "data", "mimeType", "extras"};
		System.out.println("============");
		for (String attribute : attributes) {
			PairDoubles pr = precisionRecall(golden, output, attribute);
			System.out.println("Attribute "+attribute);
			System.out.println(String.format("(precision, recall) = %s", pr));
			System.out.println("============");
		}
		
	}

	private static PairDoubles precisionRecall(List<Entry> golden, List<Entry> output, String attribute) {
		PairInts pis = findHits(output, golden, attribute);
		double precision = ((double) pis.a) / pis.b;
		
		pis = findHits(golden, output, attribute);
		double recall = ((double) pis.a) / pis.b;
		
		return new PairDoubles(precision, recall);
	}

	/**
	 * This function returns the number of hits of elements from the pivot 
	 * dataset (first parameter) against the eval dataset (second paramater), 
	 * with respect to a given attribute in the dataset.  
	 * 
	 * For example, let's assume there is only one attribute in the datasets.  
	 * For the pivot {a, b, -, -} and eval {b, c}, this function returns the 
	 * pair (1, 2) reflecting the fact that there was 1 hit (element b) and the
	 * effective size of the set was 2 instead of 4.   
	 * 
	 * @param pivot
	 * @param eval
	 * @param attribute
	 * @return
	 */
	private static PairInts findHits(List<Entry> pivot, List<Entry> eval, String attribute) {
		int hitsOutput = 0;
		int sizeOutput = 0;
		for (Entry out : pivot) {
			String s = out.select(attribute);
			if (s.equals("-")) {
				// consider as if there was no element
				continue; 
			}
			sizeOutput++;
			for (Entry gold : eval) {
				// necessary (but not sufficient condition)
				if (out.scope.equals(gold.scope) && out.methodType.equals(gold.methodType)) {
					//TODO: may need to replace equals below with something else for data or extras
					if (s.equals(gold.select(attribute))) {
						hitsOutput++; // match!
						break;
					}
				}
			}
		}
		if (hitsOutput == 0 && sizeOutput == 0) {
			hitsOutput = 1;
			sizeOutput = 1;
		}
		PairInts pis = new PairInts(hitsOutput, sizeOutput);
		return pis;
	}

	private static List<Entry> load(String s) throws FileNotFoundException, IOException {
		Gson g = new Gson();
		Reader reader = new FileReader(s);
		Type type = new TypeToken<List<Entry>>(){}.getType();
		List<Entry> ll = g.fromJson(reader, type);
		reader.close();
		return ll;
	}
	
	/** helpers **/
	
	static class PairInts { 
		int a, b;
		public PairInts(int a, int b) {
			this.a = a;
			this.b = b;
		}	
		@Override
		public String toString() {			
			return String.format("(%d, %d)", a, b);
		}
	}
	
	static class PairDoubles { 
		double p, r;
		public PairDoubles(double p, double r) {
			this.p = p;
			this.r = r;
		} 
		@Override
		public String toString() {			
			return String.format("(%.2f, %.2f)", p, r);
		}
	}

}
