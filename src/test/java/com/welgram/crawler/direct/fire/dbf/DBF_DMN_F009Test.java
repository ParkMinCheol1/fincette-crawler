package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DMN_F009Test {

  @Test
  public void testCrawling() {
    String[] args = {"-p", "37380", "-a", "50", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DMN_F009())).execute(args);
    System.exit(exitCode);

  }

  @Test
  public void testMonitoring() {
    String[] args = {"-m", "-p", "37380", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DMN_F009())).execute(args);
    System.exit(exitCode);
  }
}
