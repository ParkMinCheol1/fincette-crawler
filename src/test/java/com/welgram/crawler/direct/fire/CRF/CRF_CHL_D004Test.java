package com.welgram.crawler.direct.fire.CRF;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.fire.crf.CRF_CHL_D004;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class CRF_CHL_D004Test extends TestCase {
    @Test
    public void testMonitoring() {
        String[] args = {"-p", "40153", "-a", "0", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new CRF_CHL_D004())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "40116", "-a", "2", "-g", "F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new CRF_CHL_D004())).execute(args);
        System.exit(exitCode);

    }
}