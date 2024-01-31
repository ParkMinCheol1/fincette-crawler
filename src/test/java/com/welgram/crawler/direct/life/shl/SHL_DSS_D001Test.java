package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SHL_DSS_D001Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * 무배당 신한스포츠&레저보장보험 크롤링테스트
   */
  @Test
  public void testExecute() {

    boolean result = false;

    try {

//      CrawlingMain craling = new SHL_DSS_D001();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
