package com.welgram.crawler.direct.fire.hwf;


import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class HWF_DRV_F008 extends CrawlingHWFAnnounce {

    // 무배당 한화 운전자상해보험2310
    public static void main(String[] args) {
        executeCommand(new HWF_DRV_F008(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);
        return true;

    }

    private void crawlFromAnnounce(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        WaitUtil.waitFor(2);

        logger.info("생년월일 설정");
        WebElement $birthDayInput = driver.findElement(By.id("i_jumin"));
        setBirthday($birthDayInput, info.fullBirth);

        logger.info("성별 설정");
        WebElement $genderSelect = driver.findElement(By.id("i_no"));
        setGender($genderSelect, info.gender);

        logger.info("직업 설정");
        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("차량용도 설정 : 자가용");
        WebElement $vehicleSelect = driver.findElement(By.cssSelector("select[name=cha]"));
        setVehicle($vehicleSelect, "자가용");

        logger.info("가입구분 설정 : {}", info.textType);
        WebElement $productTypeSelect = driver.findElement(By.cssSelector("select[name=gubun]"));
        setProductType($productTypeSelect, info.textType);

        logger.info("보험기간 설정 : {}", info.insTerm);
        WebElement $insTermSelect = driver.findElement(By.cssSelector("select[name=bogi]"));
        setInsTerm($insTermSelect, info.insTerm);

        logger.info("납입기간 설정: {}", info.napTerm);
        WebElement $napTermSelect = driver.findElement(By.cssSelector("select[name=napgi]"));
        String napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
        setNapTerm($napTermSelect, napTerm);

        logger.info("납입주기 설정: 월납");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=napbang]"));
        setNapCycle($napCycleSelect, "월납");

        logger.info("특약 설정");
        List<WebElement> $trList = driver.findElements(By.xpath("//th[@class='sub_tit']/parent::tr[not(contains(@style, 'display: none'))]"));
        setTreaties(info.treatyList, $trList, "./th[1]");

        logger.info("보험료 계산 버튼 클릭");
        announceBtnClick(By.id("btnCalc"));

        logger.info("보험료 크롤링");
        crawlAnnouncePagePremiums(info, "gnPrm", "cuPrm");

        logger.info("스크린샷 찍기");
        element = driver.findElement(By.xpath("//h3[text()='기본정보']"));
        helper.moveToElementByJavascriptExecutor(element);
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlAnnouncePageReturnPremiums(info, "DSS");

    }



    @Override
    protected void setTreaties(Object...obj) throws SetTreatyException {

        List<CrawlingTreaty> treaties = (List<CrawlingTreaty>) obj[0];
        List<WebElement> $trList = (List<WebElement>) obj[1];
        String treatyNameTdTag = (String) obj[2];

        String script = "";

        try {
            /**
             * 한화손해보험 공시실 대면상품의 경우 ui가 굉장히 번거롭다.
             * 미가입하는 특약들에 대해서 전부 0만원으로 세팅해줘야하는 작업을 해야한다.
             * 손쉽게 작업하기 위해서 처음부터 모든 특약을 미가입처리한채로 시작한다.
             */

            // 활성화된 input값 0만원으로 초기화 (= 특약 미가입 처리)
            script = "$('tr:visible input[name*=ainsure]:not(:disabled)').val('0');";
            executeJavascript(script);

            // 활성화된 select "선택" 값으로 초기화 (= 특약 미가입 처리)
            script = "$('tr:visible select[name*=ainsure]:not(:disabled) option[value=\"0\"]').prop('selected', true);";
            executeJavascript(script);

            // 원수사 특약명이 존재하는 tr만 조회
            List<CrawlingTreaty> targetTreaties = new ArrayList<>();

            for (WebElement $tr : $trList) {
                WebElement $treatyNameTd = $tr.findElement(By.xpath(treatyNameTdTag));
                String targetTreatyName = $treatyNameTd.getText().trim();

                // 가입설계 특약 조회
                for (CrawlingTreaty treaty : treaties) {

                    String treatyName = treaty.treatyName.trim();
                    String treatyAssureMoney = String.valueOf(treaty.assureMoney);

                    // 가입설계 특약 보기, 납기, 가입금액 세팅하기
                    if (targetTreatyName.equals(treatyName)) {
                        CrawlingTreaty targetTreaty = new CrawlingTreaty();

                        String targetInsTerm;
                        String targetNapTerm;

                        // 특약명 보이게 스크롤 이동
                        moveToElementByJavascriptExecutor($tr);
                        helper.executeJavascript("window.scrollBy(0, -50)");

                        logger.info("특약명 : {}", targetTreatyName);

                        // 특약명 태그가 th -> td1~3 / 특약명 태그가 td -> td2~4
                        String[] tdArray = treatyNameTdTag.contains("th") ? new String[]{"./td[1]", "./td[2]", "./td[3]"} : new String[]{"./td[2]", "./td[3]", "./td[4]"};

                        // 원수사에서의 특약 보험기간 element 찾기
                        WebElement $treatyInsTermTd = $tr.findElement(By.xpath(tdArray[0]));
                        targetInsTerm = $treatyInsTermTd.getText().trim().replace("만기", "");

                        // 원수사에서의 특약 납입기간 element 찾기
                        WebElement $treatyNapTermTd = $tr.findElement(By.xpath(tdArray[1]));
                        targetNapTerm = $treatyNapTermTd.getText().trim();

                        if (targetNapTerm.equals("전기납")) {
                            targetNapTerm = targetInsTerm;
                        } else {
                            targetNapTerm = targetNapTerm.replace("납", "년");
                        }

                        // 원수사에서의 특약 가입금액 element
                        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath(tdArray[2]));
                        WebElement $child = null;
                        String targetAssureMoney = "";

                        int unit = 1;
                        String assureMoneyUnit = $treatyAssureMoneyTd.getText().trim();

                        if (assureMoneyUnit.equals("만원")) {
                            unit = 10000;
                        } else if (assureMoneyUnit.equals("천원")) {
                            unit = 1000;
                        }

                        try {
                            $child = $treatyAssureMoneyTd.findElement(By.xpath("./*[contains(@name, 'ainsure')]"));
                            $child.click();

                            if ("input".equals($child.getTagName())) {
                                // 가입금액 세팅란이 input인 경우
                                String type = $child.getAttribute("type");
                                if ("hidden".equals(type)) {
                                    // hidden 경우 수동으로 셋팅불가 -> 자동셋팅됨
                                    targetAssureMoney = treatyAssureMoney;
                                } else {
                                    // 특약금액을 선택할 때 다른 특약에 영향을 주는게 있어서 초기화 시키고 가입할 특약 금액 셋팅
                                    // 한화손해보험 공시실에서는 input을 초기화할 때 ctrl + a + delete가 작동하지 않는다.
                                    // 무조건 backspace로 지워야함. 현재 입력된 text의 길이만큼 backspace를 누른다.
                                    script = "return $(arguments[0]).val();";
                                    String currentValue = String.valueOf(executeJavascript(script, $child));
                                    for (int i = 0; i < currentValue.length(); i++) {
                                        $child.sendKeys(Keys.BACK_SPACE);
                                    }

                                    // 금액 단위에 따라 가입금액 변환
                                    treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);
                                    // 가입금액 입력
                                    targetAssureMoney = helper.sendKeys4_check($child, treatyAssureMoney);
                                    // 입력된 금액 단위 반영해서 재변환
                                    targetAssureMoney = String.valueOf(Integer.parseInt(targetAssureMoney) * unit);
                                }
                            } else if ("select".equals($child.getTagName())) {
                                // 가입금액 세팅란이 select인 경우
                                targetAssureMoney = selectOptionFor($child, treatyAssureMoney);
                            }
                        } catch (Exception e) {
                            // 특약가입이 고정이라 $child 를 찾지 못한경우
                            logger.info("해당 특약은 가입금액이 고정입니다.");
                            $treatyAssureMoneyTd.click();
                            WaitUtil.waitFor(1);
                            targetAssureMoney = $treatyAssureMoneyTd.getText();
                            targetAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetAssureMoney));
                            logger.info("가입금액: {}", targetAssureMoney);
                        }

                        targetTreaty.setTreatyName(treatyName);
                        targetTreaty.setInsTerm(targetInsTerm);
                        targetTreaty.setNapTerm(targetNapTerm);
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
