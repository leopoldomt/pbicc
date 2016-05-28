package icc.data;

import java.util.ArrayList;

public class IntentFilter
{
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
