package icc.data;

import java.util.ArrayList;
import java.util.Iterator;

public class FieldList extends ArrayList<String> {

  /**
   * 
   */
  private static final long serialVersionUID = 2949554359338631287L;

  @Override
  public String toString() {
    Iterator<String> it = iterator();
    if (! it.hasNext())
        return "[]";

    StringBuilder sb = new StringBuilder();
    //sb.append('[');
    for (;;) {
        String e = it.next();
        sb.append(e);
        if (! it.hasNext())
            return sb.toString();
        sb.append(';').append(' ');
    }

  }

  
  
}
