package icc;

import icc.data.ICCLinkFindingResults;
import icc.visitors.ActivityVisitor;
import icc.visitors.SymbolTableVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class TestVisitor {
  
  
  
  public static void main(String[] args) {
    String baseDir = "/Users/leopoldomt/Documents/";
    String appSourceDir = "";
    appSourceDir = baseDir + "workspaces/android/OlaFulano/src/br/ufpe/cin/olafulano"; 
    //appSourceDir = baseDir + "cin/pbicc/test-data/implicit_gmaps/src/br/ufpe/cin/olagooglemaps";
    String fileName = "TelaInicial.java";
    File file = new File(appSourceDir, fileName);
    // creates an input stream for the file to be parsed
    FileInputStream in;
    try {
      in = new FileInputStream(file);
      CompilationUnit cu = JavaParser.parse(in);    
      if(cu != null){
        ICCLinkFindingResults results = new ICCLinkFindingResults();
        
        SymbolTableVisitor sTVisitor = new SymbolTableVisitor(results);
        sTVisitor.visit(cu, null);
        
        ActivityVisitor activityVisitor = new ActivityVisitor(results);
        activityVisitor.visit(cu, null);
        results.accessStats();
        System.out.println(results.iccLinks);
      }
      in.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
}
