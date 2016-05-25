package icc.data;

import java.util.ArrayList;

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
  
  public ArrayList<String> actions = new ArrayList<String>();
  public ArrayList<String> categories = new ArrayList<String>();
  public Data data = new Data();
  
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    
    for(int i=0; i<actions.size(); i++){
    	builder.append(String.format("%s(%d): %s\n", "Action", i, actions.get(i)));	
    }
    
    for(int i=0; i<categories.size(); i++){
    	builder.append(String.format("%s(%d): %s\n", "Category",i,categories.get(i)));
    }
    builder.append("Data:");
    builder.append(String.format("%s\n", data));
    
    return builder.toString();
  }
}
