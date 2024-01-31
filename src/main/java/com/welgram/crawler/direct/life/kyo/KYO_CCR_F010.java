package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * 해당상품은 대면상품이지만 표준화가 불가능함. 다른 대면 상품과 ui가 매우 다름.
 */
// 2023.07.31 | 조하연 |
public class KYO_CCR_F010 extends CrawlingKYOAnnounce2 {

    public static void main(String[] args) {
        executeCommand(new KYO_CCR_F010(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $element = null;

        //step1 : 공시실 상품명 찾기
        findProductName(info.getProductNamePublic());

        //step2 : 고객정보 입력
        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 계산 버튼 클릭");
        $element = driver.findElement(By.id("isPrcClc0"));
        click($element);

        //step3 : 주계약 정보 비교
        logger.info("주계약 가입금액 비교");
        setAssureMoney(info.getAssureMoney());

        logger.info("주계약 보험기간 비교");
        setInsTerm(info.getInsTerm());

        logger.info("주계약 납입기간 비교");
        String napTerm = info.getInsTerm().equals(info.getNapTerm()) ? "전기납" : info.getNapTerm();
        setNapTerm(napTerm);

        logger.info("주계약 납입주기 비교");
        setNapCycle(info.getNapCycleName());

        //step4 : 선택특약 정보 비교
        setSubTreatiesInfo(info);

        //step5 : 보험료 크롤링
        crawlPremium(info);

        //step6 : 해약환급금 크롤링
        crawlReturnMoneyList(info);

        return true;
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.주계약)
            .findFirst()
            .get();

        try {
            WebElement $premiumEm = driver.findElement(By.xpath("//*[@id='tabsld_calc']/div/ul/li[2]/div[1]/div[1]/article/div/ul/li[1]/div/div[2]/span/em"));
            String premium = "";

            premium = $premiumEm.getText().trim();
            premium = premium.replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                String msg = "주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.";
                logger.info(msg);
                throw new Exception(msg);
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

            logger.info("스크린샷 찍기");
            takeScreenShot(info);
        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String category = info.getCategoryName();
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        PlanReturnMoney lastPlanReturnMoney = null;

        try {
            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//button[normalize-space()='해약환급금']"));
            click($button);

            logger.info("해약환급금 크롤링 시작~");
            WebElement $returnMoneyTbody = driver.findElement(By.id("trmRview_1"));
            List<WebElement> $trList = $returnMoneyTbody.findElements(By.tagName("tr"));
            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                String term = "";
                String premiumSum = "";
                String returnMoney = "";
                String returnRate = "";
                String returnMoneyAvg = "";
                String returnRateAvg = "";
                String returnMoneyMin = "";
                String returnRateMin = "";

                PlanReturnMoney p = new PlanReturnMoney();

                if($tdList.size() == 4) {
                    term = $tdList.get(0).getText().trim();
                    premiumSum = $tdList.get(1).getText().trim();
                    returnMoney = $tdList.get(2).getText().trim();
                    returnRate = $tdList.get(3).getText().trim();

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                    logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate);
                } else if($tdList.size() == 7) {
                    boolean isOnlyMainTreaty = info.getTreatyList().stream()
                        .noneMatch(t -> t.productGubun == ProductGubun.선택특약);

                    term = $tdList.get(0).getText().trim();

                    if(isOnlyMainTreaty) {
                        //주계약만 있는 경우
                        premiumSum = $tdList.get(4).getText().trim();
                        returnMoney = $tdList.get(5).getText().trim();
                        returnRate = $tdList.get(6).getText().trim();
                    } else {
                        //주계약 + 선택특약인 경우
                        premiumSum = $tdList.get(1).getText().trim();
                        returnMoney = $tdList.get(2).getText().trim();
                        returnRate = $tdList.get(3).getText().trim();
                    }

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                    logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate);
                } else if($tdList.size() == 8) {
                    //최저, 평균, 공시 환급금을 모두 제공하는 경우
                    term = $tdList.get(0).getText().trim();
                    premiumSum = $tdList.get(1).getText().trim();
                    returnMoney = $tdList.get(2).getText().trim();
                    returnRate = $tdList.get(3).getText().trim();
                    returnMoneyAvg = $tdList.get(4).getText().trim();
                    returnRateAvg = $tdList.get(5).getText().trim();
                    returnMoneyMin = $tdList.get(6).getText().trim();
                    returnRateMin = $tdList.get(7).getText().trim();

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));
                    returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
                    returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));

                    p.setReturnMoneyAvg(returnMoneyAvg);
                    p.setReturnRateAvg(returnRateAvg);
                    p.setReturnMoneyMin(returnMoneyMin);
                    p.setReturnRateMin(returnRateMin);

                    logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {} | 평균환급금 : {} | 평균환급률 : {} | 최저환급금 : {} | 최저환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate, returnMoneyAvg, returnRateAvg, returnMoneyMin, returnRateMin);

                }

                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                lastPlanReturnMoney = p;
                planReturnMoneyList.add(p);

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }

            logger.info("해약환급금 크롤링 끝~");

            //해약환급금 표의 마지막 값이 실제 만기에 해당하는 값인지 체크
            String lastTerm = lastPlanReturnMoney.getTerm().replaceAll(" ", "");
            int lastTermNum = Integer.parseInt(lastTerm.replaceAll("[^0-9]", ""));
            String insTerm = info.getInsTerm();
            int insTermNum = Integer.parseInt(insTerm.replaceAll("[^0-9]", ""));
            int age = "태아보험".equals(category) ? 0 : Integer.parseInt(info.getAge());    //태아보험의 경우 나이를 태아기준으로 계산해야 함.

            if (insTerm.contains("세")) {
                //보험기간이 "N세"인 경우 = 세납인 경우

                /**
                 * 해약환급금 경과기간 = 보험기간 - 실제나이 인 경우에만 만기환급금 세팅
                 * ex)
                 * 보험기간 : 100세
                 * 실제나이 : 1세
                 * 해약환급금 경과기간 : "만기" or "99년"인 경우에만 만기환급금 세팅
                 */
                if (lastTerm.contains("만기") || lastTermNum == insTermNum - age) {
                    info.returnPremium = lastPlanReturnMoney.getReturnMoney();
                } else {
                    info.returnPremium = "0";
                }
            }

            logger.info("만기환급금 : {}원", info.returnPremium);
        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }

    public void setSubTreatiesInfo(CrawlingProduct info) throws CommonCrawlerException {

        //선택특약 추려내기
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.선택특약)
            .collect(Collectors.toList());

        //가입금액이 연령,성별마다 달라지는 특약 추려내기
        List<CrawlingTreaty> specialTreatyList = info.getTreatyList().stream()
            .filter(t -> t.getAssureMoney() == 0)
            .collect(Collectors.toList());

        try {

            //가입설계에 선택특약이 있을 경우에만
            if (subTreatyList.size() > 0 || specialTreatyList.size() > 0) {
                logger.info("실제 원수사 특약 정보 읽어오기");

                List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
                WebElement $ul = driver.findElement(By.xpath("//ul[@class='bullet a lg']"));
                List<WebElement> $liList = $ul.findElements(By.tagName("li"));

                //선택특약 정보 읽어오기기
               for(WebElement $li : $liList) {
                    String treatyName = "";
                    String treatyInsTerm = "";
                    String treatyNapTerm = "";
                    String treatyAssureMoney = "";

                    WebElement $treatyNameDiv = $li.findElement(By.xpath(".//div[@class='name']"));
                    WebElement $treatyInfoDiv = $li.findElement(By.xpath(".//div[@class='edt']"));
                    WebElement $treatyInsTermEm = $treatyInfoDiv.findElement(By.xpath("./em[1]"));
                    WebElement $treatyNapTermEm = $treatyInfoDiv.findElement(By.xpath("./em[2]"));
                    WebElement $treatyAssureMoneyEm = $treatyInfoDiv.findElement(By.xpath("./em[3]"));

                    treatyName = $treatyNameDiv.getText().trim();
                    treatyInsTerm = $treatyInsTermEm.getText().trim().replace("만기", "");
                    treatyNapTerm = $treatyNapTermEm.getText().trim();
                    treatyNapTerm = "전기납".equals(treatyNapTerm) ? treatyInsTerm : treatyNapTerm;
                    treatyAssureMoney = $treatyAssureMoneyEm.getText();
                    treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

                    CrawlingTreaty t = new CrawlingTreaty();
                    t.setTreatyName(treatyName);
                    t.setInsTerm(treatyInsTerm);
                    t.setNapTerm(treatyNapTerm);
                    t.setAssureMoney(Integer.parseInt(treatyAssureMoney));

                    targetTreatyList.add(t);
                }

                logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
                boolean result = advancedCompareTreaties(targetTreatyList, subTreatyList, new CrawlingTreatyEqualStrategy2());
                if (result) {
                    logger.info("특약 정보 모두 일치");
                } else {
                    logger.info("특약 정보 불일치");
                    throw new Exception();
                }

            } else {
                logger.info("가입설계에 선택특약이 존재하지 않습니다.");
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_TREATY, e.getMessage());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $mainTreatyDiv = driver.findElement(By.xpath("//div[@class='splist info']"));
            WebElement $insTermTitleDiv = $mainTreatyDiv.findElement(By.xpath(".//div[normalize-space()='" + title + "']"));
            WebElement $insTermValueDiv = $insTermTitleDiv.findElement(By.xpath("./following-sibling::div"));

            actual = $insTermValueDiv.getText().trim();
            actual = actual.replace("만기", "");

            super.printLogAndCompare(title, expected, actual);
        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expected = (String) obj[0];
        String actual = "";
        try {
            WebElement $mainTreatyDiv = driver.findElement(By.xpath("//div[@class='splist info']"));
            WebElement $napTermTitleDiv = $mainTreatyDiv.findElement(By.xpath(".//div[normalize-space()='" + title + "']"));
            WebElement $napTermValueDiv = $napTermTitleDiv.findElement(By.xpath("./following-sibling::div"));

            actual = $napTermValueDiv.getText().trim();

            super.printLogAndCompare(title, expected, actual);
        } catch (Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $mainTreatyDiv = driver.findElement(By.xpath("//div[@class='splist info']"));
            WebElement $napCycleTitleDiv = $mainTreatyDiv.findElement(By.xpath(".//div[normalize-space()='" + title + "']"));
            WebElement $napCycleValueDiv = $napCycleTitleDiv.findElement(By.xpath("./following-sibling::div"));

            actual = $napCycleValueDiv.getText().trim();

            super.printLogAndCompare(title, expected, actual);
        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $mainTreatyDiv = driver.findElement(By.xpath("//div[@class='splist info']"));
            WebElement $assureMoneyTitleDiv = $mainTreatyDiv.findElement(By.xpath(".//div[normalize-space()='" + title + "']"));
            WebElement $assureMoneyValueDiv = $assureMoneyTitleDiv.findElement(By.xpath("./following-sibling::div"));

            actual = $assureMoneyValueDiv.getText();
            actual = String.valueOf(MoneyUtil.toDigitMoney(actual));

            super.printLogAndCompare(title, expected, actual);
        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {
            //생년월일 관련 element 찾기
            WebElement $birthInput = driver.findElement(By.id("ins0_id0"));
            String id = $birthInput.getAttribute("id");
            WebElement $birthLabel = driver.findElement(By.xpath(".//label[@for='" + id + "']"));

            //생년월일 입력
            $birthLabel.click();
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

            //비교
            super.printLogAndCompare(title, expectedBirth, actualBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "남성" : "여성";
        String actualGender = "";

        try {
            //성별 관련 element 찾기
            WebElement $genderLabel = driver.findElement(By.xpath("//label[normalize-space()='" + expectedGender + "']"));
            click($genderLabel);

            //실제 클릭된 성별 읽어오기
            WebElement $genderInput = $genderLabel.findElement(By.tagName("input"));
            String attrName = $genderInput.getAttribute("name");
            String script = "return $('input[name=" + attrName + "]:checked').attr('id');";
            String attrId = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath("//label[@for='" + attrId + "']"));
            actualGender = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);
        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }

}