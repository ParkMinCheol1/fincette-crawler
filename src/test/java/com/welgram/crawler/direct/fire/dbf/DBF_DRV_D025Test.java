package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DRV_D025Test {

  @Test
  public void testMonitoring() {
    String[] args = {"-p", "38369", "-a", "30", "-g", "M", "-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DRV_D025())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testCrawling() {
    String[] args = {"-p", "38369", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DRV_D025())).execute(args);
    System.exit(exitCode);
  }
}
