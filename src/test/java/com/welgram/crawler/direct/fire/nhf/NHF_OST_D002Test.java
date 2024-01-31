package com.welgram.crawler.direct.fire.nhf;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.SeleniumCrawler;
import org.junit.Test;

public class NHF_OST_D002Test {

  @Test
  public void testExecute() {

    boolean result = false;

    try {

      String[] args = {"1"};
      SeleniumCrawler craling = new NHF_OST_D002();
//      result = craling.execute(args);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
