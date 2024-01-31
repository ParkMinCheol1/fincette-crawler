package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_DTL_D001Test {


    /**
     * (무)ABL인터넷치아보험Ⅱ(갱신형)
     */
    @Test
    public void testMonitoring() {
        String[] args = {"-p","1569","-a","37", "-g", "M", "-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DTL_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","1569","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DTL_D001())).execute(args);
        System.exit(exitCode);
    }
}