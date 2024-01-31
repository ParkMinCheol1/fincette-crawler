package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CHL_D001Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * (무)Chubb다이렉트 점프업 어린이보험
   */
  @Test
  public void testMonitoring() {

    String[] args = {"-a", "2","-g", "M", "-p", "1880", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_CHL_D001())).execute(args);
    System.exit(exitCode);
  }
}
