package icc;

import icc.data.ContentProvider;
import icc.parsing.AndroidManifestParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAndroidManifestParserContentProvider implements
		ITestAndroidManifestParserComponent {

	String manifestPath = "test-data/manifestparser_test/AndroidManifest_1.xml";
	AndroidManifestParser manifestParser;
	ContentProvider cp;

	@Before
	public void init() throws Exception {
		manifestParser = new AndroidManifestParser(manifestPath);
		cp = (ContentProvider) manifestParser.components.get(3);
	}

	@Test
	@Override
	public void testEnabled() {
		Assert.assertTrue(cp.enabled);
	}

	@Test
	@Override
	public void testExported() {
		if (manifestParser.minSdkVersion <= 16
				|| manifestParser.targetSdkVersion <= 16) {
			Assert.assertTrue(cp.exported);
		} else {
			Assert.assertFalse(cp.exported);
		}
	}

	@Test
	@Override
	public void testIcon() {
	}

	@Test
	@Override
	public void testLabel() {
		// TODO Auto-generated method stub

	}

	@Test
	@Override
	public void testName() {
		Assert.assertNotEquals(ContentProvider.NOT_SET, cp.name);
	}

	@Test
	@Override
	public void testPermission() {
	}

	@Test
	@Override
	public void testProcess() {
		Assert.assertEquals(manifestParser.application.process, cp.process);
	}

	@Test
	public void testAuthorities() {
		Assert.assertNotEquals(cp.authorities.size(), 0);
	}

	@Test
	public void testGrantUriPermissions() {
		Assert.assertFalse(cp.grantUriPermissions);
	}

	@Test
	public void testInitOrder() {
	}

	@Test
	public void testMultiprocess() {
		Assert.assertFalse(cp.multiprocess);
	}

	@Test
	public void testReadPermission() {
	}

	@Test
	public void testSyncable() {
	}

	@Test
	public void testWritePermission() {
	}
}
