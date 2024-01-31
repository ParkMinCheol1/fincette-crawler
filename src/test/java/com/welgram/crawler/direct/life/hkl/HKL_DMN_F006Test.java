package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;


public class HKL_DMN_F006Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_DMN_F006())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37857", "-a", "40", "-g", "F", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_DMN_F006())).execute(args);
        System.exit(exitCode);
    }

}