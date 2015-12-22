package icc.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import icc.data.IntentFilter;

public class AndroidManifestParser extends DefaultHandler
{
  static final String MANIFEST_TAG = "manifest";
  static final String ACTIVITY_TAG = "activity";
  static final String SERVICE_TAG = "service";
  static final String RECEIVER_TAG = "receiver";
  static final String INTENT_FILTER_TAG = "intent-filter";
  static final String ACTION_TAG = "action";
  static final String CATEGORY_TAG = "category";
  static final String DATA_TAG = "data";

  static final String DATA_SCHEME_ATTR = "android:scheme";
  static final String DATA_HOST_ATTR = "android:host";
  static final String DATA_PORT_ATTR = "android:port";
  static final String DATA_PATH_ATTR = "android:path";
  static final String DATA_PATH_PATTERN_ATTR = "android:pathPattern";
  static final String DATA_PATH_PREFIX_ATTR = "android:pathPrefix";
  static final String DATA_MIME_TYPE_ATTR = "android:mimeType";

  public String appPackage;
  public Map<String, List<IntentFilter>> intentFilters;
  String currentComponent;
  IntentFilter currentIntentFilter;

  public AndroidManifestParser(String manifestPath) throws Exception
  {
    this.intentFilters = new HashMap<String, List<IntentFilter>>();

    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();    
    parser.parse(new FileInputStream(new File(manifestPath)), this);
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

    case ACTIVITY_TAG:
    case SERVICE_TAG:
    case RECEIVER_TAG:

      if (attributes != null)
      {
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
    }
  }

  public static void main(String[] args) throws Exception {

    String manifestPath = "/home/vinicius/Coding/Monografia/main-tools/source_code/pbicc/presentation_study/src/android-chess/app/src/main/AndroidManifest.xml";

    AndroidManifestParser manifestParser = new AndroidManifestParser(manifestPath);

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
  }
}
