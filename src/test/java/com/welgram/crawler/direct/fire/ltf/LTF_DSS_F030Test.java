package com.welgram.crawler.direct.fire.ltf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LTF_DSS_F030Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37379", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_DSS_F030())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_DSS_F030())).execute(args);
        System.exit(exitCode);
    }
}
