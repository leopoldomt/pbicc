package util;

/**
 * 
 * This is an example class that shows how to use Java 8 (some features 
 * available since Java 6, I think) to obtain ASTs as generated from the 
 * Java compiler, which is accessed programmatically from Java.  This is 
 * the preferable choice for doing source code analysis as it is maintained 
 * by a big team.  The Checker Framework builds on this infrastructure.
 * 
 * Note that we currently use a lightweight alternative library for parsing 
 * Java code -- javaparser, which is by the way written by a Brazilian :), 
 * and seems good enough for us so far.  I realized there is a newer 
 * version available but did not read the release notes to see what is new. 
 * 
 * With this example handy you can decide when/if new to port to Java 8.
 *
 * -Marcelo
 */

/**
 * This code does not compile because it depends on package available 
 * from the "tool.jar" library (see com.sun.source.**).  Each JDK 
 * provides such library, under the "libs" directory.  
 * 
 * As I did not know what JDK version you use I hesitated to add by myself.  
 * If everyone uses Java 8, I think it is fine to add tools.jar of that JDK 
 * to the repository.
 * 
 * You can remove this section after resolving that issue.  -Marcelo
 */

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

// need to import tools.jar for this to work.  
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

public class JavaCompilerAST {

  // example (so that this example class becomes self-contained)
  static class Bar {
    Bar(Map<String, Integer> m) {
      m.put("key", 1); // method call 
    }
  }
  
  /**
   * References 
   * Example - https://github.com/jakubholynet/blog/blob/master/miniprojects/generics-detector/CollectionGenericsTypeExctractor.java
   * Overview - http://openjdk.java.net/groups/compiler/doc/hhgtjavac/index.html#moreInfo
   */
  
  /**
   * visitor class for traversing the ast 
   */
  public static class CodeAnalyzerTreeVisitor extends TreePathScanner<Object, Trees> {
    
    boolean ENABLE = false;
    
    @Override
    public Object visitClass(ClassTree node, Trees trees) {
      if (node.getSimpleName().toString().contains("Bar")) {
        ENABLE = true;
      }
      Object res = super.visitClass(node, trees);
      ENABLE = false; 
      return res;
    }

    // See TreeScanner for other "visit" methods: https://docs.oracle.com/javase/7/docs/jdk/api/javac/tree/com/sun/source/util/TreeScanner.html
    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Trees trees) {
        Object res = super.visitMethodInvocation(node, trees); // don't forget to call super!
        if (ENABLE) {
          System.out.printf("found %s!", node.getMethodSelect());
        }
        return res;
    }
    
  }

  /***
   * processor class that invokes visitor
   */
  @SupportedSourceVersion(SourceVersion.RELEASE_6)
  @SupportedAnnotationTypes("*")
  public static class CodeAnalyzerProcessor extends AbstractProcessor {

    private final CodeAnalyzerTreeVisitor visitor = new CodeAnalyzerTreeVisitor();
    private Trees trees;

    @Override
    public void init(ProcessingEnvironment pe) {
      super.init(pe);
      trees = Trees.instance(pe);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
      for (Element e : roundEnvironment.getRootElements()) {
        // Normally the ellement should represent a class                                                                                                    
        TreePath tp = trees.getPath(e);
        // invoke the scanner                                                                                                                                
        visitor.scan(tp, trees);
      }
      return true;    // handled, don't invoke other processors                                                                                                    
    }
  }

  public static void main(String[] args) {
    // name of source files
    File[] files1 = new File[]{new File("src/JavaCompilerAST.java")};

    // access compile object
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    // reading source files as java object (not ast's)  
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    Iterable<? extends JavaFileObject> sources = /* check fileManager.list(...) */
        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));
    compiler.getTask(null, fileManager, null, null, null, sources).call();

    // Create the compilation task
    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, sources);

    // invoke processor (to do whatever with ASTs).  for this case, only print method name 
    Iterable<Processor> processors = Arrays.asList(new CodeAnalyzerProcessor());
    task.setProcessors(processors);;
    task.call();
  }

}