package icc.intent;

import icc.data.Component;
import icc.data.IntentFilter;
import icc.parsing.AndroidManifestParser;

public class IntentResolution {

	public IntentResolution() {

	}

	public static boolean resolve(Intent it, Component comp) {
		System.out.println(comp.name);
		boolean response = false;
		for (IntentFilter ifilter : comp.intentFilters) {
			System.out.println(">>>> Start intent-fiter");

			try {
				actionTest(it, ifilter);
				dataTest(it, ifilter);
				categoryTest(it, ifilter);

				throw new Exception();

			} catch (ActionTestException e1) {
				System.out.println(e1.getMessage());
				response = false;
			} catch (DataTestException e2) {
				System.out.println(e2.getMessage());
				response = false;
			} catch (CategoryTestException e3) {
				System.out.println(e3.getMessage());
				response = false;
			} catch (Exception e4) {
				response = true;
				System.out.println(">>>> End intent-filter");
				break;
			}
			System.out.println(">>>> End intent-filter");
		}
		return response;
	}

	private static void actionTest(Intent it, IntentFilter ifilter)
			throws ActionTestException {
		System.out.println(ifilter.action);
		if (null == ifilter.action) {
			throw new ActionTestException();
		}

		if (null != it.getAction()) {
			// TODO Intent-Filter can has one or more actions.
			if (!it.getAction().equals(ifilter.action)) {
				throw new ActionTestException();
			}
		}

	}

	private static void dataTest(Intent it, IntentFilter ifilter)
			throws DataTestException {
		System.out.println(ifilter.data);
		// throw new DataTestException();

	}

	private static void categoryTest(Intent it, IntentFilter ifilter)
			throws CategoryTestException {

		System.out.println(ifilter.category);

		if(it.getCategories().size() > 0){
			if(null != ifilter.category){
				boolean pass = true;
				for(String ctg : it.getCategories()){
					if(!pass){
						throw new CategoryTestException();
					}
					
					// TODO for-each in ifilter.catergory (has to be a list);
					if(ctg.equals(ifilter.category)){
						pass = true;
					} else {
						pass = false;
					}
				}
			} else {
				throw new CategoryTestException();
			}
		} else if(null != ifilter.category){
			// TODO what happen when has no category on intent neither on intent-filter?
			//throw new CategoryTestException();
		}
		
	}

	static class ActionTestException extends Exception {
		public ActionTestException() {
			super("ActionTestException");
		}
	}

	static class DataTestException extends Exception {
		public DataTestException() {
			super("DataTesdtException");
		}

	}

	static class CategoryTestException extends Exception {
		public CategoryTestException() {
			super("CategoryException");
		}
	}

	public static void main(String[] args) throws Exception {

		String manifestPath = "test-data/k9/AndroidManifest.xml";

		AndroidManifestParser androidManifest = new AndroidManifestParser(
				manifestPath);

		Intent it = new Intent();

		it.setAction("android.intent.action.MAIN");
		it.addData("mailto://contacts/people/1");
		//it.addCategory("android.intent.category.DEFAULT");

		for (Component c : androidManifest.components) {
			System.out.println(IntentResolution.resolve(it, c));
		}

	}

}
