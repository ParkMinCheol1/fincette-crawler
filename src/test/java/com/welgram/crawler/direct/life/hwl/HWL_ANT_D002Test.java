package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_ANT_D002Test {

  @Test
  public void testMonitoring() {
    String [] args = {"-p","38865","-a","30","-m","-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new HWL_ANT_D002())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testCrawling() {
    String[] args = {"-p","32120","-a","30"};
    int exitCode = new CommandLine(new CrawlerCommand(new HWL_ANT_D002())).execute(args);
    System.exit(exitCode);
  }

}
