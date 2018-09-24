package com.taotao.store.order.controller;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常控制
 */
public class GlobalExceptionController {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());// this.getClass()是子类的

	/**
	 * 异常的同一处理，一旦有异常，所有继承了这个BaseController的子类，都会将异常信息添加到LOGGER日志中。
	 * 根据/taotao-manage-web/src/main/resources/log4j.properties日志文件的配置，决定日志的等级及其处理方式（是打印到控制台，还是保持为文件等等）。
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Void> handleGrobalException(Exception exception, HttpServletRequest req) {
		LOGGER.error(getRequestParam(req), exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	/**
	 * 拿到request中的url、method和parameter，并拼接成字符串返回。
	 */
	private String getRequestParam(HttpServletRequest req) {

		StringBuilder sb = new StringBuilder();

		// 通过uri+method定位到具体的执行方法：
		sb.append("url:" + req.getRequestURI() + ";");
		sb.append("method:" + req.getMethod() + ";");

		// 拿到所有的参数：
		sb.append("parameters=[");
		Map<String, String[]> map = req.getParameterMap();
		for (Entry<String, String[]> node : map.entrySet()) {
			sb.append(node.getKey() + "=");
			String[] values = node.getValue();
			for (String value : values) {
				sb.append(value);
			}
			sb.append(",");
		}

		// 去掉最后一个参数后面的逗号
		// 第一种方法，会产生很多String/StringBuilder对象，效率低：
		// String subString = sb.substring(0, sb.length()-1);
		// return subString + "]";
		// 就两个对象，一个是StringBuilder对象，用于字符串拼接； 一个是String对象，最后用于返回：
		sb.delete(sb.length() - 1, sb.length());
		return sb.append("]").toString();
	}
}
