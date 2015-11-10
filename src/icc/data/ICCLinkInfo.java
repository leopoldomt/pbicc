package icc.data;

public class ICCLinkInfo <T>
{
  private String methodName;
  private String scope;
  private T target;

  public ICCLinkInfo(String scope, String methodName, T target)
  {
    this.scope = scope;
    this.methodName = methodName;
    this.target = target;
  }
  
  public String toString()
  {
    return String.format("Scope: %s\nLink {\n---Start:\n\t%s\n---End:\n\t%s\n}",
                         scope,
                         methodName,
                         target.toString().replace("\n", "\n\t"));
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public T getTarget() {
    return target;
  }

  public void setTarget(T target) {
    this.target = target;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }
}
