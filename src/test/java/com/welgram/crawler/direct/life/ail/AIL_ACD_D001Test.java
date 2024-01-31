package com.welgram.crawler.direct.life.ail;

    import com.welgram.crawler.cli.CrawlerCommand;
    import org.junit.Test;
    import picocli.CommandLine;

public class AIL_ACD_D001Test {

    @Test
    public void testMonitoring() {
//        String[] args = {"-m","-n","mudfish","-ss", "Y"};
        String[] args = {"-m","-ss", "Y"};
        int exitCode = new CommandLine(new CrawlerCommand(new AIL_ACD_D001())).execute(args);
        System.exit(exitCode);
    }
}
