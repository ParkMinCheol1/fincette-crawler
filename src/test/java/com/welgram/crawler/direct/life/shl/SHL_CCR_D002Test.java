package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SHL_CCR_D002Test {



  /**
   * 무배당 신한인터넷 생활비주는암보험 크롤링테스트
   */
  @Test
  public void testExecute() {

    boolean result = false;

    try {

      String[] args = {"1"};
//      CrawlingMain craling = new SHL_CCR_D002();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
