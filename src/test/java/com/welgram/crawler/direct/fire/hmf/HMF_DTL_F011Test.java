package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HMF_DTL_F011Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "38360", "-a", "40", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DTL_F011())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "38360", "-a", "34", "-g", "F"};
        int exitCode = new CommandLine(new CrawlerCommand(new HMF_DTL_F011())).execute(args);
        System.exit(exitCode);

    }
}
