package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_DSS_F017Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","37757","-a","30","-g","M", "-m"};
//        String[] args = {"-m" , "-ss", "Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_F017())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p" ,"37757","-a", "30","-g","M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_DSS_F017())).execute(args);
        System.exit(exitCode);
    }
}
