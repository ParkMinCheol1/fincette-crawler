package com.welgram.crawler.direct.fire.hwf;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HWF_CCR_D006Test extends TestCase {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "39378", "-a", "20", "-g", "F", "-m", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_CCR_D006())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "39378", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_CCR_D006())).execute(args);
        System.exit(exitCode);
    }
}