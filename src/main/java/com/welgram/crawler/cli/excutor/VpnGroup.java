package com.welgram.crawler.cli.excutor;

import picocli.CommandLine;

public class VpnGroup {


    @CommandLine.ArgGroup(validate = false, heading = "vpn 사용시 국가코드리스트\n", exclusive = true)
    public Section section;

    public static class Section {

        @CommandLine.Option(names = {"-n", "--vpn"}, description = "VPN(mudfish) 사용", defaultValue = "", order = 0, required = false)
        public String vpn;

        @CommandLine.Option(names = {"-cc", "--countries"}, description = "국가코드 리스트", defaultValue = "KR", order = 1, required = false)
        public String[] countries;

    }
}
