package com.welgram.crawler.direct.life.shl;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class SHL_CHL_D002Test {

  /**
   * (무)신한인터넷 어린이보험 테스트
   */
  @Test
  public void testExecute() {

    boolean result = false;

    try {

      String[] args = {"-m", "-n", "mudfish"};
      int exitCode = new CommandLine(new CrawlerCommand(new SHL_CHL_D002())).execute(args);
      System.exit(exitCode);

    } catch (Exception e) {
      e.printStackTrace();
    }

    assertTrue(result);
  }
}
