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
import icc.visitors.CFPVisitor;
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
  static boolean ICC_SAVE_RESULTS = true;
  

  static String fileListFile;
  static String appSourceDir;

  public static void main(String[] args) throws Exception {

    init(args[0], args[1]);
    getICCLinkResults();

    State.getInstance().setManifestParser(new AndroidManifestParser(args[2]));
    
    DirectedGraph<String, DefaultEdge> g = createDependencyGraph();
    StringBuilder results = new StringBuilder();

    
    if (ICC_SHOW_INTENT_FILTERS) {
    	StringBuilder localResults = new StringBuilder();
    	for (String component : State.getInstance().getManifestParser().intentFilters.keySet()) {
    		localResults.append("###\n");
    		localResults.append("Component: " + component+"\n");
    		localResults.append("Filters:\n");

    		for (IntentFilter filter : State.getInstance().getManifestParser().intentFilters.get(component)) {
    			localResults.append("---\n");
    			localResults.append(filter+"\n");
    		}
    	}
    	String toPrint = localResults.toString();
    	System.out.println(toPrint);
    	results.append(toPrint);
    }
    
    if (DEBUG_KEYS) {
    	StringBuilder localResults = new StringBuilder();
    	localResults.append("INTENT KEYS\n");
    	for (Map.Entry<String, PutsAndGets> entry : State.getInstance().pgMap().entrySet()) {
    		localResults.append(String.format("COMP:%s, KEYS:%s\n", entry.getKey().toString(), entry.getValue().toString()));
    	}
    	String toPrint = localResults.toString();
    	System.out.println(toPrint);
    	results.append(toPrint);
    }

    IntentStats appIntentStats = new IntentStats();

    for(Map.Entry<String, ICCLinkFindingResults> resultsEntry : State.getInstance().resultsMap().entrySet()) {
    	StringBuilder localResults = new StringBuilder();
    	localResults.append(String.format("### File: %s", resultsEntry.getKey()));
    	// printing the intents
    	Map<String, IntentInfo> intents = resultsEntry.getValue().intentsST.getMap();
    	IntentInfo info = null;
    	for(Map.Entry<String, IntentInfo> intentEntry : intents.entrySet()) {
    		info = intentEntry.getValue();
    		if ((info.isExplicit() && ICC_SHOW_EXPLICIT_INTENTS) || (!info.isExplicit() && ICC_SHOW_IMPLICIT_INTENTS)) {
    			localResults.append(String.format("%s:\n%s\n----------\n", intentEntry.getKey(), intentEntry.getValue()));
    		}
    	}
    	
    	//printing the vars
    	if (ICC_SHOW_VARS) { 
    		Map<String, VarInfo> vars = resultsEntry.getValue().varsST.getMap();
    		for(Map.Entry<String, VarInfo> varEntry : vars.entrySet()) {
    			String name = varEntry.getKey(); 
    			VarInfo varInfo = varEntry.getValue();
    			localResults.append(String.format("%s %s = %s\n", info.type, name, varInfo.value));
    		}
    	}
    	
    	// printing the links
    	if (ICC_SHOW_LINKS) {
    		List<ICCLinkInfo<IntentInfo>> links = resultsEntry.getValue().iccLinks;
    		for (ICCLinkInfo<IntentInfo> link : links) {
    			localResults.append(link+"\n");
    		}
    	}
    	
    	IntentStats stats = resultsEntry.getValue().stats;
    	appIntentStats.add(stats);
    	if (ICC_SHOW_STATS_PER_FILE) {
    		localResults.append(resultsEntry.getValue().stats+"\n");
    	}
    	String toPrint = localResults.toString();
    	System.out.println(toPrint);
    	results.append(toPrint);
    }

    if (ICC_SHOW_FINAL_STATS) {
    	StringBuilder localResults = new StringBuilder();
    	localResults.append("### App Intent Stats\n");
    	localResults.append(appIntentStats+"\n");
    	localResults.append(appIntentStats.getExtendedAnalysis()+"\n");
    	String toPrint = localResults.toString();
    	System.out.println(toPrint);
    	results.append(toPrint);
    }

    // saving ICC link results information
    if (ICC_SAVE_RESULTS) {
    	String toSave = results.toString();
    	String name = fileListFile.split("-")[0] + "-icc-results.txt"; // component dependency graph
    	BufferedWriter bw = new BufferedWriter(new FileWriter(name));
    	bw.write(toSave);
        bw.flush();
        bw.close();
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
    processFileList(fileListFile, appSourceDir, new CompUnitProcessable() {
		      @Override
		      public void process(String name, CompilationUnit cu) {
		        String replacedFilename = name.replaceAll("/", ".");
		        State.getInstance().astMap().put(replacedFilename, cu);
		      }
    }
        );

    //TODO: need to check if we still need the code below
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
    ICCLinkFindingResults results = new ICCLinkFindingResults();
    State.getInstance().setICCResults(results);
    
    for (Map.Entry<String, CompilationUnit> entry : asts.entrySet()) {
    	CFPVisitor cfpVisitor = new CFPVisitor(results);
        cfpVisitor.visit(entry.getValue(), null);
    }
    
    Map<String,String> mapStrings = results.propagate();
    
    for (Map.Entry<String, CompilationUnit> entry : asts.entrySet()) {
      ICCLinkFinder.findICCLinks(entry.getValue(),results);
    }
  }

  public static void processFileList(String fileListFile, String appSourceDir, CompUnitProcessable cup) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(fileListFile));
    String line;
    while ((line = br.readLine()) != null) {
      String fileName = line.substring(2);
      File file = new File(appSourceDir, fileName);
      if (!file.exists()) {
    	  throw new RuntimeException("CANT FIND FILE: " + fileName);
      }
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
