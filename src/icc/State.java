package icc;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton implementation of state
 * 
 * @author damorim
 *
 */
public class State {
  
  private Map<String/*classname*/, PutsAndGets> pgMap = new HashMap<String, PutsAndGets>();
  
  private static State instance;
  private State() {} // private constructor
  
  public static State getInstance() {
    if (instance == null) {
      instance = new State(); // only call to constructor and called at most once
    }
    return instance;
  }
  
  public Map<String/*classname*/, PutsAndGets> pgMap() {
    return pgMap;
  }

}