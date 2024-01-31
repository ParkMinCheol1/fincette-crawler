package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_SAV_D003Test {

    /**
     * (무)ABL인터넷보너스주는저축보험
     */
    @Test
    public void testMonitoring() {
        String[] args = {"-m", "-p", "2152", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_SAV_D003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "2152", "-a", "39", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_SAV_D003())).execute(args);
        System.exit(exitCode);
    }

}




