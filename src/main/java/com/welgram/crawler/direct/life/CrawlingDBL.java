package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.PlanReturnMoney;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingDBL extends SeleniumCrawler {

    protected void getReturnsTable(CrawlingProduct info) {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        //최저
        elements = driver.findElements(By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
        int Size = elements.size();


        for (int i=0; i<Size; i++) {
            //최저
            String term = elements.get(i).findElements(By.tagName("th")).get(0).getAttribute("innerText");               // 경과기간
            String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1).getAttribute("innerText");         // 납입보험료
            //현재공시
            String returnMoney = elements.get(i).findElements(By.tagName("td")).get(2).getAttribute("innerText");        // 현재해약환급금
            String returnRate = elements.get(i).findElements(By.tagName("td")).get(3).getAttribute("innerText");         // 현재해약환급률

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
            planReturnMoney.setReturnMoney(returnMoney); // 환급금
            planReturnMoney.setReturnRate(returnRate); // 환급률

            planReturnMoneyList.add(planReturnMoney);

            if (i == elements.size()-1) {

                if(info.productKind.contains("순수")){
                    info.returnPremium = "0";
                    logger.info("환급유형 : "+info.productKind);
                    logger.info("순수보장형의 경우는 만기시에 환급금이 0원으로 고정");
                    logger.info("만기환급금 : " + info.returnPremium);
                }else{
                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                    logger.info("만기환급금 : " + info.returnPremium);
                }
            }
        }
        info.planReturnMoneyList = planReturnMoneyList;
    }

    protected void getAllReturnsTable(CrawlingProduct info) {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        //최저
        elements = driver.findElements(By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
        int Size = elements.size();


        for (int i=0; i<Size; i++) {

            String term = elements.get(i).findElements(By.tagName("th")).get(0).getAttribute("innerText");               // 경과기간
            String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1).getAttribute("innerText");         // 납입보험료

            //최저공시
            String returnMoneyMin = elements.get(i).findElements(By.tagName("td")).get(2).getAttribute("innerText");        // 최저해약환급금
            String returnRateMin = elements.get(i).findElements(By.tagName("td")).get(3).getAttribute("innerText");         // 최저해약환급률

            //현재공시
            String returnMoney = elements.get(i).findElements(By.tagName("td")).get(4).getAttribute("innerText");        // 현재해약환급금
            String returnRate = elements.get(i).findElements(By.tagName("td")).get(5).getAttribute("innerText");         // 현재해약환급률

            //평균공시
            String returnMoneyAvg = elements.get(i).findElements(By.tagName("td")).get(6).getAttribute("innerText");        // 평균해약환급금
            String returnRateAvg = elements.get(i).findElements(By.tagName("td")).get(7).getAttribute("innerText");         // 평균해약환급률

            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);

            logger.info("|--최저해약환급금: {}", returnMoneyMin);
            logger.info("|--최저해약환급률: {}", returnRateMin);

            logger.info("|--현재해약환급금: {}", returnMoney);
            logger.info("|--현재해약환급률: {}", returnRate);

            logger.info("|--평균해약환급금: {}", returnMoneyAvg);
            logger.info("|--평균해약환급률: {}", returnRateAvg);

            logger.info("|_______________________");


            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender(info.getGenderEnum().name());
            planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

            planReturnMoney.setTerm(term); // 경과기간
            planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계

            planReturnMoney.setReturnMoney(returnMoney); // 환급금
            planReturnMoney.setReturnRate(returnRate); // 환급률

            planReturnMoney.setReturnMoneyMin(returnMoneyMin); // 최저환급금
            planReturnMoney.setReturnRateMin(returnRateMin); //최저 환급률

            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg); // 평균환급금
            planReturnMoney.setReturnRateAvg(returnRateAvg); // 평균환급률

            planReturnMoneyList.add(planReturnMoney);

            /*if (i == elements.size()-1) {

                if(info.productKind.contains("순수")){
                    info.returnPremium = "0";
                    logger.info("환급유형 : "+info.productKind);
                    logger.info("순수보장형의 경우는 만기시에 환급금이 0원으로 고정");
                    logger.info("만기환급금 : " + info.returnPremium);
                }else{
                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                    logger.info("만기환급금 : " + info.returnPremium);
                }
            }*/
        }
        info.planReturnMoneyList = planReturnMoneyList;
    }


    protected void getTRMReturnsTable(CrawlingProduct info) {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        //최저
        elements = driver.findElements(By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
        int Size = elements.size();


        for (int i=0; i<Size; i++) {

            String term = elements.get(i).findElements(By.tagName("th")).get(0).getAttribute("innerText");               // 경과기간
            String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1).getAttribute("innerText");         // 납입보험료

            //최저공시
            String returnMoneyMin = elements.get(i).findElements(By.tagName("td")).get(2).getAttribute("innerText");        // 최저해약환급금
            String returnRateMin = elements.get(i).findElements(By.tagName("td")).get(3).getAttribute("innerText");         // 최저해약환급률

            //평균공시
            String returnMoneyAvg = elements.get(i).findElements(By.tagName("td")).get(4).getAttribute("innerText");        // 평균해약환급금
            String returnRateAvg = elements.get(i).findElements(By.tagName("td")).get(5).getAttribute("innerText");         // 평균해약환급률

            //현재공시
            String returnMoney = elements.get(i).findElements(By.tagName("td")).get(6).getAttribute("innerText");        // 현재해약환급금
            String returnRate = elements.get(i).findElements(By.tagName("td")).get(7).getAttribute("innerText");         // 현재해약환급률

            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);

            logger.info("|--최저해약환급금: {}", returnMoneyMin);
            logger.info("|--최저해약환급률: {}", returnRateMin);

            logger.info("|--현재해약환급금: {}", returnMoney);
            logger.info("|--현재해약환급률: {}", returnRate);

            logger.info("|--평균해약환급금: {}", returnMoneyAvg);
            logger.info("|--평균해약환급률: {}", returnRateAvg);

            logger.info("|_______________________");


            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender(info.getGenderEnum().name());
            planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

            planReturnMoney.setTerm(term); // 경과기간
            planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계

            planReturnMoney.setReturnMoney(returnMoney); // 현재환급금
            planReturnMoney.setReturnRate(returnRate); // 현재환급률

            planReturnMoney.setReturnMoneyMin(returnMoneyMin); // 최저환급금
            planReturnMoney.setReturnRateMin(returnRateMin); //최저 환급률

            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg); // 평균환급금
            planReturnMoney.setReturnRateAvg(returnRateAvg); // 평균환급률

            planReturnMoneyList.add(planReturnMoney);

        }
        info.planReturnMoneyList = planReturnMoneyList;
    }



    protected void getWLFReturnsTable(CrawlingProduct info) {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        //최저
        elements = driver.findElements(By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
        int Size = elements.size();


        for (int i=0; i<Size; i++) {

            String term = elements.get(i).findElements(By.tagName("th")).get(0).getAttribute("innerText");               // 경과기간
            String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1).getAttribute("innerText");         // 납입보험료

            //현재공시
            String returnMoney = elements.get(i).findElements(By.tagName("td")).get(4).getAttribute("innerText");        // 현재해약환급금
            String returnRate = elements.get(i).findElements(By.tagName("td")).get(5).getAttribute("innerText");         // 현재해약환급률

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

            if(Size == (i+1)){
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                logger.info("만기환급금 : "+info.returnPremium);
            }

        }
        info.planReturnMoneyList = planReturnMoneyList;
    }


    protected void getReturnsTableWLF(CrawlingProduct info) {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        //최저
        elements = driver.findElements(By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));
        int Size = elements.size();


        for (int i=0; i<Size; i++) {

            String term = elements.get(i).findElements(By.tagName("th")).get(0).getAttribute("innerText");               // 경과기간
            String age = elements.get(i).findElements(By.tagName("td")).get(0).getAttribute("innerText");                // 나이
            String premiumSum = elements.get(i).findElements(By.tagName("td")).get(1).getAttribute("innerText");         // 납입보험료

            //현재공시
            String returnMoney = elements.get(i).findElements(By.tagName("td")).get(3).getAttribute("innerText");        // 현재해약환급금
            String returnRate = elements.get(i).findElements(By.tagName("td")).get(4).getAttribute("innerText");         // 현재해약환급률

            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--현재해약환급금: {}", returnMoney);
            logger.info("|--현재해약환급률: {}", returnRate);
            logger.info("|_______________________");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender(info.getGenderEnum().name());
            planReturnMoney.setInsAge(Integer.parseInt(age));
            planReturnMoney.setTerm(term); // 경과기간
            planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
            planReturnMoney.setReturnMoney(returnMoney); // 현재환급금
            planReturnMoney.setReturnRate(returnRate); // 현재환급률

            planReturnMoneyList.add(planReturnMoney);

            if(Size == (i+1)){
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                logger.info("만기환급금 : "+info.returnPremium);
            }

        }
        info.planReturnMoneyList = planReturnMoneyList;
    }


    /*********************************************************
     * <주상품에서 해당하는 상품 찾기 메소드> - 공시실
     * @param  info {CrawlingProduct} - 상품 크롤링 객체
     * @throws Exception - 특약 세팅시 예외처리
     *********************************************************/
    protected void compareProduct(CrawlingProduct info) throws Exception{

        try {

            String webProductName = info.productName.replace(" 1형(100%형,가입금액형)","").replace(" 3형(체증형,가입금액형)","");

            element = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div[2]/div/ul/li/a[contains(text(),'" + webProductName + "')]"));
            element.click();

            logger.info("========================");
            logger.info("{} 클릭!",info.productName);
            logger.info("========================");

            WaitUtil.waitFor(3);

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /*********************************************************
     * <해약환급금 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 해약환급금 세팅시 예외처리
     *********************************************************/
    protected void getReturnAllAssuremoies(CrawlingProduct info) throws Exception {
        try {

            WaitUtil.loading(3);
            elements = driver.findElements(By.xpath("//*[@id=\"refund_result\"]/div[2]/table/tbody/tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term;
                String premiumSum;
                String returnMoneyMin;
                String returnRateMin;
                String returnMoneyAvg;
                String returnRateAvg;
                String returnMoney;
                String returnRate;

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
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


    /*********************************************************
     * <해약환급금 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 해약환급금 세팅시 예외처리
     *********************************************************/
    protected void getReturnAssuremoies(CrawlingProduct info) throws Exception {
        try {

            WaitUtil.loading(3);
            //elements = driver.findElements(By.xpath("//*[@id=\"refund_result\"]/div[2]/table/tbody/tr"));

            elements = driver.findElements(By.cssSelector("#refund_result > div.tableType01 > table > tbody > tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            for (int i=0; i<elements.size(); i++) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term;
                String premiumSum;
                String returnMoney;
                String returnRate;

                term = elements.get(i).findElement(By.cssSelector("th")).getText();
                premiumSum = elements.get(i).findElement(By.cssSelector("td:nth-child(3)")).getText();
                returnMoney = elements.get(i).findElement(By.cssSelector("td:nth-child(5)")).getText();
                returnRate = elements.get(i).findElement(By.cssSelector("td:nth-child(6)")).getText();

                logger.info("경과기간   :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("환급률    :: {}", returnRate);
                logger.info("=================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                // 기본 해약환급금 세팅
                info.returnPremium = elements.get(i).findElement(By.cssSelector("td:nth-child(5)")).getText().replace(",", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


    protected void selGender(CrawlingProduct info){

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        String age = info.age;

        int calcAge = year - Integer.parseInt(age);

        logger.info("계산된 년도 : "+calcAge);

        if(calcAge < 2000){
            if(info.gender == 0){
                driver.findElement(By.cssSelector("#base_sexType")).sendKeys("1");
            }else{
                driver.findElement(By.cssSelector("#base_sexType")).sendKeys("2");
            }
        }else{
            if(info.gender == 0){
                driver.findElement(By.cssSelector("#base_sexType")).sendKeys("3");
            }else{
                driver.findElement(By.cssSelector("#base_sexType")).sendKeys("4");
            }
        }

    }


    protected void nextBtn() throws InterruptedException {
        logger.info("다음 버튼클릭");
        driver.findElement(By.cssSelector("#content > div > div.conDtail > div:nth-child(6) > div.btnArea > a:nth-child(2)")).click();
        WaitUtil.waitFor(3);
    }


}
