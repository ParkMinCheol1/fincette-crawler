package com.welgram.crawler.direct.fire.axf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class AXF_DTL_F001Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p","37082","-a","31","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new AXF_DTL_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish","-ss","Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new AXF_DTL_F001())).execute(args);
        System.exit(exitCode);
    }

}
