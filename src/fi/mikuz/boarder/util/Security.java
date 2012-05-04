package fi.mikuz.boarder.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class Security {
	
	/**
	 * Md5 will be taken from the password and then again from the md5.
	 * The server will make more salting and more md5's before storing the password.
	 * 
	 * @param input
	 * @return md5 hash
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String input) throws NoSuchAlgorithmException {
	    String result = input;
	    for (int i = 0; i <= 1; i++) {
	        MessageDigest md;
			md = MessageDigest.getInstance("MD5");
	        md.update(result.getBytes());
	        BigInteger hash = new BigInteger(1, md.digest());
	        result = hash.toString(16);
	        while(result.length() < 32) {
	            result = "0" + result;
	        }
	    }
	    return result.substring(0, 30);
	}
}
