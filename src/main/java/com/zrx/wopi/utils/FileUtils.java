package com.zrx.wopi.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * @Title: FileUtils.java
 * @Description: 文件处理的工具类，此处不深入
 * @author zhaorx E-Mail:v-zhaorx04@vanke.com
 * @date 2019年1月16日 下午4:52:08
 */
public class FileUtils {

  private static Map<String, File> FILES_MAP = new HashMap<String, File>();

  /**
	 * 获取文件的SHA-256值
	 * 
	 * @param file
	 * @return
	 */
	public static String getHash256(InputStream fis) throws IOException, NoSuchAlgorithmException {
		String value = "";
		try  {
			byte[] buffer = new byte[1024];
			int numRead;
			// 返回实现指定摘要算法的 MessageDigest 对象
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					// 更新摘要
					digest.update(buffer, 0, numRead);
				}
			} while (numRead != -1);

			value = new String(Base64.encodeBase64(digest.digest()));
			fis.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}

	/***
	 * 利用Apache的工具类实现SHA-256加密
	 * 
	 * @param str
	 *            加密后的报文
	 * @return
	 */
	public static String getSHA256Str(String str) {
		MessageDigest messageDigest;
		String encdeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
			encdeStr = Hex.encodeHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encdeStr;
	}
}