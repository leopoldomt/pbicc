package icc;

import com.github.javaparser.ast.CompilationUnit;

public interface CompUnitProcessable {
  void process(String fullyQualifiedName, CompilationUnit cu);
}
