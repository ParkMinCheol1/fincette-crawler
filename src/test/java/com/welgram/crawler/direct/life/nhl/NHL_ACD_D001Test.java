package com.welgram.crawler.direct.life.nhl;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.lin.LIN_WLF_F011;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class NHL_ACD_D001Test extends TestCase {


    @Test
    public void testCrawling() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new NHL_ACD_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new NHL_ACD_D001())).execute(args);
        System.exit(exitCode);
    }

}