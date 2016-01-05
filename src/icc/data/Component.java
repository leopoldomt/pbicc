package icc.data;

import java.util.LinkedList;
import java.util.List;

public class Component
{
    public final String NOT_SET = "#NOT SET#";
    //TODO: do we need to create subclasses for each kind of component? If so, remove ComponentType 
    public ComponentType type;
    public String label = NOT_SET;
    public String name = NOT_SET;
    public boolean exported = false;
    public List<IntentFilter> intentFilters;
    
    //TODO: capture other kinds of common information among components
    
    public Component()
    {
      intentFilters = new LinkedList<IntentFilter>();
    }
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
            
        builder.append(String.format("Type: %s\n", type));
        builder.append(String.format("Label: %s\n", label));
        builder.append(String.format("Name: %s\n", name));
        
        builder.append("Intent Filters: \n");
        
        for (IntentFilter iFilter : intentFilters)
        {
            builder.append(String.format("%s\n", iFilter));
        }
        
        return builder.toString();
    }
    
}
