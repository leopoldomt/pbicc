package mparser;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * TODO: I guess this can be made more general; we will 
 * need to access other info in addition to main activity. -M
 * 
 */
public class GetMainActivity {

    static BufferedWriter bf;

    public static String getMainActivity(String manifestPath) throws Exception {
        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        SAXHandler handler = new SAXHandler();
        FileInputStream fis = new FileInputStream(manifestPath); // Manifest file
        parser.parse(fis,handler);
        return handler.activityOutput;
    }

    // args[0] = Manifest path
    // args[1] = output path
    public static void main(String[] args) {
        try {
            bf = new BufferedWriter(new FileWriter(args[1], false));
            String mainActivity = getMainActivity(args[0]);
            if (mainActivity == null) mainActivity = "android.app.Activity";
            bf.append(mainActivity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}

