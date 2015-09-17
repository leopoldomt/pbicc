package icc;

import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;

public class TestKeysPrinter {

  String sep = System.getProperty("file.separator");
  String TEST_DATA_DIR = System.getProperty("user.dir") + sep + "test-data";

  @Test
  public void testZooBorn() throws Exception {
    String fullyQualifiedFileName = "ZooBorns.java";
    String dirName = TEST_DATA_DIR + sep + "zooborns" + sep + "src" + sep;
    Util.processJavaFile(dirName, fullyQualifiedFileName);
    PutsAndGets putsAndGets = State.getInstance().pgMap().get(fullyQualifiedFileName);
    /**
     * a bug in the implementation of the visitor KeysReader (missing super.visit)
     * prevented us from finding these keys
     */
    Assert.assertTrue(putsAndGets.puts.contains("\"cachedImageList\""));
    Assert.assertTrue(putsAndGets.puts.contains("\"gallery\""));
    Assert.assertTrue(putsAndGets.puts.contains("\"currentImageIndex\""));
  }
  
  @Test
  public void testEmptyKeys() throws Exception {
    Util.processFileList("out/zooborns-javafiles.txt", "test-data/zooborns//src/");
    DirectedGraph<String, DefaultEdge> g = Main.createDependencyGraph();
    Set<String> vertices = g.vertexSet();
    Assert.assertTrue(vertices.contains("ZooBorns.java"));
    // See "out/zooborns-javafiles.txt" and "out/zooborns-graph-summary.txt".  They should contain the same set of elements
    Assert.assertTrue(vertices.contains("ZooBornsPhoto.java"));
  }

}
