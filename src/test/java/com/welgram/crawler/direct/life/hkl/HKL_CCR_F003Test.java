package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HKL_CCR_F003Test extends TestCase {


    @Test
    public void testCrawling() {
        String[] args = {"-p", "37689", "-a","30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_CCR_F003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_CCR_F003())).execute(args);
        System.exit(exitCode);
    }

}