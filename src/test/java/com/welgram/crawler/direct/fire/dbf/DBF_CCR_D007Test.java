package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_CCR_D007Test {

    // 단건 조회 조건
    String[] args = {"1"};


  @Test
  public void testCrawling() {
    String[] args = {"-p", "35062", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_CCR_D007())).execute(args);
    System.exit(exitCode);
  }
}
