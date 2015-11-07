package icc.data;

public class ICCLinkInfo <T>
{
  private String originFile;
  private String methodName;
  private T target;

  public ICCLinkInfo(String methodName, T target)
  {
    this.methodName = methodName;
    this.target = target;
  }

  public String getOriginFile() {
    return originFile;
  }

  public void setOriginFile(String originFile) {
    this.originFile = originFile;
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
}
