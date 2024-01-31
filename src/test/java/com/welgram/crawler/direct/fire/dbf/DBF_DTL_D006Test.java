package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DTL_D006Test {

    // 단건 조회 조건
    String[] args = {"1"};


  @Test
  public void testCrawling() {
    String[] args = {"-m", "-ss", "Y", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DTL_D006())).execute(args);
    System.exit(exitCode);
  }
}