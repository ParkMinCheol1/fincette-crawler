package com.welgram.crawler.direct.life.kdb;

import static org.junit.Assert.assertTrue;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class KDB_CCR_D003Test {

    // 단건 조회 조건
    String[] args = {"1"};

    /**
     * (무) KDB다이렉트암보험
     */

    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new KDB_CCR_D003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testExecutePlan() {

//    String[] args = {"1", "2250", "28"};
//    boolean result = new KDB_CCR_D003().execute(args);
//
//    assertTrue(result);
    }
}
