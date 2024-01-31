package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.cli.CrawlerCommand;

import org.junit.Test;
import picocli.CommandLine;

public class HKL_TRM_D001Test {

  @Test
  public void testMonitoring() {
    String[] args = {"-m","-ss","Y","-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new HKL_TRM_D001())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testCrawling() {
    String[] args = {"-p","31680","-a","29","-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new HKL_TRM_D001())).execute(args);
    System.exit(exitCode);
  }
}
