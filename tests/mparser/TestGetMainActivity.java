package mparser;

import org.junit.Assert;
import org.junit.Test;

public class TestGetMainActivity {

  @Test
  public void test() throws Exception {
    String name = GetMainActivity.getMainActivity("test-data/k9/AndroidManifest.xml");
    Assert.assertEquals(name, "com.fsck.k9.activity.Accounts");
  }

}
