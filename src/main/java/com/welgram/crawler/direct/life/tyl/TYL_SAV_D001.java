package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


// 2022.11.02           | 최우진               | 대면_저축보험
// TYL_SAV_D001         | (무배당)수호천사온라인 더좋은저축보험
public class TYL_SAV_D001 extends CrawlingTYL {

    public static void main(String[] args) {
        executeCommand(new TYL_SAV_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("생년월일 설정 : {}", info.getFullBirth());
        setHomepageBirth(info.getFullBirth());

        logger.info("성별 설정 : {}", (info.getGender() == MALE) ? "남자" : "여자");
        setHomepageGender(info.getGender());

        logger.info("월납입금액 설정 : {}", ( Integer.parseInt(info.getAssureMoney()) / 10000 ) + "만원");
        setHomepageAssureMoney(info, By.id("input_pay"));

        logger.info("납입기간 설정 : {}", info.getNapTerm());
        setHomepageNapTerm(info.getNapTerm());

        logger.info("확인하기 버튼 클릭!");
        homepageOkBtnClick();
        WaitUtil.waitFor(2);

        logger.info("내 보험기간과 홈페이지 보험기간 일치여부 확인");
        matchInsTerm(info.getInsTerm());

        logger.info("주계약 보험료 설정");
        setHomepageMonthlyPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 조회");
        WebElement returnPremiumBtn = driver.findElement(By.cssSelector(".col-xs-4.active.on")).findElement(By.cssSelector(".row.btn-sub button:nth-child(2)"));
        getHomepageFullReturnPremiums(info, returnPremiumBtn);

        logger.info("스크래핑 완료");

        return true;

    }



    // todo 2022.04.14 | 최우진 | 크롤링 수정
    //홈페이지용 납입기간 설정 메서드
    protected void setHomepageNapTerm(String napTerm) throws Exception {

        btnClick(By.xpath("//span[contains(.,'" + napTerm + "')]/parent::label"));

    }



    //홈페이지용 주계약 보험료 설정 메서드
    private void setHomepageMonthlyPremium(CrawlingProduct info) {

        info.treatyList.get(0).monthlyPremium = info.assureMoney;

        WebElement activeDivEl = driver.findElement(By.cssSelector(".col-xs-4.active.on"));
        String returnPremium = activeDivEl.findElement(By.cssSelector(".row.won .col-xs-12.text-right")).getText().replaceAll("[^0-9]", "").trim();

        logger.info("만기환급금 : {}원", returnPremium);
        info.returnPremium = returnPremium;

    }



    //내 보험기간과 홈페이지 보험기간이 일치하는 여부 검사
    private void matchInsTerm(String insTerm) throws Exception {

        String myInsTerm = insTerm.trim();
        String targetInsTerm = "";

        //활성화 되어있는(=클릭된) div 박스
        WebElement activeDivEl = driver.findElement(By.cssSelector(".col-xs-4.on.active"));

        //활성화 된 div박스로부터 보험기간 text를 찾는다. text가 일치하지 않으면 내 가입설계가 잘못되었다는 뜻
        try {
            WebElement insTermEl = activeDivEl.findElement(By.cssSelector(".col-xs-12.info")).findElement(By.xpath(".//dt[text()='보험기간']/../dd[contains(., '" + myInsTerm + "')]"));

            myInsTerm = myInsTerm.replaceAll("[^0-9]", "");
            targetInsTerm = insTermEl.getText().replaceAll("[^0-9]", "").trim();

            if (!myInsTerm.equals(targetInsTerm)) {
                throw new InsTermMismatchException("가입설계 보험기간과 홈페이지의 보험기간이 일치하지 않습니다.");
            } else {
                logger.info("내 보험기간({}년) == 홈페이지 보험기간({}년)", myInsTerm, targetInsTerm);
            }

        } catch (NoSuchElementException e) {
            logger.info("div박스 안에 보험기간이 나와있지 않습니다.");
        }

    }



    @Override
    //홈페이지용 해약환급금 조회 및 세팅 메서드(경과기간, 납입보험료, 최저.평균.공시 정보 모두 나온 경우 사용)
    protected void getHomepageFullReturnPremiums(CrawlingProduct info, WebElement element) throws Exception {
        homepageBtnClick(element);		//해약환급금 버튼 클릭

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trList = driver.findElements(By.cssSelector(".table.text-center tbody tr"));
        for (WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().trim();
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText().trim();
            String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText().trim();
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText().trim();
            String returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText().trim();
            String returnMoney = tr.findElements(By.tagName("td")).get(6).getText().trim();
            String returnRate = tr.findElements(By.tagName("td")).get(7).getText().trim();

            logger.info("______해약환급급__________ ");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--최저납입보험료: {}", premiumSum);
            logger.info("|--최저해약환급금: {}", returnMoneyMin);
            logger.info("|--최저해약환급률: {}", returnRateMin);
            logger.info("|--평균해약환급금: {}", returnMoneyAvg);
            logger.info("|--평균해약환급률: {}", returnRateAvg);
            logger.info("|--환급률: {}", returnRate);
            logger.info("|_______________________");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
            planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);

            // 보험기간에 해당하는 해약환급금을 returnPremium 변수에 세팅한다.
            String insTerm = info.getInsTerm().trim();

            if (term.equals(insTerm)) {
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                logger.info("만기환급금 세팅 :: {}", info.getReturnPremium());
            }
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.info(info.getInsTerm() + " 보험기간의 해약환급금 : {}원", info.getReturnPremium());

        btnClick(By.cssSelector(".ui-dialog-titlebar-close"));
        logger.info("해약환급금 스크래핑완료");

    }

}

