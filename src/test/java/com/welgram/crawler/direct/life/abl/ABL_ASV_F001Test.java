package com.welgram.crawler.direct.life.abl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class ABL_ASV_F001Test extends TestCase {

    @Test
    public void testMonitoring() {
        String[] args = {"-p","36445","-a","15", "-g", "F", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_ASV_F001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","36445","-a","28", "-g", "M"};
        int exitCode = new CommandLine(new CrawlerCommand(new ABL_ASV_F001())).execute(args);
        System.exit(exitCode);
    }
    
    
}