package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class SHL_DTL_F001Test extends TestCase {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "35114","-a","12","-g","F"};
        int exitCode = new CommandLine(new CrawlerCommand(new SHL_DTL_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new SHL_DTL_F001())).execute(args);
        System.exit(exitCode);
    }

}