package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_CCR_D009Test {

  @Test
  public void testMonitoring() {

    String[] args = {"-m","-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new HWL_CCR_D009())).execute(args);
    System.exit(exitCode);
  }
}
