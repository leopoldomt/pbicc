package icc.visitors;

import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class NameVisitor extends VoidVisitorAdapter<Object> {
  
  //TODO: use this to find encapsulation of components

  @Override
  public void visit(NameExpr n, Object arg) {
    super.visit(n, arg);
//    System.out.println(n);
  }
  
  @Override
  public void visit(QualifiedNameExpr n, Object arg) {
    super.visit(n, arg);
//    System.out.println(n);
  }


}