package tg;


import junit.framework.TestCase;

import org.junit.Test;

public class TestIntentDeserializer extends TestCase {

	IntentDeserializer id = new IntentDeserializer();
	
	@Test
	public void testGetActions() {
		String action1 = "Intent.ACTION_VIEW";
		String action2 = "Intent.ACTION_MAIN";
		String action3 = "Intent.ACTION_EDIT";
		String action4 = "Intent.ACTION_PICK";
		
		assertEquals(Intent.ACTION_VIEW, IntentParser.parseAction(action1));
		assertEquals(Intent.ACTION_MAIN, IntentParser.parseAction(action2));
		assertEquals(Intent.ACTION_EDIT, IntentParser.parseAction(action3));
		assertEquals(Intent.ACTION_PICK, IntentParser.parseAction(action4));
	}

	@Test
	public void testGetCategories() {
		String category1 = "Intent.CATEGORY_DEFAULT";
		String category2 = "Intent.CATEGORY_BROWSABLE";
		String category3 = "Intent.CATEGORY_LAUNCHER";
		String category4 = "Intent.CATEGORY_HOME";
		
		assertEquals(Intent.CATEGORY_DEFAULT, IntentParser.parseCategory(category1));
		assertEquals(Intent.CATEGORY_BROWSABLE, IntentParser.parseCategory(category2));
		assertEquals(Intent.CATEGORY_LAUNCHER, IntentParser.parseCategory(category3));
		assertEquals(Intent.CATEGORY_HOME, IntentParser.parseCategory(category4));		
	}
}
