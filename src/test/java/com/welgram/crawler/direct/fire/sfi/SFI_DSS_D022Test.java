package com.welgram.crawler.direct.fire.sfi;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class SFI_DSS_D022Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new SFI_DSS_D022())).execute(args);
        System.exit(exitCode);
    }

}