package com.welgram.crawler.direct.life.ail;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class AIL_TRM_F001Test {

    @Test
    public void testMonitoring() {
        String[] args = {"-p", "31703", "-a", "30","-n","mudfish"};
        int exitCode = new CommandLine(new CrawlerCommand(new AIL_TRM_F001())).execute(args);
        System.exit(exitCode);
    }
}
