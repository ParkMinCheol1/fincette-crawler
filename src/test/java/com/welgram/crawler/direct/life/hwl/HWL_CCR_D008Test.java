package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_CCR_D008Test {

  @Test
  public void testMonitoring() {

    String[] args = {"-p","38932","-a","30","-g","M","-m","-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new HWL_CCR_D008())).execute(args);
    System.exit(exitCode);
  }
}
