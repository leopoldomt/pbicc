package tg.parse;

import java.util.ArrayList;

import tg.DataURI;
import tg.IntentFromJson;
import tg.helper.Constants;
import tg.resolution.IntentForResolution;
import tg.resolution.IntentResolution;

public class IntentParser {

	public static ArrayList<IntentForResolution> parseToResolution(IntentFromJson it, String parentId) {
		ArrayList<IntentForResolution> ifrs = new ArrayList<IntentForResolution>();
		if(it.actions.size() > 0) {
			for(String a : it.actions){
				ifrs.addAll(getMimeTypes(it,parseAction(a), parentId));
			}
			return ifrs;
		} 
		return getMimeTypes(it, null, parentId);
	}

	private static ArrayList<IntentForResolution> getMimeTypes(IntentFromJson ifj, String action, String parentId) {
		ArrayList<IntentForResolution> ifrs = new ArrayList<IntentForResolution>();

		if(ifj.getMimeTypes().size() > 0){
			for(String mimeType : ifj.getMimeTypes()){
				ifrs.addAll(getDataUris(ifj, mimeType, action, parentId));
			}
			return ifrs;
		}
		return 	getDataUris(ifj, null, action, parentId);

	}

	public static ArrayList<IntentForResolution> getDataUris(IntentFromJson ifj, String mimeType, String action, String parentId){
		ArrayList<IntentForResolution> ifrs = new ArrayList<IntentForResolution>();
		DataURI data;
		if(ifj.datas.size() > 0){
			for(String dt : ifj.datas){
				data = parseData(dt, mimeType);
				ifrs.addAll(getComponents(ifj, data, action, parentId));
			}
			return ifrs;
		} 
		
		data = parseData("", mimeType);
		return getComponents(ifj, data, action, parentId);
	}

	private static ArrayList<IntentForResolution> getComponents(IntentFromJson ifj, DataURI data, String action, String parentId) {
		ArrayList<IntentForResolution> ifrs = new ArrayList<IntentForResolution>();
		IntentForResolution ifr;
		if(ifj.getComponents().size()>0){
			for(String c : ifj.getComponents()){
				ifr = new IntentForResolution();
				ifr.setData(data);
				ifr.setParentId(parentId);
				ifr.setAction(action);
				ifr.setComponentName(c);
				for(String ctg: ifj.categories){
					ifr.addCategory(parseCategory(ctg));
				}
				ifr.setMethodType(ifj.methodType);
				ifrs.add(ifr);
			}
		} else {
			ifr = new IntentForResolution();
			ifr.setData(data);
			ifr.setParentId(parentId);
			ifr.setAction(action);
			ifr.setMethodType(ifj.methodType);
			for(String ctg: ifj.categories){
				ifr.addCategory(parseCategory(ctg));
			}
			ifrs.add(ifr);
		}
		return ifrs;
	}

	public static DataURI parseData(String data, String mimeType) {

		if("-".equals(data)) data = "";

		DataURI dt = new DataURI(data);  

		if (!"-".equals(mimeType)) dt.setType(mimeType);		
		return dt;
	}

