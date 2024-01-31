package com.welgram.crawler.direct.fire.hwf;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HWF_DSS_D008Test extends TestCase {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "39377", "-a", "20", "-g", "F", "-m", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_DSS_D008())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "39377", "-a", "19", "-g", "F", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_DSS_D008())).execute(args);
        System.exit(exitCode);
    }

}