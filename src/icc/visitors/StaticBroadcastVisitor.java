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

public class StaticBroadcastVisitor extends BaseVisitor {
	private final String M_SEND_BROADCAST = "sendBroadcast";
	private final String M_SEND_BROADCAST_AS_USER = "sendBroadcastAsUser";
	private final String M_SEND_ORDERED_BROADCAST_AS_USER = "sendOrderedBroadcastAsUser";
	private final String M_SEND_STICKY_BROADCAST = "sendStickyBroadcast";
	private final String M_SEND_STICKY_ORDERED_BROADCAST = "sendStickyOrderedBroadcast";
	private final String M_SEND_STICKY_ORDERED_BROADCAST_AS_USER = "sendStickyOrderedBroadcastAsUser";
	private final String M_SEND_ORDERED_BROADCAST = "sendOrderedBroadcast";

	private final List<String> BROADCAST_CALLS = new ArrayList<String>(
			Arrays.asList(new String[] { 
					M_SEND_BROADCAST, M_SEND_BROADCAST_AS_USER, M_SEND_ORDERED_BROADCAST_AS_USER,
					M_SEND_STICKY_BROADCAST, M_SEND_STICKY_ORDERED_BROADCAST, M_SEND_STICKY_ORDERED_BROADCAST_AS_USER, 
					M_SEND_ORDERED_BROADCAST
			}));

	public StaticBroadcastVisitor(ICCLinkFindingResults data) {
		super(data);
	}

	@Override
	public void visit(MethodCallExpr expr, Object arg) {
		super.visit(expr, arg);

		String methodCall = expr.getName();
		List<Expression> args = expr.getArgs();

		if (BROADCAST_CALLS.contains(methodCall)) {
			//TODO adjust this so we collect stats of the actual method being called - adjust on IntentStats class
			//if (name.equals(M_SEND_BROADCAST)) {
				// stats being incremented
				this.data.stats.addSendBroadcast();
			//}

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
					//TODO try to capture method calls that return Intent type
					
					// TODO: improve this heuristic (empty implicit intent)
					this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, new IntentInfo()));

				} 
				else {
					// TODO: improve this heuristic (empty implicit intent)
					this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, new IntentInfo()));
				}
			}
		}
	}
}
