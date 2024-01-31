package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_CCR_F003Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","37653","-a","45", "-g", "F", "-m" , "-ss", "Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_F003())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","37653","-a","30"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_CCR_F003())).execute(args);
        System.exit(exitCode);
    }


}
