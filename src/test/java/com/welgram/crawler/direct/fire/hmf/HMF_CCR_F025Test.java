package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_CCR_F025Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37922", "-a", "50", "-g", "M", "-m"};

        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F025())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37922", "-a", "56", "-g", "F"};

        int exitCode = new CommandLine(new CrawlerCommand(new HMF_CCR_F025())).execute(args);
        System.exit(exitCode);

    }
}
