package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_CCR_D003Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-n", "mudfish", "-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_D003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "30729", "-a","22"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_D003())).execute(args);
        System.exit(exitCode);
    }
}