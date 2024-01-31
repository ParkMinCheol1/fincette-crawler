package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DSS_D005Test {

    // 단건 조회 조건
    String[] args = {"1"};


  @Test
  public void testCrawling() {
    String[] args = {"-p", "2408", "-a", "30", "-g", "M", "-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DSS_D005())).execute(args);
    System.exit(exitCode);
  }
}
