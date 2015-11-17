package icc;

import icc.data.ICCLinkFindingResults;
import icc.visitors.IntentVisitor;
import japa.parser.ast.CompilationUnit;

public class ICCLinkFinder
{
  public static ICCLinkFindingResults findICCLinks(CompilationUnit cu)
  {
    ICCLinkFindingResults results = new ICCLinkFindingResults();
    
    IntentVisitor visitor = new IntentVisitor(results);
    visitor.visit(cu, null);
    
    results.accessStats();
    
    return results;
  }
}
