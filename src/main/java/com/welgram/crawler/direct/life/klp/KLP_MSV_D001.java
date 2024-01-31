package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 교보라이프플래닛 - (무)만원부터m저축보험Ⅱ
 */
public class KLP_MSV_D001 extends CrawlingKLP {



    public static void main(String[] args) throws InterruptedException {
        executeCommand(new KLP_MSV_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }


    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

            WaitUtil.loading(2);
            logger.debug("보험료 설정");
            setPremium(info.assureMoney);

            WaitUtil.loading(2);
            logger.debug("보험기간 확인");
            setInsTerm(info.insTerm);

            WaitUtil.loading(2);
            logger.debug("납입기간 확인");
            setNapTerm(info.napTerm);

            WaitUtil.loading(2);
            logger.debug("성별 설정");
            setGender(info.gender);

            WaitUtil.loading(2);
            logger.debug("생년월일 설정");
            setBirth(info.birth);

            WaitUtil.loading(2);
            logger.debug("보험료 확인");
            setConfirmPremium();

//      logger.debug("상품마스터 조회");
//      getTreaties(info, exeType);

            // 보험료
            WaitUtil.loading(2);
            logger.debug("보험료 조회");
            getPremium(info);

            // 해약환급금(예시표)
            logger.debug("해약환급금(예시표)");
            getReturns("cancel1", info);

    }


    // 가입금액
    protected void setPremium(String premium) throws Exception {
        element = driver.findElement(By.id("pltcPrm"));

        Integer a = Integer.valueOf(premium) / 10000;
        element.clear();
        element.sendKeys(String.valueOf(a));
    }

    private void setInsTerm(String insTerm) {
        elements = driver.findElements(By.cssSelector("#radio_area_year1 > label"));
        for (WebElement el : elements) {
            String insTermLabel = el.getText();

            if (insTerm.indexOf(insTermLabel) > -1) {
                el.click();
                break;
            }
        }
    }


    protected void setNapTerm(String napTerm) {
        elements = driver.findElements(By.cssSelector("#radio_area_year2 > label"));

        for (WebElement el : elements) {
            String napTermLabel = el.getText();

            if (napTerm.indexOf(napTermLabel) > -1) {
                el.click();
                break;
            }
        }
    }

    private void getPremium(CrawlingProduct info) {
//    element = driver.findElement(By.id("div_monthlyPrm"));
//    String assureMoney = element.getAttribute("innerText");

        info.treatyList.get(0).monthlyPremium = info.assureMoney;
    }

    /**
     * 보험료확인
     */
    private void setConfirmPremium() throws InterruptedException {

        driver.findElement(By.id("btn_recal3Chk")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea")));

    }

    protected void getReturns(String id, CrawlingProduct info) throws InterruptedException {

        element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
//		element = driver.findElement(By.id(id));
        element.click();

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            logger.debug(subHandle);
            WaitUtil.loading(2);
        }

        driver.switchTo().window(subHandle);
        elements = driver.findElements(By.cssSelector("#listArea > tr"));

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        if (elements.size() > 0) {

            for (int i = 0; i < elements.size(); i++) {

                WebElement tr = elements.get(i);
                List<WebElement> tdList = tr.findElements(By.tagName("td"));

                logger.info("______해약환급급[{}]_______ ", i);

                String term = tdList.get(0).getAttribute("innerText"); // 경과기간

                String premiumSum = tdList.get(1).getAttribute("innerText"); // 납입보험료
                String returnMoney = tdList.get(2).getAttribute("innerText"); // 해약환급금
                String returnRate = tdList.get(3).getAttribute("innerText"); // 환급률

                //String returnMoneyAge = tdList.get(4).getAttribute("innerText"); // 해약환급금
                //String returnRateAge = tdList.get(5).getAttribute("innerText"); // 환급률

                //String returnMoney = tdList.get(6).getAttribute("innerText"); // 해약환급금
                //String returnRate = tdList.get(7).getAttribute("innerText"); // 환급률

                logger.info("|--경과기간: {}", term);

                logger.info("|--납입보험료: {}", premiumSum);
//        logger.info("|--최저보증이율 기준 해약환급금: {}", returnMoneyMin);
//        logger.info("|--최저보증이율 기준 환급률: {}", returnRateMin);
//
//        logger.info("|--Min 해약환급금: {}", returnMoneyAge);
//        logger.info("|--Min 환급률: {}", returnRateAge);

                logger.info("|--현재공시이율 해약환급금: {}", returnMoney);
                logger.info("|--현재공시이율 환급률: {}", returnRate);

                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term); // 경과기간

                planReturnMoney.setPremiumSum(premiumSum);      // 납입보험료
                planReturnMoney.setReturnMoneyMin(returnMoney); // 환급금
                planReturnMoney.setReturnRateMin(returnRate); // 환급률

                planReturnMoney.setReturnMoneyAvg(returnMoney); // 환급금
                planReturnMoney.setReturnRateAvg(returnRate); // 환급률

                planReturnMoney.setReturnMoney(returnMoney); // 환급금
                planReturnMoney.setReturnRate(returnRate); // 환급률

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
        } else {

            logger.info("해약환급금 내역이 없습니다.");
        }
    }

    @Override
    protected void setGender(int gender) {
        String genderName = gender == 0 ? "#genderDiv > label:nth-child(2)" : "#genderDiv > label:nth-child(4)";

        driver.findElement(By.cssSelector(genderName)).click();
    }

    private void setBirth(String birth) {
        element = driver.findElement(By.cssSelector("#plnnrBrdt"));
        element.clear();
        element.sendKeys(birth);
    }

    /**
     * 주계약 상품마스터 조회
     */
    @Override
    protected void getMainTreaty(CrawlingProduct info) {

        List<String> assureMoneys = new ArrayList<>(
                Arrays.asList("1만원", "5만원", "10만원", "50", "100만원", "500만원", "1000만원")); // 진단보험금

        String minAssureMoney = assureMoneys.get(0);
        String maxAssureMoney = assureMoneys.get(assureMoneys.size() - 1);

        List<String> insTerms = new ArrayList<>(Arrays.asList("1년", "2년", "3년")); // 보험기간
        List<String> napTerms = new ArrayList<>(Arrays.asList("1년", "2년", "3년")); // 납입기간
        List<String> napCycles = new ArrayList<>(Arrays.asList("월납")); // 납입주기

        ProductMasterVO productMasterVO = new ProductMasterVO();
        productMasterVO.setProductId(info.productCode);
        productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
        productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
        productMasterVO.setProductGubuns("주계약");
        productMasterVO.setSaleChannel(info.getSaleChannel());
        productMasterVO.setProductName(info.productName);
        productMasterVO.setInsTerms(insTerms);
        productMasterVO.setNapTerms(napTerms);
        productMasterVO.setNapCycles(napCycles);
        productMasterVO.setAssureMoneys(assureMoneys);
        productMasterVO.setMinAssureMoney(minAssureMoney);
        productMasterVO.setMaxAssureMoney(maxAssureMoney);
        productMasterVO.setCompanyId(info.getCompanyId());

        logger.info("상품마스터 :: " + productMasterVO.toString());
        info.getProductMasterVOList().add(productMasterVO);

    }

    /**
     * 특약 상품마스터 조회
     */
    @Override
    protected void getSubTreaty(CrawlingProduct info) {

        logger.info("특약 없음..");

    }

}
