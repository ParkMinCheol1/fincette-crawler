
package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;


public class HKL_ASV_D001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_ASV_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","36430","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_ASV_D001())).execute(args);
        System.exit(exitCode);
    }

}
