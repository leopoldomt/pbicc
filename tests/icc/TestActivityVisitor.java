package icc;

import icc.data.ICCLinkFindingResults;
import icc.data.ICCLinkInfo;
import icc.data.IntentInfo;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestActivityVisitor {
	// TODO fix this to your path
	String pathComputer = "/Users/leopoldomt/Documents/cin/pbicc/";
	String pathApp = "test-data/explicit_intent_test/";
	String file = pathComputer + pathApp + "list.txt";
	
	@Test
	public void testDataComingFromAnotherClass() throws Exception {
		String scope = "MainActivity.onCreate.onClick";
		Main.init(file, pathComputer + pathApp);
		Main.getICCLinkResults();

		for (Map.Entry<String, ICCLinkFindingResults> resultsEntry : State.getInstance().resultsMap().entrySet()) {
			List<ICCLinkInfo<IntentInfo>> links = resultsEntry.getValue().iccLinks;

			for (ICCLinkInfo<IntentInfo> link : links) {
				if (link.getScope().equals(scope)){
					IntentInfo info = link.getTarget();
					
					Assert.assertEquals("br.ufpe.cin.pbicc.test.intents.explicit.TestActivity",info.getComponent());
				}
			}
		}
	}

}