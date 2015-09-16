package mparser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * The Handler for SAX Events.
 */
class SAXHandler extends DefaultHandler {

    public String activityOutput = null;
    String pckg = "";
    String curCompName = null;
    private String MAIN_STR = "android.intent.action.MAIN";

    @Override
    // Triggered when the start of tag is found.
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (activityOutput != null) return; // Found main activity already.

        switch (qName) {
            case "manifest":
                pckg = attributes.getValue("package");
                break;
            case "activity":
                curCompName = attributes.getValue("android:name");
                if (curCompName.startsWith(".")) {
                    curCompName = pckg + curCompName;
                } else if (!curCompName.contains(".")) {
                     curCompName = pckg + "." + curCompName;
                }
                break;
            case "action":
                String action = attributes.getValue("android:name");
                if (action.contains(MAIN_STR)) {
                    activityOutput = curCompName;
                }
                break;
            default:
                break;
        }
    }

}
