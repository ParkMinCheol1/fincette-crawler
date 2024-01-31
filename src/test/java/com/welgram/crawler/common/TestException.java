package com.welgram.crawler.common;

import com.welgram.common.MoneyUtil;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TestException extends Exception {
    @Test
    public void test() {
        String[] arr = {
            "선택",
            "5,000만원",
            "7,000만원",
            "1억원",
            "1억5천원",
            "2억원"
        };

        for(String str : arr) {
            try {
                System.out.println(str + " -> " + MoneyUtil.getDigitMoneyFromHangul(str));
            } catch (NullPointerException e) {

            }
        }
    }





    @Test
    public void test2() {
        List<CrawlingTreaty> welgramTreatyList = new ArrayList<>();
        List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();


        CrawlingTreaty t1 = new CrawlingTreaty("특약1", 3000, "10년", "10년");
        CrawlingTreaty t2 = new CrawlingTreaty("특약2", 1000, "10년", "10년");
        CrawlingTreaty t3 = new CrawlingTreaty("특약3", 1000, "10년", "10년");
        CrawlingTreaty t4 = new CrawlingTreaty("특약4", 1000, "10년", "10년");
//        CrawlingTreaty t1 = new CrawlingTreaty("특약1", 3000);
//        CrawlingTreaty t2 = new CrawlingTreaty("특약2", 1000);
//        CrawlingTreaty t3 = new CrawlingTreaty("특약3", 1000);
//        CrawlingTreaty t4 = new CrawlingTreaty("특약4", 1000);
        homepageTreatyList.add(t1);
        homepageTreatyList.add(t2);
        homepageTreatyList.add(t3);
        homepageTreatyList.add(t4);


        CrawlingTreaty w1 = new CrawlingTreaty("특약1", 1000, "10년", "10년");
        CrawlingTreaty w2 = new CrawlingTreaty("특약22", 1000, "10년", "10년");
        CrawlingTreaty w3 = new CrawlingTreaty("특약33", 1000, "10년", "10년");
        CrawlingTreaty w4 = new CrawlingTreaty("특약4", 1000, "10년", "10년");
//        CrawlingTreaty w1 = new CrawlingTreaty("특약1", 1000);
//        CrawlingTreaty w2 = new CrawlingTreaty("특약22", 1000);
//        CrawlingTreaty w3 = new CrawlingTreaty("특약33", 1000);
//        CrawlingTreaty w4 = new CrawlingTreaty("특약4", 1000);
        welgramTreatyList.add(w1);
        welgramTreatyList.add(w2);
        welgramTreatyList.add(w3);
        welgramTreatyList.add(w4);

        boolean result = compare(homepageTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());

    }


    /**
     * 원수사 특약 목록과 가입설계 특약 목록 정보를 비교한다.
     *
     * ex)
     *
     * [homepageTreatyList]                                         [welgramTreatyList]
     * 특약명      가입금액        보험기간        납입기간            특약명      가입금액        보험기간        납입기간
     * 특약1      10000000        10년만기        10년납              특약1      10000000        10년만기        10년납
     * 특약2      10000000        10년만기        10년납              특약22     10000000        10년만기        10년납
     * 특약3      10000000        10년만기        10년납              특약33     10000000        10년만기        10년납
     * 특약4      10000000        10년만기        10년납              특약4      10000000        10년만기        10년납
     *
     *
     * toAddTreatyList::CrawlingTreaty      => 가입설계에 추가해야하는 특약 목록         [특약2, 특약3]
     * toRemoveTreatyList::CrawlingTreaty   => 가입설계에서 제거해야하는 특약 목록       [특약22, 특약33]
     *
     *
     * @param homepageTreatyList 원수사 특약 목록
     * @param welgramTreatyList 가입설계 특약 목록
     * @param strategy 특약을 비교하는 기준 전략(특약 목록을 비교할 때 특약의 같다는 기준을 어떻게 설정할지에 대한 전략)
     * @return true : 일치, false : 불일치
     */
    public boolean compare(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList
        , CrawlingTreatyEqualStrategy strategy) {

        List<CrawlingTreaty> toAddTreatyList = new ArrayList<>();
        List<CrawlingTreaty> toRemoveTreatyList = new ArrayList<>();


        /**
         * 가입설계에 추가해야할 특약 처리
         */
        for(CrawlingTreaty homepageTreaty : homepageTreatyList) {
            boolean isEqual = false;
            List<Boolean> isEquals = new ArrayList<>();

            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                isEqual = strategy.isEqual(homepageTreaty, welgramTreaty);
                isEquals.add(isEqual);
            }

            //가입설계에 추가해야할 특약리스트에 담는다.
            if(!isEquals.contains(true)) {
                toAddTreatyList.add(homepageTreaty);
            }
        }


        /**
         * 가입설계에서 제거해야할 특약 처리
         */
        for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
            boolean isEqual = false;
            List<Boolean> isEquals = new ArrayList<>();

            for(CrawlingTreaty homepageTreaty : homepageTreatyList) {
                isEqual = strategy.isEqual(welgramTreaty, homepageTreaty);
                isEquals.add(isEqual);
            }

            //가입설계에서 제거해야할 특약리스트에 담는다.
            if(!isEquals.contains(true)) {
                toRemoveTreatyList.add(welgramTreaty);
            }
        }


        /**
         * 가입설계에서 수정돼야할 특약 처리
         * - toAddTreatyList와 toRemoveTreatyList에서 특약명이 같은 케이스가 수정돼야할 특약이다.
         */
        List<String> toModifyTreatyNameList = new ArrayList<>();
        System.out.println("▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ 불일치 특약 목록 ◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀◀");
        System.out.println("■■■■■■■■■■■■ 아래의 정보대로 가입설계 특약 내용을 변경해주세요 ■■■■■■■■■■■■");
        for(CrawlingTreaty homepageTreaty : toAddTreatyList) {
            String homepageTreatyName = homepageTreaty.getTreatyName();

            for(CrawlingTreaty welgramTreaty : toRemoveTreatyList) {
                String welgramTreatyName = welgramTreaty.getTreatyName();

                if(homepageTreatyName.equals(welgramTreatyName)) {
                    strategy.printDifferentInfo(welgramTreaty, homepageTreaty);

                    toModifyTreatyNameList.add(homepageTreatyName);
                    break;
                }
            }
        }


        //가입설계에 추가/삭제해야하는 특약목록 중 수정해야하는 케이스는 제거해줘야한다.
        toAddTreatyList.removeIf(t -> toModifyTreatyNameList.contains(t.getTreatyName()));
        toRemoveTreatyList.removeIf(t -> toModifyTreatyNameList.contains(t.getTreatyName()));

        System.out.println("■■■■■■■■■■■■ 가입설계에 다음의 특약들을 추가해주세요 ■■■■■■■■■■■■");
        toAddTreatyList.forEach(strategy::printInfo);

        System.out.println("■■■■■■■■■■■■ 가입설계에서 다음의 특약들을 제거해주세요 ■■■■■■■■■■■■");
        toRemoveTreatyList.forEach(strategy::printInfo);

        return false;
    }


}
