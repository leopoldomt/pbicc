package icc;

import java.util.HashSet;
import java.util.Set;

public class PutsAndGets {
  
  public Set<String> gets = new HashSet<String>();
  public Set<String> puts = new HashSet<String>();

  public String toString() {
    return String.format("\n  GETS %s\n  PUTS %s\n", gets.toString(), puts.toString());
  }

  /**
   * true if there is a dependency across the two pg's 
   */
  
  enum HEURISTIC_MATCH {ONE, ALL}; // TODO: we should think about other options -M
  static HEURISTIC_MATCH heuristic = HEURISTIC_MATCH.ONE;
  
  public boolean isDep(PutsAndGets pg) {
    switch (heuristic) {
    case ONE: // one key match suffices
      for (String read : pg.gets) {
        if (this.puts.contains(read)) {
          return true;
        }
      }
      return false;
    case ALL: // all keys written (if exist) must be present 
      return !pg.gets.isEmpty() && !puts.isEmpty() && pg.gets.containsAll(puts);
    default:
      throw new RuntimeException("don't know this heuristic");      
    }    
  }
  
}