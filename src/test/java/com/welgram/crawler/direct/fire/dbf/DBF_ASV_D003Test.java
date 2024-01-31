package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_ASV_D003Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37155", "-a", "30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_ASV_D003())).execute(args);
        System.exit(exitCode);
    }

  @Test
  public void testCrawling() {
    String[] args = {"-p", "37155", "-a", "24", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_ASV_D003())).execute(args);
    System.exit(exitCode);
  }
}
