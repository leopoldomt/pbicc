package icc.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import icc.data.ICCLinkFindingResults;
import icc.data.IntentInfo;
import icc.data.VarInfo;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public abstract class BaseVisitor extends ScopeAwareVisitor
{
  // constants
  protected final String CHOOSER_ACTION = "ACTION_CHOOSER";
  protected final String M_SET_ACTION = "setAction";
  protected final String M_SET_CLASS = "setClass";
  protected final String M_SET_CLASS_NAME = "setClassName";
  protected final String M_SET_COMPONENT = "setComponent";
  protected final String M_SET_DATA = "setData";
  protected final String M_SET_DATA_AND_NORMALIZE = "setDataAndNormalize";
  protected final String M_SET_DATA_AND_TYPE = "setDataAndType";
  protected final String M_SET_DATA_AND_TYPE_AND_NORMALIZE = "setDataAndTypeAndNormalize";
  protected final String M_SET_PACKAGE = "setPackage";
  protected final String M_SET_TYPE = "setType";
  protected final String M_SET_TYPE_AND_NORMALIZE = "setTypeAndNormalize";
  protected final String M_PUT_EXTRA = "putExtra";
  protected final String M_CREATE_CHOOSER = "createChooser";

  protected final List<Pattern> CONTEXT_STRINGS = new ArrayList<Pattern>(Arrays.asList(new Pattern[]{Pattern.compile("activity"),
      Pattern.compile("context"),
      Pattern.compile("([\\w]+\\.)?this")}));

  protected List<String> VAR_TYPES = Arrays.asList(new String[] {"bool", "Boolean", "char", "Character",
      "float", "Float", "byte", "Byte",
      "double", "Double", "int", "Integer",
      "short", "Short", "long", "Long", "String"});
  
  protected ICCLinkFindingResults data;
  
  public BaseVisitor(ICCLinkFindingResults data)
  {
    this.data = data;
  }
  
  protected String getFullScopeName(String name)
  {
    return String.format("%s.%s", this.getScope(), name);
  }
  
  protected String getVarValue(Expression expr)
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
  
  protected void handleSetAction(List<Expression> args, IntentInfo info)
  {
    info.action = getVarValue(args.get(0));
  }

  protected void handleSetClass(List<Expression> args, IntentInfo info)
  {
    info.className = getVarValue(args.get(1));
  }

  protected void handleSetClassName(List<Expression> args, IntentInfo info)
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

  protected void handleSetPackage(List<Expression> args, IntentInfo info)
  {
    info.packageName = getVarValue(args.get(0));
  }

  protected void handleSetComponent(List<Expression> args, IntentInfo info)
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

  protected void handleSetData(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
  }

  protected void handleSetDataAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
  }

  protected void handleSetDataAndType(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
    info.type = getVarValue(args.get(1));
  }

  protected void handleSetDataAndTypeAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.data = getVarValue(args.get(0));
    info.type = getVarValue(args.get(1));
  }

  protected void handleSetType(List<Expression> args, IntentInfo info)
  {
    info.type = getVarValue(args.get(0));
  }

  protected void handleSetTypeAndNormalize(List<Expression> args, IntentInfo info)
  {
    info.type = getVarValue(args.get(0));
  }

  protected void handleSetPutExtra(List<Expression> args, IntentInfo info)
  {
    info.extras.put(getVarValue(args.get(0)), getVarValue(args.get(1)));
  }

  protected IntentInfo handleCreateChooser(MethodCallExpr callExpr)
  {
    IntentInfo info = new IntentInfo();

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

  protected IntentInfo handleIntentCreation(ObjectCreationExpr expr)
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

  protected void handleVarDeclaration(String varType, VariableDeclarator declarator)
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

  protected void handleVarAssignment(VarInfo varInfo, String targetFullName, AssignExpr expr)
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
  protected boolean matchesContextString(String candidate)
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

  // variable handling

  protected boolean isBooleanVar(String varType)
  {
    return varType.equals("Boolean") || varType.equals("boolean");
  }

  protected String handleBooleanObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  protected boolean isCharVar(String varType)
  {
    return varType.equals("Character") || varType.equals("char");
  }

  protected String handleCharObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  protected boolean isShortVar(String varType)
  {
    return varType.equals("Short") || varType.equals("short");
  }

  protected String handleShortObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  protected boolean isFloatVar(String varType)
  {
    return varType.equals("Float") || varType.equals("float");
  }

  protected String handleFloatObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();  }

  protected boolean isDoubleVar(String varType)
  {
    return varType.equals("Double") || varType.equals("double");
  }

  protected String handleDoubleObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  protected boolean isIntVar(String varType)
  {
    return varType.equals("Integer") || varType.equals("int");
  }

  protected String handleIntObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();  }

  protected boolean isLongVar(String varType)
  {
    return varType.equals("Long") || varType.equals("long");
  }

  protected String handleLongObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  protected boolean isByteVar(String varType)
  {
    return varType.equals("Byte") || varType.equals("byte");
  }

  protected String handleByteObjectCreation(String varName, ObjectCreationExpr expr)
  {
    // TODO: improve constructor handling
    return expr.getArgs().get(0).toString();
  }

  protected boolean isStringVar(String varType)
  {
    return varType.equals("String");
  }

  protected String handleStringObjectCreation(String varName, ObjectCreationExpr expr)
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
}
