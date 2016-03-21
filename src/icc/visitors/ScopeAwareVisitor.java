package icc.visitors;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public abstract class ScopeAwareVisitor extends VoidVisitorAdapter<Object> {
	protected List<String> scope;

	protected String lastPackageVisited;
	protected String lastClassVisited;
	protected String lastMethodVisited;

	public ScopeAwareVisitor() {
		this.scope = new ArrayList<String>();
	}

	@Override
	public void visit(final PackageDeclaration n, final Object arg) {
		this.scope.add(n.getName().toString());
		lastPackageVisited = n.getName().toString();
		super.visit(n, arg);

		// lastPackageVisited = null;
		/**
		 * I expected to see the entire class encapsulated in object denoted by
		 * n. But that is not the case. Because of that, I can't reset
		 * lastPackageVisited to null. This will work right as long as there are
		 * no classes without a package or if they exist they are processed
		 * first. -Marcelo
		 */

		this.scope.remove(this.scope.size() - 1);
	}

	public void visit(ClassOrInterfaceDeclaration expr, Object arg) {
		this.scope.add(expr.getName());
		lastClassVisited = expr.getName();
		super.visit(expr, arg);
		lastClassVisited = null;
		this.scope.remove(this.scope.size() - 1);
	}

	public void visit(MethodDeclaration expr, Object arg) {
		this.scope.add(expr.toString());
		lastMethodVisited = expr.toString();

		super.visit(expr, arg);

		lastMethodVisited = null;
		this.scope.remove(this.scope.size() - 1);
	}

	// utils

	protected String getScope() {
		/*
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < this.scope.size(); i++) {
			builder.append(this.scope.get(i));

			if (i < this.scope.size() - 1) {
				builder.append(".");
			}
		}

		return builder.toString();
		/**/
		return canonicalize();
	}
	
	protected String canonicalize() {
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
		return String.join(".", l);		
	}

	// TODO: please check if this is a reasonable canonical form,
	// necessary to avoid mismatches. Using:
	// ** package.class.method.varname **
	protected String canonicalize(String varName) {

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

	protected String getFullScopeName(String name) {
		//TODO changing canonic form to Marcelo's CFPvisitor style - run more tests to make sure we are not breaking anything
		//return String.format("%s.%s", this.getScope(), name);
		
		return canonicalize(name);
		
	}
}
