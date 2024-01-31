package com.welgram.crawler.direct.fire.hwf;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HWF_DRV_D009Test extends TestCase {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "39375", "-a", "52", "-g", "M", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_DRV_D009())).execute(args);
        System.exit(exitCode);
    }

    @Test
    public void testCrawling() {
        String[] args = {"-p", "39375", "-a", "48", "-g", "M", "-n", "mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new HWF_DRV_D009())).execute(args);
        System.exit(exitCode);
    }


}