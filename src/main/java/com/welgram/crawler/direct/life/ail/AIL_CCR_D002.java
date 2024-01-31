package com.welgram.crawler.direct.life.ail;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.NotFoundPlanTypeException;
import com.welgram.common.except.PlanTypeMismatchException;
import com.welgram.common.except.TreatyMisMatchException;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;



// 2022.09.23       | 최우진           | 다이렉트_암보험
// AIL_CCR_D002     | (무)AIA Vitality 베스트핏 보장보험
public class AIL_CCR_D002 extends CrawlingAIL {

    public static void main(String[] args) { executeCommand( new AIL_CCR_D002(), args); }



    // 모바일 켜기
    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception { option.setMobile(true); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        //홈페이지 접속시 바로 로딩바 뜸. 로딩바 사라질때까지 대기
        waitMobileLoadingBar();

        // 임의 모달 창 끄기
        try {
            driver.findElement(By.xpath("//*[@id='modalNotice']/div/div/div[3]/div/button")).click();
            WaitUtil.waitFor(4);
        } catch(Exception e) {
            logger.info("모달창이 없습니다");
        }

        //전체 메뉴 버튼이 클릭 가능한 상태가 될 때까지 대기한 후에 클릭
        logger.info("전체 메뉴 버튼 클릭");
        WebElement element = driver.findElement(By.xpath("//button[@class='ly-header__gnb']"));
        waitElementToBeClickable(element).click();

        //든든튼튼 암보험 버튼이 클릭 가능한 상태가 될 때까지 대기한 후에 클릭. 클릭한 후에 로딩바 사라질 때까지 대기
        logger.info("베스트핏 암보장 버튼 클릭");
        element = driver.findElement(By.xpath("//span[text()='베스트핏 암보장 ']/parent::a"));
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();
        WaitUtil.waitFor(1);
        waitMobileLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("보험료 계산 버튼 클릭");
        element = driver.findElement(By.id("btnCalc"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(3);

        //출생년도에 따라 주민번호 뒷자리의 시작번호가 달라짐. 2000년생 이후의 사람은 남자일 경우 3, 여자일 경우 4로 시작함.
        logger.info("생년월일 설정");
        setTextToInputBox(By.id("inputRegistNum1"), info.birth);
        int year = Integer.parseInt(info.fullBirth.substring(0, 4));
        int startGenderValue = (info.gender == MALE) ? 1 : 2;
        if (year >= 2000) {
            startGenderValue = (info.gender == MALE) ? 3 : 4;
        }
        setTextToInputBox(By.id("inputRegistNum2"), String.valueOf(startGenderValue));

        //보험료 설계 버튼이 클릭 가능한 상태가 될 때까지 대기한 후에 클릭. 클릭한 후에 로딩바 사라질 때까지 대기
        logger.info("보험료 설계 버튼 클릭");
        element = driver.findElement(By.id("btnCalc02"));
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();
        WaitUtil.waitFor(3);

        //상세 설계하기 버튼이 클릭 가능한 상태가 될 때까지 대기한 후에 클릭. 클릭한 후에 로딩바 사라질 때까지 대기
        logger.info("상세 설계하기 버튼 클릭");
        element = driver.findElement(By.id("btnCalc"));
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("플랜 유형 선택");
        logger.info("플랜 유형 :: {}", info.planSubName);
        setMobilePlanType(info.planSubName);

        logger.info("보험기간 설정");
        setMobileInsTerm(info.insTerm);

        logger.info("납입기간 설정");
        info.napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
        setMobileNapTerm(info.napTerm);

        logger.info("특약 선택 및 비교");
        compareTreaties(info.treatyList);

        logger.info("바이탈리티 멤버십 가입 체크 해제");
        boolean isChecked = driver.findElement(By.id("chkSubscr")).isSelected();
        if (isChecked) {
            element = driver.findElement(By.xpath("//label[@for='chkSubscr']"));
            moveToElementByJavascriptExecutor(element);
            waitElementToBeClickable(element).click();
            WaitUtil.waitFor(3);

            logger.info("체크 해제 완료");
        }

        logger.info("주계약 보험료 크롤링");
        String homepageMonthlyPremium = driver.findElement(By.id("topDcbfTotPrm")).getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = homepageMonthlyPremium;

        String mainTreatyMonthlyPremium = info.treatyList.get(0).monthlyPremium;

        if ("0".equals(mainTreatyMonthlyPremium)) {
            throw new Exception("주계약 보험료를 세팅해주세요");
        } else {
            logger.info("보험료 : {}원", info.treatyList.get(0).monthlyPremium);
        }

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 보기 버튼 클릭");
        element = driver.findElement(By.xpath("//button[text()='해약환급금 보기']"));
        moveToElementByJavascriptExecutor(element);
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
        WaitUtil.waitFor(2);
        getHomepageReturnPremiums(info);

        return true;
    }



    //플랜 유형 선택
    private void setMobilePlanType(String myPlanType) throws Exception {

        myPlanType = myPlanType.trim();

        try {
            //플랜유형 버튼 찾기
            WebElement button = driver.findElement(By.xpath("//div[@id='plan_button_area']/button[contains(., '" + myPlanType + "')]"));

            WaitUtil.waitFor(3);

//            ((JavascriptExecutor) driver).executeScript("scrollTo(0, document.body.scrollHeight / 2);");
            ((JavascriptExecutor) driver).executeScript("scrollTo(0, 300);");

            WaitUtil.waitFor(3);

            //버튼 클릭 후에 로딩바 대기
            waitElementToBeClickable(button).click();
            waitMobileLoadingBar();

            //실제 홈페이지에서 클릭된 플랜유형이 잘 클릭되었는지 확인
            //홈페이지의 클릭된 플랜유형 버튼을 찾는다.
            button = driver.findElement(By.xpath("//div[@id='plan_button_area']/button[@class[contains(., 'is-active')]]"));

            //홈페이지에서 클릭된 플랜유형 버튼의 이름
            String targetPlanType = button.getText().trim();

            logger.info("=======================================================");
            logger.info("가입설계 플랜유형 : {}", myPlanType);
            logger.info("홈페이지 플랜유형 : {}", targetPlanType);
            logger.info("=======================================================");

            if (targetPlanType.contains(myPlanType)) {
                logger.info("=======================================================");
                logger.info("가입설계 플랜유형({}) == 홈페이지 플랜유형({})", myPlanType, targetPlanType);
                logger.info("=======================================================");
            } else {
                logger.info("=======================================================");
                logger.info("가입설계 플랜유형({}) ≠ 홈페이지 플랜유형({})", myPlanType, targetPlanType);
                logger.info("=======================================================");
                throw new PlanTypeMismatchException("플랜유형 불일치");
            }

        } catch(NoSuchElementException e) {
            String errorMsg = "플랜유형(" + myPlanType + ")이 존재하지 않습니다.";
            logger.info(errorMsg);
            throw new NotFoundPlanTypeException(errorMsg);
        }
    }



    //보험기간 설정
    private void setMobileInsTerm(String myInsTerm) throws Exception {

        String targetInsTerm = driver.findElement(By.id("poltermtext")).getText();

        logger.info("=======================================================");
        logger.info("가입설계 보험기간 : {}", myInsTerm);
        logger.info("홈페이지 설정된 보험기간 : {}", targetInsTerm);
        logger.info("=======================================================");

        if (targetInsTerm.contains(myInsTerm)) {
            logger.info("=======================================================");
            logger.info("가입설계 보험기간({}) == 홈페이지 보험기간({})", myInsTerm, targetInsTerm);
            logger.info("=======================================================");
        } else {
            logger.info("=======================================================");
            logger.info("가입설계 보험기간({}) ≠ 홈페이지 보험기간({})", myInsTerm, targetInsTerm);
            logger.info("=======================================================");
            throw new InsTermMismatchException("보험기간 불일치");
        }
    }



    //납입기간 설정
    private void setMobileNapTerm(String myNapTerm) throws Exception {

        myNapTerm = myNapTerm.trim() + "납";

        WebElement element = driver.findElement(By.xpath("//dd[@id='paytermtext']"));
        WebElement button = null;
        String targetNapTerm = "";

        boolean toSetNapTerm = existElement(element, By.xpath(".//button[@class='c-btn-select']"));

        if (toSetNapTerm) {
            logger.info("납입기간을 직접 세팅해야합니다.");

            //납입기간 설정을 위해 버튼 클릭
            button = element.findElement(By.xpath(".//button[@class='c-btn-select']"));
            moveToElementByJavascriptExecutor(button);
            waitElementToBeClickable(button).click();
            WaitUtil.waitFor(1);


            //가입설계의 납입기간으로 클릭
            element = driver.findElement(By.xpath("//div[@class[contains(., 'is-active')]]//li[@class='bs-select-list']/button[contains(., '" + myNapTerm + "')]"));
            waitElementToBeClickable(element).click();
            WaitUtil.waitFor(2);


            element = driver.findElement(By.xpath("//dd[@id='paytermtext']//button[@class='c-btn-select']"));

        } else {
            logger.info("납입기간이 고정입니다.");
        }

        targetNapTerm = element.getText().trim();

        logger.info("=======================================================");
        logger.info("가입설계 납입기간 : {}", myNapTerm);
        logger.info("홈페이지 설정된 납입기간 : {}", targetNapTerm);
        logger.info("=======================================================");

        if (targetNapTerm.equals(myNapTerm)) {
            logger.info("=======================================================");
            logger.info("가입설계 납입기간({}) == 홈페이지 납입기간({})", myNapTerm, targetNapTerm);
            logger.info("=======================================================");

        } else {
            logger.info("=======================================================");
            logger.info("가입설계 납입기간({}) ≠ 홈페이지 납입기간({})", myNapTerm, targetNapTerm);
            logger.info("=======================================================");

            throw new NapTermMismatchException("납입기간 불일치");
        }
    }



    //홈페이지 해약환급금 조회 메서드
    protected void getHomepageReturnPremiums(CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='dsGrdSurrefnDtl']/tr"));
        for (WebElement tr : trList) {
            String term = tr.findElement(By.xpath("./th[1]")).getText();
            String premiumSum = tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");
            String returnMoney = tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElement(By.xpath("./td[3]")).getText();

            logger.info("=========해약환급금========");
            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("해약환급금 : {}", returnMoney);
            logger.info("해약환급률 : {}", returnRate);
            logger.info("===========================");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney;
        }

        info.planReturnMoneyList = planReturnMoneyList;
        logger.info("만기환급금 : {}원", info.returnPremium);
    }



    private void compareTreaties(List<CrawlingTreaty> myTreatyList) throws Exception {

        HashMap<String, String> homepageTreatyMap = new HashMap<>();
        HashMap<String, String> myTreatyGrtMap = new HashMap<>();

        //가입설계 특약 정보를 map에 담는다.
        for (CrawlingTreaty myTreaty : myTreatyList) {
            String myTreatyName = myTreaty.treatyName;
            String  myTreatyAssureMoney = String.valueOf(myTreaty.assureMoney);
            int idx = myTreatyName.indexOf("/");
            String myTreatyGrtName = myTreatyName.substring(0, idx);
            String myTreatyTndName = myTreatyName.substring(idx + 1);

            myTreatyGrtMap.put(myTreatyGrtName, myTreatyAssureMoney);
        }

        //먼저 가입설계에 존재하는 특약들만 가입처리하고, 나머지는 미가입처리 한다.
        List<WebElement> liList = driver.findElements(By.xpath("//ul[@id='table_area']//li[@class[contains(., 'c-box c-box--border-type4')]]"));
        for (WebElement li : liList) {

            //li에 포함되는 하위 element들
            WebElement checkbox = null;                     //checkbox element
            WebElement label = null;                        //checkbox를 클릭하기 위한 label element
            WebElement ul = null;                           //홈페이지 grt명과 grt가입금액을 담고 있는 ul element
            String targetGrtName = "";                      //홈페이지 grt명
            String targetGrtAssureMoney = "";               //홈페이지 grt가입금액
            boolean isChecked = false;                      //체크박스 체크여부
            boolean toJoin = false;                         //가입해야하는 보장인지 여부(true : 가입해야하는 보장, false : 미가입해야하는 보장)
            boolean toSetAssureMoney = false;               //보장금액을 직접 세팅해야하는지 여부(true : 보장금액 직접 세팅, false : 보장금액 고정)

            if (li.isDisplayed()) {
                //li element가 보이도록 스크롤 이동
                moveToElementByJavascriptExecutor(li);

                checkbox = li.findElement(By.xpath(".//input[@type='checkbox']"));
                label = driver.findElement(By.xpath("//label[@for='" + checkbox.getAttribute("id") + "']"));
                ul = li.findElement(By.xpath(".//ul[@class='item-group']"));
                targetGrtName = ul.findElement(By.xpath(".//strong")).getText().trim();
                isChecked = checkbox.isEnabled() && checkbox.isDisplayed() && checkbox.isSelected();

                //홈페이지 보장명이 가입설계에 존재하고 체크해제 되어있는 경우에만 해당 보장을 체크해야함.
                toJoin = myTreatyGrtMap.containsKey(targetGrtName);
                if (!toJoin && isChecked) {

                    //가입 안해야하는데 체크되어 있는 경우에만 클릭
                    logger.info("보장명 : {} 미가입 처리", targetGrtName);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

                } else if (toJoin && !isChecked) {

                    //가입 해야하는데 체크 해제되어 있는 경우에만 클릭
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

                }

                //보장금액을 직접 세팅해야하는 경우인지 판단
                toSetAssureMoney = existElement(li, By.xpath(".//button[@class='c-btn-select']"));
                if (toSetAssureMoney) {
                    //보장금액을 직접 세팅하는 경우
                    WebElement grtAssureMoneyButton = li.findElement(By.xpath(".//button[@class='c-btn-select']"));
                    waitElementToBeClickable(grtAssureMoneyButton).click();
                    WaitUtil.waitFor(2);

                    //보장금액 값을 설정해준다.
                    String toSetGrtAssureMoney = myTreatyGrtMap.get(targetGrtName);
                    List<WebElement> buttons = driver.findElements(By.xpath("//div[@class[contains(., 'is-active')]]//div[@class='c-modal__body']//li//button"));
                    for (WebElement button : buttons) {
                        targetGrtAssureMoney = button.getText();
                        targetGrtAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetGrtAssureMoney));

                        if (targetGrtAssureMoney.equals(toSetGrtAssureMoney)) {
                            waitElementToBeClickable(button).click();
                            WaitUtil.waitFor(2);
                            break;
                        }
                    }
                }
            }
        }

