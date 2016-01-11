package icc;

import icc.data.ICCLinkFindingResults;
import icc.visitors.ActivityVisitor;
import icc.visitors.ServiceVisitor;
import icc.visitors.SymbolTableVisitor;
import japa.parser.ast.CompilationUnit;

public class ICCLinkFinder
{
  public static ICCLinkFindingResults findICCLinks(CompilationUnit cu)
  {
    ICCLinkFindingResults results = new ICCLinkFindingResults();
    
    SymbolTableVisitor sTVisitor = new SymbolTableVisitor(results);
    sTVisitor.visit(cu, null);
    
    ActivityVisitor activityVisitor = new ActivityVisitor(results);
    activityVisitor.visit(cu, null);
    
    ServiceVisitor serviceVisitor = new ServiceVisitor(results);
    serviceVisitor.visit(cu, null);
    
    results.accessStats();
    
    return results;
  }
}
