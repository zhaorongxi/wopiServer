package com.zrx.wopi.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zrx.wopi.entity.FileInfo;
import com.zrx.wopi.utils.DESUtils;
import com.zrx.wopi.utils.FileUtils;
import com.zrx.wopi.utils.TokenUtils;

/**
 * WOPI HOST Created by 赵榕溪 on 2019/1/16.
 */
@RestController
@RequestMapping(value = "/wopi")
public class WopiHostContrller {

	Logger logger = LoggerFactory.getLogger(WopiHostContrller.class);

	@Value("${file.path}")
	private String filePath;

	@Value("${cryptKey}")
	private String cryptKey;

	private static final String CHARSET_UTF8 = "UTF-8";

	/**
	 * 获取文件流
	 * 
	 * @param name
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("/files/{name}/contents")
	public void getFile(@PathVariable String name, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 文件的路径
		String path = null;
		InputStream fis = null;
		try {
			path = DESUtils.decrypt(name, cryptKey);
			name = URLDecoder.decode(path.substring(path.lastIndexOf("_") + 1), CHARSET_UTF8);
			logger.info("开始请求获取文件,文件名为{}", name);
			URL url = new URL(path);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			Long fileLength = uc.getContentLengthLong();// 获取文件大小
			uc.connect();
			fis = uc.getInputStream();
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			// 清空response
			response.reset();

			// 设置response的Header
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String(name.getBytes(CHARSET_UTF8), "ISO-8859-1"));
			response.addHeader("Content-Length", String.valueOf(fileLength));
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			toClient.write(buffer);
			toClient.flush();
		} catch (IOException e) {
			logger.error("getFile failed, errMsg: {}", e.getMessage());
		} finally {
			if (null != fis) {
				fis.close();
			}
		}
	}

	/**
	 * 保存更新文件
	 * 
	 * @param name
	 * @param content
	 */
	@PostMapping("/files/{name}/contents")
	public void postFile(@PathVariable(name = "name") String name, @RequestBody byte[] content) {
		// 文件的路径
		String path = filePath + name;
		File file = new File(path);

		try (FileOutputStream fop = new FileOutputStream(file)) {
			fop.write(content);
			fop.flush();
			fop.close();
		} catch (IOException e) {
			logger.error("postFile failed, errMsg: {}", e.getMessage());

		}
	}

	/**
	 * 获取文件信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping("/files/{name}")
	public void getFileInfo(@PathVariable String name, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		logger.info("获取到请求的路径为:{}",request.getRequestURI());
		FileInfo info = new FileInfo();
		ObjectMapper mapper = new ObjectMapper();
		String accessToken = request.getParameter("access_token");
		logger.info("获取到的token为{}", accessToken);
		String fileName = null;
		HttpURLConnection uc = null;
		InputStream is = null;
		// 下载url单个文件
		try {
			fileName =TokenUtils.verifyToken(accessToken, cryptKey);
			if (null == fileName) {
				logger.info("未获取到token或token校验失败");
				info.setErrorMsg("未获取到token或token校验失败");
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				response.getWriter().write(mapper.writeValueAsString(info));
				return;
			}
			Long time = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
			name = DESUtils.decrypt(name, cryptKey);
			logger.info("获取解密后的路径为{},文件名为{}", name, fileName);
			URL url = new URL(name);
			uc = (HttpURLConnection) url.openConnection();
			if (uc.getResponseCode() != 200) {
				logger.info("打开文件预览异常,打开的文件地址为{}", name);
				info.setErrorMsg("打开文件预览异常!");
			} else {
				Long fileLength = uc.getContentLengthLong();// 获取文件大小
				uc.connect();
				is = uc.getInputStream();
				info.setBaseFileName(fileName);
				info.setSize(fileLength);
				info.setOwnerId("admin");
				info.setVersion(time);
				info.setSha256(FileUtils.getHash256(is));
			}
			response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
			response.getWriter().write(mapper.writeValueAsString(info));
		} catch (Exception e) {
			logger.error("getFileInfo failed, errMsg: {}", e.getMessage());
		} finally {
			if (null != is) {
				is.close();
			}
			if (null != uc) {
				uc.disconnect();
			}
		}
	}
}