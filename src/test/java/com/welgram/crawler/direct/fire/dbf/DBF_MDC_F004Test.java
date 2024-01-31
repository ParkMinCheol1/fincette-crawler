package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_MDC_F004Test {

  /**
   * 프로미 다이렉트 국내여행보험(CM)：선택형, 고급형, 실손담보포함
   */
  @Test
  public void testExecute() {
    String[] args = {"-p", "30272", "-a", "30","-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_MDC_F004())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testMonitoring() {
    String[] args = {"-p", "30272", "-a", "30","-g", "M", "-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_MDC_F004())).execute(args);
    System.exit(exitCode);
  }
}
