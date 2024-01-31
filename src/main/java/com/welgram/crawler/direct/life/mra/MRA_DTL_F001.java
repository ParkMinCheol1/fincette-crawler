package com.welgram.crawler.direct.life.mra;


import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MRA_DTL_F001 extends CrawlingMRAAnnounce {

    public static void main(String[] args) {
        executeCommand(new MRA_DTL_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("공시실 상품명 찾기");
        findProductName(info.getProductNamePublic());

        logger.info("고객정보 입력");
        setUserInfo(info);

        logger.info("가입조건 입력");
        setJoinCondition(info);

        logger.info("주계약 정보 설정");
        setMainTreatyInfo(info);

        logger.info("특약 설정");
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.선택특약)
            .collect(Collectors.toList());
        setTreaties(subTreatyList);

        logger.info("보험료 계산하기 버튼 클릭");
        WebElement $button = driver.findElement(By.id("btnCalc"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

//        logger.info("해약환급금 크롤링");

        return true;
    }

}





//package com.welgram.crawler.direct.life.mra;
//
//import com.welgram.common.WaitUtil;
//import com.welgram.crawler.direct.life.CrawlingMRA;
//import com.welgram.crawler.general.CrawlingProduct;
//import com.welgram.crawler.general.CrawlingProduct.DisCount;
//import com.welgram.crawler.general.CrawlingTreaty;
//import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
//import java.util.ArrayList;
//import java.util.List;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.WebElement;
//
//public class MRA_DTL_F001 extends CrawlingMRA {
//
//    public static void main(String[] args) {
//        executeCommand(new MRA_DTL_F001(), args);
//    }
//
//
//    @Override
//    protected boolean scrap(CrawlingProduct info) throws Exception {
//        crawlFromAnnouncePage(info);
//
//        return true;
//    }
//
//
//    //공시실용 크롤링
//    private void crawlFromAnnouncePage(CrawlingProduct info) throws Exception {
//        driver.manage().window().maximize();
//        waitAnnouncePageLoadingBar();
//
//
//        logger.info("공시실 상품명 : {} 클릭", info.productName);
//        element = driver.findElement(By.xpath("//span[text()='" + info.productName+ "']/parent::a"));
//        waitElementToBeClickable(element).click();
//        WaitUtil.waitFor(3);
//
//
//        logger.info("생년월일 설정 : {}", info.birth);
//        setTextToInputBox(By.id("txtI1Jumin1"), info.birth);
//
//
//        logger.info("성별 설정");
//        setAnnounceGender(info.gender);
//
//
//        logger.info("위험등급 설정 : 비위험(고정)");
//        selectOptionByText(By.id("cboI1RiskGcd"), "비위험");
//
//
//        logger.info("납입주기 설정");
//        setAnnounceNapCycle(info.getNapCycleName());
//
//
//        logger.info("비흡연 할인 설정");
//        setSmoke(info.discount);
//
//
//        logger.info("주계약 가입조건 설정");
//        setMainTreatyInfo(info);
//
//
//        //TODO iframe이 잘 안먹혀서 코드 수정 필요. 추후에 특약 가입금액 세팅 및 비교 코드 추가하기
//        logger.info("특약 가입조건 설정");
//        setSubTreatyInfo(info);
//
//
//        logger.info("iframe 창에서 기존 창으로 전환");
//        driver.switchTo().defaultContent();
//
//
//        logger.info("보험료계산하기 버튼 클릭");
//        element = driver.findElement(By.id("btnCalc"));
//        moveToElementByJavascriptExecutor(element);
//        helper.waitElementToBeClickable(element).click();
//        waitAnnouncePageLoadingBar();
//
//
//        logger.info("주계약 보험료 설정");
//        setAnnounceMonthlyPremium(info.treatyList.get(0));
//
//
//        logger.info("스크린샷 찍기");
//        takeScreenShot(info);
//
//
//
//
//
//    }
//
//
//    private void setSmoke(DisCount smoke) throws Exception{
//        String smokeType = "";
//
//        if(smoke == DisCount.일반) {
//            smokeType = "notSmokDcRqynNo";
//        } else if(smoke == DisCount.비흡연) {
//            smokeType = "notSmokDcRqynYes";
//        }
//
//        logger.info("비흡연할인 설정");
//        element = driver.findElement(By.xpath("//label[@for='" + smokeType + "']"));
//        waitElementToBeClickable(element).click();
//
//
//        String script = "return $('input[name=notSmokDcRqyn]:checked').attr('id');";
//        String targetSmoke = String.valueOf(executeJavascript(script));
//
//
//        if(smokeType.equals(targetSmoke)) {
//            logger.info("가입설계 흡연여부 == 홈페이지 흡연여부");
//        } else {
//            throw new Exception("흡연여부 불일치");
//        }
//
//    }
//
//
//    private void setMainTreatyInfo(CrawlingProduct info) throws Exception{
//        logger.info("주보험 설정");
//        setAnnouncePlanType(info.textType);
//
//
//        logger.info("주계약 가입금액 설정");
//        setAnnounceAssureMoney(info.treatyList.get(0).assureMoney);
//
//
//        logger.info("주계약 보험기간 설정");
//        setAnnounceInsTerm(info.insTerm);
//
//
//        logger.info("주계약 납입기간 설정");
//        String napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
//        setAnnounceNapTerm(napTerm);
//    }
//
//
//
//
//    private void setSubTreatyInfo(CrawlingProduct info) throws Exception {
//        boolean existSubTreaty = false;             //가입설계중 선택특약 존재 여부
//
//        List<CrawlingTreaty> subTreaties = new ArrayList<>();
//        for(CrawlingTreaty treaty : info.treatyList) {
//            if(ProductGubun.선택특약.equals(treaty.productGubun)) {
//                existSubTreaty = true;
//                subTreaties.add(treaty);
//                break;
//            }
//        }
//
//
//
//        //선택특약이 존재할 때만
//        if(existSubTreaty) {
//            logger.info("특약선택 버튼 클릭");
//            element = driver.findElement(By.id("btnSpecialContract"));
//            waitElementToBeClickable(element).click();
//            waitAnnouncePageLoadingBar();
//            WaitUtil.waitFor(2);
//
//
//            logger.info("특약 iframe 창 전환");
//            driver.switchTo().frame("ifrmPopSpecialContract");
//
//
//            logger.info("특약 선택하기");
//            for(CrawlingTreaty treaty : subTreaties) {
//
//                WebElement label = driver.findElement(By.cssSelector("#viewScrtTgtDcdI tbody tr label"));
//                String id = label.getAttribute("for");
//                WebElement input = driver.findElement(By.id(id));
//
//                if(input.isEnabled() && !input.isSelected()) {
//                    String script = "arguments[0].click();";
//                    executeJavascript(script, input);
//                }
//            }
//
//
//            logger.info("확인 버튼 클릭");
//            element = driver.findElement(By.cssSelector("div.btn-center a"));
//            waitElementToBeClickable(element).click();
//            waitAnnouncePageLoadingBar();
//            WaitUtil.waitFor(2);
//
//        }
//
//    }
//
//
//
//
//    //공시실 주계약 보험료 설정
//    private void setAnnounceMonthlyPremium(CrawlingTreaty mainTreaty) throws Exception {
//        WebElement input = driver.findElement(By.id("spTotDcPrm"));
//        String script = "return $(arguments[0]).val();";
//        String monthlyPremium = String.valueOf(executeJavascript(script, input)).replaceAll("[^0-9]", "");
//
//        mainTreaty.monthlyPremium = monthlyPremium;
//
//        if("0".equals(mainTreaty.monthlyPremium)) {
//            throw new Exception("주계약 보험료 설정을 필수입니다.");
//        } else {
//            logger.info("보험료 : {}원", mainTreaty.monthlyPremium);
//        }
//    }
//
//
//
//    private void setAnnounceGender(int gender) throws Exception {
//        String genderText = (gender == MALE) ? "남" : "여";
//
//        WebElement label = driver.findElement(By.xpath("//tr[@id='spI1']//label[text()='" + genderText + "']"));
//        waitElementToBeClickable(label).click();
//
//
//        //맞게 클릭됐는지 검사
//        String script = "return $('input[name=\"rdoI1GndrCd\"]:checked').attr('id');";
//        String checkedGenderId = executeJavascript(script).toString();
//        String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedGenderId + "']")).getText();
//
//
//        logger.info("=====================================");
//        logger.info("가입설계 성별 : {}", genderText);
//        logger.info("홈페이지 클릭된 성별 : {}", checkedGender);
//        logger.info("=====================================");
//
//        if(genderText.equals(checkedGender)) {
//            logger.info("가입설계 성별 : {} == 홈페이지 클릭된 성별 : {}", genderText, checkedGender);
//        } else {
//            logger.info("가입설계 성별 : {} ≠ 홈페이지 클릭된 성별 : {}", genderText, checkedGender);
//            throw new Exception("성별 불일치");
//        }
//
//    }
//
//
//
//
//
//    //공시실 납입주기 설정 메서드
//    private void setAnnounceNapCycle(String napCycle) throws Exception {
//        //납입주기 설정
//        selectOptionByText(By.id("selFNCMA024List"), napCycle);
//
//
//        //맞게 클릭됐는지 검사
//        String script = "return $('#selFNCMA024List option:selected').text();";
//        String checkedNapCycle = executeJavascript(script).toString();
//
//        logger.info("======================================================");
//        logger.info("가입설계 납입주기 : {}", napCycle);
//        logger.info("홈페이지 클릭된 납입주기 : {}", checkedNapCycle);
//        logger.info("======================================================");
//
//        if(napCycle.equals(checkedNapCycle)) {
//            logger.info("가입설계 납입주기 : {} == 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
//        } else {
//            logger.info("가입설계 납입주기 : {} ≠ 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
//            throw new Exception("납입주기 불일치");
//        }
//    }
//
//
//
//    //공시실 판매플랜 설정 메서드
//    private void setAnnouncePlanType(String planType) throws Exception {
//        //판매플랜 설정
//        selectOptionByText(By.id("selFNCMA025List"), planType);
//
//
//        //맞게 클릭됐는지 검사
//        String script = "return $('#selFNCMA025List option:selected').text();";
//        String checkedPlanType = executeJavascript(script).toString();
//
//        logger.info("======================================================");
//        logger.info("가입설계 주보험 : {}", planType);
//        logger.info("홈페이지 클릭된 주보험 : {}", checkedPlanType);
//        logger.info("======================================================");
//
//        if(planType.equals(checkedPlanType)) {
//            logger.info("가입설계 주보험 : {} == 홈페이지 클릭된 주보험 : {}", planType, checkedPlanType);
//        } else {
//            logger.info("가입설계 주보험 : {} ≠ 홈페이지 클릭된 주보험 : {}", planType, checkedPlanType);
//            throw new Exception("주보험 불일치");
//        }
//    }
//
//
//
//
//    private void setAnnounceAssureMoney(int assureMoney) throws Exception {
//        String unit = driver.findElement(By.id("spNtryUnit")).getText().trim();
//        int unitNum = 1;
//
//        if("억원".equals(unit)) {
//            unitNum = 100000000;
//        } else if("천만원".equals(unit)) {
//            unitNum = 10000000;
//        } else if("백만원".equals(unit)) {
//            unitNum = 1000000;
//        } else if("십만원".equals(unit)) {
//            unitNum = 100000;
//        } else if("만원".equals(unit)) {
//            unitNum = 10000;
//        }
//
//        assureMoney = assureMoney / unitNum;
//
//        logger.info("가입금액 : {}{}", assureMoney, unit);
//        setTextToInputBox(By.id("applyMoney"), String.valueOf(assureMoney));
//
//
//        WebElement input = driver.findElement(By.id("applyMoney"));
//        input.click();
//        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
//        input.sendKeys(String.valueOf(assureMoney));
//    }
//
//
//
//
//    //공시실 보험기간 설정
//    protected void setAnnounceInsTerm(String insTerm) throws Exception {
//        insTerm = insTerm + "만기";
//
//        //보험기간 클릭
//        WebElement select = driver.findElement(By.id("selFNCMA022List"));
//        selectOptionByText(select, insTerm);
//
//
//        //맞게 클릭됐는지 검사
//        String script = "return $('#selFNCMA022List option:selected').text();";
//        String checkedInsTerm = executeJavascript(script).toString();
//
//        logger.info("=========================================");
//        logger.info("가입설계 보험기간 : {}", insTerm);
//        logger.info("홈페이지 클릭된 보험기간 : {}", checkedInsTerm);
//        logger.info("=========================================");
//
//        if(insTerm.equals(checkedInsTerm)) {
//            logger.info("가입설계 보험기간 : {} == 홈페이지 클릭된 보험기간 : {}", insTerm, checkedInsTerm);
//        } else {
//            logger.info("가입설계 보험기간 : {} ≠ 홈페이지 클릭된 보험기간 : {}", insTerm, checkedInsTerm);
//            throw new Exception("보험기간 불일치");
//        }
//    }
//
//
//    //공시실 납입기간 설정
//    protected void setAnnounceNapTerm(String napTerm) throws Exception {
//        napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";
//
//        //납입기간 클릭
//        WebElement select = driver.findElement(By.id("selFNCMA023List"));
//        selectOptionByText(select, napTerm);
//
//
//        //맞게 클릭됐는지 검사
//        String script = "return $('#selFNCMA023List option:selected').text();";
//        String checkedNapTerm = executeJavascript(script).toString();
//
//        logger.info("=========================================");
//        logger.info("가입설계 납입기간 : {}", napTerm);
//        logger.info("홈페이지 클릭된 납입기간 : {}", checkedNapTerm);
//        logger.info("=========================================");
//
//        if(napTerm.equals(checkedNapTerm)) {
//            logger.info("가입설계 납입기간 : {} == 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
//        } else {
//            logger.info("가입설계 납입기간 : {} ≠ 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
//            throw new Exception("납입기간 불일치");
//        }
//    }
//
//
//
//
////    //해약환급금 조회 메서드
////    @Override
////    protected void getReturnPremiums(CrawlingProduct info) throws Exception{
////        int startPage = 2;
////        logger.info("{}페이지로 이동", startPage);
////        setTextToInputBox(By.xpath("//input[@class='report_menu_pageCount_input']"), String.valueOf(startPage));
////        WaitUtil.waitFor(2);
////
////
////        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='report_paint_div']//*[name()='svg']//*[name()='g']//*[name()='g'][last()]/*"));
////        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
////        PlanReturnMoney lastPlanReturnMoney = null;
////        LinkedHashSet<Integer> baseLineXSet = new LinkedHashSet<Integer>();
////        List<Integer> baseLineXList = null;
////        int lineY = 0;                      //line태그의 y2 좌표값
////        List<String> strList = new ArrayList<>();
////        String currentTagName = "";
////        String tmp = "";
////
////        int idx = 0;
////        int size = elements.size();
////        while (idx < size) {
////            WebElement element = elements.get(idx);
////
////            String elementTagName = element.getTagName();
////
////            //line -> text 태그로 전환될 때
////            if("line".equals(currentTagName) && "text".equals(elementTagName)) {
////                baseLineXList = new ArrayList<>(baseLineXSet);
////            }
////
////            //text -> line 태그로 전환될 때
////            if("text".equals(currentTagName) && "line".equals(elementTagName)) {
////                strList.add(tmp);
////                tmp = "";
////                baseLineXList.clear();
////                lineY = 0;
////                baseLineXSet.clear();
////
////                String term = strList.get(0);
////                String premiumSum = strList.get(2);
////                String returnMoney = strList.get(3);
////                String returnRate = strList.get(4);
////
////                returnMoney = String.valueOf(Integer.parseInt(returnMoney.replaceAll("[^0-9]", "") + "0000"));
////
////                if(!term.contains("개월") && !term.contains("만기")) {
////                    term = term + "년";
////                }
////
////                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
////                planReturnMoney.setTerm(term);
////                planReturnMoney.setPremiumSum(premiumSum);
////                planReturnMoney.setReturnMoney(returnMoney);
////                planReturnMoney.setReturnRate(returnRate);
////
////                logger.info("***해약환급금***");
////                logger.info("|--경과기간: {}", term);
////                logger.info("|--납입보험료: {}", premiumSum);
////                logger.info("|--해약환급금: {}", returnMoney);
////                logger.info("|--환급률: {}", returnRate + "\n");
////
////                planReturnMoneyList.add(planReturnMoney);
////
////                strList.clear();
////            }
////
////            //현재 페이지의 해약환급금 표의 끝에 다다랐을 때
////            if("image".equals(currentTagName)) {
////                //현재까지 쌓인 마지막 해약환급금의 경과년도가 만기가 아닌 경우 다음 페이지로 이동
////                lastPlanReturnMoney = planReturnMoneyList.get(planReturnMoneyList.size() - 1);
////
////                if(!lastPlanReturnMoney.getTerm().contains("만기")) {
////                    startPage++;
////                    logger.info("{}페이지로 이동", startPage);
////                    setTextToInputBox(By.xpath("//input[@class='report_menu_pageCount_input']"), String.valueOf(startPage));
////                    WaitUtil.waitFor(2);
////
////                    elements = driver.findElements(By.xpath("//div[@class='report_paint_div']//*[name()='svg']//*[name()='g']//*[name()='g'][last()]/*"));
////
////                    //변수 초기화
////                    baseLineXSet.clear();
////                    baseLineXList.clear();
////                    lineY = 0;
////                    strList.clear();
////                    currentTagName = "";
////                    tmp = "";
////                    idx = 0;
////                    size = elements.size();
////
////                    continue;
////                } else {
////                    break;
////                }
////            }
////
////            currentTagName = elementTagName;
////
////            if("line".equals(currentTagName)) {
////                int x1 = Integer.parseInt(element.getAttribute("x1"));
////                int x2 = Integer.parseInt(element.getAttribute("x2"));
////                int y2 = Integer.parseInt(element.getAttribute("y2"));
////
////                if(x1 != x2) {
////                    baseLineXSet.add(x1);
////                    baseLineXSet.add(x2);
////                } else {
////                    lineY = y2;
////                }
////            }
////
////            if("text".equals(currentTagName)) {
////                int currentX = Integer.parseInt(element.getAttribute("x"));
////                int currentY = Integer.parseInt(element.getAttribute("y"));
////
////                if(Math.abs(currentY - lineY) > 200) {
////                    idx++;
////                    continue;
////                }
////
////                if(currentX > baseLineXList.get(1)) {
////                    baseLineXList.remove(0);
////                    strList.add(tmp);
////                    tmp = "";
////                }
////
////                if(currentX > baseLineXList.get(0) && currentX < baseLineXList.get(1)) {
////                    tmp += element.getText();
////                }
////            }
////
////            idx++;
////        }
////
////        info.planReturnMoneyList = planReturnMoneyList;
////        info.returnPremium = lastPlanReturnMoney.getReturnMoney();
////    }
////
////
////    //생년월일 설정 메서드
////    @Override
////    protected void setBirth(String birth) {
////        helper.waitPresenceOfElementLocated(By.id("txtI1Jumin1"));
////        setTextToInputBox(By.id("txtI1Jumin1"), birth);
////    }
////
////
////    //성별 설정 메서드
////    @Override
////    protected void setGender(int gender) throws Exception{
////        String genderId = (gender == MALE) ? "rdoI1GndrCd_male" : "rdoI1GndrCd_female";
////
////        helper.waitElementToBeClickable(By.xpath("//label[@for='" + genderId + "']")).click();
////
////        //실제 홈페이지에서 클릭된 성별 확인
////        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='rdoI1GndrCd']:checked\").attr('id')").toString();
////        String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
////        logger.info("클릭된 성별 : {}", checkedGender);
////
////        if(!checkedElId.equals(genderId)) {
////            logger.error("가입설계 성별 : {}", (gender == MALE) ? "남" : "여");
////            logger.error("홈페이지에서 클릭된 성별 : {}", checkedGender);
////            throw new GenderMismatchException("성별 불일치");
////        }
////    }
////
////    //건강인우대 설정 메서드
////    private void setHealthType(String healthType) throws Exception {
////        helper.waitElementToBeClickable(By.xpath("//span[@id='spChkXclbApptYn_I1']//label[text()='" + healthType + "']")).click();
////
////        //실제 홈페이지에서 클릭된 건강인우대 확인
////        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='rdoXclbApptYn_I1']:checked\").attr('id')").toString();
////        String checkedHealthType = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
////        logger.info("클릭된 건강인우대 : {}", checkedHealthType);
////
////        if(!checkedHealthType.equals(healthType)) {
////            logger.error("가입설계 건강인우대 타입 : {}", healthType);
////            logger.error("홈페이지에서 클릭된 건강인우대 타입 : {}", checkedHealthType);
////            throw new Exception("건강인우대 타입 불일치");
////        }
////    }
////
////
////    //위험등급 설정 메서드
////    private void setDangerRank(String dangerRank) throws Exception {
////        selectOption(By.id("cboI1RiskGcd"), dangerRank);
////
////        //실제 홈페이지에서 클릭된 위험등급 확인
////        String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#cboI1RiskGcd option:selected\").text()").toString();
////        logger.info("클릭된 위험등급 : {}", selectedOptionText);
////
////        if(!selectedOptionText.equals(dangerRank)) {
////            logger.error("가입설계 위험등급 : {}", dangerRank);
////            logger.error("홈페이지에서 클릭된 위험등급 : {}", selectedOptionText);
////            throw new Exception("위험등급 불일치");
////        }
////    }
////
////
////    //납입주기 설정 메서드
////    @Override
////    protected void setNapCycle(String napCycle) throws Exception{
////        selectOption(By.id("selFNCMA024List"), napCycle);
////
////        //실제 홈페이지에서 클릭된 납입주기 확인
////        String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#selFNCMA024List option:selected\").text()").toString();
////        logger.info("클릭된 납입주기 : {}", selectedOptionText);
////
////        if(!selectedOptionText.equals(napCycle)) {
////            logger.error("가입설계 납입주기 : {}", napCycle);
////            logger.error("홈페이지에서 클릭된 납입주기 : {}", selectedOptionText);
////            throw new NapCycleMismatchException("납입주기 불일치");
////        }
////    }
////
////    //주보험 유형 설정 메서드
////    protected void setPlanType(String planType) throws Exception{
////        selectOptionContainsText(By.id("selFNCMA025List"), planType);
////
////        //실제 홈페이지에서 클릭된 납입주기 확인
////        String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#selFNCMA025List option:selected\").text()").toString();
////        logger.info("클릭된 주보험 유형 : {}", selectedOptionText);
////
////        if(!selectedOptionText.contains(planType)) {
////            logger.error("가입설계 주보험 유형 : {}", planType);
////            logger.error("홈페이지에서 클릭된 주보험 유형 : {}", selectedOptionText);
////            throw new Exception("주보험 유형 불일치");
////        }
////    }
////
////    //가입금액 설정 메서드
////    @Override
////    protected void setAssureMoney(String assureMoney) throws Exception{
////        String unit = driver.findElement(By.id("spNtryUnit")).getText().trim();
////
////        if("억원".equals(unit)) {
////            unit = "100000000";
////        } else if("천만원".equals(unit)) {
////            unit = "10000000";
////        } else if("백만원".equals(unit)) {
////            unit = "1000000";
////        } else if("십만원".equals(unit)) {
////            unit = "100000";
////        } else if("만원".equals(unit)) {
////            unit = "10000";
////        } else if("천원".equals(unit)) {
////            unit = "1000";
////        } else if("백원".equals(unit)) {
////            unit = "100";
////        } else if("십원".equals(unit)) {
////            unit = "10";
////        } else if("원".equals(unit)) {
////            unit = "1";
////        }
////
////        assureMoney = String.valueOf(Integer.parseInt(assureMoney) / Integer.parseInt(unit));
////        setTextToInputBox(By.id("applyMoney"), assureMoney);
////    }
////
////
////    //보험기간 설정 메서드
////    @Override
////    protected void setInsTerm(String insTerm) throws Exception{
////        selectOptionContainsText(By.id("selFNCMA022List"), insTerm);
////
////        //실제 홈페이지에서 클릭된 보험기간 확인
////        String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#selFNCMA022List option:selected\").text()").toString();
////        logger.info("클릭된 보험기간 유형 : {}", selectedOptionText);
////
////        if(!selectedOptionText.contains(insTerm)) {
////            logger.error("가입설계 보험기간 유형 : {}", insTerm);
////            logger.error("홈페이지에서 클릭된 보험기간 유형 : {}", selectedOptionText);
////            throw new InsTermMismatchException("보험기간 불일치");
////        }
////    }
////
////
////    //납입기간 설정 메서드
////    @Override
////    protected void setNapTerm(String napTerm) throws Exception{
////        selectOptionContainsText(By.id("selFNCMA023List"), napTerm);
////
////        //실제 홈페이지에서 클릭된 납입기간 확인
////        String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#selFNCMA023List option:selected\").text()").toString();
////        logger.info("클릭된 납입기간 유형 : {}", selectedOptionText);
////
////        if(!selectedOptionText.contains(napTerm)) {
////            logger.error("가입설계 납입기간 유형 : {}", napTerm);
////            logger.error("홈페이지에서 클릭된 납입기간 유형 : {}", selectedOptionText);
////            throw new NapTermMismatchException("납입기간 불일치");
////        }
////    }
////
////
////    //주계약 보험료 세팅 메서드
////    private void setMonthlyPremium(CrawlingTreaty mainTreaty) {
////        WebElement element = driver.findElement(By.id("spTotDcPrm"));
////        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
////
////        String monthlyPremium = element.getAttribute("value").replaceAll("[^0-9]", "");
////        mainTreaty.monthlyPremium = monthlyPremium;
////
////        logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
////    }
//}
