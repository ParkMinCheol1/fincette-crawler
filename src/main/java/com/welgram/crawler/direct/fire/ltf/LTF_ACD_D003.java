package com.welgram.crawler.direct.fire.ltf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LTF_ACD_D003 extends CrawlingLTFDirect {

    public static void main(String[] args) { executeCommand(new LTF_ACD_D003(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception { // https://alice.lotteins.co.kr/product/golf/main

        enter();

        // 라운드 일자 : 아무 날짜 선택해도 보험료 동일. 이미 설정되어 있어서 생략
        // 집에서 출발하는 라운드 시간 : 아무 시간이나 선택해도 보험료 동일. 이미 설정되어 있어서 생략

        // 다음 클릭
        helper.click(By.xpath("//button[text()='다음']"), "다음 클릭");

        // 가입유형
        helper.click(By.xpath(
            "//strong[@class='txt1' and text()='" + info.textType + "']//ancestor::label")
            , "가입 유형선택 : " + info.textType);

        // 보험 유형 선택 (planSubName)
        helper.click(By.xpath(
                "//ul[@role='tablist']//a[text()='" + info.planSubName + "']/ancestor::li")
            , "보험 유형선택 : " + info.planSubName);

        // 보장 내용 비교 todo 분석팀과 상의 필요
//        compareLTFTreaties(info);

        // 보험료 수집
        crawlPremium(info);

        return true;
    }

}
