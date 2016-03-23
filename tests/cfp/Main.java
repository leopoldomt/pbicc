package cfp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * This is a quick-and-dirty constant-folding and propagation 
 * for strings.  Please use at your discretion. :) 
 * 
 * @author damorim
 *
 */
public class Main {
	
	/**
	 * Constant Folding and Propagation visitor.
	 *
	 */
	static class CFPVisitor extends VoidVisitorAdapter<Object> {
		
		Map<String, String> literalStrings = new HashMap<String, String>();
		Map<String, String> referencedStrings = new HashMap<String, String>();
		
		@Override public void visit(final AssignExpr n, final Object arg) {
			super.visit(n, arg);
			String key = canonicalize(n.getTarget().toString());
			Expression argument = n.getValue();
			updateMaps(key, argument);
		}
		
		// this kind of AST node is reachable from field declaration nodes 
		@Override public void visit(final VariableDeclarator n, final Object arg) {
			super.visit(n, arg);
			String key = canonicalize(n.getId().getName());
			Expression argument = n.getInit();
			updateMaps(key, argument);
		}
		
		String lastPackageVisited;
		String lastClassVisited;
		String lastMethodVisited;
		
		@Override public void visit(final PackageDeclaration n, final Object arg) {
			lastPackageVisited = n.getName().toString();
			super.visit(n, arg);
			//	lastPackageVisited = null;
			/**
			 * I expected to see the entire class encapsulated in object denoted by n.
			 * But that is not the case.  Because of that, I can't reset lastPackageVisited 
			 * to null.  This will work right as long as there are no classes without a 
			 * package or if they exist they are processed first. -Marcelo
			 */
		}
		
		@Override public void visit(final ClassOrInterfaceDeclaration n, final Object arg) {
			lastClassVisited = n.getName();
			super.visit(n, arg);
			lastClassVisited = null;
		}
		
		@Override public void visit(final MethodDeclaration n, final Object arg) {
			lastMethodVisited = n.toString();
			super.visit(n, arg);
			lastMethodVisited = null;
		}
		
		// TODO: check if there are other visit() methods to consider.  
		// this can be done adding more test cases.
		
		private void updateMaps(String key, Expression argument) {
			if (argument instanceof StringLiteralExpr) {
				literalStrings.put(key, ((StringLiteralExpr)argument).getValue());
			} else if (argument instanceof FieldAccessExpr) {
				// these cases need to be unfolded
				FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) argument;
				referencedStrings.put(key, fieldAccessExpr.toString());
			} else { // fallback solution
				StringBuffer sb = new StringBuffer();
				for (String s : argument.toString().split(" ")) {
					String tmp = canonicalize(s);
					if (literalStrings.containsKey(tmp)) {
						// if the canonicalized version is a known symbol, 
						// consider the canonicalized version.
						sb.append(tmp);	
					} else {
						sb.append(s);
					}
					sb.append(" "); // this is important!
				}
				referencedStrings.put(key, sb.toString());
			}
		}
		
		// TODO: please check if this is a reasonable canonical form, 
		// necessary to avoid mismatches. Using:
		//  ** package.class.method.varname **
		private String canonicalize(String varName) {

			List<String> l = new ArrayList<String>();
			if (lastPackageVisited != null) {
				l.add(lastPackageVisited);
			}
			if (lastClassVisited != null) {
				l.add(lastClassVisited);
			}
			if (lastMethodVisited != null) {
				l.add(lastMethodVisited);
			}
			l.add(varName);

			return String.join(".", l);
		}
		
		public Map<String, String> propagate() {
			/*
			 * Please, test more thoroughly case like this:
			 *   A { static String x1 = "Hello"; }
			 *   B { static String x2 = A.x1; } 
			 *   C { static String x3 = B.x2; }
			 *   
			 * Although the example I show does not show problems,
			 * in general, this code should **not** handle such 
			 * cases as the for loop assumes one specific ordering 
			 * of string constants to resolve.
			 * 
			 * To address this problem, I suggest to use a working
			 * list as opposed to this for loop.  -Marcelo 
			 */
			
			for (Map.Entry<String, String> entry : referencedStrings.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				// look for this value elsewhere...
				String tmp = literalStrings.get(val);
				if (tmp == null) { // We could not find an exact match!  
					// Applying some heuristic...
					String[] arr = val.split(" ");
					if (arr.length > 1) {
						// case 1: is this a compound string?
						StringBuffer sb = new StringBuffer();
						for (String part : arr) {
							String tmp2 = findWithSuffixMatch(part);
							sb.append((tmp2!=null)?tmp2:part);
							sb.append(" ");
						}
						tmp = sb.toString();
					} else {
						// case 2: not a compound string...look for a string with the same suffix...
						tmp = findWithSuffixMatch(val);
						if (tmp == null) {
							System.err.println("MISSED THIS CASE " + entry);
							continue;
						}
					}
				}
				referencedStrings.remove(entry);
				literalStrings.put(key, tmp);
			}
			return Collections.unmodifiableMap(literalStrings);
		}

		private String findWithSuffixMatch(String tmp) {
			List<String> res = new ArrayList<String>();
			for (String s: literalStrings.keySet()) {
				if (s.endsWith(tmp)) {
					res.add(literalStrings.get(s));
					break;
				}
			}
			// null if could not find substring or it is ambiguous
			return (res.size() == 1) ? res.get(0) : null;
		}
	}

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		//List<String> l = Arrays.asList("tests/cfp/B.java", "tests/cfp/A.java");
		List<String> l = Arrays.asList("test-data/explicit_intent_test/app/src/main/java/br/ufpe/cin/pbicc/test/intents/explicit/MainActivity.java","test-data/explicit_intent_test/app/src/main/java/br/ufpe/cin/pbicc/test/intents/explicit/Strings.java");		
		CFPVisitor visitor = new CFPVisitor();
		for (String s : l) {
			FileInputStream in = new FileInputStream(s);
			CompilationUnit cu = JavaParser.parse(in);
			cu.accept(visitor, null);	
		}
		
		for (Map.Entry<String, String> keyValue : visitor.propagate().entrySet()) {
			System.out.printf("%s -> %s\n", keyValue.getKey(), keyValue.getValue());
		}
	}

}