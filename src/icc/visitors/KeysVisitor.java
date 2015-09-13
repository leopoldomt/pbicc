package icc.visitors;

import icc.Main;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import japa.parser.ast.expr.Expression;

import java.util.List;

// visitor implementation
public class KeysVisitor extends VoidVisitorAdapter<Object> {

  Main.PutsAndGets putsAndGets = new Main.PutsAndGets();
  String classname;

  public KeysVisitor(String classname) {
    this.classname = classname;
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
    }

    Main.entries.put(classname,putsAndGets);
  }
}