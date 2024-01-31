package com.welgram.crawler;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.welgram.crawler.common.HostUtilTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.welgram.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

	public final static Logger logger = LoggerFactory.getLogger(Test.class);

	  // 기본 time sleep=4초
	  private static final int DEFAULT_SLEEP_TIME = 4;

	  // 기본 milli second=1000
	  private static final int DEFAULT_MILLI_SEC = 1000;
    public static void main(String[] args) throws InterruptedException {
    	
    	
    	System.setProperty("webdriver.gecko.driver","d:\\geckodriver\\geckodriver.exe");
    	WebDriver driver = new FirefoxDriver();
        String baseUrl = "http://www.facebook.com";
        String tagName = "";
        
        driver.get(baseUrl);
        tagName = driver.findElement(By.id("email")).getTagName();
        logger.debug(tagName);
        driver.close();
        System.exit(0);
        
    	String userHomePath = System.getProperty ( "user.home" );
        String url = "C:\\Users\\chunwon";
        
        logger.debug(userHomePath.replace("\\", "/"));
        
        
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < 10; i++) {
			//Thread.sleep(1000);
		}
		
		long endTime = System.currentTimeMillis();
		
		long runTime = endTime - startTime ;
		
		logger.debug("startTime :: " + startTime);
		logger.debug("endTime :: " + endTime);
		logger.debug("runTime :: " + runTime);
		logger.debug("runTime sec :: " + runTime / 1000);
		
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeStr = dayTime.format(new Date(startTime));
		String endTimeStr = dayTime.format(new Date(endTime));	
		logger.debug("startTimeS :: " + startTimeStr);
		logger.debug("endTimeS :: " + endTimeStr);
		
		
		
	    int waitTime = 4 / 1000;
	    
	    logger.debug("{}", waitTime);
	    
	    logger.debug(DateUtil.getBabyBirth("yyyyMMdd"));
	    
	    int nn = 5682;
	    
	    nn = (nn+99) / 100 *100 ;  
	    logger.debug("{}", nn);
	    
    }
    
}