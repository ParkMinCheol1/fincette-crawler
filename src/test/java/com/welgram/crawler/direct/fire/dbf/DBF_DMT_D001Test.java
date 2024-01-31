package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DMT_D001Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * 프로미 다이렉트 국내여행보험(CM)：선택형, 고급형, 실손담보포함
   */
  @Test
  public void testMonitoring() {

    boolean result = false;
    String[] args = {"-p", "30266", "-a","61","-g","M", "-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DMT_D001())).execute(args);
    System.exit(exitCode);
  }
}
