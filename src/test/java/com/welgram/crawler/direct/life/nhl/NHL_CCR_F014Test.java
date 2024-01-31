package com.welgram.crawler.direct.life.nhl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class NHL_CCR_F014Test extends TestCase {


//    @Test
//    public void testCrawling() {
//        String[] args = {"-m","-n","mudfish"};
//        int exitCode = new CommandLine(new CrawlerCommand(new NHL_CCR_F014())).execute(args);
//        System.exit(exitCode);
//    }

    @Test
    public void testMonitoring() {
        String[] args = {"-m","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new NHL_CCR_F014())).execute(args);
        System.exit(exitCode);
    }

}