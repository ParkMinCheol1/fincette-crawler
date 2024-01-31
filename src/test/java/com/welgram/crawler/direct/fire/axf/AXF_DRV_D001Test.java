package com.welgram.crawler.direct.fire.axf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class AXF_DRV_D001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish","-ss","Y"};
//        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new AXF_DRV_D001())).execute(args);
        System.exit(exitCode);
    }

}

