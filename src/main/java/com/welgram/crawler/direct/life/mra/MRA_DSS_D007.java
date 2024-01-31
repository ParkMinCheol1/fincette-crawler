package com.welgram.crawler.direct.life.mra;


import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MRA_DSS_D007 extends CrawlingMRADirect {

    public static void main(String[] args) {
        executeCommand(new MRA_DSS_D007(), args);
    }

    @Override
    protected boolean preValidation(CrawlingProduct info) {
        return info.getTreatyList().size() > 0;
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("[step1] 사용자 정보 입력");
        setUserInfo(info);

        logger.info("가입조건변경 버튼 클릭");
        $button = driver.findElement(By.id("chgterm"));
        click($button);

        logger.info("납입유형 설정");
        setNapCycle(info.getNapCycleName());

        logger.info("보험기간 설정");
        String insTerm = info.getInsTerm() + "만기";
        setInsTerm(insTerm);

        logger.info("적용 버튼 클릭");
        $button = driver.findElement(By.id("modalCalcBtn"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info, MoneyUnit.원);

        return true;
    }
}



//package com.welgram.crawler.direct.life.mra;
//
//import com.welgram.common.MoneyUtil;
//import com.welgram.common.WaitUtil;
//import com.welgram.common.except.GenderMismatchException;
//import com.welgram.common.except.InsTermMismatchException;
//import com.welgram.common.except.NapTermMismatchException;
//import com.welgram.common.except.NotFoundProductTypeException;
//import com.welgram.common.except.ProductTypeMismatchException;
//import com.welgram.crawler.direct.life.HomepageCrawlingMRA;
//import com.welgram.crawler.general.CrawlingProduct;
//import com.welgram.crawler.general.CrawlingTreaty;
//import java.util.ArrayList;
//import java.util.List;
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebElement;
//
//// 온라인 뇌경색증.뇌출혈.급성심근경색증 보장보험 (무)202003
//public class MRA_DSS_D007 extends HomepageCrawlingMRA {
//
//    public static void main(String[] args) {
//        executeCommand(new MRA_DSS_D007(), args);
//    }
//
//    @Override
//    protected boolean scrap(CrawlingProduct info) throws Exception {
//        crawlFromHomepage(info);
//
//        return true;
//    }
//
//
//    //홈페이지용 크롤링
//    private void crawlFromHomepage(CrawlingProduct info) throws Exception{
//        waitLoadingImg();
//
//        logger.info("나이 설정 버튼 클릭");
//        helper.waitElementToBeClickable(By.id("insuAgeTxt")).click();
//        WaitUtil.waitFor(2);
//
//        logger.info("생년월일 & 성별 설정");
//        setBirthAndGender(info.fullBirth, info.gender);
//
//        logger.info("확인 버튼 클릭");
//        helper.waitElementToBeClickable(By.xpath("//section[@id='birthYear']//footer//button")).click();
//        waitLoadingImg();
//
//        logger.info("가입조건 변경 버튼 클릭");
//        helper.waitElementToBeClickable(By.id("chgterm")).click();
//        WaitUtil.waitFor(2);
//
//        logger.info("납입유형 설정");
//        setHomepageNapCycle(info.getNapCycleName());
//
//        logger.info("보험기간 설정");
//        setHomepageInsTerm(info.insTerm);
//
//        logger.info("적용 버튼 클릭");
//        helper.waitElementToBeClickable(By.id("modalCalcBtn")).click();
//        waitLoadingImg();
//        WaitUtil.waitFor(2);
//
//        logger.info("특약 비교");
//        setTreaties(info.treatyList);
//
//        logger.info("주계약 보험료 설정");
//        setPremiums(info.treatyList.get(0));
//
//        logger.info("스크린샷 찍기");
//        takeScreenShot(info);
//
//        logger.info("해약환급금 조회");
//        getReturnPremiums(info);
//    }
//
//
//    private void setTreaties(List<CrawlingTreaty> myTreatyList) throws Exception {
//
//        List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();
//
//        //홈페이지의 특약명과 가입금액 수집
//        List<WebElement> trList = driver.findElements(By.xpath("//div[@class='calc-content']//table/tbody/tr"));
//        for(WebElement tr : trList) {
//            WebElement th = tr.findElement(By.xpath("./th[1]"));
//            WebElement td = tr.findElement(By.xpath("./td[1]"));
//
//            String treatyName = th.getText().trim();
//            String assureMoney = td.getText().trim();
//            assureMoney = String.valueOf(MoneyUtil.toDigitMoney(assureMoney));
//
//            CrawlingTreaty hTreaty = new CrawlingTreaty();
//            hTreaty.treatyName = treatyName;
//            hTreaty.assureMoney = Integer.parseInt(assureMoney);
//
//            homepageTreatyList.add(hTreaty);
//        }
//
//
//        boolean result = compareTreaties(homepageTreatyList, myTreatyList);
//
//
//        if(result) {
//            logger.info("특약 정보 모두 일치 ^^");
//        } else {
//            throw new Exception("특약 불일치");
//        }
//    }
//
//
//
//
//    protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {
//        boolean result = true;
//
//        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
//        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
//        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트
//
//
//        //홈페이지 특약명 리스트
//        List<String> homepageTreatyNameList = new ArrayList<>();
//        List<String> copiedHomepageTreatyNameList = null;
//        for(CrawlingTreaty t : homepageTreatyList) {
//            homepageTreatyNameList.add(t.treatyName);
//        }
//        copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);
//
//
//        //가입설계 특약명 리스트
//        List<String> myTreatyNameList = new ArrayList<>();
//        List<String> copiedMyTreatyNameList = null;
//        for(CrawlingTreaty t : welgramTreatyList) {
//            myTreatyNameList.add(t.treatyName);
//        }
//        copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);
//
//
//
//
//        //일치하는 특약명만 추림
//        homepageTreatyNameList.retainAll(myTreatyNameList);
//        samedTreatyNameList = new ArrayList<>(homepageTreatyNameList);
//        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);
//
//
//
//        //가입설계에 추가해야하는 특약명만 추림
//        homepageTreatyNameList.removeAll(myTreatyNameList);
//        toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
//        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);
//
//
//
//        //가입설계에서 제거해야하는 특약명만 추림
//        myTreatyNameList.removeAll(homepageTreatyNameList);
//        toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);
//        myTreatyNameList = new ArrayList<>(copiedMyTreatyNameList);
//
//
//
//        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
//        for(String treatyName : samedTreatyNameList) {
//            CrawlingTreaty homepageTreaty = getCrawlingTreaty(homepageTreatyList, treatyName);
//            CrawlingTreaty myTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);
//
//            int homepageTreatyAssureMoney = homepageTreaty.assureMoney;
//            int myTreatyAssureMoney = myTreaty.assureMoney;
//
//
//            //가입금액 비교
//            if(homepageTreatyAssureMoney == myTreatyAssureMoney) {
//                //금액이 일치하는 경우
//                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, myTreatyAssureMoney);
//            } else {
//                //금액이 불일치하는 경우 특약정보 출력
//                result = false;
//
//                logger.info("[불일치 특약]");
//                logger.info("특약명 : {}", treatyName);
//                logger.info("가입설계 가입금액 : {}", myTreatyAssureMoney);
//                logger.info("홈페이지 가입금액 : {}", homepageTreatyAssureMoney);
//                logger.info("==============================================================");
//            }
//        }
//
//
//        //가입설계 추가해야하는 특약정보 출력
//        if(toAddTreatyNameList.size() > 0) {
//            result = false;
//
//            logger.info("==============================================================");
//            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
//            logger.info("==============================================================");
//
//            for(int i=0; i<toAddTreatyNameList.size(); i++) {
//                String treatyName = toAddTreatyNameList.get(i);
//
//                CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
//                logger.info("특약명 : {}", treaty.treatyName);
//                logger.info("가입금액 : {}", treaty.assureMoney);
//                logger.info("==============================================================");
//            }
//
//        }
//
//
//
//        //가입설계 제거해야하는 특약정보 출력
//        if(toRemoveTreatyNameList.size() > 0) {
//            result = false;
//
//            logger.info("==============================================================");
//            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
//            logger.info("==============================================================");
//
//            for(int i=0; i<toRemoveTreatyNameList.size(); i++) {
//                String treatyName = toRemoveTreatyNameList.get(i);
//
//                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
//                logger.info("특약명 : {}", treaty.treatyName);
//                logger.info("가입금액 : {}", treaty.assureMoney);
//                logger.info("==============================================================");
//            }
//        }
//
//
//        return result;
//    }
//
//
//
//    private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {
//        CrawlingTreaty result = null;
//
//        for(CrawlingTreaty treaty : treatyList) {
//            if(treaty.treatyName.equals(treatyName)) {
//                result = treaty;
//            }
//        }
//
//        return result;
//    }
//
//
//
//
//
//
//
//
//    //주계약 보험료 세팅 메서드
//    protected void setPremiums(CrawlingTreaty mainTreaty) throws Exception {
//        String monthlyPremium = driver.findElement(By.xpath("//strong[@data-id='totDcPrmTxt']")).getText().replaceAll("[^0-9]", "");
//
//        mainTreaty.monthlyPremium = monthlyPremium;
//
//        if(!"0".equals(mainTreaty.monthlyPremium)) {
//            logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
//        } else {
//            throw new Exception("주계약 보험료를 설정해주세요. 보험료는 0원일 수 없습니다.");
//        }
//    }
//
//
//    //보험기간 설정 메서드
//    private void setHomepageInsTerm(String insTerm) throws Exception{
//        insTerm = insTerm + "만기";
//
//        //1. 보험기간 클릭
//        helper.waitElementToBeClickable(By.xpath("//h2[text()='보험기간']/following-sibling::div[1]//label[contains(., '" + insTerm + "')]")).click();
//
//        //2. 실제 홈페이지에서 클릭된 보험기간 확인
//        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='scrtPrid']:checked\").attr('id')").toString();
//        String checkedInsTerm = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
//        logger.info("============================================================================");
//        logger.info("가입설계 보험기간 : {}", insTerm);
//        logger.info("홈페이지에서 클릭된 보험기간 : {}", checkedInsTerm);
//        logger.info("============================================================================");
//
//        if(!checkedInsTerm.contains(insTerm)) {
//            logger.info("============================================================================");
//            logger.info("가입설계 보험기간 : {}", insTerm);
//            logger.info("홈페이지에서 클릭된 보험기간 : {}", checkedInsTerm);
//            logger.info("============================================================================");
//            throw new InsTermMismatchException("보험기간 불일치");
//        } else {
//            logger.info("result :: 가입설계 보험기간({}) == 홈페이지에서 클릭된 보험기간({})", insTerm, checkedInsTerm);
//            logger.info("============================================================================");
//        }
//    }
//
//
//
//    //납입기간 설정 메서드
//    private void setHomepageNapTerm(String napTerm) throws Exception{
//        //1. 납입기간 클릭
//        helper.waitElementToBeClickable(By.xpath("//h2[text()='납입기간']/following-sibling::div[1]//label[contains(., '" + napTerm + "')]")).click();
//
//        //2. 실제 홈페이지에서 클릭된 납입기간 확인
//        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='rvpd']:checked\").attr('id')").toString();
//        String checkedNapTerm = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
//        logger.info("============================================================================");
//        logger.info("가입설계 납입기간 : {}", napTerm);
//        logger.info("홈페이지에서 클릭된 납입기간 : {}", checkedNapTerm);
//        logger.info("============================================================================");
//
//        if(!checkedNapTerm.contains(napTerm)) {
//            logger.info("============================================================================");
//            logger.info("가입설계 납입기간 : {}", napTerm);
//            logger.info("홈페이지에서 클릭된 납입기간 : {}", checkedNapTerm);
//            logger.info("============================================================================");
//            throw new NapTermMismatchException("납입기간 불일치");
//        } else {
//            logger.info("result :: 가입설계 납입기간({}) == 홈페이지에서 클릭된 납입기간({})", napTerm, checkedNapTerm);
//            logger.info("============================================================================");
//        }
//    }
//
//
//    //납입유형 설정 메서드
//    private void setHomepageNapCycle(String napCycle) throws Exception{
//        //1. 납입유형 클릭
//        helper.waitElementToBeClickable(By.xpath("//h2[text()='납입유형']/following-sibling::div[1]//label[text()='" + napCycle + "']")).click();
//
//        //2. 실제 홈페이지에서 클릭된 납입유형 확인
//        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='rvcy']:checked\").attr('id')").toString();
//        String checkedNapCycle = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
//        logger.info("============================================================================");
//        logger.info("가입설계 납입유형 : {}", napCycle);
//        logger.info("홈페이지에서 클릭된 납입유형 : {}", checkedNapCycle);
//        logger.info("============================================================================");
//
//        if(!checkedNapCycle.contains(napCycle)) {
//            logger.info("============================================================================");
//            logger.info("가입설계 납입유형 : {}", napCycle);
//            logger.info("홈페이지에서 클릭된 납입유형 : {}", checkedNapCycle);
//            logger.info("============================================================================");
//            throw new NapTermMismatchException("납입유형 불일치");
//        } else {
//            logger.info("result :: 가입설계 납입유형({}) == 홈페이지에서 클릭된 납입유형({})", napCycle, checkedNapCycle);
//            logger.info("============================================================================");
//        }
//    }
//
//
//
//    //상품유형 설정 메서드
//    private void setProductType(List<String> planSubNameList) throws Exception{
//        String productType = "";
//        boolean isExist = false;
//
//        //','로 이어진 planSubName에서 상품유형 값이 존재하는지 확인
//        List<WebElement> elements = driver.findElements(By.xpath("//h2[text()='상품유형']/following-sibling::div[1]//label"));
//        for(WebElement element : elements) {
//            String targetProductType = element.getText().replace("\n", "").replace(" ", "");
//
//            for(String planSubName : planSubNameList) {
//                planSubName = planSubName.replaceAll(" ", "");
//
//                if(targetProductType.equals(planSubName)) {
//                    productType = planSubName;
//                    isExist = true;
//
//                    //상품유형 클릭
//                    helper.waitElementToBeClickable(element).click();
//                    break;
//                }
//            }
//
//            if(isExist) {
//                break;
//            }
//        }
//
//        //내 가입설계의 상품유형이 홈페이지에도 존재해야만
//        if(isExist) {
//            //실제 홈페이지에서 클릭된 상품유형 확인
//            String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='plType']:checked\").attr('id')").toString();
//            String checkedProductType = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText().replace("\n", "").replace(" ", "");
//            logger.info("============================================================================");
//            logger.info("가입설계 상품유형 : {}", productType);
//            logger.info("홈페이지에서 클릭된 상품유형 : {}", checkedProductType);
//            logger.info("============================================================================");
//
//            if(!checkedProductType.equals(productType)) {
//                logger.error("가입설계 상품유형 : {}", productType);
//                logger.error("홈페이지에서 클릭된 상품유형 : {}", checkedProductType);
//                throw new ProductTypeMismatchException("상품유형 불일치");
//            } else {
//                logger.info("result :: 가입설계 상품유형({}) == 홈페이지에서 클릭된 상품유형({})", productType, checkedProductType);
//                logger.info("============================================================================");
//            }
//        } else {
//            logger.info("상품유형을 다시 확인해주세요");
//            throw new NotFoundProductTypeException("상품유형이 존재하지 않습니다.");
//        }
//
//    }
//
//
//    //주계약 선택 설정 메서드
//    private void setMainTreatyType(List<String> planSubNameList) throws Exception{
//        String mainTreatyType = "";
//        boolean isExist = false;
//
//        //','로 이어진 planSubName에서 주계약 선택 값이 존재하는지 확인
//        List<WebElement> elements = driver.findElements(By.xpath("//h2[text()='주계약 선택']/following-sibling::div[1]//label"));
//        for(WebElement element : elements) {
//            String targetMainTreatyType = element.getText().replaceAll(" ", "");
//
//            for(String planSubName : planSubNameList) {
//                planSubName = planSubName.replaceAll(" ", "");
//
//                if(targetMainTreatyType.equals(planSubName)) {
//                    mainTreatyType = planSubName;
//                    isExist = true;
//
//                    //상품유형 클릭
//                    helper.waitElementToBeClickable(element).click();
//                    break;
//                }
//            }
//
//            if(isExist) {
//                break;
//            }
//        }
//
//        //내 가입설계의 주계약 선택 타입이 홈페이지에도 존재해야만
//        if(isExist) {
//            //실제 홈페이지에서 클릭된 주계약 선택 타입 확인
//            String checkedId = ((JavascriptExecutor)driver).executeScript("return $('input[name=\"subTypeP\"]:checked').attr('id');").toString();
//            String checkedMainTreatyType = driver.findElement(By.xpath("//label[@for='" + checkedId + "']")).getText().replaceAll(" ", "");
//            logger.info("============================================================================");
//            logger.info("가입설계 주계약 선택 타입 : {}", mainTreatyType);
//            logger.info("홈페이지에서 클릭된 주계약 선택 타입 : {}", checkedMainTreatyType);
//            logger.info("============================================================================");
//
//            if(!checkedMainTreatyType.equals(mainTreatyType)) {
//                logger.error("가입설계 주계약 선택 타입 : {}", mainTreatyType);
//                logger.error("홈페이지에서 클릭된 주계약 선택 타입 : {}", checkedMainTreatyType);
//                throw new Exception("주계약 선택 타입 불일치");
//            } else {
//                logger.info("result :: 가입설계 주계약 선택 타입({}) == 홈페이지에서 클릭된 주계약 선택 타입({})", mainTreatyType, checkedMainTreatyType);
//                logger.info("============================================================================");
//            }
//        } else {
//            logger.info("주계약 선택 타입을 다시 확인해주세요");
//            throw new Exception("주계약 선택 타입이 존재하지 않습니다.");
//        }
//    }
//
//
//    //생년월일과 성별 설정 메서드
//    private void setBirthAndGender(String birth, int gender) throws Exception {
//        String genderId = (gender == MALE) ? "gender_1" : "gender_2";
//        String genderText = (gender == MALE) ? "남" : "여";
//
//        //1. 생년월일 입력
//        logger.info("생년월일 설정 : {}", birth);
//        setTextToInputBox(By.id("birthDays"), birth);
//
//        //2. 성별 클릭
//        logger.info("성별 설정");
//        driver.findElement(By.xpath("//label[@for='" + genderId + "']")).click();
//
//        //3. 실제 홈페이지에서 클릭된 성별 확인
//        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='genderType']:checked\").attr('id')").toString();
//        String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
//
//        logger.info("============================================================================");
//        logger.info("가입설계 성별 : {}", genderText);
//        logger.info("홈페이지에서 클릭된 성별 : {}", checkedGender);
//        logger.info("============================================================================");
//
//        if(!checkedGender.equals(genderText)) {
//            logger.error("가입설계 성별 : {}", genderText);
//            logger.error("홈페이지에서 클릭된 성별 : {}", checkedGender);
//            throw new GenderMismatchException("성별 불일치");
//        } else {
//            logger.info("result :: 가입설계 성별({}) == 홈페이지에서 클릭된 성별({})", genderText, checkedGender);
//            logger.info("============================================================================");
//        }
//    }
//}
