package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_CCR_F018Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37491", "-a", "40", "-g", "M", "-m"};

        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F018())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37491", "-a", "40", "-g", "M"};

        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F018())).execute(args);
        System.exit(exitCode);

    }
}
