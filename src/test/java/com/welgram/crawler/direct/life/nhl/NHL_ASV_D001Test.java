package com.welgram.crawler.direct.life.nhl;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class NHL_ASV_D001Test {

//  // 단건 조회 조건
//  String[] args = {"1"};
//
//
//  @Test
//  public void testExecute() {
//    boolean result = new NHL_ASV_D001().execute(args);
//
//    assertTrue(result);
//  }

  @Test
  public void testMonitoring() {
    String[] args = {"-m","-n","mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHL_ASV_D001())).execute(args);
    System.exit(exitCode);
  }
}
