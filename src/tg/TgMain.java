package tg;

import icc.parsing.AndroidManifestParser;


public class TgMain {

	public static void main(String[] args) {

		String manifestPath = "test-data/k9/AndroidManifest.xml";


		String path = "/home/jpttrindade/Mega/CIN/TCC/inputs/";


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
			Intent[] its =IntentReader.fromJsonFile(path+file4);

			AndroidManifestParser manifestParser = new AndroidManifestParser(manifestPath);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
