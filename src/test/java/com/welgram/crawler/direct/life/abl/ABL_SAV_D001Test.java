package com.welgram.crawler.direct.life.abl;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_SAV_D001Test {

  /**
   * (무)ABL인터넷보너스주는저축보험
   */
  @Test
  public void testMonitoring() {
    String[] args = {"-m", "-ss","Y"};
    int exitCode = new CommandLine(new CrawlerCommand(new ABL_SAV_D001())).execute(args);
    System.exit(exitCode);
  }
}
