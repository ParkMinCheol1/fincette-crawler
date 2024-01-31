package com.welgram.crawler.direct.life.lin;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class LIN_CCR_D003Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_CCR_D003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","36427","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new LIN_CCR_D003())).execute(args);
        System.exit(exitCode);
    }

}