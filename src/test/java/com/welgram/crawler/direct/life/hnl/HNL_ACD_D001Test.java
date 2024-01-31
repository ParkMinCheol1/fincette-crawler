package com.welgram.crawler.direct.life.hnl;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.dgl.DGL_ACD_F001;
import junit.framework.TestCase;
import org.junit.Test;
import picocli.CommandLine;

public class HNL_ACD_D001Test extends TestCase {


    @Test
    public void testMonitoring() {
        String[] args = {"-m"};
        int exitCode = new CommandLine(new CrawlerCommand(new HNL_ACD_D001())).execute(args);
        System.exit(exitCode);
    }

}