package com.zrx.wopi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: TokenUtil.java
 * @Description: token的工具类，此处不深入
 * @author zhaorx 
 * @date 2019年1月16日 下午4:52:08
 */
public class TokenUtils {

	private static Logger logger = LoggerFactory.getLogger(TokenUtils.class);
	// private static final String GLOBAL_TOKEN = "OfficeOnlineServer";

	public static String verifyToken(String token,String cryptKey) {
		String accessToken = null;
		if (null == token || "".equals(token)) {
			return accessToken;
		}
		try {
			accessToken = DESUtils.decrypt(token, cryptKey);
			if (null == accessToken) {
				logger.info("解密token失败!");
				return accessToken;
			}
		} catch (Exception e) {
			logger.info("token解析失败",e.getMessage());
			return accessToken;
		}
		return accessToken;
	}

}