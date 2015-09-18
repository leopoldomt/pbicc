package icc;

import japa.parser.ast.CompilationUnit;

public interface CompUnitProcessable {
  void process(String fullyQualifiedName, CompilationUnit cu);
}
