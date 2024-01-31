package com.welgram.crawler.direct.life.hnl;

import com.welgram.crawler.cli.CrawlerCommand;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HNL_TRM_D003Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-p", "37133", "-a", "52", "-g", "M", "-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HNL_TRM_D003())).execute(args);
        System.exit(exitCode);
    }

}