package com.welgram.crawler.common;

import com.welgram.common.HttpClientUtil;
import org.junit.Test;

public class HttpClientUtilTest {

	@Test
	public void testPost() {
		
		String url = "https://hooks.slack.com/services/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";
		String jsonData = "{\r\n" + 
				"    \"attachments\": [\r\n" + 
				"        {\r\n" + 
				"        	\"title\": \"오류 발생(제목)\",\r\n" + 
				"            \"fallback\": \"오류 발생(제목)\",\r\n" + 
				"            \"text\": \"어떤 오류가 발생했는지 알려주는 내용(상세)\",\r\n" + 
				"            \"fields\": [\r\n" + 
				"                {\r\n" + 
				"                    \"title\": \"보험상품\",\r\n" + 
				"                    \"value\": \"ABC00011\",\r\n" + 
				"                    \"short\": true\r\n" + 
				"                },\r\n" + 
				"                {\r\n" + 
				"                    \"title\": \"Environment\",\r\n" + 
				"                    \"value\": \"production\",\r\n" + 
				"                    \"short\": true\r\n" + 
				"                }\r\n" + 
				"            ],\r\n" + 
				"            \"color\": \"#F35A00\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		HttpClientUtil.sendPost(url, jsonData);
	}
}
