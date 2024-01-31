package com.welgram.crawler.direct.fire.ltf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LTF_DTL_F003Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p","35007","-a","25"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_DTL_F003())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_DTL_F003())).execute(args);
        System.exit(exitCode);
    }
}
