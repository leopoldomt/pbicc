package icc;

import icc.data.BroadcastReceiver;
import icc.parsing.AndroidManifestParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAndroidManifestParserBroadcastReceiver implements
		ITestAndroidManifestParserComponent {

	String manifestPath = "test-data/manifestparser_test/AndroidManifest_1.xml";
	AndroidManifestParser manifestParser;
	BroadcastReceiver br;

	@Before
	public void init() throws Exception{
		manifestParser = new AndroidManifestParser(manifestPath);
		br = (BroadcastReceiver) manifestParser.components.get(4);
	}
	
	@Test
	@Override
	public void testEnabled() {
		Assert.assertTrue(br.enabled);
	}
	
	@Test
	@Override
	public void testExported() {
		Assert.assertTrue(br.exported);
	}

	@Test
	@Override
	public void testIcon() {
	}

	@Test
	@Override
	public void testLabel() {
	}

	@Test
	@Override
	public void testName() {
		Assert.assertNotEquals(BroadcastReceiver.NOT_SET, br.name);
	}

	@Test
	@Override
	public void testPermission() {
		Assert.assertEquals(manifestParser.application.permission, br.permission);
	}

	@Test
	@Override
	public void testProcess() {
		Assert.assertEquals(manifestParser.application.process, br.process);
	}

}
