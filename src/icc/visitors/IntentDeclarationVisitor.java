package icc.visitors;

import java.util.List;
import java.util.Map;

import icc.data.IntentInfo;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;

public class IntentDeclarationVisitor extends ScopeAwareVisitor
{
  final Map<String, IntentInfo> table;

  public IntentDeclarationVisitor(Map<String, IntentInfo> table)
  {
    this.table = table;
  }

  public void visit(VariableDeclarationExpr expr, Object arg)
  {
    super.visit(expr, arg);

    if (expr.getType().toString().equals("Intent"))
    {
      for(VariableDeclarator var : expr.getVars())
      {   
        IntentInfo info = new IntentInfo();

        if (var.getInit() instanceof ObjectCreationExpr)
        {
          ObjectCreationExpr objCreation = (ObjectCreationExpr) var.getInit();

          List<Expression> args = objCreation.getArgs();

          if (args != null)
          {
            if (args.size() == 1)
            {
              // TODO: check if the argument isn't another intent instead of an action
              info.action = args.get(0).toString();
            }
            else if (args.size() == 2)
            {

              // TODO: improve the heuristic being used to determine if the argument
              // is a Context or String object.

              if (args.get(0) instanceof ThisExpr)
              {
                info.className = args.get(1).toString();
              }
              else
              {
                info.action = args.get(0).toString();
                info.data = args.get(1).toString();
              }
            }
            else if (args.size() == 4)
            {
                info.action = args.get(0).toString();
                info.data = args.get(1).toString();
                info.className = args.get(3).toString();
            }
          }
        }

        this.table.put(String.format("%s.%s", getScope(), var.getId().toString()), info);
      }
    }
  }
}
