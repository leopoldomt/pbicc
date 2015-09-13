package icc;

import icc.visitors.KeysVisitor;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IntentKeysReader {

   public static void main(String[] args) throws Exception {
      String fileName =  args[0];
      String filePath = args[1];
      IntentKeysReader.processFileList(fileName, filePath);
   }

   public static void processFileList(String name, String pathRoot) throws Exception {
      try (BufferedReader br = new BufferedReader(new FileReader(name))) {
         String line;
         while ((line = br.readLine()) != null) {
            String fileName = line.substring(2);
            processJavaFile(pathRoot, fileName);
         }
      }
   }

   public static void processDir(String dirName) throws Exception {
      File dir = new File(dirName);
      for (File fileName : dir.listFiles()) {
          if (fileName.isDirectory()) {
            processDir(dirName+fileName.getName());
          } else {
            processJavaFile(dirName, fileName.getName());
          }
      }
   }

   static void processJavaFile(String pathRoot, String fullyQualifiedName)
         throws FileNotFoundException, ParseException, IOException {

      File file = new File(pathRoot, fullyQualifiedName);

      // creates an input stream for the file to be parse
      FileInputStream in = new FileInputStream(file);
      CompilationUnit cu;

      try { // parse the file
         cu = JavaParser.parse(in);
         if(cu != null){
        	 String className = fullyQualifiedName.replaceAll("/", ".");
        	 new KeysVisitor(className).visit(cu, null);
         }
      } finally {
         in.close();
      }
   }

}
