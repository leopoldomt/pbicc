package icc.data;

public class ICCLinkInfo <T>
{
  private String methodCall;
  private String packageName;
  private String className;
  private String methodName;
  private String shortScope;
  private String scope;
  private T target;

  public ICCLinkInfo(String scope, String methodCall, T target)
  {
    this.scope = scope;
    this.methodCall = methodCall;
    this.target = target;
  }
  
  public ICCLinkInfo(String scope, String shortScope, String packageName, String className, String methodName, String methodCall, T target)
  {
    this.scope = scope;
    this.shortScope = shortScope;
    this.packageName = packageName;
    this.className = className;
    this.methodName = methodName;
    this.methodCall = methodCall;
    this.target = target;
  }
  
  public String toString()
  {
    return String.format("\nScope: %s\nShort Scope: %s\nPackage: %s\nClass: %s\nMethod: %s\nLink {\n---Start:\n\t%s\n---End:\n\t%s\n}\n",
            scope, shortScope, packageName, className, methodName, methodCall, target.toString().replace("\n", "\n\t"));
  }
  
  public String toCSV() {
    String csv = "";
    if (target instanceof IntentInfo) {
      IntentInfo i = (IntentInfo) target;      
      csv = String.format("%s,%s,%s",
          scope,
          methodName,
          i.toCSV());
    }
    else {
      csv = String.format("%s,%s,%s",
          scope,
          methodName,
          target.toString());
    }
    
    return csv;
  }
  
  public String toJSON() {
	  
	  return "";
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
