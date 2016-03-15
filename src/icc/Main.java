package icc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentFilter;
import icc.data.IntentInfo;
import icc.data.IntentStats;
import icc.data.VarInfo;
import icc.parsing.AndroidManifestParser;
import icc.visitors.KeysVisitor;

public class Main {

  //TODO: use commons CLI to organize options: https://commons.apache.org/proper/commons-cli/ -M
  static boolean DEBUG_KEYS = false;
  static boolean ICC_SHOW_EXPLICIT_INTENTS = false;
  static boolean ICC_SHOW_IMPLICIT_INTENTS = false;
  static boolean ICC_SHOW_VARS = false;
  static boolean ICC_SHOW_LINKS = true;
  static boolean ICC_SHOW_STATS_PER_FILE = false;
  static boolean ICC_SHOW_FINAL_STATS = false;
  static boolean ICC_SHOW_INTENT_FILTERS = false;
  static boolean PRINT_DOT = false;
  static boolean PRINT_TOPO_ORDER = false;

  static String fileListFile;
  static String appSourceDir;

  public static void main(String[] args) throws Exception {

    init(args[0], args[1]);
    getICCLinkResults();

    State.getInstance().setManifestParser(new AndroidManifestParser(args[2]));
    
    DirectedGraph<String, DefaultEdge> g = createDependencyGraph();

    if (ICC_SHOW_INTENT_FILTERS)
    {
      for (String component : State.getInstance().getManifestParser().intentFilters.keySet())
      {
        System.out.println("###");
        System.out.println("Component: " + component);
        System.out.println("Filters:");

        for (IntentFilter filter : State.getInstance().getManifestParser().intentFilters.get(component))
        {
          System.out.println("---");
          System.out.println(filter);
        }
      }
    }
    
    if (DEBUG_KEYS) {
      System.out.println("INTENT KEYS");
      for (Map.Entry<String, PutsAndGets> entry : State.getInstance().pgMap().entrySet()) {
        System.out.printf("COMP:%s, KEYS:%s", entry.getKey().toString(), entry.getValue().toString());
      }
    }

    IntentStats appIntentStats = new IntentStats();

    for(Map.Entry<String, ICCLinkFindingResults> resultsEntry : State.getInstance().resultsMap().entrySet())
    {
      System.out.println(String.format("### File: %s", resultsEntry.getKey()));

      // printing the intents
      Map<String, IntentInfo> intents = resultsEntry.getValue().intentsST.getMap();
      IntentInfo info = null;

      for(Map.Entry<String, IntentInfo> intentEntry : intents.entrySet())
      {
        info = intentEntry.getValue();

        if ((info.isExplicit() && ICC_SHOW_EXPLICIT_INTENTS) ||
            (!info.isExplicit() && ICC_SHOW_IMPLICIT_INTENTS))
        {
          System.out.println(String.format("%s:\n%s\n----------", intentEntry.getKey(), intentEntry.getValue()));
        }
      }

      // printing the vars
      if (ICC_SHOW_VARS)
      {
        Map<String, VarInfo> vars = resultsEntry.getValue().varsST.getMap();

        for(Map.Entry<String, VarInfo> varEntry : vars.entrySet())
        {
          String name = varEntry.getKey();
          VarInfo varInfo = varEntry.getValue();

          System.out.printf("%s %s = %s\n", info.type, name, varInfo.value);
        }
      }

      // printing the links
      if (ICC_SHOW_LINKS)
      {
        List<ICCLinkInfo<IntentInfo>> links = resultsEntry.getValue().iccLinks;

        for (ICCLinkInfo<IntentInfo> link : links)
        {
          System.out.println(link);
        }
      }

      IntentStats stats = resultsEntry.getValue().stats;

      appIntentStats.add(stats);

      if (ICC_SHOW_STATS_PER_FILE)
      {
        System.out.println(resultsEntry.getValue().stats);
      }
    }

    if (ICC_SHOW_FINAL_STATS)
    {
      System.out.println("### App Intent Stats");
      System.out.println(appIntentStats);
      System.out.println(appIntentStats.getExtendedAnalysis());
    }

    if (PRINT_DOT) {
      String dot = toDot(g);
      String name = fileListFile.split("-")[0] + "-cdg.dot"; // component dependency graph
      BufferedWriter bw = new BufferedWriter(new FileWriter(name));
      bw.write(dot);
      bw.flush();
      bw.close();
    }

    if (PRINT_TOPO_ORDER) {
      System.out.println("TOPO ORDER: ");
      String topoOrder = getTopoOrder(g);
      System.out.println(topoOrder);
      String name = fileListFile.split("-")[0] + "-graph-summary.txt"; // component dependency graph
      BufferedWriter bw = new BufferedWriter(new FileWriter(name));
      bw.write(topoOrder);
      bw.flush();
      bw.close();
    }
  }

