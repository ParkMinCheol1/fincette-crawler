package com.welgram.crawler.direct.life.klp;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class KLP_ANT_D001Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * (무)Chubb다이렉트 점프업 어린이보험
   */
  @Test
  public void testMonitoring() {

    String[] args = {"-p", "30857", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new KLP_ANT_D001())).execute(args);
    System.exit(exitCode);
  }
}