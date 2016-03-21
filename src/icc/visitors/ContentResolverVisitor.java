package icc.visitors;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import icc.data.ICCLinkFindingResults;

public class ContentResolverVisitor extends BaseVisitor {

	/**
	 * Auxiliary visitor.It visits {@link VariableDeclarationExpr} nodes and
	 * checks if the visited node declares a given targetId.
	 * <p>
	 * If the visited node does not have any declaration of targetId, this
	 * visitor returns null. Otherwise, it returns the
	 * {@link VariableDeclarator} of targetId.
	 * </p>
	 */
	class DeclaratorResolver extends GenericVisitorAdapter<VariableDeclarator, String> {

		private String targetType;

		public DeclaratorResolver(String targetType) {
			this.targetType = targetType;
		}

		@Override
		public VariableDeclarator visit(VariableDeclarationExpr n, String targetId) {
			String typeName = n.getType().toString();
			VariableDeclarator declarator = n.getVars().get(0);
			if (targetType.equals(typeName) && targetId.equals(declarator.getId().toString())) {
				return declarator;
			}
			return null;
		}

	}

	public ContentResolverVisitor(ICCLinkFindingResults data) {
		super(data);
	}

	@Override
	public void visit(MethodCallExpr methodCall, Object arg) {
		super.visit(methodCall, arg);

		// This visitor is interested only on QUERY method calls.
		if ("query".equals(methodCall.getName())) {
			Expression contentUri = getContentUriFrom(methodCall);
			VariableDeclarator node = findDeclarationNode(contentUri.getParentNode(), contentUri.toString());

			Expression contentUriInitExpression = node.getInit();
			List<String> argumentReferences = resolveReferencesFrom(contentUriInitExpression);

			// RESULT
			System.out.printf("query(%s, ...):\n", contentUri.toString());
			for (String value : argumentReferences) {
				System.out.printf("\t%s -> %s\n", contentUri, value);
			}
			System.out.println("");
		}

	}

	// FIXME Necessary to handle NameExpr and FieldAccessExpr...
	private List<String> resolveReferencesFrom(Expression initExpression) {
		List<String> values = new ArrayList<>();

		if (initExpression instanceof MethodCallExpr) {
			MethodCallExpr expr = (MethodCallExpr) initExpression;
			for (Expression e : expr.getArgs()) {
				if (e instanceof LiteralExpr) {
					values.add(e.toString());

				} else if (e instanceof FieldAccessExpr) {
					System.out.println(e);

				} else if (e instanceof NameExpr) {
					System.out.println(e);
				}
			}
		}
		return values;
	}

	// FIXME: Behavior is UNKNOWN if target is IMPORTED ie, it does not belong
	// in the current compilation unit. Possibly this scenario might cause a
	// NullPointerException/StackOverflowException. -Jean
	/**
	 * Recursively searches the declaration node of "targetId" in parent nodes.
	 * 
	 * @param node
	 *            The current node to be searched
	 * @param targetId
	 *            The declared identifier
	 * @return The declaration node for the given Id.
	 */
	private VariableDeclarator findDeclarationNode(Node node, String targetId) {
		VariableDeclarator currentNode = node.accept(new DeclaratorResolver("Uri"), targetId);
		if (currentNode != null) {
			return currentNode;
		}
		// Recursively searches the declaration of targetId in the outer scope.
		return findDeclarationNode(node.getParentNode(), targetId);
	}

	// Assuming MethodCallExpr is a call to query(Uri contentUri, ....)
	private Expression getContentUriFrom(MethodCallExpr n) {
		return n.getArgs().get(0);
	}

}