  public static void init(String fileListFile, String appSourceDir) throws Exception {
    Main.fileListFile = fileListFile;
    Main.appSourceDir = appSourceDir;

    // processFileList makes a pass in all ASTs: one pass for each compilation unit
    processFileList(fileListFile, appSourceDir,
        new CompUnitProcessable() {
      @Override
      public void process(String name, CompilationUnit cu) {

        String replacedFilename = name.replaceAll("/", ".");
        State.getInstance().astMap().put(replacedFilename, cu);
      }
    }
        );

    //TODO: we need to implement component analysis
    //    processFileList(fileListFile, appSourceDir,
    //        new CompUnitProcessable() {
    //          DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
    //          { // initialize g (done once)
    //            for (String fileName : State.getInstance().pgMap().keySet()) {
    //              g.addVertex(fileName.split("\\.")[0]);
    //            }
    //          }
    //          @Override
    //          public void process(String name, CompilationUnit cu) {
    //            String regex = String.join("|", State.getInstance().pgMap().keySet());
    //            cu.toString().matches(regex);
    //          }
    //        }
    //    );
  }
  
  public static void getExtraPutExtraPairs() {
    Map<String,CompilationUnit> asts = State.getInstance().astMap();
    for (Map.Entry<String, CompilationUnit> entry : asts.entrySet()) {
      KeysVisitor kv = new KeysVisitor();
      kv.visit(entry.getValue(), null);
      State.getInstance().pgMap().put(entry.getKey(), kv.getPGs());
    }
  }

  public static void getICCLinkResults() {
    Map<String,CompilationUnit> asts = State.getInstance().astMap();
    for (Map.Entry<String, CompilationUnit> entry : asts.entrySet()) {
      State.getInstance().resultsMap().put(entry.getKey(), ICCLinkFinder.findICCLinks(entry.getValue()));
    }
  }

  public static void processFileList(String fileListFile, String appSourceDir, CompUnitProcessable cup) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(fileListFile));
    String line;
    while ((line = br.readLine()) != null) {
      String fileName = line.substring(2);
      File file = new File(appSourceDir, fileName);
      // creates an input stream for the file to be parse
      FileInputStream in = new FileInputStream(file);
      CompilationUnit cu = JavaParser.parse(in);
      
      if(cu != null){
        cup.process(fileName, cu);
      }
      in.close();
    }
    br.close();
  }

  // create dependency graph
  public static DirectedGraph<String, DefaultEdge> createDependencyGraph() {
    DirectedGraph<String, DefaultEdge> g =
        new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

    // add vertices
    for (String key : State.getInstance().pgMap().keySet()) {
      g.addVertex(key);
    }

    // add edges
    for (Map.Entry<String, PutsAndGets> entry1 : State.getInstance().pgMap().entrySet()) {
      PutsAndGets pg1 = entry1.getValue();

      for (Map.Entry<String, PutsAndGets> entry2 : State.getInstance().pgMap().entrySet()) {
        PutsAndGets pg2 = entry2.getValue();

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
