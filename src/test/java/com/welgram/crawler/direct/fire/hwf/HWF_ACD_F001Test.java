package com.welgram.crawler.direct.fire.hwf;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWF_ACD_F001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37618", "-a", "30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_ACD_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "37618", "-a", "40", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_ACD_F001())).execute(args);
        System.exit(exitCode);
    }
}
