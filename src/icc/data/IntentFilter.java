package icc.data;

public class IntentFilter
{
  public static class Data
  {
    public String scheme;
    public String host;
    public String port;
    public String path;
    public String pathPattern;
    public String pathPrefix;
    public String mimeType;
    
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      
      System.out.println(String.format("%s: %s\n", "scheme", scheme));
      System.out.println(String.format("%s: %s\n", "host", host));
      System.out.println(String.format("%s: %s\n", "port", port));
      System.out.println(String.format("%s: %s\n", "path", path));
      System.out.println(String.format("%s: %s\n", "pathPattern", pathPattern));
      System.out.println(String.format("%s: %s\n", "pathPrefix", pathPrefix));
      System.out.println(String.format("%s: %s\n", "mimeType", mimeType));
      
      return builder.toString();
    }
  }
  
  public String action;
  public String category;
  public Data data = new Data();
  
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    
    System.out.println(String.format("%s: %s\n", "Action", action));
    System.out.println(String.format("%s: %s\n", "Category",category));
    System.out.println("Data:");
    System.out.println(String.format("%s\n", data));
    
    return builder.toString();
  }
}
