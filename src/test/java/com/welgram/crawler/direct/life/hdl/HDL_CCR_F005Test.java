package com.welgram.crawler.direct.life.hdl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HDL_CCR_F005Test {

    @Test
    public void testMonitoring() {

        String[] args = {"-m","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_CCR_F005())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testPlanCrawling() {

        String[] args = {"-p", "35258", "-a", "40", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HDL_CCR_F005())).execute(args);
        System.exit(exitCode);
    }

}
