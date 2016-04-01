package icc.visitors;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class ActivityVisitor extends BaseVisitor {
	private final String M_START_ACTIVITY = "startActivity";
	private final String M_START_ACTIVITY_FOR_RESULT = "startActivityForResult";
	//public boolean startActivityIfNeeded (Intent intent, int requestCode, (Bundle options)?)
	private final String M_START_ACTIVITY_IF_NEEDED = "startActivityIfNeeded";
	//public boolean startNextMatchingActivity (Intent intent, (Bundle options)?)
	private final String M_START_NEXT_MATCHING_ACTIVITY = "startNextMatchingActivity";
	//public void startActivityFromChild (Activity child, Intent intent, int requestCode, (Bundle options)?)
	private final String M_START_ACTIVITY_FROM_CHILD = "startActivityFromChild";
	//public void startActivityFromFragment (Fragment fragment, Intent intent, int requestCode, (Bundle options)?)
	private final String M_START_ACTIVITY_FROM_FRAGMENT = "startActivityFromFragment";
	
	//TODO add support for the other methods below, on a if-need-be basis
	//public void startActivities (Intent[] intents, (Bundle bundle)?)
	private final String M_START_ACTIVITIES = "startActivities"; 
	//public void startIntentSender (IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, (Bundle options)?)
	private final String M_START_INTENT_SENDER = "startIntentSender";
	//public void startIntentSenderForResult (IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, (Bundle options)?)
	private final String M_START_INTENT_SENDER_FOR_RESULT = "startIntentSenderForResult";
	//public void startIntentSenderFromChild (Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, (Bundle options)?)
	private final String M_START_INTENT_SENDER_FROM_CHILD = "startIntentSenderFromChild";

	private final List<String> ACTIVITY_CALLS = new ArrayList<String>(Arrays.asList(new String[] { 
			M_START_ACTIVITY, M_START_ACTIVITY_FOR_RESULT, M_START_ACTIVITY_IF_NEEDED, 
			M_START_NEXT_MATCHING_ACTIVITY, M_START_ACTIVITY_FROM_CHILD, M_START_ACTIVITY_FROM_FRAGMENT 
		}
	));

	public ActivityVisitor(ICCLinkFindingResults data) {
		super(data);
	}

	@Override
	public void visit(MethodCallExpr expr, Object arg) {
		super.visit(expr, arg);

		String methodCall = expr.getName();
		List<Expression> args = expr.getArgs();

		if (ACTIVITY_CALLS.contains(methodCall)) {
			// stats being incremented
			this.data.stats.addStartActivity();
			
			Expression argExpr = null; 
			
			if (methodCall.equals(M_START_ACTIVITY) || methodCall.equals(M_START_ACTIVITY_FOR_RESULT) 
				|| methodCall.equals(M_START_ACTIVITY_IF_NEEDED) || methodCall.equals(M_START_NEXT_MATCHING_ACTIVITY)) {
				argExpr = args.get(0);
			}
			else if (methodCall.equals(M_START_ACTIVITY_FROM_CHILD) || methodCall.equals(M_START_ACTIVITY_FROM_FRAGMENT)) {
				argExpr = args.get(1);
			}
			//TODO when adding support for other methods, handle argExpr with other else if branches
			

			// 1 or more args
			if (args != null && args.size() >= 1) {
				String fullScope = this.getScope();
				String shortScope = this.getNameScope();
				String packageName = this.getLastPackageVisited();
				String className = this.getLastClassVisited();
				String methodName = this.getLastMethodNameVisited();
				
				// object creation
				if (argExpr instanceof ObjectCreationExpr) {
					ObjectCreationExpr newIntent = (ObjectCreationExpr) argExpr;
					
					IntentInfo info = handleIntentCreation(newIntent);

					if (info != null) {
						this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, info));
					}
				} 
				else if (argExpr instanceof NameExpr) {
					NameExpr intentVar = (NameExpr) argExpr;
					String fullName = getFullScopeName(intentVar.getName());
					IntentInfo existingInfo = data.intentsST.get(fullName);
					if (existingInfo != null) {
						existingInfo.identifier = intentVar.getName();
						this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, existingInfo));
					} else {
						System.out.println(String.format("Intent instance '%s' is not on the symbol data.intentsST!", intentVar.getName()));
					}
				} 
				else if (argExpr instanceof MethodCallExpr) {
					MethodCallExpr callExpr = (MethodCallExpr) argExpr;
					//System.out.println(callExpr.getName());

					if (callExpr.getName().equals("putExtra")) {
						Expression e = callExpr.getScope();
						if (e instanceof ObjectCreationExpr) {
							ObjectCreationExpr newIntent = (ObjectCreationExpr) e;
							IntentInfo info = handleIntentCreation(newIntent);
							List<Expression> extraArgs = callExpr.getArgs();
							handleSetPutExtra(extraArgs, info);
							if (info != null) {
								this.data.iccLinks.add(new ICCLinkInfo<IntentInfo>(fullScope, shortScope, packageName, className, methodName, methodCall, info));
							}
						}
					}
					else if (callExpr.getName().equals(M_CREATE_CHOOSER)) {
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
