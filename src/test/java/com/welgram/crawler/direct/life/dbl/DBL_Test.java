package com.welgram.crawler.direct.life.dbl;

import com.welgram.crawler.cli.CrawlerCommand;
import org.junit.Test;
import picocli.CommandLine;

public class DBL_Test {

  // 단건 조회 조건
  String[] args = {"1"};

  @Test
  public void DBL_ACD_F002() {

    String[] args = {"-p", "37925", "-a", "63", "-g", "F"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_ACD_F002())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_CCR_F012() {

    String[] args = {"-p", "37930", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_CCR_F012())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_CCR_F013() {

    String[] args = {"-p", "37932", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_CCR_F013())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_CCR_F014() {

    String[] args = {"-p", "37933", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_CCR_F014())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F013() {

    String[] args = {"-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F013())).execute(args);
    System.exit(exitCode);
  }



  @Test
  public void DBL_DMN_F004() {

    String[] args = {"-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DMN_F004())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_WLF_F027() {

    String[] args = {"-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_WLF_F027())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_WLF_F029() {

    String[] args = {"-p", "38324", "-a", "23", "-g", "F"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_WLF_F029())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F001() {

    String[] args = {"-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F001())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F007() {

    String[] args = {"-m"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F007())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_CCR_F015() {

    String[] args = {"-p", "38374", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_CCR_F015())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_CCR_F016() {

    String[] args = {"-p", "38375", "-a", "40", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_CCR_F016())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_CCR_F017() {

    String[] args = {"-p", "38376", "-a", "40", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_CCR_F017())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F017() {

    String[] args = {"-p", "38420", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F017())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F018() {

    String[] args = {"-p", "38423", "-a", "40", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F018())).execute(args);
    System.exit(exitCode);
  }



  @Test
  public void DBL_DSS_F019() {

    String[] args = {"-p", "38428", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F019())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F023() {

    String[] args = {"-p", "38432", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F023())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F022() {

    String[] args = {"-p", "38431", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F022())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F025() {

    String[] args = {"-p", "38434", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F025())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F026() {

    String[] args = {"-p", "38435", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F026())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F024() {

    String[] args = {"-p", "38433", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F024())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F020() {

    String[] args = {"-p", "38429", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F020())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void DBL_DSS_F021() {

    String[] args = {"-p", "38430", "-a", "30", "-g", "M"};
    int exitCode = new CommandLine(new CrawlerCommand(new DBL_DSS_F021())).execute(args);
    System.exit(exitCode);
  }
}

