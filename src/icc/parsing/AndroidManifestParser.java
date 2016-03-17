package icc.parsing;

import icc.data.Component;
import icc.data.ComponentType;
import icc.data.IntentFilter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AndroidManifestParser extends DefaultHandler
{
	static final String MANIFEST_TAG = "manifest";
	static final String ACTIVITY_TAG = "activity";
	static final String SERVICE_TAG = "service";
	static final String PROVIDER_TAG = "provider";
	static final String RECEIVER_TAG = "receiver";
	static final String INTENT_FILTER_TAG = "intent-filter";
	static final String ACTION_TAG = "action";
	static final String CATEGORY_TAG = "category";
	static final String DATA_TAG = "data";
	static final String USES_PERMISSIONS_TAG = "uses-permission";

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

	//TODO: remove the following 2 lines once we fix the remaining of the implementation
	public Map<String, List<IntentFilter>> intentFilters;
	String currentComponent;

	Component currComponent;
	IntentFilter currentIntentFilter;

	public AndroidManifestParser(String manifestPath) throws Exception
	{
		this.permissions = new LinkedList<String>();
		this.components = new LinkedList<Component>();
		this.intentFilters = new HashMap<String, List<IntentFilter>>();

		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();    
		parser.parse(new FileInputStream(new File(manifestPath)), this);
	}

	public void setCommonComponentsAttrs(Component c, Attributes attrs){
		//TODO: maybe checking if getValue == null so we do not set label and other attributes to null?
		c.label = attrs.getValue("android:label");
		c.name = attrs.getValue("android:name");
		c.permission = attrs.getValue("android:permission");
		c.process = attrs.getValue("android:process");

		if (attrs.getValue("android:enabled") == "false") {
			c.enabled = false;
		}
		if (attrs.getValue("android:exported") == "true") {
			c.exported = true;
		}
	}

	public void startElement(String uri, String localName,
			String qName, Attributes attributes) throws SAXException 
	{
		switch (qName)
		{
		case MANIFEST_TAG:

			if (attributes != null)
			{
				this.appPackage = attributes.getValue("package");
			}

			break;

		case USES_PERMISSIONS_TAG:
			if (attributes != null) {
				this.permissions.add(attributes.getValue("android:name"));
			}
			break;       

		case ACTIVITY_TAG:
			if (attributes != null)
			{
				Component c = new Component();

				setCommonComponentsAttrs(c, attributes);
				
				c.type = ComponentType.ACTIVITY;

				this.currComponent = c;
				this.components.add(c);

				//TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}

			break;

		case SERVICE_TAG:
			if (attributes != null)
			{
				Component c = new Component();
				
				setCommonComponentsAttrs(c, attributes);				
				
				c.type = ComponentType.SERVICE;

				this.currComponent = c;
				this.components.add(c);

				//TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}

			break;

			//TODO: capture dynamically registered broadcast receiver       
		case RECEIVER_TAG:

			if (attributes != null)
			{
				Component c = new Component();
				
				setCommonComponentsAttrs(c, attributes);

				c.type = ComponentType.BROADCAST_RECEIVER;
				this.currComponent = c;
				this.components.add(c);

				//TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}

			break;

			//TODO: capture specific content provider information
		case PROVIDER_TAG:

			if (attributes != null)
			{
				Component c = new Component();
				
				setCommonComponentsAttrs(c, attributes);

				c.type = ComponentType.CONTENT_PROVIDER;
				this.currComponent = c;
				this.components.add(c);

				//TODO: remove the following line once we finish working on the rest of implementation
				this.currentComponent = attributes.getValue("android:name");
				this.intentFilters.put(this.currentComponent, new ArrayList<IntentFilter>());
			}

			break;

		case INTENT_FILTER_TAG:

			if (attributes != null)
			{
				this.currentIntentFilter = new IntentFilter();
			}

			break;

		case ACTION_TAG:

			if (attributes != null)
			{
				this.currentIntentFilter.action = attributes.getValue("android:name");
			}

			break;

		case CATEGORY_TAG:

			if (attributes != null)
			{
				this.currentIntentFilter.category = attributes.getValue("android:name");
			}

			break;

		case DATA_TAG:

			if (attributes != null)
			{
				for (int i = 0; i < attributes.getLength() ; i++)
				{
					switch (attributes.getQName(i))
					{
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

	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		if (qName.equals(INTENT_FILTER_TAG))
		{
			((ArrayList<IntentFilter>) this.intentFilters.get(this.currentComponent)).add(this.currentIntentFilter);
			this.currComponent.intentFilters.add(this.currentIntentFilter);
		}
	}

	public static void main(String[] args) throws Exception {

		//String manifestPath = "/home/vinicius/Coding/Monografia/main-tools/source_code/pbicc/presentation_study/src/android-chess/app/src/main/AndroidManifest.xml";
		//String manifestPath = "/Users/leopoldomt/Documents/cin/pbicc/test-data/zooborns/AndroidManifest.xml";
		//String manifestPath = "/Users/leopoldomt/Documents/cin/pbicc/test-data/k9/AndroidManifest.xml";
		String manifestPath = "/home/jpttrindade/developer/workspaces/CIn/TG/pbicc/test-data/k9/AndroidManifest.xml";


		AndroidManifestParser manifestParser = new AndroidManifestParser(manifestPath);
		/*
    for (String component : manifestParser.intentFilters.keySet())
    {
      System.out.println("###");
      System.out.println("Component: " + component);
      System.out.println("Filters:");

      for (IntentFilter filter : manifestParser.intentFilters.get(component))
      {
        System.out.println("---");
        System.out.println(filter);
      }
    }

    System.out.println("---");
    for (String permission : manifestParser.permissions) {
      System.out.println("Permission: " + permission);
    }
    System.out.println("---");
/**/
		System.out.println("---");
		for (Component c : manifestParser.components) {
			System.out.println("Component: ");
			System.out.println(c);
		}
		System.out.println("---");
	}


}
