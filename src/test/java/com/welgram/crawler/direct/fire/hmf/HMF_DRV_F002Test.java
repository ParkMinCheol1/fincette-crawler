package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_DRV_F002Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37931", "-a", "30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DRV_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37931", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DRV_F002())).execute(args);
        System.exit(exitCode);
    }
}
