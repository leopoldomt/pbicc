package icc.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;

public class IntentVisitor extends ScopeAwareVisitor
{
  //constants
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
  private final String M_START_ACTIVITY = "startActivity";
  private final String M_START_SERVICE = "startService";

  private final List<Pattern> CONTEXT_STRINGS = new ArrayList<Pattern>(Arrays.asList(new Pattern[]{Pattern.compile("activity"),
      Pattern.compile("context"),
      Pattern.compile("([\\w]+\\.)?this")}));

  // attrs
  private ICCLinkFindingResults data;

  public IntentVisitor(ICCLinkFindingResults data)
  {
    this.data = data;
  }

  @Override
  public void visit(VariableDeclarationExpr expr, Object arg)
  {
    super.visit(expr, arg);

    if (! expr.getType().toString().equals("Intent"))
    {
      return;
    }

    for(VariableDeclarator var : expr.getVars())
    {   
      IntentInfo info = new IntentInfo();

      // if this is not an assignment, retrieve the intent information
      if (var.getInit() instanceof ObjectCreationExpr)
      {
        ObjectCreationExpr objCreation = (ObjectCreationExpr) var.getInit();

        info = handleIntentCreation(objCreation);
      }
      else if(var.getInit() instanceof NameExpr)
      {
        NameExpr nameExpr = (NameExpr) var.getInit();

        String fullExistingIntentName = getFullScopeName(nameExpr.getName());

        // if this is an assignment of an item that already exists on the list,
        // point the intent name to the existing
        IntentInfo existingInfo = this.data.intentsST.get(fullExistingIntentName);
        if(existingInfo != null)
        {
          info = existingInfo;
        }

        // TODO: handle other cases
      }
      
      // TODO: handler other cases

      if (info != null)
      {        
        this.data.intentsST.put(String.format("%s.%s", getScope(), var.getId().toString()), info);
      }
    }
  }

  @Override
  public void visit(AssignExpr expr, Object arg)
  {
    super.visit(expr, arg);

    if (expr.getOperator().equals(AssignExpr.Operator.assign))
    {
      String fullName = getFullScopeName(expr.getTarget().toString());

      if (this.data.intentsST.get(fullName) != null)
      {
        Expression value = expr.getValue();

        if (value instanceof ObjectCreationExpr)
        {
          ObjectCreationExpr creationExpr = (ObjectCreationExpr) value;

          IntentInfo info = handleIntentCreation(creationExpr);

          if (info != null)
          {
            this.data.intentsST.put(fullName, info);
          }
        }
        else if(value instanceof NameExpr)
        {
          NameExpr nameExpr = (NameExpr) value;

          String fullExistingIntentName = getFullScopeName(nameExpr.getName());

          // if this is an assignment of an item that already exists on the list,
          // point the intent name to the existing
          IntentInfo existingInfo = this.data.intentsST.get(fullExistingIntentName);
          if(existingInfo != null)
          {
            this.data.intentsST.put(fullName, existingInfo);
          }

          // TODO: handle other cases
        }

        // TODO: handle other cases
      }
    }
  }

  @Override
  public void visit(MethodCallExpr expr, Object arg)
  {
    super.visit(expr, arg);

    String name = expr.getName();
    Expression scope = expr.getScope();
    List<Expression> args = expr.getArgs();

    // startActivity | startService
    if (name.equals(M_START_ACTIVITY) || name.equals(M_START_SERVICE))
    {
      // 1 arg
      if (args.size() == 1)
      {
        Expression argExpr = args.get(0);

        // object creation
        if (argExpr instanceof ObjectCreationExpr)
        {
          ObjectCreationExpr newIntent = (ObjectCreationExpr) argExpr;

          IntentInfo info = handleIntentCreation(newIntent);

          if (info != null)
          {
            this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(this.getScope(), name, info));
          }
        }
        else if (argExpr instanceof NameExpr)
        { 
          NameExpr intentVar = (NameExpr) argExpr;

          String fullName = getFullScopeName(intentVar.getName());

          IntentInfo existingInfo = data.intentsST.get(fullName);
          
          if (existingInfo != null)
          {
            this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(this.getScope(), name, existingInfo));
          }
          else
          {
            System.out.println(String.format("Intent instance '%s' is not on the symbol data.intentsST!",
                intentVar.getName()));
          }
        }
        
        // TODO: handle other cases
      }
    }
    // possible intent method call
    else
    {
      if (scope != null)
      {
        // the var is in the current symbol data.intentsST
        String varName = getFullScopeName(scope.toString());

        // a call to a method of an intent object
        IntentInfo info = data.intentsST.get(varName);
        
        if (info != null)
        {
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

    if (!(matchesContextString(firstArg.toString())))
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

  private IntentInfo handleIntentCreation(ObjectCreationExpr expr)
  {
    // an intent is being created
    if (! expr.getType().toString().equals("Intent"))
    {
      return null;
    }

    IntentInfo info = new IntentInfo();
    List<Expression> args = expr.getArgs();

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

        if (matchesContextString(args.get(0).toString()))
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

    return info;
  }

  // utils
  private boolean matchesContextString(String candidate)
  {
    for (Pattern p : CONTEXT_STRINGS)
    {
      if (p.matcher(candidate).matches())
      {
        return true;
      }
    }

    return false;
  }

  private String getFullScopeName(String name)
  {
    return String.format("%s.%s", this.getScope(), name);
  }
}
