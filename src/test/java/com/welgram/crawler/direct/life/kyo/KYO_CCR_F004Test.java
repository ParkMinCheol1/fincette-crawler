package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class KYO_CCR_F004Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * (무)Chubb다이렉트 점프업 어린이보험
   */
  @Test
  public void testMonitoring() {

//    String[] args = {"-m", "-ss", "Y", "-n","mudfish"};
    String[] args = {"-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new KYO_CCR_F004())).execute(args);
    System.exit(exitCode);
  }
}
