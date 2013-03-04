/* ========================================================================= *
 * Boarder                                                                   *
 * http://boarder.mikuz.org/                                                 *
 * ========================================================================= *
 * Copyright (C) 2013 Boarder                                                *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 * ========================================================================= */

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
