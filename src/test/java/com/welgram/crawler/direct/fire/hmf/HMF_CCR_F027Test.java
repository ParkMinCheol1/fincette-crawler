package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_CCR_F027Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37927", "-a", "40", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F027())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37927", "-a", "48", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F027())).execute(args);
        System.exit(exitCode);

    }
}
