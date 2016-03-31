package icc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestAndroidManifestParserActivity.class,
		TestAndroidManifestParserService.class,
		TestAndroidManifestParserContentProvider.class,
		TestAndroidManifestParserBroadcastReceiver.class })
public class TestAndroidManifestParserSuite {

}
