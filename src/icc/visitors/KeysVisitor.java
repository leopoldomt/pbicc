package icc.visitors;

import icc.PutsAndGets;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class KeysVisitor extends VoidVisitorAdapter<Object> {

  PutsAndGets putsAndGets = new PutsAndGets();
  
  public PutsAndGets getPGs() {
    return putsAndGets;
  }

  @Override
  public void visit(MethodCallExpr n, Object arg) {

    super.visit(n, arg);

    String name = n.getName();
    List<Expression> arguments = n.getArgs();

    // check all methods to populate 'gets' and 'puts' with its arguments
    if (n != null && arguments != null) {
      // get*Extra
      // a fix to avoid getExtras()
      if (name.contains("get") && name.contains("Extra") && arguments.size() > 0) {
        if(arguments.get(0).toString().contains(".")) // this is to get just the content after the dot
        {
          int i = arguments.get(0).toString().indexOf('.');
          putsAndGets.gets.add(arguments.get(0).toString().substring(i+1));
        }
        else
        {
          putsAndGets.gets.add(arguments.get(0).toString());
        }

      }
      // putExtra
      // a fix to avoid putExtras method -Wei
      else if (name.contains("put") && name.contains("Extra") && !name.contains("putExtras")){
        if(arguments.get(0).toString().contains("."))
        {
          int i = arguments.get(0).toString().indexOf('.');
          putsAndGets.puts.add(arguments.get(0).toString().substring(i+1));
        }
        else
        {
          putsAndGets.puts.add(arguments.get(0).toString());
        }
      }
    } // end if

  }
}