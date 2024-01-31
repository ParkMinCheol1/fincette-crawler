package com.welgram.crawler.direct.fire.ltf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LTF_AMD_F001Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p","35109","-a","30","-ss","y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_AMD_F001())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_AMD_F001())).execute(args);
        System.exit(exitCode);
    }
}
