package com.welgram.crawler.direct.life.kdb;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


public abstract class CrawlingKDBMobile extends CrawlingKDBNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {

            //생년월일 element 찾기
            WebElement $birthInput = driver.findElement(By.id("pYmd"));

            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            //성별 element 찾기
            WebElement $genderDiv = driver.findElement(By.id("calculator_box"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            actualGenderText = ((JavascriptExecutor)driver).executeScript("return $('input[name=pGender]:checked').next().text();").toString().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try{
            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setRadioButtonInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try{
            WebElement $insTermUl = driver.findElement(By.id("PINSTERM_AREA"));

            List<WebElement> list = $insTermUl.findElements(By.tagName("li"));
            for (WebElement li : list) {
                String target = li.getText();

                if (target.equals(expectedInsTerm)) {
                    click(li);
                    break;
                }
            }

            actualInsTerm = ((JavascriptExecutor)driver).executeScript("return $('input[name=pInsTerm]:checked').next().text();").toString().trim();

            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setSelectBoxNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            WebElement $napTermSelectBox = driver.findElement(By.id("pNapTerm"));

            List<WebElement> list = $napTermSelectBox.findElements(By.tagName("option"));
            for (WebElement option : list) {
                String target = option.getText();

                if (target.equals(expectedNapTerm)) {
                    click(option);
                    logger.info(option + " 선택");
                    break;
                }
            }

            actualNapTerm = ((JavascriptExecutor)driver).executeScript("return $('#pNapTerm option:selected').text();").toString().trim();

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setRadioButtonNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            WebElement $napTermUl = driver.findElement(By.id("PNAPTERM_AREA"));

            List<WebElement> list = $napTermUl.findElements(By.tagName("li"));
            for (WebElement option : list) {
                String target = option.getText();

                if (target.equals(expectedNapTerm)) {
                    click(option);
                    break;
                }
            }

            actualNapTerm = ((JavascriptExecutor)driver).executeScript("return $('input[name=pNapTerm]:checked').next().text();").toString().trim();

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        String title = "보험료 크롤링";
        String script = "";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By monthlyPremium = (By) obj[1];
        CrawlingTreaty mainTreaty
            = info.getTreatyList()
                .stream()
                .filter(t -> t.productGubun.equals(ProductGubun.주계약))
                .findFirst()
                .get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumEm = driver.findElement(monthlyPremium);
            String premium = $premiumEm.getText().replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입 주기";
        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    public void setRadioButtonAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        WebElement $label = null;

        boolean existAssureMoney = false;

        try {
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000).replaceAll("[^0-9]", "");
            WebElement $assureMoneyUl = driver.findElement(By.id("PPAYAMT_AREA"));

            List<WebElement> list = $assureMoneyUl.findElements(By.tagName("li"));
            for (WebElement li : list) {
                String target = li.getText().replaceAll("[^0-9]", "");;

                if (target.equals(expectedAssureMoney)) {
                    click(li);
                    logger.info(li.getText() + " 선택");
                    existAssureMoney = true;
                    break;
                }
            }

            if (!existAssureMoney) {
                logger.info("선택지에 웰그램 가설의 {}원 가입금액이 없습니다.", info.assureMoney);
                logger.info("직접입력에 가입금액 입력");
                $label = $assureMoneyUl.findElement(By.xpath(".//label[text()='직접입력']"));
                click($label);

                WebElement $assureMoneyInput = driver.findElement(By.id("pPayAmtEtc"));

                actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);

                super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

            } else {
                String selectedAssureMoney = ((JavascriptExecutor)driver).executeScript("return $('input[name=pPayAmt]:checked').next().text();").toString().trim();

                actualAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(selectedAssureMoney) / 10000);

                super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setButtonAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        WebElement $label = null;

        try {
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney)).replaceAll("[^0-9]", "");
            WebElement $assureMoneyDiv = driver.findElement(By.id("ASSUREAMT_AREA"));

            List<WebElement> list = $assureMoneyDiv.findElements(By.tagName("li"));
            for (WebElement li : list) {
                String target = String.valueOf(MoneyUtil.toDigitMoney(li.getText())).replaceAll("[^0-9]", "");

                if (target.equals(expectedAssureMoney)) {
                    click(li);
                    logger.info(li.getText() + " 선택");
                    break;
                }
            }

            String selectedAssureMoney = ((JavascriptExecutor)driver).executeScript("return $('input[name=pAssureAmt]:checked').next().text();").toString().trim();

            actualAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(selectedAssureMoney));

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setSelectBoxAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        WebElement $label = null;

        try {
            WebElement $assureMoneySelectBox = driver.findElement(By.id("pAssureAmt"));

            List<WebElement> list = $assureMoneySelectBox.findElements(By.tagName("option"));
            for (WebElement option : list) {
                String target = String.valueOf(MoneyUtil.toDigitMoney(option.getText()));

                if (target.equals(expectedAssureMoney)) {
                    click(option);
                    logger.info(option + " 선택");
                    break;
                }
            }

            actualAssureMoney = ((JavascriptExecutor)driver).executeScript("return $('#pAssureAmt option:selected').text();").toString().trim();
            actualAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(actualAssureMoney));

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        String title = "연금개시나이";
        String expectedAnnuityAge = (String) obj[0];
        String actualAnnuityAge = "";

        try {
            WebElement $annuityAgeInput = driver.findElement(By.id("pBeginAge"));

            actualAnnuityAge = helper.sendKeys4_check($annuityAgeInput, expectedAnnuityAge);

            super.printLogAndCompare(title, expectedAnnuityAge, actualAnnuityAge);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try {
            List<WebElement> homepageTreatyList = driver.findElements(By.xpath("//*[@id='cureTeuk']//tr"));

            int homepageTreatyCnt = 0;
            int welgramTreatyCnt = 0;

            for (int i = 0; i < info.treatyList.size(); i++) {
                CrawlingTreaty infoTreatyList = info.treatyList.get(i);
                if (!(infoTreatyList.productGubun.name().equals("주계약"))) {
                    welgramTreatyCnt++;
                }
            }

            for (int i = 0; i < homepageTreatyList.size(); i++) {
                CrawlingTreaty targetTreaty = new CrawlingTreaty();

                String $treatyName = homepageTreatyList.get(i).findElement(By.xpath(".//td[1]")).getText();
                logger.info("원수사 특약명 :: "+ $treatyName);

                for (int j = 0; j < info.treatyList.size(); j++) {

                    if ($treatyName.contains(info.treatyList.get(j).treatyName)) {
                        targetTreaty.setTreatyName($treatyName);
                        homepageTreatyCnt++;
                        logger.info("가입설계에 동일한 특약 존재");
                        int infoAssureMoney = info.treatyList.get(j).assureMoney;
                        logger.info("가입금액 설정 :: "+ infoAssureMoney);

                        List<WebElement> optionList = homepageTreatyList.get(i).findElements(By.xpath(".//select/option"));
                        String myMoney = String.valueOf(infoAssureMoney / 10000);
                        for (WebElement option : optionList) {
                            String targetMoney = option.getAttribute("value");
                            if (targetMoney.equals(myMoney)) {
                                logger.info("{} 선택", targetMoney);
                                click(option);
                                targetTreaty.setAssureMoney(infoAssureMoney);
                                if (helper.isAlertShowed()) {
                                    throw new Exception("해당 특약 가입 금액 선택 불가 :: " + $treatyName);
                                }
                                break;
                            }
                        }
                        targetTreatyList.add(targetTreaty);
                    }
                }
            }

            if (welgramTreatyCnt != homepageTreatyCnt) { throw new Exception("가입설계 특약의 개수만큼 선택하지 않았습니다."); }

            for (int i = 0; i<homepageTreatyList.size(); i++) {
                String treatyName = homepageTreatyList.get(i).findElement(By.xpath(".//td[1]")).getText();
                logger.info("원수사 특약명 :: "+ treatyName);

                for (int j = 0; j < info.treatyList.size(); j++) {
                    CrawlingTreaty infoTreatyList = info.treatyList.get(j);

                    if (treatyName.contains(infoTreatyList.treatyName)) {
                        int infoAssureMoney = infoTreatyList.assureMoney;
                        logger.info("가입금액 확인 :: "+ infoAssureMoney);

                        String script = "return $(arguments[0]).find('select option:selected').text();";
                        String selectedOption = String.valueOf(helper.executeJavascript(script, homepageTreatyList.get(i)));

                        logger.info("선택되어있는 option :: " + selectedOption);
                        String myMoney = String.valueOf(infoAssureMoney / 10000);
                        if (selectedOption.replaceAll("[^0-9]","").equals(myMoney)) {
                            logger.info("특약명 :: " + infoTreatyList.treatyName);
                            logger.info("가입금액 :: " + infoTreatyList.assureMoney);
                            logger.info("원수사와 일치 ");
                            break;
                        } else {
                            throw new Exception("가입설계의 특약 가입설계와 원수사의 가입금액이 일치하지않습니다. 확인바랍니다.");
                        }
                    }
                }
            }

            List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
                .filter(t -> t.productGubun == ProductGubun.선택특약)
                .collect(Collectors.toList());

            boolean result = advancedCompareTreaties(targetTreatyList, subTreatyList , new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다~~~");
            } else {
                logger.error("특약 정보 불일치~~~");
                throw new Exception();
            }


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlAnnuityPremium(Object... obj) throws CommonCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            WaitUtil.waitFor(2);

            PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
            String whl10y = driver.findElement(By.cssSelector("#L10")).getText();
            String whl20y = driver.findElement(By.cssSelector("#L20")).getText();
            String whl30y = driver.findElement(By.cssSelector("#L30")).getText();
            String whl100a = driver.findElement(By.cssSelector("#L100")).getText();

            String fxd10y = driver.findElement(By.cssSelector("#F10")).getText();
            String fxd15y = driver.findElement(By.cssSelector("#F15")).getText();
            String fxd20y = driver.findElement(By.cssSelector("#F20")).getText();
            String fxd25y = driver.findElement(By.cssSelector("#F25")).getText();
            String fxd30y = driver.findElement(By.cssSelector("#F30")).getText();

            planAnnuityMoney.setWhl10Y(String.valueOf(MoneyUtil.toDigitMoney(whl10y)));
            planAnnuityMoney.setWhl20Y(String.valueOf(MoneyUtil.toDigitMoney(whl20y)));
            planAnnuityMoney.setWhl30Y(String.valueOf(MoneyUtil.toDigitMoney(whl30y)));
            planAnnuityMoney.setWhl100A(String.valueOf(MoneyUtil.toDigitMoney(whl100a)));

            planAnnuityMoney.setFxd10Y(String.valueOf(MoneyUtil.toDigitMoney(fxd10y)));
            planAnnuityMoney.setFxd15Y(String.valueOf(MoneyUtil.toDigitMoney(fxd15y)));
            planAnnuityMoney.setFxd20Y(String.valueOf(MoneyUtil.toDigitMoney(fxd20y)));
            planAnnuityMoney.setFxd25Y(String.valueOf(MoneyUtil.toDigitMoney(fxd25y)));
            planAnnuityMoney.setFxd30Y(String.valueOf(MoneyUtil.toDigitMoney(fxd30y)));

            info.planAnnuityMoney = planAnnuityMoney;

            if (info.annuityType.contains("종신 10년")) {
                info.annuityPremium = planAnnuityMoney.getWhl10Y();

            } else if(info.annuityType.contains("종신 20년")) {
                info.annuityPremium = planAnnuityMoney.getWhl20Y();

            } else if(info.annuityType.contains("종신 30년")) {
                info.annuityPremium = planAnnuityMoney.getWhl30Y();

            } else if (info.annuityType.contains("종신 100세")) {
                info.annuityPremium = planAnnuityMoney.getWhl100A();

            } else if(info.annuityType.contains("확정 10년")) {
                info.annuityPremium = planAnnuityMoney.getWhl10Y();
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd10Y();

            } else if(info.annuityType.contains("확정 15년")) {
                info.annuityPremium = planAnnuityMoney.getWhl10Y();
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd15Y();

            } else if(info.annuityType.contains("확정 20년")) {
                info.annuityPremium = planAnnuityMoney.getWhl10Y();
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd20Y();

            } else if(info.annuityType.contains("확정 25년")) {
                info.annuityPremium = planAnnuityMoney.getWhl10Y();
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd25Y();

            } else if(info.annuityType.contains("확정 30년")) {
                info.annuityPremium = planAnnuityMoney.getWhl10Y();
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd30Y();

            } else {
                logger.info("{} 을 찾을 수 없습니다.", info.annuityType);
                throw new Exception();
            }

            logger.info("info.annuityPremium :: {}", info.annuityPremium);
            logger.info("info.fixedAnnuityPremium :: {}", info.fixedAnnuityPremium);
            logger.info("|---보증--------------------");
            logger.info("|-- 10년 보증 :: {}", planAnnuityMoney.getWhl10Y());
            logger.info("|-- 20년 보증 :: {}", planAnnuityMoney.getWhl20Y());
            logger.info("|-- 30년 보증 :: {}", planAnnuityMoney.getWhl30Y());
            logger.info("|-- 100세 보증 :: {}", planAnnuityMoney.getWhl100A());
            logger.info("|---확정--------------------");
            logger.info("|-- 10년 확정 :: {}", planAnnuityMoney.getFxd10Y());
            logger.info("|-- 15년 확정 :: {}", planAnnuityMoney.getFxd15Y());
            logger.info("|-- 20년 확정 :: {}", planAnnuityMoney.getFxd20Y());
            logger.info("|-- 25년 확정 :: {}", planAnnuityMoney.getFxd25Y());
            logger.info("|-- 30년 확정 :: {}", planAnnuityMoney.getFxd30Y());
            logger.info("--------------------------");

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlReturnMoneyListTwo(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = (By) obj[1];
        WebElement $a = null;

        try {
            $a = driver.findElement(By.id("btnShowDetail"));
            click($a);

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            List<WebElement> trList = driver.findElements(location);
            for (WebElement tr : trList) {
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                logger.info("______해약환급급__________ ");
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
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



    public void crawlReturnMoneyListSix(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = (By) obj[1];
        WebElement $a = null;

        try {
            $a = driver.findElement(By.id("btnShowDetail"));
            click($a);

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            List<WebElement> trList = driver.findElements(location);

            for (WebElement tr : trList) {
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText();
                String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
                String returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
                String returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(6).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(7).getText();

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

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setPlan(Object... obj) throws CommonCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        String title = "플랜";

        String expectedPlan = info.textType;
        String actualPlan = "";

        try {

            WebElement $planUl = driver.findElement(By.id("PINSTYPE_AREA"));

            List<WebElement> list = $planUl.findElements(By.tagName("li"));
            for (WebElement li : list) {
                String target = li.getText();

                if (target.contains(expectedPlan)) {
                    click(li.findElement(By.xpath(".//label")));
                    logger.info(li.getText() + " 선택");
                    break;
                }
            }

            String selectedPlan = ((JavascriptExecutor)driver).executeScript("return $('input[name=pInsType]:checked').next().text();").toString().trim();

            if (selectedPlan.contains(expectedPlan)) {
                actualPlan = expectedPlan;
            }

            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    protected void moveToElement(By location) {

        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }



    protected void moveToElement(WebElement location) {

        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }



    //로딩바 명시적 대기
    public void waitLoadingBar() {

        try {
            helper.waitForCSSElement("#dialogProgress");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}