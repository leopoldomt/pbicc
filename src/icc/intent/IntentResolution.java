package icc.intent;

import icc.data.Component;
import icc.data.IntentFilter;
import icc.parsing.AndroidManifestParser;

public class IntentResolution {

	public IntentResolution() {

	}

	public static boolean resolve(Intent it, Component comp) {
		// System.out.println(comp.name);
		boolean response = false;
		for (IntentFilter ifilter : comp.intentFilters) {
			// System.out.println(">>>> Start intent-fiter");

			try {
				actionTest(it, ifilter);
				dataTest(it, ifilter);
				categoryTest(it, ifilter);

				throw new Exception();

			} catch (ActionTestException e1) {
				// System.out.println(e1.getMessage());
				response = false;
			} catch (DataTestException e2) {
				// System.out.println(e2.getMessage());
				response = false;
			} catch (CategoryTestException e3) {
				// System.out.println(e3.getMessage());
				response = false;
			} catch (Exception e4) {
				response = true;
				// System.out.println(">>>> End intent-filter");
				break;
			}
			// System.out.println(">>>> End intent-filter");
		}
		return response;
	}

	private static void actionTest(Intent it, IntentFilter ifilter)
			throws ActionTestException {
		// System.out.println(ifilter.action);
		if (0 == ifilter.actions.size()) {
			throw new ActionTestException();
		}

		if (null != it.getAction()) {
			//if (!it.getAction().equals(ifilter.action)) {
			if(!ifilter.actions.contains(it.getAction())){
				throw new ActionTestException();
			}
		}

	}

	private static void dataTest(Intent it, IntentFilter ifilter)
			throws DataTestException {
		// System.out.println(ifilter.data);

		DataTestException e = new DataTestException();
		if (it.getData().scheme == null && it.getData().mimeType == null) {

			if (ifilter.data.scheme != null || ifilter.data.mimeType != null) {
				throw e;
			}
			// TODO nothing?

		} else if (it.getData().scheme != null && it.getData().mimeType == null) {

			if (ifilter.data.mimeType == null
					&& !matchUri(it.getData(), ifilter.data)) {
				throw e;
			}

		} else if (it.getData().scheme == null && it.getData().mimeType != null) {
			if (ifilter.data.scheme != null
					|| !matchMimetype(it.getData(), ifilter.data)) {
				throw e;
			}
		} else {
			// (it.getData().scheme != null && it.getData().mimeType != null)
			if (ifilter.data.mimeType == null
					|| !(ifilter.data.scheme == null && (it.getData().scheme
							.equals("content") || it.getData().scheme
							.equals("file")))
					|| (!matchMimetype(it.getData(), ifilter.data) || !matchUri(
							it.getData(), ifilter.data))) {
				throw e;
			}
			// ....
		}

	}

	private static boolean matchMimetype(IntentFilter.Data dt1,
			IntentFilter.Data dt2) {
		boolean match = false;
		// it.getData().mimeType.equals(ifilter.data.mimeType)
		String prefix_dt1 = dt1.mimeType
				.substring(0, dt1.mimeType.indexOf("/"));
		String prefix_dt2 = dt2.mimeType
				.substring(0, dt2.mimeType.indexOf("/"));
		String posfix_dt1 = dt1.mimeType.substring(dt1.mimeType.indexOf("/"));
		String posfix_dt2 = dt2.mimeType.substring(dt2.mimeType.indexOf("/"));

		if (dt1.mimeType.equals(dt2.mimeType)
				|| (dt2.mimeType.startsWith("*/"))
				|| (prefix_dt1.equals(prefix_dt2) && (posfix_dt2.equals("*") || posfix_dt1
						.equals(posfix_dt2)))) {
			match = true;
		}
		return match;

	}

	private static boolean matchUriPath(IntentFilter.Data dt1,
			IntentFilter.Data dt2) {
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
						} else if (dt1.pathPattern != null
								&& dt2.pathPattern != null) {
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
						if (dt1.port == null && dt2.port == null
								|| dt1.port != null && dt2.port != null) {
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

	private static void categoryTest(Intent it, IntentFilter ifilter)
			throws CategoryTestException {

		// System.out.println(ifilter.category);

		if (it.getCategories().size() > 0) {
			if (ifilter.categories.size() > 0) {
				boolean pass = true;
				for (String ctg : it.getCategories()) {
					if (!pass) {
						throw new CategoryTestException();
					}

					if (ifilter.categories.contains(ctg)) {
						pass = true;
					} else {
						pass = false;
					}
				}
			} else {
				throw new CategoryTestException();
			}
		} else if (ifilter.categories.size() > 0) {
			
		} else {
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

		AndroidManifestParser androidManifest = new AndroidManifestParser(
				manifestPath);

		Intent it = new Intent();

		IntentFilter.Data dt = new IntentFilter.Data();

		// it.setAction("android.intent.action.VIEW");
		// it.addCategory("android.intent.category.DEFAULT");
		// dt.scheme = "email";
		// dt.host = "messages";

		// it.setAction("android.intent.action.SENDTO");
		// it.addCategory("android.intent.category.DEFAULT");
		// dt.scheme = "mailto";

		// it.setAction("android.intent.action.SEND");
		// it.addCategory("android.intent.category.DEFAULT");
		// dt.mimeType = "text/*";

		it.setAction("android.intent.action.MEDIA_MOUNTED");
		dt.scheme = "file";

		it.setData(dt);

		for (Component c : androidManifest.components) {
			System.out.println(String.format("%s - %s", c.name,
					IntentResolution.resolve(it, c)));
		}

	}

}
