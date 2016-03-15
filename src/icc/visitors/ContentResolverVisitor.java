package icc.visitors;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import icc.data.ICCLinkFindingResults;

public class ContentResolverVisitor extends BaseVisitor {

	public ContentResolverVisitor(ICCLinkFindingResults data) {
		super(data);
	}

	@Override
	public void visit(MethodCallExpr n, Object arg) {
		super.visit(n, arg);
		String calleeName = n.getName();
		if ("query".equals(calleeName)) {
			List<Expression> calleeArguments = n.getArgs();
			System.out.println(calleeArguments);
		}
	}
}
