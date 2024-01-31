package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_DTL_F001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y","-p","34987","-a","30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DTL_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","34987","-a","30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DTL_F001())).execute(args);
        System.exit(exitCode);
    }
}