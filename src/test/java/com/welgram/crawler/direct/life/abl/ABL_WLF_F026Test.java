package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_WLF_F026Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","37375","-a","15", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_WLF_F026())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","37375","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_WLF_F026())).execute(args);
        System.exit(exitCode);
    }

}
