package icc;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;

import java.util.List;

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
		
		for (ICCLinkInfo<IntentInfo> link : links) {
			//System.out.println(link);
			//System.out.println("=====");
			if (link.getScope().equals(scope)){
				IntentInfo info = link.getTarget();
				Assert.assertEquals("Intent.ACTION_VIEW",info.action.toString());
			}
			
		}
	}
	
	@Test
	public void testObjectCreationWithAction() throws Exception {
		String scope = "Intent i = new Intent(that, MainActivity.class);\n        i.setData(Uri.parse(this.imgCache.getImages().get(position).filesystemUri()));\n        i.putExtra(\"currentImageIndex\", 1);\n        i.putExtra(\"cachedImageList\", 2);\n        i.putExtra(\"gallery\", 3);\n        that.startActivity(i);";
		Main.init(file, pathComputer + pathApp);
		Main.getICCLinkResults();
		
		ICCLinkFindingResults results = State.getInstance().iccResults();
		List<ICCLinkInfo<IntentInfo>> links = results.iccLinks;
		
		for (ICCLinkInfo<IntentInfo> link : links) {
			if (link.getScope().equals(scope)){
				IntentInfo info = link.getTarget();
				Assert.assertEquals("MainActivity.class",info.getComponent());
			}			
		}
	}

	@Test
	public void testJSON() throws Exception {
		Main.init(file, pathComputer + pathApp);
		Main.getICCLinkResults();		
		ICCLinkFindingResults results = State.getInstance().iccResults();
		System.out.println(results.toJSON());
		
	}

}