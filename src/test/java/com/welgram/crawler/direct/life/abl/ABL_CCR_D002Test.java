package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_CCR_D002Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "30318","-a","27", "-g", "M", "-n", "mudfish","-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_D002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "30318","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_D002())).execute(args);
        System.exit(exitCode);
    }

}