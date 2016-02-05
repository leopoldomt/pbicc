package icc.data;

import java.util.HashMap;
import java.util.Map;

public class IntentInfo
{
    public final String NOT_SET = "#NOT SET#";
  
    public FieldList category = new FieldList();
    public FieldList className = new FieldList();
    public FieldList packageName = new FieldList();
    public FieldList type = new FieldList();
    public FieldList action = new FieldList();
    public FieldList data = new FieldList();
    public Map<String, String> extras;
    
    public IntentInfo target = null;
    
    public IntentInfo()
    {
        extras = new HashMap<String, String>();
    }
    
    //TODO: fix implementation when we have more than one possibility for a field   
    public String toCSV() {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format("%s,", category));
      String component = getComponent();
      builder.append(String.format("%s,", component));
      builder.append(String.format("%s,", type));
      builder.append(String.format("%s,", action));
      builder.append(String.format("%s,", data));
      for (Map.Entry<String, String> entry : extras.entrySet()) {
          builder.append(String.format("%s: %s; ", entry.getKey(), entry.getValue()));
      }
      return builder.toString();
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
      
      if (!className.isEmpty() && !packageName.isEmpty())
      {
        int numC = className.size();
        int numP = packageName.size();
        if (numC == numP) {
          component = "";
          for (int i = 0; i < numC; i++) {
            component+= String.format("%s.%s", packageName.get(i), className.get(i));
          }
        }
        else {
          throw new RuntimeException("Mismatch between size of packageName and className elements");
        }
      }
      else if (!className.isEmpty())
      {
        component = "";
        for (String c: className) {
          component+= c+ "; " ;
        }
      }
      else if (!packageName.equals(NOT_SET))
      {
        component = "";
        for (String c: packageName) {
          component+= String.format("%s.?", c) + "; ";
        }
      }
      
      if (!component.equals(NOT_SET))
      {
         component = component.replace("\"", "");
      }
      
      return component;
    }   
    
    public boolean isExplicit()
    {
      return !className.isEmpty() || !packageName.isEmpty();
    }
    
    public boolean isChooser()
    {
      return this.target != null;
    }
}
