package tg;


import junit.framework.TestCase;

import org.junit.Test;

import tg.helper.Constants;
import tg.helper.IntentDeserializer;
import tg.parse.IntentParser;

public class TestIntentDeserializer extends TestCase {

	IntentDeserializer id = new IntentDeserializer();
	
	@Test
	public void testGetActions() {
		String action1 = "Intent.ACTION_VIEW";
		String action2 = "Intent.ACTION_MAIN";
		String action3 = "Intent.ACTION_EDIT";
		String action4 = "Intent.ACTION_PICK";
		
		assertEquals(Constants.ACTION_VIEW, IntentParser.parseAction(action1));
		assertEquals(Constants.ACTION_MAIN, IntentParser.parseAction(action2));
		assertEquals(Constants.ACTION_EDIT, IntentParser.parseAction(action3));
		assertEquals(Constants.ACTION_PICK, IntentParser.parseAction(action4));
	}

	@Test
	public void testGetCategories() {
		String category1 = "Intent.CATEGORY_DEFAULT";
		String category2 = "Intent.CATEGORY_BROWSABLE";
		String category3 = "Intent.CATEGORY_LAUNCHER";
		String category4 = "Intent.CATEGORY_HOME";
		
		assertEquals(Constants.CATEGORY_DEFAULT, IntentParser.parseCategory(category1));
		assertEquals(Constants.CATEGORY_BROWSABLE, IntentParser.parseCategory(category2));
		assertEquals(Constants.CATEGORY_LAUNCHER, IntentParser.parseCategory(category3));
		assertEquals(Constants.CATEGORY_HOME, IntentParser.parseCategory(category4));		
	}
}
