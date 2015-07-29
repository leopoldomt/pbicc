package icc;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;

public class TestMain {

   String sep = System.getProperty("file.separator");
   String TEST_DATA_DIR = System.getProperty("user.dir") + sep + "test-data";

   @Test
   public void testZooBorn() throws Exception {
      KeysReader.processDir(TEST_DATA_DIR + sep + "zooborns" + sep + "src");
      DirectedGraph<String, DefaultEdge> graph = Main.createDependencyGraph();
      /* FullScreenImage communicates with ZooBorns */
      Assert.assertTrue(graph.containsEdge("ZooBorns.java", "FullscreenImage.java"));
      /* In fact, there is only this edge in this graph */
      Assert.assertEquals(1, graph.edgeSet().size());
   }

}
