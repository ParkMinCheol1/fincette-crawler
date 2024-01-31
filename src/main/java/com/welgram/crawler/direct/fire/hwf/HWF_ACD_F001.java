package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class HWF_ACD_F001 extends CrawlingHWFAnnounce {

    // 보통상해보험
    public static void main(String[] args) {
        executeCommand(new HWF_ACD_F001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);
        return true;
    }



    private void crawlFromAnnounce(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("주민등록번호 설정");
        WebElement $input1 = driver.findElement(By.xpath("//input[@name='i_jumin']"));
        WebElement $input2 = driver.findElement(By.xpath("//input[@name='i_no']"));
        setRegistrationNumber($input1, $input2, info.fullBirth, info.gender);

        logger.info("직업 설정");
        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("특약별 가입금액 설정");
        setTreatiesNew(info.treatyList);

        logger.info("보험료 계산 버튼 클릭");
        announceBtnClick(By.id("btnCalc"));

        logger.info("스크린샷 찍기 위해 최상단으로 이동");
        helper.executeJavascript("window.scrollTo(0, 0);");

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("보험료 크롤링");
        crawlAnnouncePagePremiums(info, "ccPrm");

    }



    private void setTreatiesNew(List<CrawlingTreaty> treaties) throws SetTreatyException {

        try {
            // 원수사 특약명이 존재하는 tr만 조회
            List<WebElement> $trList = driver.findElements(By.xpath("//*[@class='tb_left02 tbl104_last']/parent::tr"));
            List<CrawlingTreaty> targetTreaties = new ArrayList<>();

            for (WebElement $tr : $trList) {
                WebElement $treatyNameTd = $tr.findElement(By.xpath("./td[1]"));
                String targetTreatyName = $treatyNameTd.getText();

                // 가입설계 특약 조회
                for (CrawlingTreaty treaty : treaties) {

                    String treatyName = treaty.treatyName.trim();
                    String treatyAssureMoney = String.valueOf(treaty.assureMoney);

                    // 가입설계 특약 보기, 납기, 가입금액 세팅하기
                    if (targetTreatyName.equals(treatyName)) {
                        CrawlingTreaty targetTreaty = new CrawlingTreaty();

                        // 특약명 보이게 스크롤 이동
                        moveToElementByJavascriptExecutor($tr);
                        helper.executeJavascript("window.scrollBy(0, -50)");

                        logger.info("특약명 : {}", targetTreatyName);

                        // 원수사에서의 특약 가입금액 element
                        $treatyNameTd = $tr.findElement(By.xpath("./td[2]"));
                        WebElement $child = $treatyNameTd.findElement(By.xpath("./*[contains(@name, 'Isamt')]"));
                        String targetAssureMoney = "";

                        if ("input".equals($child.getTagName())) {
                            // 가입금액 세팅란이 input인 경우
                            if ("hidden".equals($child.getAttribute("type"))) {
                                // hidden 경우 수동으로 셋팅불가 -> 자동셋팅됨
                                targetAssureMoney = treatyAssureMoney;
                            } else {
                                treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);

                                // 가입금액 입력
                                targetAssureMoney = helper.sendKeys4_check($child, treatyAssureMoney);
                                targetAssureMoney = targetAssureMoney + "0000" ;
                            }

                        } else if ("select".equals($child.getTagName())) {
                            // 가입금액 세팅란이 select인 경우
                            treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);
                            targetAssureMoney = selectOptionFor($child, treatyAssureMoney);
                            targetAssureMoney = targetAssureMoney + "0000" ;
                        }

                        targetTreaty.setTreatyName(treatyName);
                        targetTreaty.setAssureMoney(Integer.parseInt(targetAssureMoney));

                        targetTreaties.add(targetTreaty);

                        break;
                    }
                }
            }

            boolean result = compareTreaties(targetTreaties, treaties);

            if (result) {
                logger.info("특약 정보 모두 일치 ^^");
            } else {
                throw new Exception("특약 불일치");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }

    }

}
