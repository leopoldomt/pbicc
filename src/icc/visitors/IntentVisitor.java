package icc.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;
import icc.data.VarInfo;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.LiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;

public class IntentVisitor extends ScopeAwareVisitor
{
  //constants
  private final String CHOOSER_ACTION = "ACTION_CHOOSER";

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
  private final String M_START_ACTIVITY_FOR_RESULT = "startActivityForResult";
  private final String M_START_SERVICE = "startService";
  private final String M_CREATE_CHOOSER = "createChooser";

  private final List<Pattern> CONTEXT_STRINGS = new ArrayList<Pattern>(Arrays.asList(new Pattern[]{Pattern.compile("activity"),
      Pattern.compile("context"),
      Pattern.compile("([\\w]+\\.)?this")}));

  private List<String> VAR_TYPES = Arrays.asList(new String[] {"bool", "Boolean", "char", "Character",
      "float", "Float", "byte", "Byte",
      "double", "Double", "int", "Integer",
      "short", "Short", "long", "Long", "String"});

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

    String varType = expr.getType().toString();

    if (varType.equals("Intent"))
    {
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
        else if (var.getInit() instanceof MethodCallExpr)
        {
          MethodCallExpr callExpr = (MethodCallExpr) var.getInit();

          if (callExpr.getName().equals(M_CREATE_CHOOSER))
          {
            info = handleCreateChooser(callExpr);
          }
          else
          {
            // TODO: Improve this heuristic
            info = new IntentInfo();
          }
        }
        else
        {
          // TODO: Improve this heuristic
          info = new IntentInfo();
        }


        if (info != null)
        {        
          this.data.intentsST.put(getFullScopeName(var.getId().toString()), info);
        }
      }
    }
    else
    {
      for(VariableDeclarator var : expr.getVars())
      {
        if (VAR_TYPES.contains(varType))
        {
          handleVarDeclaration(varType, var);
        }
      }
    }
  }

  @Override
  public void visit(FieldDeclaration expr, Object arg)
  {
    super.visit(expr, arg);

    String varType = expr.getType().toString();

    if (varType.equals("Intent"))
    {
      for(VariableDeclarator var : expr.getVariables())
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
        }
        else if (var.getInit() instanceof MethodCallExpr)
        {
          MethodCallExpr callExpr = (MethodCallExpr) var.getInit();

          if (callExpr.getName().equals(M_CREATE_CHOOSER))
          {
            info = handleCreateChooser(callExpr);
          }
          else
          {
            // TODO: Improve this heuristic
            info = new IntentInfo();
          }
        }
        else
        {
          // TODO: Improve this heuristic
          info = new IntentInfo();
        }

        this.data.intentsST.put(getFullScopeName(var.getId().toString()), info);
      }
    }
    else
    {
      for(VariableDeclarator var : expr.getVariables())
      {
        if (VAR_TYPES.contains(varType))
        {
          handleVarDeclaration(varType, var);
        }
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
      VarInfo varInfo = this.data.varsST.get(fullName);

      // it's an intent
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
        }
        else if (value instanceof MethodCallExpr)
        {
          MethodCallExpr callExpr = (MethodCallExpr) value;

          if (callExpr.getName().equals(M_CREATE_CHOOSER))
          {
            IntentInfo info = handleCreateChooser(callExpr);

            if (info != null)
            {
              this.data.intentsST.put(fullName, info);
            }
          }
          else
          {
            // TODO: Improve this heuristic
            this.data.intentsST.put(fullName, new IntentInfo());
          }
        }
        else
        {
          // TODO: Improve this heuristic
          this.data.intentsST.put(fullName, new IntentInfo());
        }
      }
      // other var
      else if (varInfo != null)
      {

        handleVarAssignment(varInfo, fullName, expr);
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
    if (name.equals(M_START_ACTIVITY) || name.equals(M_START_SERVICE) || name.equals(M_START_ACTIVITY_FOR_RESULT))
    {      
      if (name.equals(M_START_ACTIVITY))
      {
        this.data.stats.addStartActivity();
      }
      else if (name.equals(M_START_SERVICE))
      {
        this.data.stats.addStartService();
      }

      // 1 or more args
      if (args != null && args.size() >= 1)
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
        else if (argExpr instanceof MethodCallExpr)
        {
          MethodCallExpr callExpr = (MethodCallExpr) argExpr;

          System.out.println(callExpr.getName());

          if (callExpr.getName().equals(M_CREATE_CHOOSER))
          {
            IntentInfo info = handleCreateChooser(callExpr);

            if (info != null)
            {
              this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(this.getScope(), name, info));
            }
          }
          else
          {
//            System.out.println("@@@ ESCAPED @@@");
//            System.out.println(name);
//            System.out.println(argExpr.getClass().getCanonicalName());
//            System.out.println(argExpr.toString());

            // TODO: improve this heuristic (empty implicit intent)
            this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(this.getScope(), name, new IntentInfo()));
          }
        }
        else
        {
//          System.out.println("@@@ ESCAPED @@@");
//          System.out.println(name);
//          System.out.println(argExpr.getClass().getCanonicalName());
//          System.out.println(argExpr.toString());

          // TODO: improve this heuristic (empty implicit intent)
          this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(this.getScope(), name, new IntentInfo()));
        }
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
    info.action = getVarValue(args.get(0));
  }

  private void handleSetClass(List<Expression> args, IntentInfo info)
  {
    info.className = getVarValue(args.get(1));
  }

  private void handleSetClassName(List<Expression> args, IntentInfo info)
  {
    info.className = getVarValue(args.get(1));

    // TODO: improve the heuristic being used to determine if the argument
    // is a Context or String object.

    Expression firstArg = args.get(0);

    if (!(matchesContextString(firstArg.toString())))
    {
      info.packageName = getVarValue(firstArg);
    }
  }

  private void handleSetPackage(List<Expression> args, IntentInfo info)
  {
    info.packageName = getVarValue(args.get(0));
  }

  private void handleSetComponent(List<Expression> args, IntentInfo info)
  {
    Expression firstArg = args.get(0);

    if (firstArg instanceof ObjectCreationExpr)
    {
      ObjectCreationExpr component = (ObjectCreationExpr) firstArg;

      info.packageName = getVarValue(component.getArgs().get(0));
      info.className = getVarValue(component.getArgs().get(1));
    }
    else
    {
      // TODO: handle component objects
    }
  }

  private void handleSetData(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
  }

  private void handleSetDataAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
  }

  private void handleSetDataAndType(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
    info.type = getVarValue(args.get(1));
  }

  private void handleSetDataAndTypeAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
    info.type = getVarValue(args.get(1));
  }

  private void handleSetType(List<Expression> args, IntentInfo info)
  {
    info.type = getVarValue(args.get(0));
  }

  private void handleSetTypeAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.type = getVarValue(args.get(0));
  }

  private void handleSetPutExtra(List<Expression> args, IntentInfo info)
  {
    info.extras.put(getVarValue(args.get(0)), getVarValue(args.get(1)));
  }

  private IntentInfo handleCreateChooser(MethodCallExpr callExpr)
  {
    IntentInfo info = null;

    Expression intentExpr = callExpr.getArgs().get(0);

    if (intentExpr instanceof ObjectCreationExpr)
    {
      info = handleIntentCreation((ObjectCreationExpr) intentExpr);
    }
    else if (intentExpr instanceof NameExpr)
    {
      String existingVar = getFullScopeName(((NameExpr) intentExpr).getName());

      IntentInfo existingIntentInfo = this.data.intentsST.get(existingVar);

      if (existingIntentInfo != null)
      {
        info = new IntentInfo();

        info.action = CHOOSER_ACTION;
        info.target = existingIntentInfo;
      }
      else
      {
        // TODO: Fix tool limitation
        info.action = CHOOSER_ACTION;
        info.target = new IntentInfo();
      }
    }
    else
    {
      // TODO: Fix tool limitation
      info.action = CHOOSER_ACTION;
      info.target = new IntentInfo();
    }

    return info;
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

  private void handleVarDeclaration(String varType, VariableDeclarator declarator)
  {
    Expression init = declarator.getInit();
    String varName = getFullScopeName(declarator.getId().toString());

    if (init == null)
    {
      this.data.varsST.put(varName, new VarInfo(varType, "null"));
    }
    else
    {
      if (init instanceof NameExpr)
      {
        NameExpr nameExpr = (NameExpr) init;

        String fullExistingName = getFullScopeName(nameExpr.getName());
        VarInfo varInfo = this.data.varsST.get(fullExistingName);    

        if (varInfo != null)
        { 
          this.data.varsST.put(varName, varInfo);
        }
      }
      else if (init instanceof ObjectCreationExpr)
      {
        ObjectCreationExpr objCreation = (ObjectCreationExpr) init;
        String value = null;

        if (isBooleanVar(varType))
        {
          value = handleBooleanObjectCreation(varName, objCreation);
        }
        else if(isCharVar(varType))
        {
          value = handleCharObjectCreation(varName, objCreation);
        }
        else if(isShortVar(varType))
        {
          value = handleShortObjectCreation(varName, objCreation);
        }
        else if(isFloatVar(varType))
        {
          value = handleFloatObjectCreation(varName, objCreation);
        }
        else if(isDoubleVar(varType))
        {
          value = handleDoubleObjectCreation(varName, objCreation);
        }
        else if(isIntVar(varType))
        {
          value = handleIntObjectCreation(varName, objCreation);
        }
        else if(isByteVar(varType))
        {
          value = handleByteObjectCreation(varName, objCreation);
        }
        else if(isStringVar(varType))
        {
          value = handleStringObjectCreation(varName, objCreation);
        }
        else if(isLongVar(varType))
        {
          value = handleLongObjectCreation(varName, objCreation);
        }

        this.data.varsST.put(varName, new VarInfo(varType, value));
      }
      else if (init instanceof LiteralExpr)
      {
        LiteralExpr literalExpr = (LiteralExpr) init;

        this.data.varsST.put(varName, new VarInfo(varType, literalExpr.toString()));
      }
      else
      {
        this.data.varsST.put(varName, new VarInfo(varType, init.toString()));
      }
    }
  }

  private void handleVarAssignment(VarInfo varInfo, String targetFullName, AssignExpr expr)
  {
    Expression value = expr.getValue();

    if(value instanceof NameExpr)
    {
      NameExpr nameExpr = (NameExpr) value;

      String fullExistingName = getFullScopeName(nameExpr.getName());
      VarInfo existingVarValue = this.data.varsST.get(fullExistingName);    

      if (existingVarValue != null)
      {
        this.data.varsST.put(targetFullName, existingVarValue);
      }
    }
    else if (value instanceof ObjectCreationExpr)
    {
      ObjectCreationExpr objCreation = (ObjectCreationExpr) value;
      String varType = varInfo.type;
      String creationValue = null;

      if (isBooleanVar(varType))
      {
        creationValue = handleBooleanObjectCreation(targetFullName, objCreation);
      }
      else if(isCharVar(varType))
      {
        creationValue = handleCharObjectCreation(targetFullName, objCreation);
      }
      else if(isShortVar(varType))
      {
        creationValue = handleShortObjectCreation(targetFullName, objCreation);
      }
      else if(isFloatVar(varType))
      {
        creationValue = handleFloatObjectCreation(targetFullName, objCreation);
      }
      else if(isDoubleVar(varType))
      {
        creationValue = handleDoubleObjectCreation(targetFullName, objCreation);
      }
      else if(isIntVar(varType))
      {
        creationValue = handleIntObjectCreation(targetFullName, objCreation);
      }
      else if(isByteVar(varType))
      {
        creationValue = handleByteObjectCreation(targetFullName, objCreation);
      }
      else if(isStringVar(varType))
      {
        creationValue = handleStringObjectCreation(targetFullName, objCreation);
      }
      else if(isLongVar(varType))
      {
        creationValue = handleLongObjectCreation(targetFullName, objCreation);
      }

      varInfo.value = creationValue;
    } 
    else
    {
      varInfo.value = value.toString();
    }
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

  // variable handling

  private boolean isBooleanVar(String varType)
  {
    return varType.equals("Boolean") || varType.equals("boolean");
  }

  private String handleBooleanObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  private boolean isCharVar(String varType)
  {
    return varType.equals("Character") || varType.equals("char");
  }

  private String handleCharObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  private boolean isShortVar(String varType)
  {
    return varType.equals("Short") || varType.equals("short");
  }

  private String handleShortObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  private boolean isFloatVar(String varType)
  {
    return varType.equals("Float") || varType.equals("float");
  }

  private String handleFloatObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();  }

  private boolean isDoubleVar(String varType)
  {
    return varType.equals("Double") || varType.equals("double");
  }

  private String handleDoubleObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  private boolean isIntVar(String varType)
  {
    return varType.equals("Integer") || varType.equals("int");
  }

  private String handleIntObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();  }

  private boolean isLongVar(String varType)
  {
    return varType.equals("Long") || varType.equals("long");
  }

  private String handleLongObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  private boolean isByteVar(String varType)
  {
    return varType.equals("Byte") || varType.equals("byte");
  }

  private String handleByteObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  private boolean isStringVar(String varType)
  {
    return varType.equals("String");
  }

  private String handleStringObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling

    String result = null;

    List<Expression> args = expr.getArgs();

    if (args != null && args.size() == 0)
    {
      result = "";
    }
    else
    {
      result = expr.toString();
    }

    return result;
  }

  private String getVarValue(Expression expr)
  {
    // if there's no var, return the original result
    String result = expr.toString();

    if (expr instanceof NameExpr)
    {
      NameExpr nameExpr = (NameExpr) expr;

      VarInfo info = this.data.varsST.get(getFullScopeName(nameExpr.getName()));

      if (info != null)
      {
        result = info.value; 
      }
      else
      {
        result = String.format("could not retrieve var '%s'", result);
      }
    }

    return result;
  }
}
