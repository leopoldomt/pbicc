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
  //TODO: see commons-cli related comment on Main.java
  enum HEURISTIC_MATCH {ONE, ALL}; // TODO: we should think about other options -M
  static HEURISTIC_MATCH heuristic = HEURISTIC_MATCH.ALL;
  static boolean IGNORE_EXTRA_KEYS = false;
  
  // does it make sense to report confidence level based on some heuristics?
  /**
   * 
   * @param pg
   * @return true if there is a dependence from the component 
   * characterized with THIS objects to the component characterized 
   * with the parameter object
   */
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