package icc.data;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable<T>
{
  private Map<String, T> map;

  public SymbolTable()
  {
    this.map = new HashMap<String, T>();
  }

  public void put(String symbol, T value)
  {
    this.map.put(symbol, value);
  }

  public T get(String symbol)
  {
    T result = null;
    String currentSymbol = symbol;

    while(currentSymbol != null && !this.map.containsKey(currentSymbol))
    {
      currentSymbol = getVarInBroaderScope(currentSymbol);
    }
    
    if (currentSymbol != null)
    {
      result = this.map.get(currentSymbol);
    }

    return result;
  }
  
  public String getClosestVar(String var)
  {
    String result = null;
    
    // ...
    
    return result;
  }

  // utils

  private String getVarInBroaderScope(String symbol)
  {
    String var = null;

    if (symbol.contains("."))
    {
      String[] tokens = symbol.split("\\.");
      int tokenCount = tokens.length;      

      if (tokenCount != 2)
      {
          StringBuilder builder = new StringBuilder();
          
          for (int i = 0; i < tokenCount - 2; i++)
          {
            builder.append(tokens[i]);
            builder.append(".");
          }
          
          builder.append(tokens[tokenCount - 1]);
          
          var = builder.toString();
      }
    }

    return var;
  }
  
  // getters/setters
  public Map<String, T> getMap()
  {
    return map;
  }
}
