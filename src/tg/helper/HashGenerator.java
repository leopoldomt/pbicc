package tg.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import tg.parse.IntentForResolution;

public class HashGenerator {

	public static String generateHashMD5(String string){
		String hash = "";
		try {		
			//String string = ifr.toStringLine();
			
			MessageDigest m = MessageDigest.getInstance("MD5"); 
			m.update(string.getBytes(),0,string.length()); 
			BigInteger i = new BigInteger(1, m.digest()); 

			//Formatando o resuldado em uma cadeia de 32 caracteres, completando com 0 caso falte 
			hash = String.format("%1$032X", i); 

			//System.out.println("MD5: "+string); 
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return hash;
	}
	
	public static String generateBase64Hash(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}
}
