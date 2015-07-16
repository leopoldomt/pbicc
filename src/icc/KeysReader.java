package icc;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class KeysReader {

   static String classname;
   
   public static void main(String[] args) throws Exception { 
      String fileName =  args[0];
      String filePath = args[1];
      KeysReader.processFileList(fileName, filePath);
   }

   public static void processFileList(String name, String pathRoot) throws Exception {
      try (BufferedReader br = new BufferedReader(new FileReader(name))) {
         String line;
         while ((line = br.readLine()) != null) {
            String fileName = line.substring(1);
            processJavaFile(pathRoot+fileName);
         }
      }
   }
   
   public static void processDir(String dirName) throws Exception {
      File dir = new File(dirName);
      for (File fileName : dir.listFiles()) {
         processJavaFile(fileName.toString());
      }
   }

   static void processJavaFile(String fullyQualifiedName)
         throws FileNotFoundException, ParseException, IOException {
      
      File file = new File(fullyQualifiedName);
      
      classname = file.getName();

      
      // creates an input stream for the file to be parse     
      FileInputStream in = new FileInputStream(file);
      CompilationUnit cu;

      try { // parse the file
         cu = JavaParser.parse(in);
         if(cu != null){
            new KeysVisitor().visit(cu, null);
         }
      } finally {
         in.close();
      }
   }

   // visitor implementation
   private static class KeysVisitor extends VoidVisitorAdapter<Object> {
      Main.PutsAndGets putsAndGets = new Main.PutsAndGets();
      @Override
      public void visit(MethodCallExpr n, Object arg) {
         
         super.visit(n, arg);  // Paulo, please confirm this line was missing. -Marcelo 

         String name = n.getName();
         List<Expression> arguments = n.getArgs();

         // check all methods to populate 'gets' and 'puts' with its arguments
         if (n != null && arguments != null) { 
            // get*Extra
        	// a fix to avoid getExtras()
            if (name.contains("get") && name.contains("Extra") && arguments.size() > 0) {
               if(arguments.get(0).toString().contains(".")) // this is to get just the content after the dot 
               {
                  int i = arguments.get(0).toString().indexOf('.');
                  putsAndGets.gets.add(arguments.get(0).toString().substring(i+1));
               }
               else
               {   
                  putsAndGets.gets.add(arguments.get(0).toString()); 
               }

            }
            // putExtra
            // a fix to avoid putExtras method -Wei
            else if (name.contains("put") && name.contains("Extra") && !name.contains("putExtras")){ 
               if(arguments.get(0).toString().contains("."))
               {
                  int i = arguments.get(0).toString().indexOf('.');
                  putsAndGets.puts.add(arguments.get(0).toString().substring(i+1));
               }
               else
               {
                  putsAndGets.puts.add(arguments.get(0).toString());
               }
            }
         }

         Main.entries.put(classname,putsAndGets);
      }
   }   
}