package com.welgram.crawler.direct.life.abl;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ABL_MSV_D001Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * (무)ABL인터넷확정금리저축보험
   */
  @Test
  public void testExecute() {

    boolean result = false;

    //CrawlingGeneral crawling = new ABL_MSV_D001();
//    result = crawling.execute(args);

    assertTrue(result);
  }
}
