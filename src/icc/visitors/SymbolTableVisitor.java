package icc.visitors;

import java.util.List;

import icc.data.ICCLinkFindingResults;
import icc.data.IntentInfo;
import icc.data.VarInfo;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class SymbolTableVisitor extends BaseVisitor
{
  public SymbolTableVisitor(ICCLinkFindingResults data)
  {
    super(data);
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
        	//System.out.println("VariableDeclarationExpr [vartype: "+ varType + " | var: " + var + "]");
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
        	//System.out.println("FieldDeclaration [vartype: "+ varType + " | var: " + var + "]");
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
    	// System.out.println("vartype: "+ varInfo + " | fullName: " + fullName + " | expr: " + expr);
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
