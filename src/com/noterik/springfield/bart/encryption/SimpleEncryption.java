package com.noterik.springfield.bart.encryption;

import java.util.Random;

public class SimpleEncryption {

	public SimpleEncryption() {
		
	}
	
	/* Really simple encryption, just adding up the data with the key */
	
	public String Encrypt(String key, String data) {		
		StringBuilder encryptedData = new StringBuilder(data.length());
		int keyLength = key.length();
		int dataLength = data.length();
		
		String length = Integer.toString(dataLength);
		int firstNrLength = Integer.parseInt((Character.toString(length.charAt(0))));
		System.out.println("first nr length = "+firstNrLength);
		
		//shift first round key
		for (int i = 0; i < keyLength-firstNrLength; i++) {
			if (i < dataLength) {
				int code = ((int)data.charAt(i)) + ((int)key.charAt((i+firstNrLength)%keyLength)) + (dataLength%100);
				encryptedData.append((char) code);
			}
		}
		
		for (int i = keyLength-firstNrLength; i < dataLength; i++) {
			int code = ((int)data.charAt(i)) + ((int)key.charAt(i%keyLength)) + (dataLength%100);
			encryptedData.append((char) code);
		}		
		return encryptedData.toString();
	}
	
	public String Decrypt(String key, String data) {
		StringBuilder decryptedData = new StringBuilder(data.length());
		int keyLength = key.length();
		int dataLength = data.length();
		
		String length = Integer.toString(dataLength);
		int firstNrLength = Integer.parseInt((Character.toString(length.charAt(0))));
		
		//shift first round key
		for (int i = 0; i < keyLength-firstNrLength; i++) {
			if (i < dataLength) {
				int code = ((int)data.charAt(i)) - ((int)key.charAt((i+firstNrLength)%keyLength)) - (dataLength%100);
				decryptedData.append((char) code);
			}
		}
		
		for (int i = keyLength-firstNrLength; i < dataLength; i++) {
			int code = ((int)data.charAt(i)) - ((int)key.charAt(i%keyLength)) - (dataLength%100);
			decryptedData.append((char) code);
		}		
		return decryptedData.toString();
	}
	
	public String RandomEncrypt(int length) {
		StringBuilder data = new StringBuilder(length);
		Random r = new Random();
		
		for (int i = 0; i < length; i++) {
			data.append((char) r.nextInt(245));
		}		
		return data.toString();
	}	
}
