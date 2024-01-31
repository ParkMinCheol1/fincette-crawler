package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.shl.deleted.SHL_ACD_D002;
import org.junit.Test;
import picocli.CommandLine;

public class SHL_ACD_D002Test {

  @Test
  public void testMonitoring() {

    String[] args = {"-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new SHL_ACD_D002())).execute(args);
    System.exit(exitCode);
  }
}
