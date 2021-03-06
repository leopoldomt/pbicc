package icc.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class ServiceVisitor extends BaseVisitor {
	private final String M_START_SERVICE = "startService";
	private final String M_BIND_SERVICE = "bindService";
	private final List<String> SERVICE_CALLS = new ArrayList<String>(
			Arrays.asList(new String[] { M_START_SERVICE, M_BIND_SERVICE }));

	public ServiceVisitor(ICCLinkFindingResults data) {
		super(data);
	}

	@Override
	public void visit(MethodCallExpr expr, Object arg) {
		super.visit(expr, arg);

		String methodCall = expr.getName();
		List<Expression> args = expr.getArgs();

		if (SERVICE_CALLS.contains(methodCall)) {
			if (methodCall.equals(M_START_SERVICE)) {
				// stats being incremented
				this.data.stats.addStartService();
			}
			else if (methodCall.equals(M_BIND_SERVICE)) {
				// stats being incremented
				this.data.stats.addBindService();
			}

			// 1 or more args
			if (args != null && args.size() >= 1) {
				String fullScope = this.getScope();
				String shortScope = this.getNameScope();
				String packageName = this.getLastPackageVisited();
				String className = this.getLastClassVisited();
				String methodName = this.getLastMethodNameVisited();
				Expression argExpr = args.get(0);

				// object creation
				if (argExpr instanceof ObjectCreationExpr) {
					ObjectCreationExpr newIntent = (ObjectCreationExpr) argExpr;
					IntentInfo info = handleIntentCreation(newIntent);
					if (info != null) {
						this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, info));
					}
				} 
				//intent var
				else if (argExpr instanceof NameExpr) {
					NameExpr intentVar = (NameExpr) argExpr;
					String fullName = getFullScopeName(intentVar.getName());
					IntentInfo existingInfo = data.intentsST.get(fullName);
					if (existingInfo != null) {
						existingInfo.identifier = intentVar.getName();
						this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, existingInfo));
					} 
					else {
						System.out.println(String.format("Intent instance '%s' is not on the symbol data.intentsST!", intentVar.getName()));
					}
				} 
				//method call
				else if (argExpr instanceof MethodCallExpr) {
					MethodCallExpr callExpr = (MethodCallExpr) argExpr;
					//System.out.println(callExpr.getName());
					if (callExpr.getName().equals(M_CREATE_CHOOSER)) {
						IntentInfo info = handleCreateChooser(callExpr);
						if (info != null) {
							this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, info));
						}
					} 
					//TODO try to capture method calls that return Intent type
					else {
						// TODO: improve this heuristic (empty implicit intent)
						this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, new IntentInfo()));
					}
				} 
				else {
					// TODO: improve this heuristic (empty implicit intent)
					this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, new IntentInfo()));
				}
			}
		}
	}
}