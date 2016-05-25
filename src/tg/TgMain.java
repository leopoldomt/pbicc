package tg;

import icc.data.Component;
import icc.intent.IntentForResolution;
import icc.intent.IntentResolution;
import icc.parsing.AndroidManifestParser;

import java.util.ArrayList;


public class TgMain {

	public static void main(String[] args) {

		

		String path = "/home/jpttrindade/Mega/CIN/TCC/inputs/";
		
		
		
		
		String manifestPath = "test-data/k9/AndroidManifest.xml";
		String manifestPath00 = path + "/manifests/AndroidManifest00.xml";
		String manifestPath01 = path + "/manifests/AndroidManifest01.xml";
		String manifestPath02 = path + "/manifests/AndroidManifest02.xml";

		String file00 = "test00.json";
		String file01 = "test01.json";
		String file02 = "test02.json";

		String file1 = "abstract-art.json";
		String file2 = "adblockplus.json";
		String file3 = "arxiv.json";
		String file4 = "bluezime.json";
		String file5 = "multipicturelivewallpaper.json";
		String file6 = "owncloud.json";
		String file7 = "primitive-ftpd.json";
		String file8 = "remotedroid.json";
		String file9 = "vimtouch.json";
		String file10 = "vlc.json";
		String file11 = "wikipedia.json";
		String file12 = "wordpress.json";
		String file13 = "zooborns.json";


		try {
			IntentFromJson[] its =IntentReader.fromJsonFile(path+file01);
			
			ArrayList<IntentForResolution> intentForResolutions = new ArrayList<IntentForResolution>();
			for(IntentFromJson it : its){
				intentForResolutions.addAll(IntentParser.parse(it));
			}
			

			AndroidManifestParser manifestParser = new AndroidManifestParser(manifestPath01);
			
			int i = 1;
			int component;
			IntentResolution.Result result;
			
			for(IntentForResolution ifr : intentForResolutions){
				System.out.println("\n#Intent"+(i));
				component = 1;
				for(Component c : manifestParser.components){
					result = IntentResolution.resolve(ifr, c);
					System.out.print("##Component"+component+": "+result.match);
					System.out.println((result.match==false ? " -> "+result.reason : ""));
					
					component++;
				}
				i++;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
