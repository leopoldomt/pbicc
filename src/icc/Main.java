package icc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class Main {

   public static void main(String[] args) throws Exception {
      KeysReader.processFileList(args[0]/*fileName*/, args[1]/*filePath*/);
      DirectedGraph<String, DefaultEdge> g = createDependencyGraph();
      System.out.println("INTENT KEYS");
      for (Map.Entry<String, PutsAndGets> entry : entries.entrySet()) {
         System.out.printf("COMP:%s, KEYS:%s", entry.getKey().toString(), entry.getValue().toString());
      }
      System.out.println("GRAPH: ");
      System.out.println(toString(g));
      System.out.println("TOPO ORDER: ");
      System.out.println(getTopoOrder(g));
   }

   //TODO: this started as a script.  consider removing this "static" modifiers. -M
   public static Map<String/*classname*/, PutsAndGets> entries = new HashMap<String, PutsAndGets>();

   public static class PutsAndGets {
      public Set<String> gets = new HashSet<String>();
      public Set<String> puts = new HashSet<String>();

      public String toString() {
         return String.format("\n  GETS %s\n  PUTS %s\n", gets.toString(), puts.toString());
      }

      /**
       * returns true if
       * pg.gets (read set) intersects with this.puts (write set)
       */
      public boolean isDep(PutsAndGets pg) {
         for (String read : pg.gets) {
            if (this.puts.contains(read)) {
               return true;
            }
         }
         return false;
      }
   }


   // create dependency graph
   static DirectedGraph<String, DefaultEdge> createDependencyGraph() {
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

            if (pg2.isDep(pg1)) {
               g.addEdge(entry2.getKey(), entry1.getKey());
            }
         }
      }

      return g;
   }

   // dump topological order
   private static String getTopoOrder(@SuppressWarnings("rawtypes") DirectedGraph g) {
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

   // dump string representation of the graph
   private static String toString(DirectedGraph<String, DefaultEdge> g) {

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
}
