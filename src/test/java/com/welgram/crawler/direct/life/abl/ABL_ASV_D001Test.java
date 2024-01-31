package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_ASV_D001Test {

    @Test
    public void testCrawling() {
//        String[] args = {"-m","-n","mudfish"};
        String[] args = {"-p","36429","-a","30", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_ASV_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testMonitoring() {
        String[] args = {"-p","36429","-a","30", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_ASV_D001())).execute(args);
        System.exit(exitCode);
    }
}
