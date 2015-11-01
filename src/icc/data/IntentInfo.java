package icc.data;

import java.util.HashMap;
import java.util.Map;

public class IntentInfo
{
    public String category = null;
    public String className = null;
    public String packageName = null;
    public String type = null;
    public String action = null;
    public String data = null;
    public Map<String, String> extras;
    
    public IntentInfo()
    {
        extras = new HashMap<String, String>();
    }
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
            
        builder.append(String.format("Category: %s\n", category));
        
        String component = null;
        
        if (className != null && packageName != null)
        {
           component = String.format("%s.%s", packageName, className);
        }
        else if (className != null)
        {
           component = className;
        }
        else if (packageName != null)
        {
           component = String.format("%s.?", packageName);
        }
        
        if (component != null)
        {
           component = component.replace("\"", "");
        }
          
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
}
