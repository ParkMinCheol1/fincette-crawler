package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_TRM_D001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWL_TRM_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","8493","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWL_TRM_D001())).execute(args);
        System.exit(exitCode);
    }

}


