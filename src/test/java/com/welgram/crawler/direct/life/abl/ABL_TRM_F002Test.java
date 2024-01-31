package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_TRM_F002Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "32311","-a","64","-g","F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_TRM_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "32311","-a","30","-g","M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_TRM_F002())).execute(args);
        System.exit(exitCode);
    }

}




