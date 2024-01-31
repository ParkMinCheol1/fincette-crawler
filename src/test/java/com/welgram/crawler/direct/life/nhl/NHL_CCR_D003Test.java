package com.welgram.crawler.direct.life.nhl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NHL_CCR_D003Test {

  // 단건 조회 조건
  String[] args = {"1"};

  @Test
  public void testExecute() {

    boolean result = false;

    try {

//      CrawlingMain craling = new NHL_CCR_D003();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
