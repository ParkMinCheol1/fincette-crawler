package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_TRM_F001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","32307","-a","30", "-g", "F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_TRM_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","32307","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_TRM_F001())).execute(args);
        System.exit(exitCode);
    }

}




