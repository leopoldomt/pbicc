package icc.visitors;

import java.util.List;
import java.util.Map;

import icc.data.IntentInfo;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.ThisExpr;

public class IntentInfoVisitor extends ScopeAwareVisitor
{
  // M stands for Method
  private final String M_SET_ACTION = "setAction";
  private final String M_SET_CLASS = "setClass";
  private final String M_SET_CLASS_NAME = "setClassName";
  private final String M_SET_COMPONENT = "setComponent";
  private final String M_SET_DATA = "setData";
  private final String M_SET_DATA_AND_NORMALIZE = "setDataAndNormalize";
  private final String M_SET_DATA_AND_TYPE = "setDataAndType";
  private final String M_SET_DATA_AND_TYPE_AND_NORMALIZE = "setDataAndTypeAndNormalize";
  private final String M_SET_PACKAGE = "setPackage";
  private final String M_SET_TYPE = "setType";
  private final String M_SET_TYPE_AND_NORMALIZE = "setTypeAndNormalize";
  private final String M_PUT_EXTRA = "putExtra";

  private Map<String, IntentInfo> table;

  public IntentInfoVisitor(Map<String, IntentInfo> table)
  {
    this.table = table;
  }

  @Override
  public void visit(MethodCallExpr expr, Object arg)
  {
    super.visit(expr, arg);

    String name = expr.getName();
    Expression scope = expr.getScope();
    List<Expression> args = expr.getArgs();

    if (scope != null)
    {
      // the var is in the current symbol table
      String varName = String.format("%s.%s", this.getScope(), scope.toString());

      // a call to a method of an intent object
      if (table.containsKey(varName))
      {
        IntentInfo info = table.get(varName);

        switch (name)
        {
        case M_SET_ACTION:
          
          handleSetAction(args, info);

          break;

        case M_SET_CLASS:

          handleSetClass(args, info);
          
          break;

        case M_SET_CLASS_NAME:
          
          handleSetClassName(args, info);

          break;

        case M_SET_COMPONENT:

          handleSetComponent(args, info);
          
          break;

        case M_SET_DATA:

          handleSetData(args, info);
          
          break;
          
        case M_SET_DATA_AND_NORMALIZE:

          handleSetDataAndNormalize(args, info);
          
          break;

        case M_SET_DATA_AND_TYPE:

          handleSetDataAndType(args, info);          
          
          break;

        case M_SET_DATA_AND_TYPE_AND_NORMALIZE:

          handleSetDataAndTypeAndNormalize(args, info);
          
          break;

        case M_SET_PACKAGE:
          
          handleSetPackage(args, info);

          break;

        case M_SET_TYPE:

          handleSetType(args, info);
          
          break;

        case M_SET_TYPE_AND_NORMALIZE:

          handleSetTypeAndNormalize(args, info);
          
          break;

        case M_PUT_EXTRA:

          handleSetPutExtra(args, info);
          
          break;
        }
      }
    }
  }

  private void handleSetAction(List<Expression> args, IntentInfo info)
  {
      info.action = args.get(0).toString();
  }
  
  private void handleSetClass(List<Expression> args, IntentInfo info)
  {
      info.className = args.get(1).toString().split("[.]")[0];
  }
  
  private void handleSetClassName(List<Expression> args, IntentInfo info)
  {
      info.className = args.get(1).toString();
      
      // TODO: improve the heuristic being used to determine if the argument
      // is a Context or String object.
      
      Expression firstArg = args.get(0);
      
      if (!(firstArg instanceof ThisExpr))
      {
        info.packageName = firstArg.toString();
      }
  }
  
  private void handleSetPackage(List<Expression> args, IntentInfo info)
  {
      info.packageName = args.get(0).toString();
  }
  
  private void handleSetComponent(List<Expression> args, IntentInfo info)
  {
    ObjectCreationExpr component = (ObjectCreationExpr) args.get(0);
    
    info.packageName = component.getArgs().get(0).toString();
    info.className = component.getArgs().get(1).toString();
  }
  
  private void handleSetData(List<Expression> args, IntentInfo info)
  {
     info.data = args.get(0).toString();
  }
  
  private void handleSetDataAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.data = args.get(0).toString();
  }
  
  private void handleSetDataAndType(List<Expression> args, IntentInfo info)
  {
    info.data = args.get(0).toString();
    info.type = args.get(1).toString();
  }
  
  private void handleSetDataAndTypeAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.data = args.get(0).toString();
    info.type = args.get(1).toString();
  }
  
  private void handleSetType(List<Expression> args, IntentInfo info)
  {
    info.type = args.get(0).toString();
  }
  
  private void handleSetTypeAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.type = args.get(0).toString();
  }
  
  private void handleSetPutExtra(List<Expression> args, IntentInfo info)
  {
    info.extras.put(args.get(0).toString(), args.get(1).toString());
  }
}
