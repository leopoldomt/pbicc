package icc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import icc.data.ICCLinkInfo;

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
  private Map<String/*classname*/, List<ICCLinkInfo<String>>> explicitMap = new HashMap<String, List<ICCLinkInfo<String>>>();

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

  public Map<String/*classname*/, List<ICCLinkInfo<String>>> explicitMap()
  {
    return explicitMap;
  }

  public Set<String> getFiles() {
    return Collections.unmodifiableSet(pgMap.keySet());
  }

}
