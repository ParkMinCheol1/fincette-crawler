
package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class HWL_ASV_D001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-n", "mudfish", "-m", "-ss", "Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWL_ASV_D001())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p","36431","-a","30","-ss", "Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWL_ASV_D001())).execute(args);
        System.exit(exitCode);
    }

}
