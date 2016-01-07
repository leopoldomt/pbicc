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
      
      builder.append(String.format("%s: %s\n", "scheme", scheme));
      builder.append(String.format("%s: %s\n", "host", host));
      builder.append(String.format("%s: %s\n", "port", port));
      builder.append(String.format("%s: %s\n", "path", path));
      builder.append(String.format("%s: %s\n", "pathPattern", pathPattern));
      builder.append(String.format("%s: %s\n", "pathPrefix", pathPrefix));
      builder.append(String.format("%s: %s\n", "mimeType", mimeType));
      
      return builder.toString();
    }
  }
  
  public String action;
  public String category;
  public Data data = new Data();
  
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    
    builder.append(String.format("%s: %s\n", "Action", action));
    builder.append(String.format("%s: %s\n", "Category",category));
    builder.append("Data:");
    builder.append(String.format("%s\n", data));
    
    return builder.toString();
  }
}
