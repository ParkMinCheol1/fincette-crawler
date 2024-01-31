package com.welgram.crawler.direct.fire.nhf;

import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.direct.life.dbl.DBL_ACD_F002;
import com.welgram.crawler.direct.life.dbl.DBL_CCR_F012;
import com.welgram.crawler.direct.life.dbl.DBL_CCR_F013;
import com.welgram.crawler.direct.life.dbl.DBL_CCR_F014;
import com.welgram.crawler.direct.life.dbl.DBL_CCR_F015;
import com.welgram.crawler.direct.life.dbl.DBL_CCR_F016;
import com.welgram.crawler.direct.life.dbl.DBL_CCR_F017;
import com.welgram.crawler.direct.life.dbl.DBL_DMN_F004;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F001;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F007;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F013;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F017;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F018;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F019;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F020;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F021;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F022;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F023;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F024;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F025;
import com.welgram.crawler.direct.life.dbl.DBL_DSS_F026;
import com.welgram.crawler.direct.life.dbl.DBL_WLF_F027;
import com.welgram.crawler.direct.life.dbl.DBL_WLF_F029;
import org.junit.Test;
import picocli.CommandLine;

public class NHF_Test {

  // 단건 조회 조건
  String[] args = {"1"};

  @Test
  public void NHF_ACD_F001() {

    String[] args = {"-p", "37613", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F001())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F002() {

    String[] args = {"-p", "37614", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F002())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F004() {

    String[] args = {"-p", "39235", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F004())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F005() {

    String[] args = {"-p", "39236", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F005())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void NHF_ACD_F006() {

    String[] args = {"-p", "37939", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F006())).execute(args);
    System.exit(exitCode);
  }


  @Test
  public void NHF_ACD_F007() {

    String[] args = {"-p", "37940", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F007())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F008() {

    String[] args = {"-p", "37941", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F008())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F009() {

    String[] args = {"-p", "37942", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F009())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F010() {

    String[] args = {"-p", "37943", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F010())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F011() {

    String[] args = {"-p", "37944", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F011())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F012() {

    String[] args = {"-p", "37945", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F012())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_ACD_F013() {

    String[] args = {"-p", "37764", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_ACD_F013())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_AMD_F004() {

    String[] args = {"-p", "29199", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_AMD_F004())).execute(args);
    System.exit(exitCode);
  }

  @Test
  public void NHF_BAB_F001() {

    String[] args = {"-p", "38228", "-a", "30", "-g", "F","-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_BAB_F001())).execute(args);
    System.exit(exitCode);
  }
  @Test
  public void NHF_CCR_F001() {

    String[] args = {"-p", "39862", "-a", "30", "-g", "M", "-m", "-n", "mudfish"};
    int exitCode = new CommandLine(new CrawlerCommand(new NHF_CCR_F001())).execute(args);
    System.exit(exitCode);
  }



}

