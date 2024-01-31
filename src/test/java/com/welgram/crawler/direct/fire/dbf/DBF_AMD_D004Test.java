package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_AMD_D004Test {

  @Test
  public void testCrawling() {
    String[] args = {"-p","30492","-a","52","-g","F", "-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_AMD_D004())).execute(args);
    System.exit(exitCode);
  }

    @Test
    public void testMonitoring() {
        String[] args = {"-p","30492","-a","15","-g","F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_AMD_D004())).execute(args);
        System.exit(exitCode);
    }
}