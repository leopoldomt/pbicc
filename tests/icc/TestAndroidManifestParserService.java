package icc;

import icc.data.Service;
import icc.parsing.AndroidManifestParser;
import junit.framework.TestCase;

public class TestAndroidManifestParserService extends TestCase implements
		ITestAndroidManifestParserComponent {

	String manifestPath = "test-data/manifestparser_test/AndroidManifest_1.xml";
	AndroidManifestParser manifestParser;
	Service svc1, svc2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manifestParser = new AndroidManifestParser(manifestPath);
		svc1 = (Service) manifestParser.components.get(1);
		svc2 = (Service) manifestParser.components.get(2);
	}

	@Override
	public void testEnabled() {
		assertTrue(svc1.enabled);
	}

	@Override
	public void testExported() {
		assertFalse(svc1.exported);
		assertTrue(svc2.exported);
	}

	@Override
	public void testIcon() {

	}

	@Override
	public void testLabel() {
	}

	@Override
	public void testName() {
	}

	@Override
	public void testPermission() {
		assertTrue(!manifestParser.application.permission.equals(svc1.permission));
		assertEquals(manifestParser.application.permission, svc2.permission);
	}

	@Override
	public void testProcess() {
		assertEquals(manifestParser.application.process, svc1.process);
	}

	public void testIsolatedProcess() {
		assertFalse(svc1.isolatedProcess);
		assertTrue(svc2.isolatedProcess);
	}
}
