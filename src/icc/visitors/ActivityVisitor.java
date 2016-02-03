package icc.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class ActivityVisitor extends BaseVisitor
{ 
  private final String M_START_ACTIVITY = "startActivity";
  private final String M_START_ACTIVITY_FOR_RESULT = "startActivityForResult";

  private final List<String> ACTIVITY_CALLS = 
      new ArrayList<String>(Arrays.asList(new String[]{M_START_ACTIVITY, M_START_ACTIVITY_FOR_RESULT}));

  public ActivityVisitor(ICCLinkFindingResults data)
  {
    super(data);
  }

  @Override
  public void visit(MethodCallExpr expr, Object arg)
  {
    super.visit(expr, arg);

    String name = expr.getName();
    List<Expression> args = expr.getArgs();

    if (ACTIVITY_CALLS.contains(name))
    {
      if (name.equals(M_START_ACTIVITY) || name.equals(M_START_ACTIVITY_FOR_RESULT))
      {
        // stats being incremented
        this.data.stats.addStartActivity();
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
            // TODO: improve this heuristic (empty implicit intent)
            this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(this.getScope(), name, new IntentInfo()));
          }
        }
        else
        {
          // TODO: improve this heuristic (empty implicit intent)
          this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(this.getScope(), name, new IntentInfo()));
        }
      }
    }
  }
}
