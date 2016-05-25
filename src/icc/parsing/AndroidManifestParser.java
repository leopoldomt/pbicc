package icc.parsing;

import icc.data.Activity;
import icc.data.Application;
import icc.data.BroadcastReceiver;
import icc.data.Component;
import icc.data.ComponentType;
import icc.data.ContentProvider;
import icc.data.IntentFilter;
import icc.data.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AndroidManifestParser extends DefaultHandler {
	static final String MANIFEST_TAG = "manifest";
	static final String APPLICATION_TAG = "application";
	static final String ACTIVITY_TAG = "activity";
	static final String SERVICE_TAG = "service";
	static final String PROVIDER_TAG = "provider";
	static final String RECEIVER_TAG = "receiver";
	static final String INTENT_FILTER_TAG = "intent-filter";
	static final String ACTION_TAG = "action";
	static final String CATEGORY_TAG = "category";
	static final String DATA_TAG = "data";
	static final String USES_PERMISSIONS_TAG = "uses-permission";
	static final String USES_SDK_TAG = "uses-sdk";
	

	static final String DATA_SCHEME_ATTR = "android:scheme";
	static final String DATA_HOST_ATTR = "android:host";
	static final String DATA_PORT_ATTR = "android:port";
	static final String DATA_PATH_ATTR = "android:path";
	static final String DATA_PATH_PATTERN_ATTR = "android:pathPattern";
	static final String DATA_PATH_PREFIX_ATTR = "android:pathPrefix";
	static final String DATA_MIME_TYPE_ATTR = "android:mimeType";

	public String appPackage;
	public List<Component> components;
	public List<String> permissions;
	
	//TODO maybe create a new class (UsesSdk Class)
	public int minSdkVersion;
	public int targetSdkVersion;
	//public int maxSdkVersion;

	public Application application;
	// TODO: remove the following 2 lines once we fix the remaining of the
	// implementation
	public Map<String, List<IntentFilter>> intentFilters;
	String currentComponent;

	Component currComponent;
	IntentFilter currentIntentFilter;

	public AndroidManifestParser(){
		this.permissions = new LinkedList<String>();
		this.components = new LinkedList<Component>();
		this.intentFilters = new HashMap<String, List<IntentFilter>>();
	}
	
	public AndroidManifestParser(String manifestPath) throws Exception {
		this.permissions = new LinkedList<String>();
		this.components = new LinkedList<Component>();
		this.intentFilters = new HashMap<String, List<IntentFilter>>();
		this.application = new Application();

		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(new FileInputStream(new File(manifestPath)), this);
	}

	
	public void setCommonComponentsAttrs(Component c, Attributes attrs) {
		// TODO: maybe checking if getValue == null so we do not set label and other attributes to null?
		String s;
		c.label = null == (s = attrs.getValue("android:label")) ? c.label : s;
		c.name = null == (s = attrs.getValue("android:name")) ? c.name : s;
		
		String permission = attrs.getValue("android:permission");
		
		switch (c.type) {
		case CONTENT_PROVIDER:
			c.permission = null == (permission) ? c.permission : permission;
			break;
		default:
			if(null == permission){
				c.permission = this.application.permission;
			} else {
				c.permission = permission;
			}
			break;
		}
				
		c.process = null == (s = attrs.getValue("android:process")) ? this.application.process : s;
		c.enabled = ("false").equals(attrs.getValue("android:enabled")) ? false : true;
		//c.icon = null == (s = attributes.getValue("android:icon")) ? c.icon : s;

		// It is a common attribute, but may have different rules (by component)
		c.exported = ("true").equals(attrs.getValue("android:exported")) ? true : false;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
		
		case MANIFEST_TAG:
			if (attributes != null) {
				this.appPackage = attributes.getValue("package");
			}
			break;

		case USES_PERMISSIONS_TAG:
			if (attributes != null) {
				this.permissions.add(attributes.getValue("android:name"));
			}
			break;
		case USES_SDK_TAG:
			String in;
			this.minSdkVersion = null == (in = attributes.getValue("android:minSdkVersion")) ? 1 : Integer.parseInt(in);
			this.targetSdkVersion = null == (in = attributes.getValue("android:targetSdkVersion")) ? this.minSdkVersion : Integer.parseInt(in);
			 
			break;
		case APPLICATION_TAG:
			String a;
			if(attributes != null){
				this.application.allowTaskReparenting = ("true").equals(attributes.getValue("android:allowTaskReparenting")) ? true : false;
				this.application.allowBackup = ("false").equals(attributes.getValue("android:allowBackup")) ? false : true;
				this.application.backupAgent = (null) == (a = attributes.getValue("android:backupAgent")) ? application.backupAgent : a;
				this.application.banner = (null) == (a = attributes.getValue("android:banner")) ? application.banner : a;
				this.application.debuggable = ("true").equals(attributes.getValue("android:debuggable")) ? true : false;
				this.application.description = (null) == (a = attributes.getValue("android:description")) ? application.description : a;
				this.application.enabled = ("false").equals(attributes.getValue("android:enabled")) ? false : true;
				this.application.hasCode= ("false").equals(attributes.getValue("android:hasCode")) ? false : true; 
				
				this.application.hardwareAcceleratedWasSetted = null == attributes.getValue("android:hardwareAccelerated") ? false : true;
				if(!this.application.hardwareAcceleratedWasSetted){
					this.application.setDefaulthardwareAccelerated(this.minSdkVersion, this.targetSdkVersion);
				} else {
					this.application.hardwareAccelerated = ("true").equals(attributes.getValue("android:hardwareAccelerated")) ? true : false;
				}
								
				this.application.icon = (null) == (a = attributes.getValue("android:icon")) ? application.icon : a;
				this.application.isGame = ("true").equals(attributes.getValue("android:isGame")) ? true : false;
				this.application.killAfterRestore = ("false").equals(attributes.getValue("android:killAfterRestore")) ? false : true;
				this.application.largeHeap = ("true").equals(attributes.getValue("android:largeHeap")) ? true : false;
				this.application.label = (null) == (a = attributes.getValue("android:label")) ? application.label : a;
				this.application.logo = (null) == (a = attributes.getValue("android:logo")) ? application.logo : a;
				this.application.manageSpaceActivity = (null) == (a = attributes.getValue("android:manageSpaceActivity")) ? application.manageSpaceActivity : a;
				this.application.name = (null) == (a = attributes.getValue("android:name")) ? application.name: a;
				this.application.permission = (null) == (a = attributes.getValue("android:permission")) ? application.permission : a;
				this.application.persistent = ("true").equals(attributes.getValue("android:persistent")) ? true : false;
			    this.application.process = (null) == (a = attributes.getValue("android:process")) ? this.appPackage : a ;
				this.application.restoreAnyVersion = ("true").equals(attributes.getValue("android:restoreAnyVersion")) ? true : false;;
				this.application.requiredAccountType = (null) == (a = attributes.getValue("android:requiredAccountType")) ? application.requiredAccountType : a;
				this.application.restrictedAccountType = (null) == (a = attributes.getValue("android:restrictedAccountType")) ? application.restrictedAccountType : a;
				this.application.supportsRtl = ("true").equals(attributes.getValue("android:supportsRtl")) ? true : false;;;
			    
			    this.application.taskAffinity = (null) == (a = attributes.getValue("android:taskAffinity")) ? this.appPackage : a;
			    
				this.application.testOnly = ("true").equals(attributes.getValue("android:testOnly")) ? true : false;
				this.application.theme = (null) == (a = attributes.getValue("android:theme")) ? application.theme : a;
				this.application.uiOptions = (null) == (a = attributes.getValue("android:uiOptions")) ? application.uiOptions : a;;
				this.application.usesCleartextTraffic  = ("false").equals(attributes.getValue("android:usesCleartextTraffic")) ? false : true;
				this.application.vmSafeMode = ("true").equals(attributes.getValue("vmSafeMode")) ? true : false;;
			}			
			break;
		case ACTIVITY_TAG:
			if (attributes != null) {
				String s;
				Activity c = new Activity();
				setCommonComponentsAttrs(c, attributes);
				c.allowEmbedded = ("true").equals(attributes.getValue("android:allowEmbedded")) ? true : false;
				
				String allowTaksReparenting = attributes.getValue("android:allowTaskReparenting");
				if(null == allowTaksReparenting){
					c.allowTaskReparenting = this.application.allowTaskReparenting;
				} else if(("true").equals(allowTaksReparenting)) {
					c.allowTaskReparenting = true;
				} else {
					c.allowTaskReparenting = false;
				}
				
				c.alwaysRetainTaskState = ("true").equals(attributes.getValue("android:alwaysRetainTaskState")) ? true : false;
				c.autoRemoveFromRecents = ("true").equals(attributes.getValue("android:autoRemoveFromRecents")) ? true : false;
				c.banner = null == (s = attributes.getValue("android:banner")) ? c.banner : s;
				c.clearTaskOnLaunch = ("true").equals(attributes.getValue("android:clearTaskOnLaunch")) ? true : false;
				c.configChanges = null == (s = attributes.getValue("android:configChanges")) ? c.configChanges : s;
				c.documentLaunchMode = null == (s = attributes.getValue("android:documentLaunchMode")) ? c.documentLaunchMode : s;
				c.excludeFromRecents = ("true").equals(attributes.getValue("android:excludeFromRecents")) ? true : false;
				c.finishOnTaskLaunch = ("true").equals(attributes.getValue("android:finishOnTaskLaunch")) ? true : false;
				c.hardwareAccelerated = ("true").equals(attributes.getValue("android:hardwareAccelerated")) ? true : false;
				c.launchMode = null == (s = attributes.getValue("android:launchMode")) ? c.launchMode : s;
				c.maxRecents = null == (s = attributes.getValue("android:maxRecents")) ? c.maxRecents : Integer.parseInt(s);
				c.multiprocess = ("true").equals(attributes.getValue("android:multiprocess")) ? true : false;
				c.noHistory = ("true").equals(attributes.getValue("android:noHistory")) ? true : false;
				c.parentActivityName = null == (s = attributes.getValue("android:parentActivityName")) ? c.parentActivityName : s;
				c.relinquishTaskIdentity = ("true").equals(attributes.getValue("android:relinquishTaskIdentify")) ? true : false;
				c.screenOrientation = null == (s = attributes.getValue("android:screenOrientation")) ? c.screenOrientation : s;
				c.stateNotNeeded = ("true").equals(attributes.getValue("android:stateNotNeeded")) ? true : false;
				c.taskAffinity = null == (s = attributes.getValue("android:taskAffinity")) ? this.application.taskAffinity : s;
				c.theme = null == (s = attributes.getValue("android:theme")) ? this.application.theme : s;
				c.uiOptions = null == (s = attributes.getValue("android:uiOptions")) ? c.uiOptions : s;
				c.windowSoftInputMode = null == (s = attributes.getValue("android:windowSoftInputMode")) ? c.windowSoftInputMode : s;
				this.currComponent = c;
				this.components.add(c);

				// TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}
			break;

		case SERVICE_TAG:
			if (attributes != null) {
				Service c = new Service();
				setCommonComponentsAttrs(c, attributes);
				
				c.isolatedProcess = ("true").equals(attributes.getValue("android:isolatedProcess")) ? true : false;
				
				this.currComponent = c;
				this.components.add(c);

				// TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}
			break;

		// TODO: capture dynamically registered broadcast receiver
		// leopoldo: we have to look in the code for that, we won't be able to capture them right here
		case RECEIVER_TAG:
			if (attributes != null) {
				BroadcastReceiver c = new BroadcastReceiver();
				setCommonComponentsAttrs(c, attributes);
				
				this.currComponent = c;
				this.components.add(c);

				// TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}
			break;

		case PROVIDER_TAG:
			if (attributes != null) {
				ContentProvider c = new ContentProvider();
				setCommonComponentsAttrs(c, attributes);
			
				c.exportedWasSetted = null == attributes.getValue("android:exported") ? false : true;
				if(!c.exportedWasSetted){
					c.setDefaultExported(this.minSdkVersion, this.targetSdkVersion);
				}

				String s;
				StringTokenizer auts = new StringTokenizer(attributes.getValue("android:authorities"), ";");
				while (auts.hasMoreElements()) {
					c.authorities.add((String) auts.nextElement());
				}

				c.grantUriPermissions = ("true").equals(attributes.getValue("android:grantUriPermissions")) ? true : false;
				c.initOrder = null == (s = attributes.getValue("android:initOrder")) ? 0 : Integer.parseInt(s);
				c.multiprocess = ("true").equals(attributes.getValue("android:multiprocess")) ? true : false;
				c.readPermission = null == (s = attributes.getValue("android:readPermission")) ? c.readPermission : s;
				c.syncable = ("true").equals(attributes.getValue("android:syncable")) ? true : false;
				c.writePermission = null == (s = attributes.getValue("android:writePermission")) ? c.writePermission : s;

				
				this.currComponent = c;
				this.components.add(c);

				// TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}

			break;

		case INTENT_FILTER_TAG:
			if (attributes != null) {
				this.currentIntentFilter = new IntentFilter();
			}
			break;

		case ACTION_TAG:
			if (attributes != null) {
				this.currentIntentFilter.actions.add(attributes.getValue("android:name"));
			}
			break;

		case CATEGORY_TAG:
			if (attributes != null) {
				this.currentIntentFilter.categories.add(attributes.getValue("android:name"));
			}
			break;

		case DATA_TAG:
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					switch (attributes.getQName(i)) {
					case DATA_SCHEME_ATTR:
						this.currentIntentFilter.data.scheme = attributes.getValue(i);
						break;

					case DATA_HOST_ATTR:
						this.currentIntentFilter.data.host = attributes.getValue(i);
						break;

					case DATA_PORT_ATTR:
						this.currentIntentFilter.data.port = attributes.getValue(i);
						break;

					case DATA_PATH_ATTR:
						this.currentIntentFilter.data.path = attributes.getValue(i);
						break;

					case DATA_PATH_PATTERN_ATTR:
						this.currentIntentFilter.data.pathPattern = attributes.getValue(i);
						break;

					case DATA_PATH_PREFIX_ATTR:
						this.currentIntentFilter.data.pathPrefix = attributes.getValue(i);
						break;

					case DATA_MIME_TYPE_ATTR:
						this.currentIntentFilter.data.mimeType = attributes.getValue(i);
						break;
					}
				}
			}
			break;
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(INTENT_FILTER_TAG)) {
			((ArrayList<IntentFilter>) this.intentFilters.get(this.currentComponent)).add(this.currentIntentFilter);
			this.currComponent.intentFilters.add(this.currentIntentFilter);
			if (currComponent.intentFilters.size() > 0) {
				switch (currComponent.type) {
					case ACTIVITY:
					case BROADCAST_RECEIVER:
					case SERVICE:
						currComponent.exported = true;
						break;
					case CONTENT_PROVIDER:
						// special
						break;
				}
			}
		} else if(qName.equals(USES_SDK_TAG)){
			ContentProvider cp;
			for(Component c: this.components){
				if(c instanceof ContentProvider){
					cp = (ContentProvider) c;
					if(!cp.exportedWasSetted){
						cp.setDefaultExported(this.minSdkVersion, this.targetSdkVersion);
					}
				}
			}
		}
		
		
	}

	public static void main(String[] args) throws Exception {

		// String manifestPath = "test-data/zooborns/AndroidManifest.xml";
		String manifestPath = "test-data/k9/AndroidManifest.xml";

		AndroidManifestParser manifestParser = new AndroidManifestParser(manifestPath);
		System.out.println("---");
		System.out.println("MinSdkVersion: "+manifestParser.minSdkVersion);
		System.out.println("TargetSdkVersion: "+manifestParser.targetSdkVersion);
		System.out.println("---");
		System.out.println("");
		System.out.println(manifestParser.application);
		System.out.println("---");
		for (Component c : manifestParser.components) {
			System.out.println("Component: ");
			System.out.println(c);
		}
		System.out.println("---");
	}

}