        //설정한 특약 정보를 바탕으로 다시 계산하기 버튼을 클릭한다.
        logger.info("다시 계산하기 버튼 클릭");
        element = driver.findElement(By.id("btnCalc"));
        moveToElementByJavascriptExecutor(element);
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();

        // 다시 계산하기 버튼을 누르면 가입하는 보장들의 보장금액이 화면상에서 업데이트 된다.
        // 업데이트된 보장 정보를 map에 담는다.
        liList = driver.findElements(By.xpath("//ul[@id='table_area']//li[@class[contains(., 'c-box c-box--border-type4')]]"));
        for (WebElement li : liList) {
            //li에 포함되는 하위 element들
            WebElement checkbox = null;                     //checkbox element
            WebElement ul = null;                           //홈페이지 grt명과 grt가입금액을 담고 있는 ul element
            String targetGrtName = "";                      //홈페이지 grt명
            String targetGrtAssureMoney = "";               //홈페이지 grt가입금액

            checkbox = li.findElement(By.xpath(".//input[@type='checkbox']"));
            ul = li.findElement(By.xpath(".//ul[@class='item-group']"));
            targetGrtName = ul.findElement(By.xpath(".//strong")).getText().trim();
            boolean isChecked = checkbox.isEnabled() && checkbox.isDisplayed() && checkbox.isSelected();
            boolean isFixedGrtAssureMoney = false;

            if (li.isDisplayed() && isChecked) {
                //체크되어 있는 보장들에 대해서만 보장정보를 크롤링한다.

                isFixedGrtAssureMoney = existElement(ul, By.xpath(".//span[@class='guarantee']"));

                if (isFixedGrtAssureMoney) {
                    //보장금액이 우측에 고정으로 세팅되어 있는 경우
                    targetGrtAssureMoney = ul.findElement(By.xpath(".//span[@class='guarantee']")).getText();
                }

                if (!isFixedGrtAssureMoney || targetGrtAssureMoney.isEmpty()) {
                    //보장금액 정보가 구좌로 되어있는 경우

                    WebElement dd = li.findElement(By.xpath(".//dd[contains(., '구좌')]"));
                    targetGrtAssureMoney = dd.getText().replaceAll("[^0-9]", "");
                    targetGrtAssureMoney = String.valueOf(Integer.parseInt(targetGrtAssureMoney) * 100000);
                }

                targetGrtAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetGrtAssureMoney));
                homepageTreatyMap.put(targetGrtName, targetGrtAssureMoney);
            }
        }

        //홈페이지 보장금액과 가입설계 보장금액 비교(homepageTreatyMap 과 myTreatyGrtMap 정보 비교)
        List<String> homepageGrtNameList = new ArrayList<>(homepageTreatyMap.keySet());
        List<String> myGrtNameList = new ArrayList<>(myTreatyGrtMap.keySet());
        List<String> copiedHomepageGrtNameList = new ArrayList<>(homepageGrtNameList);

        List<String> toRemoveGrtNameList = null;
        List<String> toAddGrtNameList = null;
        List<String> samedGrtNameList = null;

        homepageGrtNameList.retainAll(myGrtNameList);
        samedGrtNameList = new ArrayList<>(homepageGrtNameList);
        homepageGrtNameList = getCloneList(copiedHomepageGrtNameList);

        homepageGrtNameList.removeAll(myGrtNameList);
        toAddGrtNameList = new ArrayList<>(homepageGrtNameList);
        homepageGrtNameList = getCloneList(copiedHomepageGrtNameList);

        myGrtNameList.removeAll(homepageGrtNameList);
        toRemoveGrtNameList = new ArrayList<>(myGrtNameList);

        //홈페이지와 가입설계 보장명이 동일한 경우에만 금액비교
        if (toAddGrtNameList.size() == 0 && toRemoveGrtNameList.size() == 0) {
            for (String key : samedGrtNameList) {
                String homepageGrtName = key;
//                String myGrtName = key;
                String homepageGrtAssureMoney = homepageTreatyMap.get(key);
                String myGrtAssureMoney = myTreatyGrtMap.get(key);

                if (homepageGrtAssureMoney.equals(myGrtAssureMoney)) {
                    logger.info("보장명 : {}", homepageGrtName);
                    logger.info("가입설계 보장금액 : {} == 홈페이지 설정된 보장금액 : {}", myGrtAssureMoney, homepageGrtAssureMoney);
                } else {
                    logger.info("보장명 : {}", homepageGrtName);
                    logger.info("가입설계 보장금액 : {}", myGrtAssureMoney);
                    logger.info("홈페이지 설정된 보장금액 : {}", homepageGrtAssureMoney);
                    throw new TreatyMisMatchException("보장금액 불일치");
                }
            }
        } else {
            logger.info("추가해야할 보장명 리스트 : {}", toAddGrtNameList.toString());
            logger.info("삭제해야할 보장명 리스트 : {}", toRemoveGrtNameList.toString());

            throw new TreatyMisMatchException("보장명 불일치");
        }
    }



    private List<String> getCloneList(List<String> originList) {

        List<String> clonedSet = new ArrayList<>();

        for (String element : originList) {
            clonedSet.add(element);
        }

        return clonedSet;
    }
}
