package com.welgram.crawler.direct.life.mra;


import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MRA_TRM_F004 extends CrawlingMRAAnnounce {

    public static void main(String[] args) {
        executeCommand(new MRA_TRM_F004(), args);
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
//import com.welgram.crawler.general.CrawlingTreaty;
//import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
//import com.welgram.crawler.general.PlanReturnMoney;
//import java.util.List;
//import org.apache.commons.lang3.ObjectUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//
//public class MRA_TRM_F004 extends CrawlingMRA {
//
//    public static void main(String[] args) {
//        executeCommand(new MRA_TRM_F004(), args);
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
//        waitAnnouncePageLoadingBar();
//
//        String announceProductName = info.productNamePublic;
//        logger.info("공시실 상품명 : {} 클릭", announceProductName);
//        element = driver.findElement(By.xpath("//span[text()='" + announceProductName + "']/parent::a"));
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
//        logger.info("주보험 설정");
//        setAnnouncePlanType(info.planSubName);
//
//
//        logger.info("가입금액 설정");
//        setAnnounceAssureMoney(info.treatyList.get(0).assureMoney);
//
//
//        logger.info("보험기간 설정");
//        setAnnounceInsTerm(info.insTerm);
//
//
//        logger.info("납입기간 설정");
//        info.napTerm = (info.napTerm.equals(info.insTerm)) ? "전기납" : info.napTerm;
//        setAnnounceNapTerm(info.napTerm);
//
//
//        logger.info("보험료계산하기 버튼 클릭");
//        element = driver.findElement(By.id("btnCalc"));
//        waitElementToBeClickable(element).click();
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
////        logger.info("해약환급금 조회");
////        logger.info("보장내용 / 해약환급금 버튼 클릭");
////        element = driver.findElement(By.linkText("보장내용 / 해약환급금"));
////        waitElementToBeClickable(element).click();
////        waitAnnouncePageLoadingBar();
////        getReturnPremiums(info);
//
//
//    }
//
//
//    private void getReturnPremiums(CrawlingProduct info) throws Exception {
//        //창 전환
//        currentHandle = driver.getWindowHandle();
//        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
//        helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
//        WaitUtil.waitFor(5);
//
//
//
//
//        String xpath = "//div[@class='report_paint_div']/div[1]/*[name()='svg']/*[name()='g']//*[name()='g'][last()]/*[name()='text' or name()='path']";
//        for (int page = 2; page <= 3; page++) {
//
//            logger.info("해약환급금 정보가 나와있는 {}페이지로 이동", page);
//            WebElement input = driver.findElement(By.xpath("//input[@title='페이지']"));
//            setTextToInputBox(input, String.valueOf(page));
//            input.sendKeys(Keys.ENTER);
//            WaitUtil.waitFor(3);
//
//
//
//
//            int cnt = 0;
//
//            //3페이지에서는 해약환급금 정보가 있는 xpath 경로가 다름
//            if (page == 3) {
//                xpath = "//div[@class='report_paint_div']/div[1]/*[name()='svg']/*[name()='g']//*[name()='g'][@font-size='28px']/*[name()='text' or name()='path']";
//                cnt = -1;
//            }
//
//
//
//
//            StringBuilder sb = new StringBuilder();
//            List<WebElement> elements = driver.findElements(By.xpath(xpath));
//            PlanReturnMoney planReturnMoney = null;
//            for (int i = 0; i < elements.size(); i++) {
//                WebElement element = elements.get(i);
//                moveToElementByJavascriptExecutor(element);
//
//                String currentTagName = element.getTagName();                       //현재 element 태그명
//                String currentText = element.getText().trim();                      //현재 element 텍스트
//                int currentY = 0;     //현재 element의 y좌표(text 태그일때만 y좌표를 갖는다)
//
//
//
//                if ("text".equals(currentTagName)) {
//                    currentY = Integer.parseInt(element.getAttribute("y"));
//
//                    //text 태그의 y좌표가 2800보다 작을때만 해약환급금 정보다.
//                    //2800을 초과하는 데이터는 의미없는 텍스트
//                    if(currentY < 2800) {
//                        sb.append(currentText);
//                    }
//                }
//
//
//                if ("path".equals(currentTagName)) {
//
//                    if(cnt % 12 == 0) {
//
//                        //데이터가 담겨있는 해약환급금 정보가 있으면 해약환급금 리스트에 담는다.
//                        if(ObjectUtils.isNotEmpty(planReturnMoney)) {
//                            info.getPlanReturnMoneyList().add(planReturnMoney);
//                        }
//
//                        planReturnMoney = new PlanReturnMoney();
//
//                    }
//
//                    currentText = String.valueOf(sb);
//
//                    switch (cnt % 12) {
//                        case 0 :
//                            //경과기간
//                            planReturnMoney.setTerm(currentText);
//                            logger.info("====== 해약환급금 =======");
//                            logger.info("경과기간 : {}", currentText);
//                            break;
//                        case 2 :
//                            //납입보험료
//                            planReturnMoney.setPremiumSum(currentText);
//                            logger.info("납입보험료 : {}", currentText);
//                            break;
//                        case 4 :
//                            //최저 해약환급금
//                            currentText = String.valueOf(Integer.parseInt(currentText.replaceAll("[^0-9]", "")) * 10000) ;
//                            planReturnMoney.setReturnMoneyMin(currentText);
//                            logger.info("최저 해약환급금 : {}", currentText);
//                            break;
//                        case 5 :
//                            //최저 해약환급률
//                            planReturnMoney.setReturnRateMin(currentText);
//                            logger.info("최저 해약환급률 : {}", currentText);
//                            break;
//                        case 7 :
//                            //평균 해약환급금
//                            currentText = String.valueOf(Integer.parseInt(currentText.replaceAll("[^0-9]", "")) * 10000) ;
//                            planReturnMoney.setReturnMoneyAvg(currentText);
//                            logger.info("평균 해약환급금 : {}", currentText);
//                            break;
//                        case 8 :
//                            //평균 해약환급률
//                            planReturnMoney.setReturnRateAvg(currentText);
//                            logger.info("평균 해약환급률 : {}", currentText);
//                            break;
//                        case 10 :
//                            //해약환급금
//                            currentText = String.valueOf(Integer.parseInt(currentText.replaceAll("[^0-9]", "")) * 10000) ;
//                            planReturnMoney.setReturnMoney(currentText);
//                            logger.info("해약환급금 : {}", currentText);
//
//                            info.returnPremium = currentText;
//                            break;
//                        case 11 :
//                            //해약환급률
//                            planReturnMoney.setReturnRate(currentText);
//                            logger.info("해약환급률 : {}", currentText);
//                            break;
//                    }
//
//
//                    cnt++;
//                    sb.setLength(0);
//
//
//
//                }
//
//
//                if(i == elements.size() - 1) {
//                    currentText = sb.toString();
//
//                    if(StringUtils.isNotEmpty(currentText)) {
//                        planReturnMoney.setReturnRate(currentText);
//                        logger.info("해약환급률 : {}", currentText);
//                    }
//
//                    info.getPlanReturnMoneyList().add(planReturnMoney);
//                }
//
//            }
//
//
//
//            //해약환급금 정보가 다음 페이지에도 이어지는지 확인
//            int lastIdx = info.getPlanReturnMoneyList().size() - 1;
//            String lastTerm = info.getPlanReturnMoneyList().get(lastIdx).getTerm();
//
//
//            String insTerm = "";
//            String myInsTerm = info.insTerm;
//            int myAge = Integer.parseInt(info.age);
//
//            if(myInsTerm.contains("세")) {
//                insTerm = String.valueOf(Integer.parseInt(myInsTerm.replaceAll("[^0-9]", "")) - myAge);
//            }
//
//
//
//            if(lastTerm.contains("만기") || lastTerm.contains(insTerm)) {
//                logger.info("lastTerm :: {} 으로 해약환급금 크롤링을 중단합니다.", lastTerm);
//                break;
//            }
//
//        }
//
//        if(info.treatyList.get(0).productKind == ProductKind.순수보장형) {
//            info.returnPremium = "0";
//        }
//
//    }
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
//        if ("0".equals(mainTreaty.monthlyPremium)) {
//            throw new Exception("주계약 보험료 설정을 필수입니다.");
//        } else {
//            logger.info("보험료 : {}원", mainTreaty.monthlyPremium);
//        }
//    }
//
//
//    private void setAnnounceGender(int gender) throws Exception {
//        String genderText = (gender == MALE) ? "남" : "여";
//
//        WebElement label = driver.findElement(By.xpath("//tr[@id='spI1']//label[text()='" + genderText + "']"));
//        waitElementToBeClickable(label).click();
//
//        //맞게 클릭됐는지 검사
//        String script = "return $('input[name=\"rdoI1GndrCd\"]:checked').attr('id');";
//        String checkedGenderId = executeJavascript(script).toString();
//        String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedGenderId + "']")).getText();
//
//        logger.info("=====================================");
//        logger.info("가입설계 성별 : {}", genderText);
//        logger.info("홈페이지 클릭된 성별 : {}", checkedGender);
//        logger.info("=====================================");
//
//        if (genderText.equals(checkedGender)) {
//            logger.info("가입설계 성별 : {} == 홈페이지 클릭된 성별 : {}", genderText, checkedGender);
//        } else {
//            logger.info("가입설계 성별 : {} ≠ 홈페이지 클릭된 성별 : {}", genderText, checkedGender);
//            throw new Exception("성별 불일치");
//        }
//
//    }
//
//
//    //공시실 납입주기 설정 메서드
//    private void setAnnounceNapCycle(String napCycle) throws Exception {
//        //납입주기 설정
//        selectOptionByText(By.id("selFNCMA024List"), napCycle);
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
//        if (napCycle.equals(checkedNapCycle)) {
//            logger.info("가입설계 납입주기 : {} == 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
//        } else {
//            logger.info("가입설계 납입주기 : {} ≠ 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
//            throw new Exception("납입주기 불일치");
//        }
//    }
//
//
//    //공시실 판매플랜 설정 메서드
//    private void setAnnouncePlanType(String planType) throws Exception {
//        //판매플랜 설정
//        selectOptionByText(By.id("selFNCMA025List"), planType);
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
//        if (planType.equals(checkedPlanType)) {
//            logger.info("가입설계 주보험 : {} == 홈페이지 클릭된 주보험 : {}", planType, checkedPlanType);
//        } else {
//            logger.info("가입설계 주보험 : {} ≠ 홈페이지 클릭된 주보험 : {}", planType, checkedPlanType);
//            throw new Exception("주보험 불일치");
//        }
//    }
//
//
//    private void setAnnounceAssureMoney(int assureMoney) throws Exception {
//        String unit = driver.findElement(By.id("spNtryUnit")).getText().trim();
//        int unitNum = 1;
//
//        if ("억원".equals(unit)) {
//            unitNum = 100000000;
//        } else if ("천만원".equals(unit)) {
//            unitNum = 10000000;
//        } else if ("만원".equals(unit)) {
//            unitNum = 10000;
//        }
//
//        assureMoney = assureMoney / unitNum;
//
//        logger.info("가입금액 : {}{}", assureMoney, unit);
//        setTextToInputBox(By.id("applyMoney"), String.valueOf(assureMoney));
//
//        WebElement input = driver.findElement(By.id("applyMoney"));
//        input.click();
//        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
//        input.sendKeys(String.valueOf(assureMoney));
//    }
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
//        //맞게 클릭됐는지 검사
//        String script = "return $('#selFNCMA022List option:selected').text();";
//        String checkedInsTerm = executeJavascript(script).toString();
//
//        logger.info("=========================================");
//        logger.info("가입설계 보험기간 : {}", insTerm);
//        logger.info("홈페이지 클릭된 보험기간 : {}", checkedInsTerm);
//        logger.info("=========================================");
//
//        if (insTerm.equals(checkedInsTerm)) {
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
//        //맞게 클릭됐는지 검사
//        String script = "return $('#selFNCMA023List option:selected').text();";
//        String checkedNapTerm = executeJavascript(script).toString();
//
//        logger.info("=========================================");
//        logger.info("가입설계 납입기간 : {}", napTerm);
//        logger.info("홈페이지 클릭된 납입기간 : {}", checkedNapTerm);
//        logger.info("=========================================");
//
//        if (napTerm.equals(checkedNapTerm)) {
//            logger.info("가입설계 납입기간 : {} == 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
//        } else {
//            logger.info("가입설계 납입기간 : {} ≠ 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
//            throw new Exception("납입기간 불일치");
//        }
//    }
//
////    //홈페이지용 크롤링
////    private void crawlFromHomepage(CrawlingProduct info) throws Exception{
////        waitLoadingImg();
////
////        logger.info("팝업창 존재할 경우에 닫기");
////        if(existElement(By.id("eventLayer"))) {
////            helper.waitElementToBeClickable(driver.findElement(By.xpath("//div[@id='eventLayer']//a[text()='닫기']"))).click();
////        }
////
////
////        logger.info("메뉴바 > 보험 클릭");
////        helper.waitElementToBeClickable(By.id("life-menu1")).click();
////        logger.info("정기보험 클릭");
////        helper.waitPresenceOfElementLocated(By.xpath("//div[@class='submenu-box']//ul//a[text()='경영인을 위한 정기보험']"));
////        helper.waitElementToBeClickable(By.xpath("//div[@class='submenu-box']//ul//a[text()='경영인을 위한 정기보험']")).click();
////        waitLoadingImg();
////
////
////        logger.info("보험료계산 버튼 클릭");
////        helper.waitElementToBeClickable(By.xpath("//strong[text()='보험료계산']/parent::button[@class='goods-cal pc-only']")).click();
////        waitLoadingImg();
////
////        logger.info("생년월일 설정");
////        setBirth(info.birth);
////
////        logger.info("성별 설정");
////        setGender(info.gender);
////
////        logger.info("위험등급 설정(비위험으로 고정)");
////        setDangerRank("비위험");
////
////        logger.info("납입주기 설정");
////        setNapCycle(info.getNapCycleName());
////
////        logger.info("주보험 유형 설정");
////        setPlanType(info.textType);
////
////        logger.info("가입금액 설정");
////        setAssureMoney(info.assureMoney);
////
////        logger.info("보험기간 설정");
////        setInsTerm(info.insTerm);
////
////        logger.info("납입기간 설정");
////        String napTerm = "전기납";
////        setNapTerm(napTerm);
////
////        logger.info("보험료 계산하기 버튼 클릭");
////        helper.waitElementToBeClickable(By.id("btnCalc")).click();
////        waitLoadingImg();
////        WaitUtil.waitFor(2);
////
////        logger.info("주계약 보험료 설정");
////        setMonthlyPremium(info.treatyList.get(0));
////
////        logger.info("스크린샷 찍기");
////        takeScreenShot(info);
////
////
//////        logger.info("보장내용 / 해약환급금 버튼 클릭");
//////        helper.waitElementToBeClickable(By.xpath("//a[text()='보장내용 / 해약환급금']")).click();
//////
//////        logger.info("해약환급금 설정");
//////        logger.info("해약환급금 새창으로 창 전환");
//////        currentHandle = driver.getWindowHandle();
//////        if(wait.until(ExpectedConditions.numberOfWindowsToBe(2))){
//////            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
//////        }
//////        WaitUtil.waitFor(5);
//////
//////        getReturnPremiums(info);
////    }
////
////    //해약환급금 조회 메서드
////    @Override
////    protected void getReturnPremiums(CrawlingProduct info) throws Exception{
////        int startPage = 2;
////        logger.info("{}페이지로 이동", startPage);
////        setTextToInputBox(By.xpath("//input[@class='report_menu_pageCount_input']"), String.valueOf(startPage));
////        WaitUtil.waitFor(2);
////
////
////        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='report_paint_div']//*[name()='svg']//*[name()='g']//*[name()='g'][last()]/*[not(self::*[name()='line'])]"));
////        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
////        PlanReturnMoney lastPlanReturnMoney = null;
////        List<String> strList = new ArrayList<>();
////        String tmp = "";
////
////        int idx = 0;
////        int size = elements.size();
////        while (idx < size) {
////            WebElement element = elements.get(idx);
////
////            String currentTagName = element.getTagName();
////
////            if("text".equals(currentTagName)) {
////                tmp += element.getText();
////            }
////
////            if("path".equals(currentTagName)) {
////                //해약환급금 정보가 아닌 불필요한 정보가 수집될 수도 있음. 검증 작업
////               String remainHangeulStr = tmp.replaceAll("[^가-힣]", "");
////               if(!StringUtils.isEmpty(remainHangeulStr)) {
////                   //한글은 오로지 경과기간에 해당하는 개월 or 만기 라는 단어만 허용됨.
////                   if(remainHangeulStr.contains("개월") || remainHangeulStr.contains("만기")) {
////                       strList.add(tmp);
////                       tmp = "";
////                   } else {
////                       tmp = "";
////                   }
////               } else {
////                   strList.add(tmp);
////                   tmp = "";
////               }
////            }
////
////            //현재 페이지 해약환급금의 끝에 다다랐을 때
////            if("image".equals(currentTagName) || (idx == elements.size() - 1) ) {
////                strList.add(tmp);
////                tmp = "";
////
////                for(int i=0; i<strList.size(); i+=12) {
////                    String term = strList.get(i);
////                    String premiumSum = strList.get(i+2);
////                    String returnMoneyMin = strList.get(i+4);
////                    String returnRateMin = strList.get(i+5);
////                    String returnMoneyAvg = strList.get(i+7);
////                    String returnRateAvg = strList.get(i+8);
////                    String returnMoney = strList.get(i+10);
////                    String returnRate = strList.get(i+11);
////
////                    returnMoneyMin = String.valueOf(Integer.parseInt(returnMoneyMin.replaceAll("[^0-9]", "") + "0000"));
////                    returnMoneyAvg = String.valueOf(Integer.parseInt(returnMoneyAvg.replaceAll("[^0-9]", "") + "0000"));
////                    returnMoney = String.valueOf(Integer.parseInt(returnMoney.replaceAll("[^0-9]", "") + "0000"));
////
////                    if(!term.contains("개월") && !term.contains("만기")) {
////                        term = term + "년";
////                    }
////
////                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
////                    planReturnMoney.setTerm(term);
////                    planReturnMoney.setPremiumSum(premiumSum);
////                    planReturnMoney.setReturnMoneyMin(returnMoneyMin);
////                    planReturnMoney.setReturnRateMin(returnRateMin);
////                    planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
////                    planReturnMoney.setReturnRateAvg(returnRateAvg);
////                    planReturnMoney.setReturnMoney(returnMoney);
////                    planReturnMoney.setReturnRate(returnRate);
////
////                    logger.info("***해약환급금***");
////                    logger.info("|--경과기간: {}", term);
////                    logger.info("|--납입보험료: {}", premiumSum);
////                    logger.info("|--최저해약환급금: {}", returnMoneyMin);
////                    logger.info("|--최저환급률: {}", returnRateMin);
////                    logger.info("|--평균해약환급금: {}", returnMoneyAvg);
////                    logger.info("|--평균환급률: {}", returnRateAvg);
////                    logger.info("|--해약환급금: {}", returnMoney);
////                    logger.info("|--환급률: {}", returnRate + "\n");
////
////                    planReturnMoneyList.add(planReturnMoney);
////                }
////
////                //현재까지 쌓인 마지막 해약환급금의 경과년도가 만기가 아닌 경우 다음 페이지로 이동
////                lastPlanReturnMoney = planReturnMoneyList.get(planReturnMoneyList.size() - 1);
////
////                String lastReturnMoneyTerm = lastPlanReturnMoney.getTerm().replaceAll("[^0-9]", "");
////                String insTerm = info.insTerm.replaceAll("[^0-9]", "");
////                String expireAge = String.valueOf(Integer.parseInt(insTerm) - Integer.parseInt(info.age));
////
////                if(!lastPlanReturnMoney.getTerm().contains("만기") && !lastReturnMoneyTerm.equals(expireAge)) {
////                    startPage++;
////                    logger.info("{}페이지로 이동", startPage);
////                    setTextToInputBox(By.xpath("//input[@class='report_menu_pageCount_input']"), String.valueOf(startPage));
////                    WaitUtil.waitFor(2);
////
////                    elements = driver.findElements(By.xpath("//div[@class='report_paint_div']//*[name()='svg']//*[name()='g']//*[name()='g'][3]/*[not(self::*[name()='line'])]"));
////
////                    //변수 초기화
////                    strList.clear();
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
////            idx++;
////        }
////
////        info.planReturnMoneyList = planReturnMoneyList;
////
////        //해당 상품의 해약환급금 표는 만기 하루 전날에 대한 해약환급금을 보여준다. 실제로 순수보장형 상품의 만기시 해약환급금은 0원이다.
////        if(info.treatyList.get(0).productKind == ProductKind.순수보장형) {
////            //해당 상품이 순수보장형 상품이면
////            info.returnPremium = "0";
////        } else {
////            info.returnPremium = lastPlanReturnMoney.getReturnMoney();
////        }
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
