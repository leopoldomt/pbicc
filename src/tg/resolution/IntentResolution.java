package tg.resolution;

import tg.DataURI;
import icc.data.Component;
import icc.data.Data;
import icc.data.IntentFilter;
import icc.parsing.AndroidManifestParser;

public class IntentResolution {

	
	public IntentResolution() {

	}
	

	public static Result resolve(IntentForResolution it, Component comp) {
		
		Result response = new Result();
		response.match = false;

		if(it.getComponentName() != null) {
			if(it.getComponentName().equals(comp.name)) {
				response.match = true;
				return response;
			} else {
				response.reason = String.format("Explict Intent: not match(%s x %s)", it.getComponentName(), comp.name);
				return response;
			}
		} 

		for (IntentFilter ifilter : comp.intentFilters) {
			// System.out.println(">>>> Start intent-fiter");

			try {
				actionTest(it, ifilter);
				dataTest(it, ifilter);
				categoryTest(it, ifilter);

				throw new Exception();

			} catch (ActionTestException e1) {
				// System.out.println(e1.getMessage());
				response.match = false;
				response.reason = e1.getMessage();
			} catch (DataTestException e2) {
				// System.out.println(e2.getMessage());
				response.match = false;
				response.reason = e2.getMessage();
			} catch (CategoryTestException e3) {
				// System.out.println(e3.getMessage());
				response.match = false;
				response.reason = e3.getMessage();
			} catch (Exception e4) {
				response.match = true;
				// System.out.println(">>>> End intent-filter");
				break;
			}
			// System.out.println(">>>> End intent-filter");
		}
		return response;
	}

	private static void actionTest(IntentForResolution it, IntentFilter ifilter)
			throws ActionTestException {
		// System.out.println(ifilter.action);
		if (!ifilter.actions.contains(it.getAction())) {
			throw new ActionTestException("filter not contains intent action.");
		}
	}
	

	private static void dataTest(IntentForResolution it, IntentFilter ifilter)
			throws DataTestException {
		//System.out.println(it.getData());
		if (it.getData().getScheme() != null && !ifilter.data.getSchemes().isEmpty()) {
			// i(+,_) 
			// f(+,_)
			if (it.getData().getType() != null && ifilter.data.getMimeTypes().isEmpty()) {
				// i(+,+) 
				// f(+,-)
				throw new DataTestException("intent has mimetype, ifilter hasn't mimetype.");
			}
			
			if (it.getData().getType() == null && !ifilter.data.getMimeTypes().isEmpty()) {
				// i(+,-) 
				// f(+,+)
				throw new DataTestException("intent hasn't mimetype, ifilter has mimetype.");
			}
			// i(+,+) 
			// f(+,+)
			
			if (!matchUri(it.getData(), ifilter.data)){
				throw new DataTestException("uri not match.");
			}
			
			if (!matchMimetype(it.getData(), ifilter.data)) {
				throw new DataTestException("mimeType not match.");
			}
			return;
		} 
		
		if (it.getData().getScheme() != null && ifilter.data.getSchemes().isEmpty()) {
			// i(+,_) 
			// f(-,_)
			
			if (it.getData().getType() != null && ifilter.data.getMimeTypes().isEmpty()) {
				// i(+,+) 
				// f(-,-)
				throw new DataTestException("filter hasn't scheme neither mimeType.");
			}
			
			if (it.getData().getType() == null && ifilter.data.getMimeTypes().isEmpty()) {
				// i(+,-) 
				// f(-,-)
				throw new DataTestException("filter hasn't scheme neither mimeType.");
			}
			
			
			if (it.getData().getType() == null && !ifilter.data.getMimeTypes().isEmpty()) {
				// i(+,-) 
				// f(-,+)
				throw new DataTestException("intent has uri but not mimeType. ifilter has mimeType but not uri.");
			}
			
			// i(+,+) 
			// f(-,+)
						
			if (it.getData().getScheme().equals("file") || it.getData().getScheme().equals("content") ) {
				throw new DataTestException("intent uri.scheme isn't <file> neither <content>.");
			}
			
			if (!matchMimetype(it.getData(), ifilter.data)) {
				throw new DataTestException("mimeType not match.");
			}		
			return;
		}
		
		
		if (it.getData().getScheme() == null && !ifilter.data.getSchemes().isEmpty()) {
			// i(-,_) 
			// f(+,_)
			throw new DataTestException("intent hasn't scheme and filter has it.");
		}
		
		if (it.getData().getScheme() == null && ifilter.data.getSchemes().isEmpty()) {
			// i(-,_) 
			// f(-,_)
			
			if (it.getData().getType() != null && ifilter.data.getMimeTypes().isEmpty()) {
				// i(-,+) 
				// f(-,-)
				throw new DataTestException("filter hasn't data.");
			}
			
			if (it.getData().getType() == null && !ifilter.data.getMimeTypes().isEmpty()) {
				// i(-,-) 
				// f(-,+)				
				throw new DataTestException("intent hasn't data.");
			}
			
			
			if (it.getData().getType() != null && !ifilter.data.getMimeTypes().isEmpty()){
				// i(-,+) 
				// f(-,+)
				if(!matchMimetype(it.getData(), ifilter.data)) {
					throw new DataTestException("mimeType not match.");
				}
			}
			
			// i(-,-) 
			// f(-,-)

			return;
		}
		
	}

