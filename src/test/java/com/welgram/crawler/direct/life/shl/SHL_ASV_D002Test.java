package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.SeleniumCrawler;
import org.junit.Test;

public class SHL_ASV_D002Test {

  /**
   * 무배당 신한연금저축보험Premium 크롤링테스트
   */
  @Test
  public void testExecute() {

    boolean result = false;

    try {

      String[] args = {"1"};
//      CrawlingMain craling = new SHL_ASV_D002();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }


  @Test
  public void testExecuteCrawling() {

    boolean result = false;

    try {

      String[] args = {"1","2379","21"};
      SeleniumCrawler craling = new SHL_ASV_D002();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
