package icc;

import icc.data.Activity;
import icc.parsing.AndroidManifestParser;
import junit.framework.TestCase;

public class TestAndroidManifestParserActivity extends TestCase implements
		ITestAndroidManifestParserComponent {

	String manifestPath = "test-data/manifestparser_test/AndroidManifest_1.xml";
	AndroidManifestParser manifestParser;
	Activity act;

	@Override
	protected void setUp() throws Exception {
		manifestParser = new AndroidManifestParser(manifestPath);
		act = (Activity) manifestParser.components.get(0);
	};

	@Override
	public void testEnabled() {
		assertTrue(act.enabled);
	}

	@Override
	public void testExported() {
		assertTrue(act.exported);
	}

	@Override
	public void testIcon() {
	}

	@Override
	public void testLabel() {
	}

	@Override
	public void testName() {
		assertTrue(!Activity.NOT_SET.equals(act.name));
	}

	@Override
	public void testPermission() {
		assertEquals(manifestParser.application.permission, act.permission);
	}

	@Override
	public void testProcess() {
		assertEquals(manifestParser.application.process, act.process);
	}

	// public void testWindowSoftInputMode() {}

	public void testUiOptions() {
		assertEquals("none", act.uiOptions);
	}

	public void testTheme() {
		assertEquals(manifestParser.application.theme, act.theme);
	}

	public void testTaskAffinity() {
		assertEquals(manifestParser.application.taskAffinity, act.taskAffinity);
	}

	public void testStateNotNeeded() {
		assertFalse(act.stateNotNeeded);
	}

	public void testScreenOrientation() {
		assertEquals("unspecified", act.screenOrientation);
	}

	public void testRelinquishTaskIdentity() {
		assertFalse(act.relinquishTaskIdentity);
	}

	public void testParentActivityName() {
	}

	public void testNoHistory() {
		assertFalse(act.noHistory);
	}

	public void testMultiprocess() {
		assertFalse(act.multiprocess);
	}

	public void testMaxRecents() {
		assertEquals(16, act.maxRecents);
		// assertTrue(act.maxRecents>=16 && act.maxRecents <= 50);
	}

	public void testLaunchMode() {
		assertEquals("standard", act.launchMode);
	}

	// TODO maybe we can use this attribute and its default value (see default
	// behavior)
	// public void testeLabel(){}

	public void testHardwareAccelerated() {
		assertFalse(act.hardwareAccelerated);
	}

	public void testFinishOnTaskLaunch() {
		assertFalse(act.finishOnTaskLaunch);
	}

	public void testExcludeFromRecents() {
		assertFalse(act.excludeFromRecents);
	}

	public void testDocumentLaunchMode() {
		assertEquals("none", act.documentLaunchMode);
	}

	// public void testConfigChanges() {}

	public void testClearTaskOnLaunch() {
		assertFalse(act.clearTaskOnLaunch);
	}

	// public void testBanner() {}

	public void testAutoRemoveFromRecents() {
		assertFalse(act.autoRemoveFromRecents);
	}

	public void testAlwaysRetainTaskState() {
		assertFalse(act.alwaysRetainTaskState);
	}

	public void testAllowTaskReparenting() {
		assertTrue(act.allowTaskReparenting);
	}

	public void testAllowEmbedded() {
		assertFalse(act.allowEmbedded);
	}

}
