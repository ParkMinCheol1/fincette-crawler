package com.welgram.crawler.direct.fire.ltf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class LTF_ACD_F002Test {

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37644", "-a", "30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_ACD_F002())).execute(args);
        System.exit(exitCode);
    }


    @Test
    public void testMonitoring() {
        String[] args = {"-m","-ss","y"};
        int exitCode = new CommandLine(new CrawlerCommand(new LTF_ACD_F002())).execute(args);
        System.exit(exitCode);
    }
}
