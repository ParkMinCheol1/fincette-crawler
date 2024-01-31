package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_DSS_F052Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38407", "-a", "58", "-g", "F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F052())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38407", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DSS_F052())).execute(args);
        System.exit(exitCode);

    }
}
