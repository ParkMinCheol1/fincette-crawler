package com.welgram.crawler.direct.life.hnl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HNL_CCR_D013Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37265", "-a", "62", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HNL_CCR_D013())).execute(args);
        System.exit(exitCode);
    }

}