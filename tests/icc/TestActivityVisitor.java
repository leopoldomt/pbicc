package icc;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestActivityVisitor {
	
	// running from inside Eclipse only needs relative path
	// TODO: provide user path if running from outside
	
	String pathComputer = ""; 
	String pathApp = "test-data/explicit_intent_test/";
	String file = pathComputer + pathApp + "list.txt";
	
	@Test
	public void testDataComingFromAnotherClass() throws Exception {
		String scope = "br.ufpe.cin.pbicc.test.intents.explicit.MainActivity.@Override\npublic void onClick(View v) {\n    Intent i = new Intent();\n    String pkg = Strings.PACKAGE_NAME;\n    i.setClassName(pkg, Strings.CLASS_NAME);\n    startActivity(i);\n}";
		Main.init(file, pathComputer + pathApp);
		Main.getICCLinkResults();
		
		ICCLinkFindingResults results = State.getInstance().iccResults();
		List<ICCLinkInfo<IntentInfo>> links = results.iccLinks;
		//System.out.println(links.size());
		//System.out.println("=====");
		
		for (ICCLinkInfo<IntentInfo> link : links) {
			//System.out.println(link);
			//System.out.println("=====");
			if (link.getScope().equals(scope)){
				IntentInfo info = link.getTarget();
				Assert.assertEquals("br.ufpe.cin.pbicc.test.intents.explicit.TestActivity",info.getComponent());
			}
			
		}
	}
	
	@Test
	public void testObjectCreationWithPutExtra() throws Exception {
		String scope = "br.ufpe.cin.pbicc.test.intents.explicit.TestActivity.void test() {\n    startActivity(new Intent(Intent.ACTION_VIEW).putExtra(\"key\", \"value\"));\n}";
		Main.init(file, pathComputer + pathApp);
		Main.getICCLinkResults();
		
		ICCLinkFindingResults results = State.getInstance().iccResults();
		List<ICCLinkInfo<IntentInfo>> links = results.iccLinks;
		System.out.println(links.size());
		System.out.println("=====");
		
		for (ICCLinkInfo<IntentInfo> link : links) {
			System.out.println(link);
			System.out.println("=====");
			if (link.getScope().equals(scope)){
				IntentInfo info = link.getTarget();
				Assert.assertEquals("Intent.ACTION_VIEW",info.action);
			}
			
		}
	}
}