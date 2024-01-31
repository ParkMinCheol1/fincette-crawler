package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_CCR_F005Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-p","35503","-a","30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_F005())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","35503","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_F005())).execute(args);
        System.exit(exitCode);
    }

}