	public static String parseAction(String act) {
		String finalAction;
		switch (act) {
		case "ACTION_MAIN":
		case "Intent.ACTION_MAIN":
			finalAction = Constants.ACTION_MAIN;
			break;
		case "ACTION_VIEW":
		case "Intent.ACTION_VIEW":
			finalAction = Constants.ACTION_VIEW;
			break;
		case "ATTACH_DATA":
		case "Intent.ACTION_ATTACH_DATA":
			finalAction =  Constants.ACTION_ATTACH_DATA;
			break;
		case "ACTION_EDIT":
		case "Intent.ACTION_EDIT":
			finalAction =  Constants.ACTION_EDIT;
			break;
		case "ACTION_PICK":
		case "Intent.ACTION_PICK":
			finalAction =  Constants.ACTION_PICK;
			break;
		case "ACTION_CHOOSER":
		case "Intent.ACTION_CHOOSER":
			finalAction =  Constants.ACTION_CHOOSER;
			break;
		case "ACTION_GET_CONTENT":
		case "Intent.ACTION_GET_CONTENT":
			finalAction =  Constants.ACTION_GET_CONTENT;
			break;
		case "ACTION_DIAL":
		case "Intent.ACTION_DIAL":
			finalAction =  Constants.ACTION_DIAL;
			break;
		case "ACTION_CALL":
		case "Intent.ACTION_CALL":
			finalAction =  Constants.ACTION_CALL;
			break;
		case "ACTION_SEND":
		case "Intent.ACTION_SEND":
			finalAction =  Constants.ACTION_SEND;
			break;
		case "ACTION_SENDTO":
		case "Intent.ACTION_SENDTO":
			finalAction =  Constants.ACTION_SENDTO;
			break;
		case "ACTION_ANSWER":
		case "Intent.ACTION_ANSWER":
			finalAction =  Constants.ACTION_ANSWER;
			break;
		case "ACTION_INSERT":
		case "Intent.ACTION_INSERT":
			finalAction =  Constants.ACTION_INSERT;
			break;
		case "ACTION_DELETE":
		case "Intent.ACTION_DELETE":
			finalAction =  Constants.ACTION_DELETE;
			break;
		case "ACTION_RUN":
		case "Intent.ACTION_RUN":
			finalAction =  Constants.ACTION_RUN;
			break;
		case "ACTION_SYNC":
		case "Intent.ACTION_SYNC":
			finalAction =  Constants.ACTION_SYNC;
			break;
		case "ACTION_PICK_ACTIVITY":
		case "Intent.ACTION_PICK_ACTIVITY":
			finalAction =  Constants.ACTION_PICK_ACTIVITY;
			break;
		case "ACTION_SEARCH":
		case "Intent.ACTION_SEARCH":
			finalAction =  Constants.ACTION_SEARCH;
			break;
		case "ACTION_WEB_SEARCH":
		case "Intent.ACTION_WEB_SEARCH":
			finalAction =  Constants.ACTION_WEB_SEARCH;
			break;
		case "ACTION_FACTORY_TEST":
		case "Intent.ACTION_FACTORY_TEST":
			finalAction =  Constants.ACTION_FACTORY_TEST;
			break;
		case "ACTION_TIME_TICK":
		case "Intent.ACTION_TIME_TICK":
			finalAction =  Constants.ACTION_TIME_TICK;
			break;
		case "ACTION_TIME_CHANGED":
		case "Intent.ACTION_TIME_CHANGED":
			finalAction =  Constants.ACTION_TIME_CHANGED;
			break;
		case "ACTION_TIMEZONE_CHANGED":
		case "Intent.ACTION_TIMEZONE_CHANGED":
			finalAction =  Constants.ACTION_TIMEZONE_CHANGED;
			break;
		case "ACTION_BOOT_COMPLETED":
		case "Intent.ACTION_BOOT_COMPLETED":
			finalAction =  Constants.ACTION_BOOT_COMPLETED;
			break;
		case "ACTION_PACKAGE_ADDED":
		case "Intent.ACTION_PACKAGE_ADDED":
			finalAction =  Constants.ACTION_PACKAGE_ADDED;
			break;
		case "ACTION_PACKAGE_CHANGED":
		case "Intent.ACTION_PACKAGE_CHANGED":
			finalAction =  Constants.ACTION_PACKAGE_CHANGED;
			break;
		case "ACTION_PACKAGE_REMOVED":
		case "Intent.ACTION_PACKAGE_REMOVED":
			finalAction =  Constants.ACTION_PACKAGE_REMOVED;
			break;
		case "ACTION_PACKAGE_RESTARTED":
		case "Intent.ACTION_PACKAGE_RESTARTED":
			finalAction =  Constants.ACTION_PACKAGE_RESTARTED;
			break;
		case "ACTION_PACKAGE_DATA_CLEARED":
		case "Intent.ACTION_PACKAGE_DATA_CLEARED":
			finalAction =  Constants.ACTION_PACKAGE_DATA_CLEARED;
			break;
		case "ACTION_UID_REMOVED":
		case "Intent.ACTION_UID_REMOVED":
			finalAction =  Constants.ACTION_UID_REMOVED;
			break;
		case "ACTION_BATTERY_CHANGED":
		case "Intent.ACTION_BATTERY_CHANGED":
			finalAction =  Constants.ACTION_BATTERY_CHANGED;
			break;
		case "ACTION_POWER_CONNECTED":
		case "Intent.ACTION_POWER_CONNECTED":
			finalAction =  Constants.ACTION_POWER_CONNECTED;
			break;
		case "ACTION_POWER_DISCONNECTED":
		case "Intent.ACTION_POWER_DISCONNECTED":
			finalAction =  Constants.ACTION_POWER_DISCONNECTED;
			break;
		case "ACTION_SHUTDOWN":
		case "Intent.ACTION_SHUTDOWN":
			finalAction =  Constants.ACTION_SHUTDOWN;
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
			finalCtg = Constants.CATEGORY_DEFAULT;
			break;
		case "Intent.CATEGORY_BROWSABLE":
			finalCtg = Constants.CATEGORY_BROWSABLE;
			break;
		case "Intent.CATEGORY_TAB":
			finalCtg = Constants.CATEGORY_TAB;
			break;
		case "Intent.CATEGORY_ALTERNATIVE":
			finalCtg = Constants.CATEGORY_ALTERNATIVE;
			break;
		case "Intent.CATEGORY_SELECTED_ALTERNATIVE":
			finalCtg = Constants.CATEGORY_SELECTED_ALTERNATIVE;
			break;
		case "Intent.CATEGORY_LAUNCHER":
			finalCtg = Constants.CATEGORY_LAUNCHER;
			break;
		case "Intent.CATEGORY_INFO":
			finalCtg = Constants.CATEGORY_INFO;
			break;
		case "Intent.CATEGORY_HOME":
			finalCtg = Constants.CATEGORY_HOME;
			break;
		case "Intent.CATEGORY_PREFERENCE":
			finalCtg = Constants.CATEGORY_PREFERENCE;
			break;
		case "Intent.CATEGORY_TEST":
			finalCtg = Constants.CATEGORY_TEST;
			break;
		case "Intent.CATEGORY_CAR_DOCK":
			finalCtg = Constants.CATEGORY_CAR_DOCK;
			break;
		case "Intent.CATEGORY_DESK_DOCK":
			finalCtg = Constants.CATEGORY_DESK_DOCK;
			break;
		case "Intent.CATEGORY_LE_DESK_DOCK":
			finalCtg = Constants.CATEGORY_LE_DESK_DOCK;
			break;
		case "Intent.CATEGORY_HE_DESK_DOCK":
			finalCtg = Constants.CATEGORY_HE_DESK_DOCK;
			break;
		case "Intent.CATEGORY_CAR_MODE":
			finalCtg = Constants.CATEGORY_CAR_MODE;
			break;
		case "Intent.CATEGORY_APP_MARKET":
			finalCtg = Constants.CATEGORY_APP_MARKET;
			break;
		default:
			finalCtg = ctg;
			break;
		}
		return finalCtg;
	}
}
