package tg;

import icc.data.IntentFilter.Data;
import icc.intent.IntentForResolution;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class IntentParser {

	public static ArrayList<IntentForResolution> parse(IntentFromJson it) {
		ArrayList<IntentForResolution> its = new ArrayList<IntentForResolution>();
		IntentForResolution ifr;

		if(it.actions.size()>0){
			for(String action: it.actions){
				if("-".equals(it.component)) ifr = new IntentForResolution();
				else ifr = new IntentForResolution(it.component); 

				ifr.setAction(parseAction(action));
				ifr.setData(parseData(it.data, it.mimeType));
				for(String ctg: it.categories){
					ifr.addCategory(parseCategory(ctg));
				}
				its.add(ifr);
			}
		} else {
			if("-".equals(it.component)) ifr = new IntentForResolution();
			else ifr = new IntentForResolution(it.component); 

			ifr.setData(parseData(it.data, it.mimeType));
			for(String ctg: it.categories){
				ifr.addCategory(parseCategory(ctg));
			}
			its.add(ifr);
		}

		return its;
	}


	private static String getScheme(String data) {
		String scheme = null;
		int index = data.indexOf(":");
		if(index != -1) {
			scheme = data.substring(0, index);
		}
		return scheme;
	}

	private static String getHost(String data, String scheme) {
		String host = null;
		if (scheme != null) {
			String scheme_specific_part = data.substring(scheme.length()+1);
			//System.out.println(data);
			if (scheme_specific_part.startsWith("//")) {
				//System.out.println("hier_part.net_path");
				
				String data_without_double_slash = scheme_specific_part.substring(2);
//				System.out.println(data_without_double_slash);
				
				int index = data_without_double_slash.indexOf(":");
				if (index<=0) {
					index = data_without_double_slash.indexOf("/"); 
					if (index<=0) {
						index = data_without_double_slash.indexOf("?"); 
						if (index<=0) {
							index = data_without_double_slash.length();
						}
					}
				}
				host = data_without_double_slash.substring(0, index);
	//			System.out.println(host);
			} else if (scheme_specific_part.startsWith("/")) {
				//System.out.println("hier_part.abs_path");
			} else {
				//System.out.println("opaque_part");
			}
		}
		return host;
	}

	private static String getPort(String data, String scheme, String host) {
		//System.out.println("getPort()");
		String port = null;
		if (host != null) {
			String scheme_specific_part = data.substring(data.indexOf(host));
			//System.out.println(scheme_specific_part);
			
			String data_without_host = scheme_specific_part.substring(host.length());
			//System.out.println(data_without_host);
			
			if (data_without_host.startsWith(":")) {
				int index = data_without_host.indexOf("/");
				if (index<=0) {
					index = data_without_host.indexOf("?");
					if (index<=0) {
						index = data_without_host.length();
					}
				}
				
				port = data_without_host.substring(1, index);
				//System.out.println(port);
			}
		}
		return port;
	}

	private static String getPath(String data, String scheme, String host,
			String port) {
		//System.out.println("getPath()");
		String path = null;
		if (host != null) {
			//System.out.println(host);
			int index = data.indexOf("/", scheme.length()+host.length() + ((port!=null)?port.length():0));
			
			if (index != -1) {
				path = data.substring(index);
			}
			
			//System.out.println("-"+path);
		}
		return path;
	}




	public static Data parseData(String data, String mimeType) {

		Data dt = new Data();

		dt.scheme = getScheme(data);
		dt.host = getHost(data, dt.scheme);
		dt.port = getPort(data, dt.scheme, dt.host);
		dt.path = getPath(data, dt.scheme, dt.host, dt.port);

		dt.mimeType = "-".equals(mimeType) ? null : mimeType;
		return dt;
	}




	/*public static Data parseData(String data, String mimeType) {
		Data dt = new Data();

		URI uri;
		try {
			uri = new URI(data);
			//System.out.println(uri);
			dt.scheme = uri.getScheme();
			dt.host = uri.getHost();
			dt.port = uri.getPort() != -1 ? uri.getPort()+"" : null;
			dt.path = uri.getPath();
			dt.mimeType = "-".equals(mimeType) ? null : mimeType;

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dt;
	}
	 */
	/*public static Data parseData(String data) {
		Data dt = new Data();
		int index = data.lastIndexOf("://");		
		if(index > 0){
			//<schema>://
			dt.scheme = data.substring(0, index);
			int i = data.indexOf(":", index+2);
			if(i>0){
				// <schema>://<host>:<port>
				dt.host = data.substring(index+3,i);
				int j = data.indexOf("/", i+1);
				if(j > 0) {
					// <schema>://<host>:<port>/<path...>
					dt.port = data.substring(i+1, j);
					dt.path = data.substring(j);

				} else {
					// <schema>://<host>:<port>
					dt.port = data.substring(i+1);
				}
			} else {

				if(data.length() > dt.scheme.length()+3) {
					// <schema>://<host>
				 	dt.host = data.substring(index+3);
				} else {
					// <schema>://
				}
			}
		}	
		return dt;
	}
	 */
	public static String parseAction(String act) {
		String finalAction;
		switch (act) {
		case "ACTION_MAIN":
		case "Intent.ACTION_MAIN":
			finalAction = Intent.ACTION_MAIN;
			break;
		case "ACTION_VIEW":
		case "Intent.ACTION_VIEW":
			finalAction = Intent.ACTION_VIEW;
			break;
		case "ATTACH_DATA":
		case "Intent.ACTION_ATTACH_DATA":
			finalAction =  Intent.ACTION_ATTACH_DATA;
			break;
		case "ACTION_EDIT":
		case "Intent.ACTION_EDIT":
			finalAction =  Intent.ACTION_EDIT;
			break;
		case "ACTION_PICK":
		case "Intent.ACTION_PICK":
			finalAction =  Intent.ACTION_PICK;
			break;
		case "ACTION_CHOOSER":
		case "Intent.ACTION_CHOOSER":
			finalAction =  Intent.ACTION_CHOOSER;
			break;
		case "ACTION_GET_CONTENT":
		case "Intent.ACTION_GET_CONTENT":
			finalAction =  Intent.ACTION_GET_CONTENT;
			break;
		case "ACTION_DIAL":
		case "Intent.ACTION_DIAL":
			finalAction =  Intent.ACTION_DIAL;
			break;
		case "ACTION_CALL":
		case "Intent.ACTION_CALL":
			finalAction =  Intent.ACTION_CALL;
			break;
		case "ACTION_SEND":
		case "Intent.ACTION_SEND":
			finalAction =  Intent.ACTION_SEND;
			break;
		case "ACTION_SENDTO":
		case "Intent.ACTION_SENDTO":
			finalAction =  Intent.ACTION_SENDTO;
			break;
		case "ACTION_ANSWER":
		case "Intent.ACTION_ANSWER":
			finalAction =  Intent.ACTION_ANSWER;
			break;
		case "ACTION_INSERT":
		case "Intent.ACTION_INSERT":
			finalAction =  Intent.ACTION_INSERT;
			break;
		case "ACTION_DELETE":
		case "Intent.ACTION_DELETE":
			finalAction =  Intent.ACTION_DELETE;
			break;
		case "ACTION_RUN":
		case "Intent.ACTION_RUN":
			finalAction =  Intent.ACTION_RUN;
			break;
		case "ACTION_SYNC":
		case "Intent.ACTION_SYNC":
			finalAction =  Intent.ACTION_SYNC;
			break;
		case "ACTION_PICK_ACTIVITY":
		case "Intent.ACTION_PICK_ACTIVITY":
			finalAction =  Intent.ACTION_PICK_ACTIVITY;
			break;
		case "ACTION_SEARCH":
		case "Intent.ACTION_SEARCH":
			finalAction =  Intent.ACTION_SEARCH;
			break;
		case "ACTION_WEB_SEARCH":
		case "Intent.ACTION_WEB_SEARCH":
			finalAction =  Intent.ACTION_WEB_SEARCH;
			break;
		case "ACTION_FACTORY_TEST":
		case "Intent.ACTION_FACTORY_TEST":
			finalAction =  Intent.ACTION_FACTORY_TEST;
			break;
		case "ACTION_TIME_TICK":
		case "Intent.ACTION_TIME_TICK":
			finalAction =  Intent.ACTION_TIME_TICK;
			break;
		case "ACTION_TIME_CHANGED":
		case "Intent.ACTION_TIME_CHANGED":
			finalAction =  Intent.ACTION_TIME_CHANGED;
			break;
		case "ACTION_TIMEZONE_CHANGED":
		case "Intent.ACTION_TIMEZONE_CHANGED":
			finalAction =  Intent.ACTION_TIMEZONE_CHANGED;
			break;
		case "ACTION_BOOT_COMPLETED":
		case "Intent.ACTION_BOOT_COMPLETED":
			finalAction =  Intent.ACTION_BOOT_COMPLETED;
			break;
		case "ACTION_PACKAGE_ADDED":
		case "Intent.ACTION_PACKAGE_ADDED":
			finalAction =  Intent.ACTION_PACKAGE_ADDED;
			break;
		case "ACTION_PACKAGE_CHANGED":
		case "Intent.ACTION_PACKAGE_CHANGED":
			finalAction =  Intent.ACTION_PACKAGE_CHANGED;
			break;
		case "ACTION_PACKAGE_REMOVED":
		case "Intent.ACTION_PACKAGE_REMOVED":
			finalAction =  Intent.ACTION_PACKAGE_REMOVED;
			break;
		case "ACTION_PACKAGE_RESTARTED":
		case "Intent.ACTION_PACKAGE_RESTARTED":
			finalAction =  Intent.ACTION_PACKAGE_RESTARTED;
			break;
		case "ACTION_PACKAGE_DATA_CLEARED":
		case "Intent.ACTION_PACKAGE_DATA_CLEARED":
			finalAction =  Intent.ACTION_PACKAGE_DATA_CLEARED;
			break;
		case "ACTION_UID_REMOVED":
		case "Intent.ACTION_UID_REMOVED":
			finalAction =  Intent.ACTION_UID_REMOVED;
			break;
		case "ACTION_BATTERY_CHANGED":
		case "Intent.ACTION_BATTERY_CHANGED":
			finalAction =  Intent.ACTION_BATTERY_CHANGED;
			break;
		case "ACTION_POWER_CONNECTED":
		case "Intent.ACTION_POWER_CONNECTED":
			finalAction =  Intent.ACTION_POWER_CONNECTED;
			break;
		case "ACTION_POWER_DISCONNECTED":
		case "Intent.ACTION_POWER_DISCONNECTED":
			finalAction =  Intent.ACTION_POWER_DISCONNECTED;
			break;
		case "ACTION_SHUTDOWN":
		case "Intent.ACTION_SHUTDOWN":
			finalAction =  Intent.ACTION_SHUTDOWN;
			break;
		default:
			finalAction = act;
			break;
		}

		return finalAction;
	}

	public static String parseCategory(String ctg) {
		String finalCtg;

		switch (ctg) {
		case "Intent.CATEGORY_DEFAULT":
			finalCtg = Intent.CATEGORY_DEFAULT;
			break;
		case "Intent.CATEGORY_BROWSABLE":
			finalCtg = Intent.CATEGORY_BROWSABLE;
			break;
		case "Intent.CATEGORY_TAB":
			finalCtg = Intent.CATEGORY_TAB;
			break;
		case "Intent.CATEGORY_ALTERNATIVE":
			finalCtg = Intent.CATEGORY_ALTERNATIVE;
			break;
		case "Intent.CATEGORY_SELECTED_ALTERNATIVE":
			finalCtg = Intent.CATEGORY_SELECTED_ALTERNATIVE;
			break;
		case "Intent.CATEGORY_LAUNCHER":
			finalCtg = Intent.CATEGORY_LAUNCHER;
			break;
		case "Intent.CATEGORY_INFO":
			finalCtg = Intent.CATEGORY_INFO;
			break;
		case "Intent.CATEGORY_HOME":
			finalCtg = Intent.CATEGORY_HOME;
			break;
		case "Intent.CATEGORY_PREFERENCE":
			finalCtg = Intent.CATEGORY_PREFERENCE;
			break;
		case "Intent.CATEGORY_TEST":
			finalCtg = Intent.CATEGORY_TEST;
			break;
		case "Intent.CATEGORY_CAR_DOCK":
			finalCtg = Intent.CATEGORY_CAR_DOCK;
			break;
		case "Intent.CATEGORY_DESK_DOCK":
			finalCtg = Intent.CATEGORY_DESK_DOCK;
			break;
		case "Intent.CATEGORY_LE_DESK_DOCK":
			finalCtg = Intent.CATEGORY_LE_DESK_DOCK;
			break;
		case "Intent.CATEGORY_HE_DESK_DOCK":
			finalCtg = Intent.CATEGORY_HE_DESK_DOCK;
			break;
		case "Intent.CATEGORY_CAR_MODE":
			finalCtg = Intent.CATEGORY_CAR_MODE;
			break;
		case "Intent.CATEGORY_APP_MARKET":
			finalCtg = Intent.CATEGORY_APP_MARKET;
			break;
		default:
			finalCtg = ctg;
			break;
		}
		return finalCtg;
	}
}
