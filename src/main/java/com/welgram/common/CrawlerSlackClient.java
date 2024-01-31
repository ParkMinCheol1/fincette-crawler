package com.welgram.common;

import com.welgram.util.StringUtil;

/**
 * crawlerClackClient
 * @author gusfo
 *
 */
public class CrawlerSlackClient {

	// slack webhook주소
//	private static String webhookUrl = "https://hooks.slack.com/services/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";
	private static String webhookUrl = "https://hooks.slack.com/services/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";

	/**
	 * slack에 오류전송
	 * @param productCode
	 * @param message
	 * @return
	 */
	public static String errorPost(String productCode, String message) {
		
		String defaultTitle= "Crawler Monitoring";
		
		String jsonData = errorTemplate("", productCode, message, defaultTitle);
		
		return HttpClientUtil.sendPost(webhookUrl, jsonData);
		
	}
	
	/**
	 * slack에 오류전송
	 * @param productCode
	 * @param message
	 * @return
	 */
	public static String errorPost(String host, String productCode, String message) {
		
		String defaultTitle= "Crawler Monitoring";
//		host = StringUtil.isEmpty(host) ? HostUtil.getHostname() : host;
		String jsonData = errorTemplate(host, productCode, message, defaultTitle);
		
		return HttpClientUtil.sendPost(webhookUrl, jsonData);
		
	}

	/**
	 * 상품 crawling 하면서 발생하는 오류를 slack에 전달하기 위한 템플릿
	 * @param productCode
	 * @param message
	 * @param defaultTitle
	 * @return
	 */
	private static String errorTemplate(String host, String productCode, String message, String defaultTitle) {
		String jsonData = "{\r\n" +
					" \"channel\":\"" +"#crawler_monitor"+"\",\r\n"+			// 슬랙방을 지정할 수 있음.
					"    \"attachments\": [\r\n" + 
					"        {\r\n" + 
					"        	\"title\": \""+defaultTitle+"\",\r\n" + 
	//				"            \"fallback\": \""+"3333"+"(제목)\",\r\n" + 
					"            \"pretext\": \""+"InsuCrawler"+"\",\r\n" + 
					"            \"text\": \""+message+"\",\r\n" + 
					"            \"fields\": [\r\n"; 
				
				if(!StringUtil.isEmpty(host)) {
					jsonData = jsonData + "                {\r\n" + 
						"                    \"title\": \"Host\",\r\n" + 
						"                    \"value\": \""+host+"\",\r\n" + 
						"                    \"short\": true\r\n" + 
						"                },\r\n";
				}
					jsonData = jsonData + "                {\r\n" + 
						"                    \"title\": \"상품코드\",\r\n" + 
						"                    \"value\": \""+productCode+"\",\r\n" +
						"                    \"short\": true\r\n" + 
						"                }\r\n" + 
						"            ],\r\n" + 
						"            \"color\": \"#F35A00\"\r\n" + 
						"        }\r\n" + 
						"    ]\r\n" + 
						"}";
		return jsonData;
	}
}
