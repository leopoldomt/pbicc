package icc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton implementation of state
 * 
 * @author damorim
 *
 */
public class State {
  
  /**
   * elements of state
   */
  private Map<String/*classname*/, PutsAndGets> pgMap = new HashMap<String, PutsAndGets>();
  
  
  /**
   * singleton implementation  
   */
  private static State instance;
  private State() {} // private constructor
  public static State getInstance() {
    if (instance == null) {
      instance = new State(); // only call to constructor and called at most once
    }
    return instance;
  }
  
  /**
   * getter functions
   *    
   */
  public Map<String/*classname*/, PutsAndGets> pgMap() {
    return pgMap;
  }
  
  public Set<String> getFiles() {
    return Collections.unmodifiableSet(pgMap.keySet());
  }
  
}