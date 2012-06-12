package fi.mikuz.boarder.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class Security {
	
	/**
	 * Basic hashing for transferring a password more securely over the Internet
	 * 
	 * @param input
	 * @return hash
	 * @throws NoSuchAlgorithmException
	 */
	public static String passwordHash(String input) throws NoSuchAlgorithmException {
	    String result = input;
	    MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");
		
		for (int i = 0; i < 100; i++) {
			md.update(result.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
		    result = hash.toString(16);
		    while(result.length() < 64) {
		    	result = "0" + result;
		    }
		}
		
	    return result;
	}

}
