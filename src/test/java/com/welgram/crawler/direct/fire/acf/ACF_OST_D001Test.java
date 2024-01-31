package com.welgram.crawler.direct.fire.acf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ACF_OST_D001Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * Chubb 해외여행보험
   */
  @Test
  public void testMonitoring() {

    String[] args = {"-m", "-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new ACF_OST_D001())).execute(args);
    System.exit(exitCode);
  }
}
