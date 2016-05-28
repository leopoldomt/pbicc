package tg;

import icc.data.Component;
import icc.parsing.AndroidManifestParser;

import java.util.ArrayList;

import tg.helper.IntentJson;
import tg.parse.IntentParser;
import tg.resolution.IntentForResolution;
import tg.resolution.IntentResolution;


public class TgMain {
	

	private static void init() {
		
		//Data data = new Data("mailto:emailaddress@emailaddress.com");
		
		//System.out.println(data.getAuthority());
		
		/*try {
			URI uri = new URI("mailto:george@georgewhiteside.net?subject=AbstractArt");
			System.out.println(uri.getScheme());
			System.out.println(uri.getHost());
			System.out.println(uri.getPath());
			System.out.println(uri.getPort());
			System.out.println(uri.getAuthority());
			System.out.println(uri.getRawSchemeSpecificPart());
			
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		*/
	}


	public static void main(String[] args) {

		init();
		
		System.out.flush();
		System.out.println("### Start TgMain ###");

		String path = "/home/jpttrindade/Mega/CIN/TCC/inputs/";
		
		
		
		
		String manifestPath = "test-data/k9/AndroidManifest.xml";
		String manifestPath00 = path + "/manifests/AndroidManifest00.xml";
		String manifestPath01 = path + "/manifests/AndroidManifest01.xml";
		String manifestPath02 = path + "/manifests/AndroidManifest02.xml";

		String file00 = "test00.json";
		String file01 = "test01.json";
		String file02 = "test02.json";

		String file1 = "abstract-art.json";
		String manifestPath1 = path + "/manifests/abstract-art.xml";
		
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
			IntentFromJson[] its =IntentJson.read(path+file01);
			
			AndroidManifestParser manifestParser = new AndroidManifestParser(manifestPath01);
			
			ArrayList<IntentForResolution> intentForResolutions = new ArrayList<IntentForResolution>();
			int i = 0;
			for(IntentFromJson it : its){
				intentForResolutions.addAll(IntentParser.parseToResolution(it, i+""));
			}
			
			IntentsApp itsApp = IntentsApp.createIntentsApp(intentForResolutions);
			
			System.out.printf(">> The app has %dexp/%dimp intents.\n", itsApp.explicits.size(), itsApp.implicits.size());

			

			System.out.printf(">> The manifest has %d components\n", manifestParser.components.size());

			int index = 1;
			int component;
			IntentResolution.Result result;
			

			int matches = 0;
			for(IntentForResolution ifr : itsApp.all){
				System.out.println("\n#Intent"+(index));
				component = 1;
				//if(i == 5) System.out.println(ifr.getData());
				if(index == 2) System.out.println(ifr.getData().getScheme());
				for(Component c : manifestParser.components){
					//if(component == 3) System.out.println(c.intentFilters.get(0).data);
					System.out.println(ifr.getData().getType());
					result = IntentResolution.resolve(ifr, c);
					System.out.print("##Component"+component+": "+result.match);
					System.out.println((result.match==false ? " -> "+result.reason : ""));
					
					if(result.match) matches++;
					
					component++;
				}
				index++;
			}
			
			System.out.printf("\n#########Total de Matches = %d", matches);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
