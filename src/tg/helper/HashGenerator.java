package tg.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tg.parse.IntentForResolution;

public class HashGenerator {

	public static void generateIFRHash(IntentForResolution ifr){
		
		try {		
			String string = ifr.toStringLine();
			
			MessageDigest m = MessageDigest.getInstance("MD5"); 
			m.update(string.getBytes(),0,string.length()); 
			BigInteger i = new BigInteger(1, m.digest()); 

			//Formatando o resuldado em uma cadeia de 32 caracteres, completando com 0 caso falte 
			string = String.format("%1$032X", i); 

			//System.out.println("MD5: "+string); 
			ifr.setHash(string);		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
