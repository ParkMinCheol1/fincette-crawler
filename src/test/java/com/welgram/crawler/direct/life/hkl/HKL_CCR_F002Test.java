package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HKL_CCR_F002Test extends TestCase {


    @Test
    public void testCrawling() {
        String[] args = {"-p", "35478", "-a","34"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_CCR_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_CCR_F002())).execute(args);
        System.exit(exitCode);
    }

}