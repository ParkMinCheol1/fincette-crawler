package com.welgram.crawler.direct.life.dbl;


import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.TreatyNameComparators;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public abstract class CrawlingDBLAnnounce extends SeleniumCrawler implements ScrapableNew {

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

//        setBirthday(info.birth);
//        setGender(info.gender);
//        setJob("보건 교사");
//        setNapCycle(info.getNapCycleName());
//        setNapTerm(info);
//        setTreaties(info.treatyList);
//        crawlPremium(info);
//        crawlReturnPremium(info);
//        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {
            String title = "생년월일";
            String welgramBirth = (String) obj[0];

            logger.info("생년월일 입력");
            WebElement input = driver.findElement(By.cssSelector("#base_birth"));
            helper.sendKeys3_check(input, welgramBirth);
            WaitUtil.waitFor(1);

            setForCompare(title, welgramBirth, input);

        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            String title = "성별";
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String fullBirth = info.getFullBirth();
            String welgramGenderText;
            if (fullBirth.startsWith("20")) {
                welgramGenderText = (info.gender == MALE) ? "3" : "4";
            } else {
                welgramGenderText = (info.gender == MALE) ? "1" : "2";
            }

            logger.info("주민번호 뒷 한자리 입력");
            WebElement input = driver.findElement(By.cssSelector("#base_sexType"));
            helper.sendKeys3_check(input, welgramGenderText);
            WaitUtil.waitFor(1);

            setForCompare(title, welgramGenderText, input);

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException { }



    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {
            logger.info("직업선택클릭 (대분류)");
            elements = driver.findElements(By.cssSelector("#jobcode > option"));
            int jobSize = elements.size();

            for (int i = 0; i < jobSize; i++) {

                if (elements.get(i).getText().trim()
                        .contains("교육, 의료, 종교, 문화예술, 스포츠, 사회복지 관련 분야")) {
                    elements.get(i).click();
                    break;
                }
            }
            WaitUtil.waitFor(1);

            logger.info("직업선택 (소분류)");
            elements = driver.findElements(By.cssSelector("#jobcodeDetail > option"));
            int job2Size = elements.size();

            for (int i = 0; i < job2Size; i++) {

                if (elements.get(i).getText().trim().contains("중고등학교 교사")) {
                    elements.get(i).click();
                    break;
                }
            }
            WaitUtil.waitFor(3);

        } catch (Exception e) {
            throw new SetJobException(e);
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try {
            String insTerm = (String) obj[0];

            if (insTerm.equals("종신보장")) {
                insTerm = "종신";
            }

            logger.info("보험기간 선택");
            logger.info("가설의 보험기간 : " + insTerm);

            driver.findElement(By.cssSelector("#instaranceTerm")).click();
            elements = driver.findElements(By.cssSelector("#instaranceTerm > option"));

            int insTermSize = elements.size();
            boolean insTermCheck = false;

            for (int i = 0; i < insTermSize; i++) {

                if (elements.get(i).getText().trim().contains(insTerm)) {
                    elements.get(i).click();
                    insTermCheck = true;
                    WaitUtil.waitFor(3);
                    break;
                }
            }

            if (!insTermCheck) {
                throw new Exception("보험기간을 선택할 수 없음 : " + insTerm);
            }

            WaitUtil.waitFor(3);

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            logger.info("납입기간 선택");
            logger.info("가설의 납입기간 : " + info.napTerm);

            driver.findElement(By.cssSelector("#napgiTerm")).click();
            WaitUtil.waitFor(3);
            elements = driver.findElements(By.cssSelector("#napgiTerm > option"));
            int napTermSize = elements.size();
            boolean napTermCheck = false;

            for (int i = 0; i < napTermSize; i++) {

                if (elements.get(i).getText().trim().contains("전기납")) {
                    elements.get(i).click();
                    napTermCheck = true;
                    WaitUtil.waitFor(3);

                } else if (elements.get(i).getText().trim().contains(info.napTerm)) {
                    elements.get(i).click();
                    napTermCheck = true;
                    WaitUtil.waitFor(3);
                    break;
                }
            }

            if (!napTermCheck) {
                throw new Exception("납입기간 선택할 수 없음 : " + info.napTerm);
            }

            WaitUtil.waitFor(3);

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException { }



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException { }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try {
            String title = "가입금액";
            String welgramAssureMoney = (String) obj[0];

            logger.info("가입금액 입력");
            int assureMoney = Integer.parseInt(welgramAssureMoney);
            assureMoney = assureMoney / 10000;

            WebElement $input = driver.findElement(By.cssSelector("#order_money0"));

            // 가입금액란이 비활성화되고 납입보험료가 활성화된 상품
            if ($input.getAttribute("class").contains("disabled")) {
                $input = driver.findElement(By.cssSelector("#premium"));
            }

            setTextIntoInputBox($input, String.valueOf(assureMoney));
            setForCompare(title, String.valueOf(assureMoney), $input);

            WaitUtil.waitFor(3);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }



    /**
     * 특약 가입금액, 보기, 납기 설정 메서드
     *
     * @param CrawlingProduct info
     * @throws SetAssureMoneyException
     */
    public void setSubTreatyConditions(Object... obj) throws SetAssureMoneyException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<WebElement> $trList = driver.findElements(By.xpath("//*[@id='elementLists']/tr"));
            WebElement $inputAssureMoney = null;
            WebElement $select = null;
            int unit = 10000;

            // 선택특약이 없을 경우
            if (info.getTreatyList().size() < 2) {
                if (helper.existElement(
                        By.xpath("//tbody[@id='elementLists']//td[contains(.,'선택하신 특약이 없습니다')]"))) {
                    // 보험료 계산 버튼
                    btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));
                }
            } else {
                logger.info("계약사항 선택");
                for (int i = 0; i < info.getTreatyList().size(); i++) {
                    CrawlingTreaty welgramTreaty = info.getTreatyList().get(i);

                    for (WebElement $tr : $trList) {
                        String pageTreatyName = $tr.findElement(By.cssSelector("th label"))
                                .getText();
                        String insTerm = welgramTreaty.insTerm + "만기";
                        String napTerm = welgramTreaty.napTerm + "납";
                        String selectedText = "";

                        if (welgramTreaty.treatyName.equals(pageTreatyName)) {
                            logger.info("선택특약 : {}", pageTreatyName);
                            logger.info("------------------------------------------------------");
                            logger.info("가입금액 입력");
                            $inputAssureMoney = $tr.findElement(
                                    By.cssSelector("td:nth-child(2) input"));
                            setTextIntoInputBox($inputAssureMoney,
                                    String.valueOf(welgramTreaty.assureMoney / unit));

                            WaitUtil.waitFor(1);

                            logger.info("보험기간 선택");
                            $select = $tr.findElement(By.cssSelector("td:nth-child(4) select"));
                            helper.selectByText_check($select, insTerm);

                            WaitUtil.waitFor(1);

                            // 선택사항 확인
                            setForCompare("보험기간", insTerm, $select);

                            logger.info("납입기간 선택");
                            $select = $tr.findElement(By.cssSelector("td:nth-child(5) select"));

                            try {
                                helper.selectByText_check($select, napTerm);

                            } catch (NoSuchElementException e) {
                                // 전기납
                                napTerm = "전기납";
                                helper.selectByText_check($select, napTerm);
                            }

                            WaitUtil.waitFor(1);

                            // 선택사항 확인
                            setForCompare("납입기간", napTerm, $select);

                            break;
                        }
                    }

                    WaitUtil.waitFor(3);
                }
            }
        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException { }



    protected void setTreaties(List<CrawlingTreaty> welgramTreaties) throws CommonCrawlerException {

        try {

            // 특약 체크박스 포함한 li 리스트 불러오기
            List<WebElement> liList = helper.waitPesenceOfAllElementsLocatedBy(
                            By.xpath("//tbody//tr")
                    ).stream().filter(WebElement::isDisplayed)
                    .map(tr -> tr.findElements(By.xpath(".//ul/li")))
                    .flatMap(List::stream).collect(Collectors.toList());

            WaitUtil.waitFor(1);

            // 특약 체크박스 선택하기
            for (CrawlingTreaty welgramTreaty : welgramTreaties) {

                if (welgramTreaty.productGubun.equals(ProductGubun.주계약)) {
                    continue;
                }

                String welgramTreatyName = welgramTreaty.getTreatyName();

                WaitUtil.waitFor(1);

                WebElement matchedLi = liList.stream()
                        .filter(li -> TreatyNameComparators.allApplied.equals(li.getText(),
                                welgramTreatyName))
                        .findFirst()
                        .orElseThrow(
                                () -> new CommonCrawlerException(welgramTreatyName + "를 찾을 수 없습니다."));

                WaitUtil.waitFor(3);

                helper.click(
                        matchedLi.findElement(By.tagName("label"))
                        , welgramTreatyName + " 체크박스 클릭"
                );

                WaitUtil.waitFor(3);
            }

 /*           List<String> planTreaty = new ArrayList<>();        // 가설 특약 리스트
            List<String> savePlanTreaty = new ArrayList<>();    //
            List<String> pageTreaty = new ArrayList<>();        // 원수사 진단, 입원수술통원, 기타 항목 리스트

            logger.info("플랜의 특약 이름넣기");
            for(int i=0; i<welgramTreaties.size(); i++){

                if(i == 0){
                    continue; // 주계약
                }
                savePlanTreaty.add(welgramTreaties.get(i).treatyName.trim());
                planTreaty.add(welgramTreaties.get(i).treatyName.trim());
            }


            logger.info("페이지의 특약이름 넣기");

            logger.info("의무부가특약");
            List<WebElement> pageDutyLi = driver.findElements(By.cssSelector("#duty_list > li"));
            int pageDutyLiSize = pageDutyLi.size();

            for(int i=0; i<pageDutyLiSize; i++){

                if(!pageDutyLi.get(i).getText().trim().equals("")) {
                    logger.info("확인 의무특약 : " + pageDutyLi.get(i).getText().trim());
                    pageTreaty.add(pageDutyLi.get(i).getText().trim());
                }
            }


            logger.info("선택특약");

            List<String> tableList = new ArrayList<>();

            if(helper.existElement(By.cssSelector("#type2_list > li"))) tableList.add("#type2_list > li");
            if(helper.existElement(By.cssSelector("#type3_list > li"))) tableList.add("#type3_list > li");
            if(helper.existElement(By.cssSelector("#type5_list > li"))) tableList.add("#type5_list > li");

            if(tableList.size() == 0) {
                if(helper.existElement(By.cssSelector("#duty_list > li")))
                tableList.add("#duty_list > li");
            }

            for(int list = 0; list<tableList.size(); list++) {

                elements = driver.findElements(By.cssSelector(tableList.get(list)));
                int pageLiSize = elements.size();

                for(int j=0; j<pageLiSize; j++){

                    logger.info("확인 특약 : "+elements.get(j).getText().trim());
                    pageTreaty.add(elements.get(j).getText().trim());
                }
            }


            logger.info("지우기전 사이즈 : "+planTreaty.size());
            logger.info("페이지에 존재하지 않는 특약들 제외하기");
            //planTreaty.removeAll(pageTreaty);

            List<String> removePlanList = new ArrayList<>();

            for(int i=0; i<planTreaty.size(); i++){

                for(int j=0; j<pageTreaty.size(); j++){

                    if(pageTreaty.get(j).contains(planTreaty.get(i))){
                        removePlanList.add(planTreaty.get(i));
                        break;
                    }
                }
            }

            planTreaty.removeAll(removePlanList);

            logger.info("지우고난 후 사이즈 : "+planTreaty.size());


            if(planTreaty.size() > 0){

                String treatyStr = "";
                for (int i = 0; i < planTreaty.size(); i++) {

                    treatyStr += planTreaty.get(i) + "\n";
                }
                throw new Exception("페이지에 존재하지 않는 특약 : "+treatyStr);
            }

            else if(planTreaty.size() == 0){

                logger.info("특약선택");

                for(int i=0; i<tableList.size(); i++){

                    elements = driver.findElements(By.cssSelector(tableList.get(i)));
                    int pageLiSize = elements.size();

                    for(int j=0; j<pageLiSize; j++){

                        for(int z=0; z<savePlanTreaty.size(); z++){

                            if(elements.get(j).getText().trim().contains(savePlanTreaty.get(z))) {

                                if (elements.get(j).getText().trim().contains("만기형")) {
                                    //if (elements.get(j).getText().trim().contains(info.insTerm)) {

                                    logger.info("확인 특약 : " + elements.get(j).getText().trim() + " / " + savePlanTreaty.get(z));
                                    elements.get(j).findElement(By.cssSelector("input")).click();
                                    WaitUtil.waitFor(1);
                                    break;
                                    //}
                                } else {
                                    logger.info("확인 특약 : " + elements.get(j).getText().trim() + " / " + savePlanTreaty.get(z));
                                    elements.get(j).findElement(By.cssSelector("input")).click();
                                    WaitUtil.waitFor(1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }*/

            //가입설계 특약정보와 원수사 특약정보 비교
//            logger.info("가입하는 특약은 총 {}개입니다.", targetTreaties.size());
//
//            boolean result = compareTreaties(targetTreaties, welgramTreaties);
//
//            if (result) {
//                logger.info("특약 정보 모두 일치 ^^");
//            } else {
//                throw new Exception("특약 불일치");
//            }
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            logger.info("월보험료 스크랩");
            String saveAssureMoney = driver.findElement(
                    By.cssSelector("#direct_total > strong > strong.pColor04")).getText();
            saveAssureMoney = refineMoneyStr(saveAssureMoney);

            info.treatyList.get(0).monthlyPremium = saveAssureMoney;
            logger.info("월보험료 : " + info.treatyList.get(0).monthlyPremium);

            if (info.getCategoryName().equals("태아보험")) {
                String nestMoney = driver.findElement(
                        By.cssSelector("#direct_total > strong > strong.pColor01")).getText();

                info.nextMoney = refineMoneyStr(nestMoney);
                logger.info("계속보험료 :" + info.nextMoney);
            }

            if ("0".equals(saveAssureMoney)) {
                throw new Exception("주계약 보험료는 0원일 수 없습니다.");

            } else {
//                logger.info("보험료 : {}원", saveAssureMoney);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }



    @NotNull
    private static String refineMoneyStr(String moneyStr) {

        if (moneyStr.contains("만원")) {
            moneyStr = moneyStr.replaceAll("[^0-9]", "") + "0000";
        } else {
            moneyStr = moneyStr.replaceAll("[^0-9]", "");
        }

        return moneyStr;
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")).click();
            WaitUtil.waitFor(5);

            //최저
            elements = driver.findElements(
                    By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
            int size = elements.size();

            for (int i = 0; i < size; i++) {

                String term = elements.get(i).findElements(By.tagName("th")).get(0)
                        .getAttribute("innerText");               // 경과기간
                String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1)
                        .getAttribute("innerText");         // 납입보험료

                //현재공시
                String returnMoney = elements.get(i).findElements(By.tagName("td")).get(2)
                        .getAttribute("innerText");        // 현재해약환급금
                String returnRate = elements.get(i).findElements(By.tagName("td")).get(3)
                        .getAttribute("innerText");         // 현재해약환급률

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);

                logger.info("|--현재해약환급금: {}", returnMoney);
                logger.info("|--현재해약환급률: {}", returnRate);

                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term); // 경과기간
                planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계

                planReturnMoney.setReturnMoney(returnMoney); // 현재환급금
                planReturnMoney.setReturnRate(returnRate); // 현재환급률

                planReturnMoneyList.add(planReturnMoney);

            }
            info.planReturnMoneyList = planReturnMoneyList;

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    public void crawlReturnMoneyList2(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a:nth-child(2)")).click();
            WaitUtil.waitFor(5);

            //최저
            elements = driver.findElements(
                    By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
            int size = elements.size();

            for (int i = 0; i < size; i++) {

                String term = elements.get(i).findElements(By.tagName("th")).get(0)
                        .getAttribute("innerText");               // 경과기간
                String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1)
                        .getAttribute("innerText");         // 납입보험료

                //현재공시
                String returnMoney = elements.get(i).findElements(By.tagName("td")).get(4)
                        .getAttribute("innerText");        // 현재해약환급금
                String returnRate = elements.get(i).findElements(By.tagName("td")).get(5)
                        .getAttribute("innerText");         // 현재해약환급률

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);

                logger.info("|--현재해약환급금: {}", returnMoney);
                logger.info("|--현재해약환급률: {}", returnRate);

                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term); // 경과기간
                planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계

                planReturnMoney.setReturnMoney(returnMoney); // 현재환급금
                planReturnMoney.setReturnRate(returnRate); // 현재환급률

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney;
            }
            info.planReturnMoneyList = planReturnMoneyList;

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    // 최저/공시 이율 해약환급금
    public void crawlReturnMoneyListAll(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")).click();
            WaitUtil.waitFor(5);

            elements = driver.findElements(By.xpath("//*[@id=\"refund_result\"]/div[2]/table/tbody/tr"));
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term = "";
                String premiumSum = "";
                String returnMoneyMin = "";
                String returnRateMin = "";
                String returnMoneyAvg = "";
                String returnRateAvg = "";
                String returnMoney = "";
                String returnRate = "";

                term = tr.findElements(By.tagName("th")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(1).getText();

                returnMoneyMin = tr.findElements(By.tagName("td")).get(3).getText();
                returnRateMin = tr.findElements(By.tagName("td")).get(4).getText();

                returnMoneyAvg = tr.findElements(By.tagName("td")).get(6).getText();
                returnRateAvg = tr.findElements(By.tagName("td")).get(7).getText();

                returnMoney = tr.findElements(By.tagName("td")).get(9).getText();
                returnRate = tr.findElements(By.tagName("td")).get(10).getText();

                logger.info("경과기간   :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("환급률    :: {}", returnRate);
                logger.info("최저해약환급금 :: {}", returnMoneyMin);
                logger.info("최저해약환급률 :: {}", returnRateMin);
                logger.info("평균해약환급금 :: {}", returnMoneyAvg);
                logger.info("평균해약환급률 :: {}", returnRateAvg);
                logger.info("=================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                planReturnMoneyList.add(planReturnMoney);

                // 기본 해약환급금 세팅
                info.returnPremium = tr.findElements(By.tagName("td")).get(9).getText()
                        .replace(",", "");
//                info.returnPremium = "-1";
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int planCalcAge = info.getCategoryName().equals("태아보험") ? 0
                    : Integer.parseInt(info.age.replaceAll("\\D", ""));

            // 수집한 중도해약환급금 목록
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

            Optional<PlanReturnMoney> returnMoneyOptional = Optional.empty();
            for (int i = planReturnMoneyList.size() - 1; i > 0; i--) {

                PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);

                // termTxt: planReturnMoney 경과기간
                String termTxt = planReturnMoney.getTerm();

                // 경과기간이 개월단위인 경우는 일단 제외 // todo 개월단위도 포함하도록 수정
                if (termTxt.contains("개월")) {
                    continue;
                }

                // 나이로된 경과기간, 년으로 된 경과기간 추출
                String termUnit = termTxt.indexOf("년") > termTxt.indexOf("세") ? "년" : "세";
                int termUnitIndex = termTxt.indexOf(termUnit);
                int termNumberValue = Integer.parseInt(
                        termTxt.substring(0, termUnitIndex).replaceAll("\\D", ""));
                int termYear = -1;
                int termAge = -1;
                switch (termUnit) {
                    case "년":
                        termYear = termNumberValue;
                        termAge = planCalcAge + termYear;
                        break;

                    case "세":
                        termYear = termNumberValue - planCalcAge;
                        termAge = termNumberValue;
                        break;
                }

                // 해당 가설(info)의 보험기간 단위 추출 (세 or 년), 숫자 추출
                String insTermUnit = "";
                int insTermNumberValue = -1;
                if (info.categoryName.contains("종신")) {
                    String napTermUnit = info.napTerm.replaceAll("[0-9]", "");
                    int napTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));
                    switch (napTermUnit) {
                        case "년":
                            insTermNumberValue = napTerm + 10;
                            break;

                        case "세":
                            insTermNumberValue = planCalcAge + napTerm;
                    }
                    insTermUnit = "년";

                } else if (info.categoryName.contains("연금")) { // 연금보험, 연금저축보험
                    insTermUnit = "세"; // 환급금 크롤링 시점은 개시나이
                    insTermNumberValue = Integer.parseInt(info.annuityAge.replaceAll("[^0-9]", ""));

                } else {
                    insTermUnit = info.insTerm.replaceAll("[0-9]", "");
                    insTermNumberValue = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));
                }

                // 보험기간 단위에 따라 비교: 경과기간이 만기에 해당하는지 여부 반환
                if ((insTermUnit.equals("세") && termAge == insTermNumberValue)
                        || (insTermUnit.equals("년") && termYear == insTermNumberValue)) {

                    logger.info("만기환급금 크롤링 :: 카테고리 :: {}", info.categoryName);
                    logger.info("만기환급금 크롤링 :: 가설 케이스 나이 :: {}세", planCalcAge);
                    logger.info("만기환급금 크롤링 :: 가설 보험기간 :: {}", info.insTerm);
                    logger.info("만기환급금 크롤링 :: 가설 납입기간 :: {}", info.napTerm);
                    logger.info("만기환급금 크롤링 :: 해약환급금 해당 경과기간 :: {}", planReturnMoney.getTerm());

                    returnMoneyOptional = Optional.of(planReturnMoney);
                }
            }

            if (returnMoneyOptional.isPresent()) {
                info.returnPremium = returnMoneyOptional.get().getReturnMoney();
            } else {
                info.returnPremium = "-1"; // 만기에 해당하는 중도해약환급금이 없을 경우
            }

            logger.info("만기환급금 크롤링 :: 만기환급금 :: {}", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException { }



    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException { }



    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException { }



    @Override
    public void setUserName(Object... obj) throws SetUserNameException { }



    @Override
    public void setDueDate(Object... obj) throws SetDueDateException { }



    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException { }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try {
            String welgramPlan = (String) obj[0];

            logger.info("보험유형 선택 :: {}", welgramPlan);
            helper.selectByText_check(By.xpath("//select[@title='보험유형']"), welgramPlan);

            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetProductTypeException(e);
        }
    }



    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException { }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException { }



    public void setTextIntoInputBox(WebElement $inputEl, String text) {

        $inputEl.click();
        $inputEl.clear();
        $inputEl.sendKeys(String.valueOf(text));
    }



    /*
     * 버튼 클릭 메서드(By로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(By element) throws Exception {

        driver.findElement(element).click();
        WaitUtil.loading(2);
    }



    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element) throws Exception {

        element.click();
        WaitUtil.loading(2);
    }



    /*
     * 계산방법선택
     * 간편보험료 / 표준보험료
     */
    protected void calculateType() throws CommonCrawlerException {

        try {
            helper.click(By.xpath("//a[@id='next_btn_text']"));
            helper.waitForLoading();
            WaitUtil.waitFor(2);
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    protected void calculate() throws CommonCrawlerException {

        try {
            helper.click(By.xpath("//a[@id='next_btn_text']"));
            helper.waitForLoading();
            WaitUtil.waitFor(3);
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    /**
     * printAndCompare 사용을 위한 정보 세팅
     *
     * @param1 title       선택값 확인 대상
     * @param2 targetEl    선택값 확인 대상 엘리먼트
     * @param1 setValue    선택하고자 한 값
     */
    protected void setForCompare(String title, String setValue, WebElement targetEl) throws Exception {

        String script = "return $(arguments[0]).val();";
        String targetValue = String.valueOf(helper.executeJavascript(script, targetEl));

        logger.info("입력된 {} 조회", title);

        if (targetEl.getTagName().equals("select")) {
            script = "return $(arguments[0]).find('option:selected').text();";
            targetValue = String.valueOf(helper.executeJavascript(script, targetEl));
        }
        logger.info("{} 비교", title);
        printAndCompare(title, setValue, targetValue);
    }



    protected void printAndCompare(String title, String welgramData, String targetData) throws Exception {

        //가입설계 정보와 원수사 정보 출력
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if (!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
        }
    }



    public void moveToElement(WebElement element) throws Exception {

        helper.moveToElementByJavascriptExecutor(element);
        WaitUtil.waitFor(1);

        //      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));
    }



    // 전환나이 선택
    public void setTranceAge(String age) throws CommonCrawlerException {

        try {
            By ageElement = By.id("tranage");
            String title = "전환나이";

            helper.waitElementToBeClickable(ageElement);
            helper.selectByText_check(ageElement, age);

            setForCompare(title, age, driver.findElement(ageElement));

        } catch (Exception e) {
            throw new CommonCrawlerException("전환나이 오류가 발생했습니다.\n" + e.getMessage());
        }
    }



    protected void setVariableTreatyAssureMoney(CrawlingProduct info) throws SetTreatyException {

        int monthlyPremium = Integer.parseInt(info.getTreatyList().get(0).monthlyPremium);
        int napTerm = Integer.parseInt(info.napTerm.replaceAll("\\D", ""));

        // 유동특약
        Optional<CrawlingTreaty> variableTreatyOpt = info.getTreatyList().stream()
                .filter(
                        t -> t.treatyName.contains("일시생활자금"))
                .findFirst();

        if (variableTreatyOpt.isPresent()) {

            CrawlingTreaty t = variableTreatyOpt.get();

            // 일시생활자금 특약 -> 가입금액이 가입설계 보험료 기반해서 계산하는 특약
            t.assureMoney = monthlyPremium * 12 * napTerm;

            logger.info("유동특약명 - {}", t.treatyName);
            logger.info("유동특약 가입금액 - {}", t.assureMoney);

            // 유동특약 가입금액 세팅
            ScrapableNew.setVariableAssureMoneyTreaty(t);
        }
    }
}