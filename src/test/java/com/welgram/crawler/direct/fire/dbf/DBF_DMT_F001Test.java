package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
//import com.welgram.crawler.direct.fire.hwf.HWF_DSS_F003;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DMT_F001Test {

  // 단건 조회 조건
  String[] args = {"1"};

  /**
   * 프로미 다이렉트 국내여행보험(CM)：선택형, 고급형, 실손담보포함
   */
  @Test
  public void testExecute() {

    boolean result = false;
    String[] args = {"-m", "-ss", "Y", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DMT_F001())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void testMonitoring() {
    String[] args = {"-p", "37111", "-a", "30", "-g", "M", "-m"};

    int exitCode = new CommandLine(new CrawlerCommand(new DBF_DMT_F001())).execute(args);
    System.exit(exitCode);
  }
}
