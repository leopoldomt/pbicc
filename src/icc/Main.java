package icc;

import icc.visitors.FileProcessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class Main {

  //TODO: use commons CLI to organize options: https://commons.apache.org/proper/commons-cli/ -M
  static boolean DEBUG_KEYS = true;
  static boolean PRINT_DOT = true;
  static boolean PRINT_TOPO_ORDER = false;

  static String fileName;
  static String filePath;

  public static void main(String[] args) throws Exception {
    fileName = args[0];
    filePath = args[1];
    FileProcessor.processFileList(fileName, filePath);
    DirectedGraph<String, DefaultEdge> g = createDependencyGraph();

    if (DEBUG_KEYS) {
      System.out.println("INTENT KEYS");
      for (Map.Entry<String, PutsAndGets> entry : entries.entrySet()) {
        System.out.printf("COMP:%s, KEYS:%s", entry.getKey().toString(), entry.getValue().toString());
      }
    }

    if (PRINT_DOT) {
      String dot = toDot(g);
      String name = fileName.split("-")[0] + "-cdg.dot"; // component dependency graph
      BufferedWriter bw = new BufferedWriter(new FileWriter(name));
      bw.write(dot);
      bw.flush();
      bw.close();
    }

    if (PRINT_TOPO_ORDER) {
      System.out.println("TOPO ORDER: ");
      System.out.println(getTopoOrder(g));
    }
  }

  //TODO: this started as a script.  consider removing this "static" modifiers. -M
  public static Map<String/*classname*/, PutsAndGets> entries = new HashMap<String, PutsAndGets>();

  // create dependency graph
  public static DirectedGraph<String, DefaultEdge> createDependencyGraph() {
    DirectedGraph<String, DefaultEdge> g =
        new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

    // add vertices
    for (String key : entries.keySet()) {
      g.addVertex(key);
    }

    // add edges
    for (Map.Entry<String, PutsAndGets> entry1 : entries.entrySet()) {
      PutsAndGets pg1 = entry1.getValue();

      for (Map.Entry<String, PutsAndGets> entry2 : entries.entrySet()) {
        PutsAndGets pg2 = entry2.getValue();

        // TODO: only requires one equal key!  strange.
        if (pg2.isDep(pg1)) {
          g.addEdge(entry2.getKey(), entry1.getKey());
        }
      }
    }

    return g;
  }

  // dump topological order
  static String getTopoOrder(@SuppressWarnings("rawtypes") DirectedGraph g) {
    StringBuffer sb = new StringBuffer();
    @SuppressWarnings("unchecked")
    TopologicalOrderIterator<Integer, DefaultEdge> toi =
    new TopologicalOrderIterator<Integer, DefaultEdge>(g);
    while (toi.hasNext()) {
      String canonicalName = toi.next()+""; // Removing .java extension from topo order.
      sb.append(canonicalName.substring(0, canonicalName.length()-5));
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * string graph representations
   */

  @SuppressWarnings("unused")
  private static String toCustomRepr(DirectedGraph<String, DefaultEdge> g) {

    StringBuffer sb = new StringBuffer();
    sb.append("Vertex set:");
    sb.append(g.vertexSet());

    Set<DefaultEdge> edges = g.edgeSet();
    sb.append("\nEdge set:");
    for (DefaultEdge e: edges) {
      sb.append(e);
    }

    return sb.toString();
  }

  /**
   * Dot is a popular graph visualization format.  See/Install Graphviz 
   **/
  private static String toDot(DirectedGraph<String, DefaultEdge> g) {

    StringBuffer sb = new StringBuffer();
    sb.append("digraph mygraph {\n ");
    Set<DefaultEdge> edges = g.edgeSet();
    for (DefaultEdge e: edges) {
      sb.append(g.getEdgeSource(e)./*remove file extension*/split("\\.")[0]);
      sb.append("->");
      sb.append(g.getEdgeTarget(e)./*remove file extension*/split("\\.")[0]);
      sb.append(";\n");
    }
    sb.append("}\n");
    return sb.toString();
  }

}
