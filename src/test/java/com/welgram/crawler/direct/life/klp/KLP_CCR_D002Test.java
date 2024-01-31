package com.welgram.crawler.direct.life.klp;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class KLP_CCR_D002Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * (무)Chubb다이렉트 점프업 어린이보험
   */
  @Test
  public void testMonitoring() {

    String[] args = {"-p", "30579", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new KLP_CCR_D002())).execute(args);
    System.exit(exitCode);
  }
}
