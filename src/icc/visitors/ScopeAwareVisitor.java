package icc.visitors;

import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public abstract class ScopeAwareVisitor extends VoidVisitorAdapter<Object>
{
  protected List<String> scope;

  public ScopeAwareVisitor()
  {
    this.scope = new ArrayList<String>();
  }

  public void visit(ClassOrInterfaceDeclaration expr, Object arg)
  {
    this.scope.add(expr.getName());

    super.visit(expr, arg);

    this.scope.remove(this.scope.size() - 1);
  }

  public void visit(MethodDeclaration expr, Object arg)
  {
    this.scope.add(expr.getName());

    super.visit(expr, arg);

    this.scope.remove(this.scope.size() - 1);
  }

  // utils
  
  protected String getScope()
  {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < this.scope.size(); i++)
    {
      builder.append(this.scope.get(i));

      if (i < this.scope.size() - 1)
      {
        builder.append(".");
      }
    }

    return builder.toString();
  }
  
  private String getFullScopeName(String name)
  {
    return String.format("%s.%s", this.getScope(), name);
  }
}
