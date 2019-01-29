package com.zrx.wopi.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.commons.codec.binary.Base64;

public class DESUtils {

	/** 加密算法,可用 DES,DESede,Blowfish. */
	private final static String ALGORITHM = "DES";

	/**
	 * DES解密算法
	 * 
	 * @param data
	 * @param cryptKey
	 *            密钥 要是偶数
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data, String cryptKey) throws Exception {
		return new String(decrypt(hex2byte(data.getBytes()), cryptKey.getBytes()));
	}

	/**
	 * DES加密算法
	 * 
	 * @param data
	 * @param cryptKey
	 * @return
	 * @throws Exception
	 */
	public final static String encrypt(String data, String cryptKey) throws Exception {
		return byte2hex(encrypt(data.getBytes(), cryptKey.getBytes()));
	}

	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(data);
	}

	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(data);
	}

	private static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}


	public static String getUtf8Url(String url) {
		char[] chars = url.toCharArray();
		StringBuilder utf8Url = new StringBuilder();
		final int charCount = chars.length;
		for (int i = 0; i < charCount; i++) {
			byte[] bytes = ("" + chars[i]).getBytes();
			if (bytes.length == 1) {
				utf8Url.append(chars[i]);
			}else{
				try {
					utf8Url.append(URLEncoder.encode(String.valueOf(chars[i]), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.getMessage();
				}
			}
		}
		return utf8Url.toString();
	}
	
	public static void main(String[] args) throws Exception {
//		String path = "http://obs.cn-south-1.myhwclouds.com/vbim-oss/rms/cdnb708c556-1db4-430b-8c02-17c451704fb3-1548497751302.pptx";
//		String test = getUtf8Url(path);
//		String name = "赵榕溪的ppt";
//		System.out.println(encrypt(name, "VBIM-fileSystem-WOPI"));
//		System.out.println(test);
//		System.out.println(encrypt(test, "VBIM-fileSystem-WOPI"));
//		System.out.println(decrypt("DD65E94E07643B17C6D5A6DB023E23C308281FA05B2CBEE1063D61863F275C9D10ABCCB0238123838071E03B60018CD685770C96640E5F84416A00E5E401FE263C1C88A32403645209451A91867EF6EEA2BFCABE39B7A1BB6985A9E0D5DBC5A00D02726B9F2912FD84200D5F47C067E2DFE55019E6657ECA002513BCEDFE4061", "VBIM-fileSystem-WOPI"));
		
//		System.out.println(getUtf8Url(path));
		double a = Math.pow(6, 4);
		System.out.println(a);
		
	}
}
