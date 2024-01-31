package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SHL_DSS_D002Test {

  /**
   * 신한생명 - 무배당 신한내게맞는2대건강보험(갱신형)
   */
  @Test
  public void testExecute() {

    boolean result = false;

    try {

      String[] args = {"1"};
//      CrawlingMain craling = new SHL_DSS_D002();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
