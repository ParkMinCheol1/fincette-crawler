
package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.cli.CrawlerCommand;

import org.junit.Test;
import picocli.CommandLine;

public class HKL_MDC_F002Test {

    @Test
    public void testMonitoring() {
//        String[] args = {"-n", "mudfish", "-p", "30330","-a","30"};
        String[] args = {"-m","-ss","Y"};

        int exitCode = new CommandLine(new CrawlerCommand(new HKL_MDC_F002())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
//        String[] args = {"-n", "mudfish", "-p", "30330","-a","30"};
        String[] args = {"-p","30330","-a","30"};

        int exitCode = new CommandLine(new CrawlerCommand(new HKL_MDC_F002())).execute(args);
        System.exit(exitCode);
    }

}
