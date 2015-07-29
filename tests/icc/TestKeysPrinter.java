package icc;

import icc.Main.PutsAndGets;

import org.junit.Assert;
import org.junit.Test;

public class TestKeysPrinter {

   String sep = System.getProperty("file.separator");
   String TEST_DATA_DIR = System.getProperty("user.dir") + sep + "test-data";

   @Test
   public void testZooBorn() throws Exception {
      String fullyQualifiedFileName = "ZooBorns.java";
      String dirName = TEST_DATA_DIR + sep + "zooborns" + sep + "src" + sep;
      KeysReader.processJavaFile(dirName, fullyQualifiedFileName);
      PutsAndGets putsAndGets = Main.entries.get(fullyQualifiedFileName);
      /**
       * a bug in the implementation of the visitor KeysReader (missing super.visit)
       * prevented us from finding these keys
       */
      Assert.assertTrue(putsAndGets.puts.contains("\"cachedImageList\""));
      Assert.assertTrue(putsAndGets.puts.contains("\"gallery\""));
      Assert.assertTrue(putsAndGets.puts.contains("\"currentImageIndex\""));
   }

}
