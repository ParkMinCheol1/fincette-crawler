package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_OST_D002Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * (무)Chubb다이렉트 점프업 어린이보험
   */
  @Test
  public void testMonitoring() {

    String[] args = {"-ss","Y","-m","-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_OST_D002())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testCrawling() {
    String[] args = {"-p", "29668", "-a", "61", "-g", "F"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_OST_D002())).execute(args);
    System.exit(exitCode);
  }
}
