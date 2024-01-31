package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SHL_MDC_D001Test {

	/**
	 * 무배당 신한인터넷정기보험 크롤링 테스트
	 */
	@Test
	public void testExecute() {

		boolean result = false;

		try {

			String[] args = { "1" };
//			CrawlingMain craling = new SHL_MDC_D001();
//			result = craling.execute(args);

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(result);
	}


	@Test
	public void testExecute2() {

		boolean result = false;

		try {

			String[] args = { "1" , "2022", "30"};
//			CrawlingMain craling = new SHL_MDC_D001();
//			result = craling.execute(args);

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(result);
	}
}
