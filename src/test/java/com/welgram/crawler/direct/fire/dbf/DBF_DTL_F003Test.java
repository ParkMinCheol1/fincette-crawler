package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.abl.ABL_SAV_D003;
import org.junit.Test;
import picocli.CommandLine;

public class DBF_DTL_F003Test {

  @Test
    public void testMonitoring() {
      String[] args = {"-p", "35329", "-a", "40", "-g", "F", "-m"};
      int exitCode = new CommandLine(new CrawlerCommand(new DBF_DTL_F003())).execute(args);
      System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "35329", "-a", "40", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new DBF_DTL_F003())).execute(args);
        System.exit(exitCode);
    }
}
