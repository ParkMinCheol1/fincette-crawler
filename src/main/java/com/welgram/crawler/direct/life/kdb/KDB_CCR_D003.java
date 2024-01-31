package com.welgram.crawler.direct.life.kdb;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/** (무) KDB다이렉트암보험
 *
 *  KDB_CCR_D003 상품의 공시실은 존재하지않아 Homepage크롤링 코드만 작성
 *  (공시실에서 현 상품의 보험료계산 클릭 시 Homepage크롤링 브라우저와 동일한 Web브라우저가 화면에 뜬다. - 21.01.15. 확인)
 *
 */
public class KDB_CCR_D003 extends CrawlingKDBDirect {

    public static void main(String[] args) {
        executeCommand(new KDB_CCR_D003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        waitLoadingBar();
        WaitUtil.waitFor(2);

        // 광고 창 닫기
//        WebElement dialog = driver.findElement(By.xpath("//*[@id='dialogMainEvent2']/div/div/div"));
//        if (dialog.isDisplayed()) {
//            driver.findElement(By.xpath("//*[@id='dialogMainEvent2']/div/div/div/a")).click();
//        }
//        WaitUtil.waitFor(3);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 확인 버튼 클릭!");
        $button = driver.findElement(By.id("btnCal"));
        click($button);

        logger.info("가입금액 설정");
        setButtonAssureMoney(info);

        logger.info("유형 선택");
        setPlan(info);

        logger.info("보험 기간 선택");
        setRadioButtonInsTerm(info.insTerm + "만기");

        logger.info("납입 기간 선택");
        setRadioButtonNapTerm(info.napTerm + "납");

        logger.info("결과 확인하기 버튼 클릭!");
        $button = driver.findElement(By.id("btnRslt"));
        click($button);

        logger.info("월 보험료 크롤링");
        crawlPremium(info, By.id("monthAmt"));

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 조회");
        returnPremiumKindDivision(info);

//        getReturnPremiums(info);

        return true;
    }

    /*
    * 상품 타입별 해약환급금 테이블의 td 개수와 테이블(tbody)의 태그값이 전부 다르다.
    * 상품별로 적절한 해약환급금 조회 메서드를 호출한다.
    *
    *  @param info : 크롤링 상품
    * */
    private void returnPremiumKindDivision(CrawlingProduct info) throws Exception {
        String productType = info.textType;     //갱신형, 순수형, 순수형(해약환급금 미지급형)

        if(productType.equals("갱신형")) {
            crawlReturnMoneyListTwo(info, By.cssSelector("#cancelRefund1 tr"));
        } else if(productType.equals("표준형")) {
            crawlReturnMoneyListTwo(info, By.cssSelector("#cancelRefund2 tr"));
        } else {
            crawlReturnMoneyListTwoPlus(info, By.cssSelector("#cancelRefund2 tr"));
        }
    }

    public void crawlReturnMoneyListTwoPlus(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = (By) obj[1];
        WebElement $a = null;

        try{
            $a = driver.findElement(By.id("btnShowDetail"));
            click($a);

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            List<WebElement> trList = driver.findElements(location);
            for (WebElement tr : trList) {
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(2).getText();

                logger.info("______해약환급급__________ ");
                logger.info("|--경과기간: {}", term);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

}
