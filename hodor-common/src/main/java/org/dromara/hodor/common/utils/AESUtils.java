package org.dromara.hodor.common.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtils {
	
	/**
	 * 
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * 
	 * @param keyWord
	 *            加密密钥
	 * 
	 * @return byte[] 加密后的字节数组
	 */
	public static byte[] encrypt(String content, String keyWord) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(keyWord.getBytes());
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param content
	 *            需要加密的内容
	 * 
	 * @param 
	 *            加密密钥
	 * 
	 * @return String 加密后的字符串
	 */
	public static String encryptToHex(String content, String password) {
		return parseByte2HexStr(encrypt(content, password));
	}
	
	/**
	 * 
	 * @param content
	 *            需要加密的内容
	 * 
	 * @param 
	 *            加密密钥
	 * 
	 * @return String 加密后的字符串
	 * @throws Exception 
	 */
	public static String encryptToBase64(String content, String password) throws Exception {
		return Base64Utils.encode(AESUtils.encrypt(content, password));
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * 
	 * @param keyWord
	 *            解密密钥
	 * 
	 * @return byte[]
	 */
	public static byte[] decrypt(byte[] content, String keyWord) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(keyWord.getBytes());
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param content
	 *            待解密内容(字符串)
	 * 
	 * @param keyWord
	 *            解密密钥
	 * 
	 * @return byte[]
	 */
	public static byte[] decryptHex(String content, String keyWord) {
		return decrypt(parseHexStr2Byte(content), keyWord);
	}
	
	/**
	 * 
	 * @param content
	 *            待解密内容(BASE64字符串)
	 * 
	 * @param keyWord
	 *            解密密钥
	 * 
	 * @return byte[]
	 * @throws Exception 
	 */
	public static byte[] decrypt(String content, String keyWord) throws Exception {
		return decrypt(Base64Utils.decode(content), keyWord);
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * 
	 * @return String
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * 
	 * @return byte[]
	 */
	public static byte[] parseHexStr2Byte(String hexString) {
		if (hexString.length() < 1)
			return null;
		byte[] byteArray = new byte[hexString.length()/2];
    	byte tmpByte;
	    for(int i=0;i<hexString.length();i=i+2)
	    {
	    	tmpByte = new Integer(Integer.parseInt(hexString.substring(i,i+2),16)).byteValue();
	    	byteArray[i/2] = tmpByte;
	    }
    	return byteArray;
	}
}
