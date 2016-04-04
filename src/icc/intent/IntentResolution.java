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

	private static void actionTest(Intent it, IntentFilter ifilter) throws ActionTestException {
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

	private static void dataTest(Intent it, IntentFilter ifilter) throws DataTestException {
		System.out.println(ifilter.data);

		DataTestException e = new DataTestException();
		if (it.getData().scheme == null && it.getData().mimeType == null) {

			if (ifilter.data.scheme != null || ifilter.data.mimeType != null) {
				throw e;
			}
			// TODO nothing?

		} else

		if (it.getData().scheme != null && it.getData().mimeType == null) {

			if (ifilter.data.mimeType != null && !matchUri(it.getData(), ifilter.data)) {
				throw e;
			}

			// match only uri

		} else

		if (it.getData().scheme == null && it.getData().mimeType != null) {
			if (ifilter.data.scheme != null || !it.getData().mimeType.equals(ifilter.data.mimeType)) {
				throw e;
			}
		} else {

			// if (it.getData().scheme != null && it.getData().mimeType != null)
			// {

			if (ifilter.data.scheme == null || ifilter.data.mimeType == null) {
				throw e;
			}

			// TODO ....
		}

	}

	private static boolean matchUriPath(IntentFilter.Data dt1, IntentFilter.Data dt2) {
		boolean match = false;
		if (dt1.path == null && dt2.path == null) {
			match = true;
		} else if (dt1.path != null && dt2.path != null) {
			if (dt1.path.equals(dt2.path)) {
				if (dt1.pathPrefix == null && dt1.pathPrefix == null) {
					match = true;
				} else if (dt1.pathPrefix != null && dt2.pathPrefix != null) {
					if (dt1.pathPrefix.equals(dt2.pathPrefix)) {
						if (dt2.pathPattern == null && dt2.pathPattern == null) {
							match = true;
						} else if (dt1.pathPattern != null && dt2.pathPattern != null) {
							if (dt1.pathPattern.equals(dt2.pathPattern)) {
								match = true;
							}
						}
					}

				}
			}
		}
		return match;
	}

	private static boolean matchUri(IntentFilter.Data dt1, IntentFilter.Data dt2) {
		boolean match = false;
		if (dt1.scheme == null && dt2.scheme == null) {
			match = true;
		} else if (dt1.scheme != null && dt2.scheme != null) {
			if (dt1.scheme.equals(dt2.scheme)) {
				if (dt1.host != null && dt2.host != null) {
					if (dt1.host.equals(dt2.host)) {
						if (dt1.port == null && dt2.port == null || dt1.port != null && dt2.port != null) {
							match = matchUriPath(dt1, dt2);
						}
					}
				} else if (dt1.host == null && dt2.host == null) {
					match = true;
				}

			}
		}
		return match;

	}

	private static void categoryTest(Intent it, IntentFilter ifilter) throws CategoryTestException {

		System.out.println(ifilter.category);

		if (it.getCategories().size() > 0) {
			if (null != ifilter.category) {
				boolean pass = true;
				for (String ctg : it.getCategories()) {
					if (!pass) {
						throw new CategoryTestException();
					}

					// TODO for-each in ifilter.catergory (has to be a list);
					if (ctg.equals(ifilter.category)) {
						pass = true;
					} else {
						pass = false;
					}
				}
			} else {
				throw new CategoryTestException();
			}
		} else if (null != ifilter.category) {
			// TODO what happen when has no category on intent neither on
			// intent-filter?
			// throw new CategoryTestException();
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

		AndroidManifestParser androidManifest = new AndroidManifestParser(manifestPath);

		Intent it = new Intent();

		it.setAction("android.intent.action.MAIN");

		IntentFilter.Data dt = new IntentFilter.Data();
		// "mailto://contacts/people/1"
		dt.scheme = "mailto";
		dt.host = "contacts";

		it.setData(dt);
		// it.addCategory("android.intent.category.DEFAULT");

		for (Component c : androidManifest.components) {
			System.out.println(IntentResolution.resolve(it, c));
		}

	}

}
