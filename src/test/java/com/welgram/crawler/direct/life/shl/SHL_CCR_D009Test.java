package com.welgram.crawler.direct.life.shl;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SHL_CCR_D009Test {



  /**
   * 무배당 신한인터넷 생활비주는암보험 크롤링테스트
   */
  @Test
  public void testExecute() {

    boolean result = false;

    try {

      String[] args = {"1"};
//      CrawlingMain craling = new SHL_CCR_D009();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }

  @Test
  public void testExecute1() {

    boolean result = false;

    try {

      String[] args = {"1", "1838", "31"};
//      CrawlingMain craling = new SHL_CCR_D009();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