	private static boolean matchMimetype(DataURI intData,
			Data filtData) {
		boolean match = false;
		// it.getData().getType().equals(ifilter.data.mimeType)
		
		
		String prefix_dt1 = intData.getType()
				.substring(0, intData.getType().indexOf("/"));
		
		//String prefix_dt2 = filtData.getMimeTypes().substring(0, filtData.getMimeTypes().indexOf("/"));
		
		
		String posfix_dt1 = intData.getType().substring(intData.getType().indexOf("/"));
		//String posfix_dt2 = filtData.getMimeTypes().substring(filtData.getMimeTypes().indexOf("/"));

		if (filtData.getMimeTypes().contains(intData.getType())
				|| (filtData.hasGenericMimeTypePrefix)
				|| filtData.getPrefixesOfMimeTypeWithGenericSufix().contains(prefix_dt1) 
				/*(prefix_dt1.equals(prefix_dt2) && (posfix_dt2.equals("*")*/
				
				|| filtData.getMimeTypeSufixes().contains((posfix_dt1))) { 
				/*posfix_dt1.equals(posfix_dt2)*/
			match = true;
		}
		return match;

	}

	private static boolean matchUriPath(DataURI intentData,
			Data filterData) {
		//TODO A path specification can contain a wildcard asterisk (*) to require only 
		//a partial match of the path name.
		boolean match = false;
		if (intentData.getPath() == null && filterData.getPaths().isEmpty()) {
			match = true;
		} else if (intentData.getPath() != null && !filterData.getPaths().isEmpty()) {
			if (filterData.getPaths().contains(intentData.getPath())) {
				/*if (intentData.pathPrefix == null && intentData.pathPrefix == null) {
					match = true;
				} else if (intentData.pathPrefix != null && filterData.pathPrefix != null) {
					if (intentData.pathPrefix.equals(filterData.pathPrefix)) {
						if (filterData.pathPattern == null && filterData.pathPattern == null) {
							match = true;
						} else if (intentData.pathPattern != null
								&& filterData.pathPattern != null) {
							if (intentData.pathPattern.equals(filterData.pathPattern)) {
								match = true;
							}
						}
					}

				}*/
				match = true;
			}
		} else {
			if(filterData.getPaths().isEmpty()){
				match = true;
			}
		}
		return match;
	}

	private static boolean matchUri(DataURI intentData, Data filterData) {

		boolean match = false;
		
		
		if (intentData.getScheme() == null && filterData.getSchemes().isEmpty()) {
			match = true;
		} else if (intentData.getScheme() != null && !filterData.getSchemes().isEmpty()) {
			if (filterData.getSchemes().contains(intentData.getScheme())) {
				if (intentData.getHost() != null && !filterData.getHosts().isEmpty()) {
					if (filterData.getHosts().contains(intentData.getHost())) {

						if (intentData.getPort() == 0 && filterData.getPorts().isEmpty()) {
							if (intentData.getPath() != null && !filterData.getPaths().isEmpty()) {
								match = matchUriPath(intentData, filterData);	
							}
						} else {
							if ((intentData.getPort()+"") != null && !filterData.getPorts().isEmpty()){
								if(filterData.getPorts().contains(intentData.getPort()+"")) {
									match = matchUriPath(intentData, filterData);
								}
							}
						}
					}
				} else if (filterData.getHosts().isEmpty()) {
					match = true;
				}
			}
		} 
		return match;

	}

	private static void categoryTest(IntentForResolution it, IntentFilter ifilter)
			throws CategoryTestException {

		// System.out.println(ifilter.category);

		for (String category : it.getCategories()) {
			if (!ifilter.categories.contains(category)){
				throw new CategoryTestException("Intent-Filter not contais Intent's category. Intent hasn't too.");
			}
		}

	}

	static class ActionTestException extends Exception {
		public String msg;
		
		private ActionTestException() {
			super("ActionTestException");
		}
		public ActionTestException(String msg) {
			this();
			this.msg = msg;
		}
		
		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			return super.getMessage() + " - "+msg;
		}
	}

	static class DataTestException extends Exception {
		public String msg;

		private DataTestException() {
			super("DataTestException");
		}
		public DataTestException(String msg) {
			this();
			this.msg = msg;
		}
		
		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			return super.getMessage() + " - "+msg;
		}

	}

	static class CategoryTestException extends Exception {
		public String msg;

		private CategoryTestException() {
			super("CategoryTestException");
		}
		public CategoryTestException(String msg) {
			this();
			this.msg = msg;
		}
		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			return super.getMessage() + " - "+msg;
		}
	}

	public static void main(String[] args) throws Exception {

		String manifestPath = "test-data/k9/AndroidManifest.xml";

		AndroidManifestParser androidManifest = new AndroidManifestParser(
				manifestPath);

		IntentForResolution it = new IntentForResolution();

		Data dt = new Data();

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
		dt.addScheme("file");

		//it.setData(dt);

		for (Component c : androidManifest.components) {
			System.out.println(String.format("%s - %s", c.name,
					IntentResolution.resolve(it, c)));
		}

	}
	
	
	public static class Result {
		public boolean match;
		public String reason;
	}

}
