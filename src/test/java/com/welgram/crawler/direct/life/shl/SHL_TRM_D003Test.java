package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHL_TRM_D003Test {

	public static final Logger logger = LoggerFactory.getLogger(SHL_TRM_D003Test.class);

	/**
	 * 무배당 신한인터넷정기보험 크롤링 테스트
	 */
	@Test
	public void testExecute() {

		boolean result = false;

		try {

			String[] args = { "1" };
//			CrawlingMain craling = new SHL_TRM_D003();
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

			String[] args = { "1", "2052", "20" };
//			CrawlingMain craling = new SHL_TRM_D003();
//			result = craling.execute(args);

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(result);
	}

	@Test
	public void testExtractStr() {
		String str = "무배당 신한인터넷정기보험 (재해보장형) ";

		Pattern p = Pattern.compile("\\((.*?)\\)");
		Matcher m = p.matcher(str);

		while (m.find()) {
			logger.debug(m.group(1));
		}
	}
	
	
	@Test
	public void testReadFileFromClasspath() {
		File f = new File("classpath:extensions/ipkbbcamfcnlflkedfdaokofdmfgocfp/4.4.8_0.crx");
		
		logger.debug(f.getAbsoluteFile().toString());
	}
	
	@Test
	public void testReadFileFromClassLoader() {
		ClassLoader classLoader = getClass().getClassLoader();
	    File f = new File(classLoader.getResource("extensions/ipkbbcamfcnlflkedfdaokofdmfgocfp/4.4.8_0.crx").getFile());
	    
		logger.debug(f.getAbsoluteFile().toString());
	}
	
	/**
	 * 로컬ip 조회
	 * @throws UnknownHostException
	 */
	@Test
	public void testGetLocalIp() throws UnknownHostException {
		
		InetAddress iAddress = InetAddress.getLocalHost();
		  String currentIp = iAddress.getHostAddress();
		  logger.debug("Current IP address : " +currentIp); //gives only host address
	}
	
	/**
	 * pc의 호스트이름 조회
	 */
	@Test
	public void testGetHostname()  {
		
		String hostname = "Unknown";

		try
		{
		    InetAddress addr;
		    addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		}
		catch (UnknownHostException ex)
		{
		    logger.debug("Hostname can not be resolved");
		}
		
		 logger.debug("hostname: " + hostname);
	}
}
