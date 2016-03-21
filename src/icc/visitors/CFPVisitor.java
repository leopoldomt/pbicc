package icc.visitors;

import icc.State;
import icc.data.ICCLinkFindingResults;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class CFPVisitor extends BaseVisitor {
	State state;

	public CFPVisitor(ICCLinkFindingResults data) {
		super(data);
		state = State.getInstance();
	}

	@Override
	public void visit(final AssignExpr n, final Object arg) {
		super.visit(n, arg);
		String key = canonicalize(n.getTarget().toString());
		Expression argument = n.getValue();
		updateMaps(key, argument);
	}

	@Override
	public void visit(VariableDeclarationExpr expr, Object arg) {
		String varType = expr.getType().toString();
	    if (varType.equals("String")) {
	    	super.visit(expr, arg);
	    }
	  }

	// this kind of AST node is reachable from field declaration nodes
	@Override
	public void visit(final VariableDeclarator n, final Object arg) {
		super.visit(n, arg);
		String key = canonicalize(n.getId().getName());
		Expression argument = n.getInit();
		updateMaps(key, argument);
	}

	// TODO: check if there are other visit() methods to consider.
	// this can be done adding more test cases.

	private void updateMaps(String key, Expression argument) {
		if (argument instanceof StringLiteralExpr) {
			data.literalStrings.put(key, ((StringLiteralExpr) argument).getValue());
		} else if (argument instanceof FieldAccessExpr) {
			// these cases need to be unfolded
			FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) argument;
			data.referencedStrings.put(key, fieldAccessExpr.toString());
		} else { // fallback solution
			StringBuffer sb = new StringBuffer();
			for (String s : argument.toString().split(" ")) {
				String tmp = canonicalize(s);
				if (data.literalStrings.containsKey(tmp)) {
					// if the canonicalized version is a known symbol,
					// consider the canonicalized version.
					sb.append(tmp);
				} else {
					sb.append(s);
				}
				sb.append(" "); // this is important!
			}
			data.referencedStrings.put(key, sb.toString());
		}
	}

}
