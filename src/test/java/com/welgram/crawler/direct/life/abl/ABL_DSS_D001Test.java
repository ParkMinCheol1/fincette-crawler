package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_DSS_D001Test extends TestCase {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","35545","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_D001())).execute(args);
        System.exit(exitCode);
    }

}