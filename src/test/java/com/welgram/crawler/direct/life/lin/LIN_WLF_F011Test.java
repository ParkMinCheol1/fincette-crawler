package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_WLF_F011Test extends TestCase {


    @Test
    public void testCrawling() {
        String[] args = {"-p", "35132","-a","68"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_WLF_F011())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_WLF_F011())).execute(args);
        System.exit(exitCode);
    }

}