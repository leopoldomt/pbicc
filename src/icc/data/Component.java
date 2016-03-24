package icc.data;

import java.util.LinkedList;
import java.util.List;

public abstract class Component
{
    public final String NOT_SET = "#NOT SET#";
    //TODO: do we need to create subclasses for each kind of component? If so, remove ComponentType 
    public ComponentType type;
    public String label = NOT_SET;
    public String name = NOT_SET;
    public boolean exported = false;
    public List<IntentFilter> intentFilters;
    
    //TODO: capture other kinds of common information among components
    
    public boolean enabled = true;
    public String permission = NOT_SET;
    public String process = NOT_SET;
    
    public Component()
    {
      intentFilters = new LinkedList<IntentFilter>();
    }
    
    
    public abstract String toStringExclusiveAttributes();
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
            
        builder.append(String.format("Type: %s\n", type));
        builder.append(String.format("Label: %s\n", label));
        builder.append(String.format("Name: %s\n", name));
        builder.append(String.format("Exported: %s\n", exported));
        builder.append(String.format("Enabled: %s\n", enabled));
        builder.append(String.format("Permission: %s\n", permission));
        builder.append(String.format("Process: %s\n", process));
        
        builder.append(toStringExclusiveAttributes());

        
        builder.append("Intent Filters: \n");       
        
        for (IntentFilter iFilter : intentFilters)
        {
            builder.append(String.format("%s\n", iFilter));
        }
        
        return builder.toString();
    }
    
}
