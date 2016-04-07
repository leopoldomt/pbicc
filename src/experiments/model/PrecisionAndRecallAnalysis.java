package experiments.model;

import java.util.List;
import java.util.Locale;

public class PrecisionAndRecallAnalysis {

	/** helpers **/

	private static class PairInts {
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

	private static class PairDoubles {
		double p, r;

		public PairDoubles(double p, double r) {
			this.p = p;
			this.r = r;
		}

		@Override
		public String toString() {
			return String.format(Locale.US, "(%.2f, %.2f)", p, r);
		}
	}

	public static String run(List<Entry> golden, List<Entry> output, String attribute) {
		PairInts pis = findHits(output, golden, attribute);
		double precision = ((double) pis.a) / pis.b;

		pis = findHits(golden, output, attribute);
		double recall = ((double) pis.a) / pis.b;

		return (new PairDoubles(precision, recall)).toString();
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
					// TODO: may need to replace equals below with something
					// else for data or extras
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

}
