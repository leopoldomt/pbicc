package icc;


import java.util.HashMap;
import java.util.Map;

import icc.data.IntentInfo;
import icc.visitors.IntentVisitor;
import japa.parser.ast.CompilationUnit;

public class SymbolTable
{    
    public static Map<String, IntentInfo> build(CompilationUnit cu)
    {
      Map<String, IntentInfo> table = new HashMap<String, IntentInfo>();
      
      // second visitor (expande intent info)
      IntentVisitor intentVisitor = new IntentVisitor(table);
      intentVisitor.visit(cu, null);
      
      return table;
    }
}
