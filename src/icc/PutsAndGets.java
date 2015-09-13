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
   * returns true if
   * pg.gets (read set) intersects with this.puts (write set)
   */
  public boolean isDep(PutsAndGets pg) {
    for (String read : pg.gets) {
      if (this.puts.contains(read)) {
        return true;
      }
    }
    return false;
  }
  
}