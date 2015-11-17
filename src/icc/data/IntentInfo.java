package icc.data;

import java.util.HashMap;
import java.util.Map;

public class IntentInfo
{
    public final String NOT_SET = "#NOT SET#";
  
    public String category = NOT_SET;
    public String className = NOT_SET;
    public String packageName = NOT_SET;
    public String type = NOT_SET;
    public String action = NOT_SET;
    public String data = NOT_SET;
    public Map<String, String> extras;
    
    public IntentInfo()
    {
        extras = new HashMap<String, String>();
    }
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
            
        builder.append(String.format("Category: %s\n", category));
        
        String component = getComponent();
          
        builder.append(String.format("Component: %s\n", component));
        
        builder.append(String.format("Type: %s\n", type));
        builder.append(String.format("Action: %s\n", action));
        builder.append(String.format("Data: %s\n", data));
        
        builder.append("Extras: \n");
        
        for (Map.Entry<String, String> entry : extras.entrySet())
        {
            builder.append(String.format("\t%s: %s\n", entry.getKey(), entry.getValue()));
        }
        
        return builder.toString();
    }
    
    public String getComponent()
    {
      String component = NOT_SET;
      
      if (!className.equals(NOT_SET) && !packageName.equals(NOT_SET))
      {
         component = String.format("%s.%s", packageName, className);
      }
      else if (!className.equals(NOT_SET))
      {
         component = className;
      }
      else if (!packageName.equals(NOT_SET))
      {
         component = String.format("%s.?", packageName);
      }
      
      if (!component.equals(NOT_SET))
      {
         component = component.replace("\"", "");
      }
      
      return component;
    }
    
    public boolean isExplicit()
    {
      return !className.equals(NOT_SET) || !packageName.equals(NOT_SET);
    }
}
