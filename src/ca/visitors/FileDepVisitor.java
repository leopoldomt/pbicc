package ca.visitors;

import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * This is incomplete!!
 * 
 * 
 * @author damorim
 *
 */
public class FileDepVisitor extends VoidVisitorAdapter<DirectedGraph<String,DefaultEdge>> {
  
  private String name;
  
  public FileDepVisitor(String name) {
    this.name = name.split("\\.")[0];
  }
  
  @Override
  public void visit(NameExpr n, DirectedGraph<String,DefaultEdge> g) {
    super.visit(n, g);
    if (n.getName().equals(name)) return;
    if (g.vertexSet().contains(n.getName())) {
      System.out.printf("%s => %s\n", name, n.getName());
      g.addEdge(name, n.getName());
    }
  }
  
  @Override
  public void visit(QualifiedNameExpr n, DirectedGraph<String,DefaultEdge> g) {
    super.visit(n, g);
    String[] ar = n.getName().split("\\.");
    String ln = ar[ar.length-1];
    if (ln.equals(name)) return;
    if (g.vertexSet().contains(n.getName())) {
      System.out.printf("%s => %s\n", name, n.getName());
      g.addEdge(name, ln);
    }
  }

}