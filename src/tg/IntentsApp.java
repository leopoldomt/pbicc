package tg;

import java.util.ArrayList;

import tg.resolution.IntentForResolution;

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
	
}