package com.welgram.crawler.general;

import com.welgram.common.CrawlerSlackClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerSlackClientTest {

	public final static Logger logger = LoggerFactory.getLogger(CrawlerSlackClientTest.class);

	@Test
	public void testPost() {
		
		String productCode = "ABC00011";
		String text = "123123123123123";

		String result = CrawlerSlackClient.errorPost("host1", productCode, text);
		logger.debug("result: " + result);
	}
}
