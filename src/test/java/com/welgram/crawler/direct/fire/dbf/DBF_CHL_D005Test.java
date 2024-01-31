package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CHL_D005Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * (무)Chubb다이렉트 점프업 어린이보험
   */
  @Test
  public void testMonitoring1() {
    String[] args = {"-p", "30551", "-a", "4", "-g", "M", "-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_CHL_D005())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testMonitoring2() {
    String[] args = {"-p", "37125", "-a", "1", "-g", "M", "-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_CHL_D005())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testCrawling() {
    String[] args = {"-p", "37125", "-a", "0", "-g", "F"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_CHL_D005())).execute(args);
    System.exit(exitCode);
  }
}
