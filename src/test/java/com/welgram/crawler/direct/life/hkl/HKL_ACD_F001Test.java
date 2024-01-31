package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HKL_ACD_F001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37641"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_ACD_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void monitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HKL_ACD_F001())).execute(args);
        System.exit(exitCode);
    }

}
