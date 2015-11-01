package icc;


import java.util.HashMap;
import java.util.Map;

import icc.data.IntentInfo;
import icc.visitors.IntentDeclarationVisitor;
import icc.visitors.IntentInfoVisitor;
import japa.parser.ast.CompilationUnit;

public class SymbolTable
{    
    public static Map<String, IntentInfo> build(CompilationUnit cu)
    {
      Map<String, IntentInfo> table = new HashMap<String, IntentInfo>();
      
      // first visitor (get intent instances)
      IntentDeclarationVisitor declarationVisitor = new IntentDeclarationVisitor(table);
      declarationVisitor.visit(cu, null);
      
      // second visitor (expande intent info)
      IntentInfoVisitor intentInfoVisitor = new IntentInfoVisitor(table);
      intentInfoVisitor.visit(cu, null);
      
      return table;
    }
}
