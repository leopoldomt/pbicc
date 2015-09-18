package icc;

import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;

public class TestMain {

  @Test
  public void testZooBorn() throws Exception {
    Main.init("out/zooborns-javafiles.txt", "test-data/zooborns//src/");
    PutsAndGets putsAndGets = State.getInstance().pgMap().get("ZooBorns.java");
    Assert.assertTrue(putsAndGets.puts.contains("\"cachedImageList\""));
    Assert.assertTrue(putsAndGets.puts.contains("\"gallery\""));
    Assert.assertTrue(putsAndGets.puts.contains("\"currentImageIndex\""));
  }
  
  @Test
  public void testEmptyKeys() throws Exception {
    Main.init("out/zooborns-javafiles.txt", "test-data/zooborns//src/");
    DirectedGraph<String, DefaultEdge> g = Main.createDependencyGraph();
    Set<String> vertices = g.vertexSet();
    Assert.assertTrue(vertices.contains("ZooBorns.java"));
    // See "out/zooborns-javafiles.txt" and "out/zooborns-graph-summary.txt".  They should contain the same set of elements
    Assert.assertTrue(vertices.contains("ZooBornsPhoto.java"));
  }
  
  @Test
  public void testFileDependency() throws Exception {
    Main.init("out/zooborns-javafiles.txt", "test-data/zooborns//src/");
//    State.getInstance().
  }

}