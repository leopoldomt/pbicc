package tg.main;

import java.util.ArrayList;

import tg.parse.IntentForResolution;

public class IntentsApp {
	
	ArrayList<IntentForResolution> implicits = new ArrayList<IntentForResolution>();
	ArrayList<IntentForResolution> explicits = new ArrayList<IntentForResolution>();
	ArrayList<IntentForResolution> all = new ArrayList<IntentForResolution>();
	
	public static IntentsApp createIntentsApp(ArrayList<IntentForResolution> ifr) {
		IntentsApp itsApp  = new IntentsApp();
				
		for(IntentForResolution i : ifr){
			if(null == i.getComponentName()){
				itsApp.implicits.add(i);
			} else {
				itsApp.explicits.add(i);
			}
			itsApp.all.add(i);
		}
		
		return itsApp;
	}
	
	public static IntentsApp createIntentsApp(IntentForResolution[] ifr) {
		IntentsApp itsApp  = new IntentsApp();
				
		for(IntentForResolution i : ifr){
			if(null == i.getComponentName()){
				itsApp.implicits.add(i);
			} else {
				itsApp.explicits.add(i);
			}
			itsApp.all.add(i);
		}
		
		return itsApp;
	}
	
}
