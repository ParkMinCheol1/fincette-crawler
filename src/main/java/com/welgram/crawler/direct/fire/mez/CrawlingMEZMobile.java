package com.welgram.crawler.direct.fire.mez;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;

public abstract class CrawlingMEZMobile extends CrawlingMEZNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {
            WebElement $birthInput = driver.findElement(By.id("birthInf"));
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

            super.printLogAndCompare(title, expectedBirth, actualBirth);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setMobileBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];

        String script = "return arguments[0].value;";
        String actualValue = "";

        try {
            WebElement $birthInput = driver.findElement(By.xpath("(//div[@class='inpWrap w60p'])[1]//input"));


            if("input".equals($birthInput.getTagName())) {
                //text 입력
                $birthInput.click();
                $birthInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                $birthInput.sendKeys(expectedBirth);

                WaitUtil.waitFor(1);
                WebElement $button = driver.findElement(By.xpath("//button[@class='btnClose']"));
                helper.click($button);

                //실제 input에 입력된 value 읽어오기
                actualValue = String.valueOf(helper.executeJavascript(script, $birthInput));
                logger.info("actual input value :: {}", actualValue);

            } else {
                logger.error("파라미터로 input element를 전달해주세요");
                throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ELEMENT);
            }

            super.printLogAndCompare(title, expectedBirth, actualValue);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "남자" : "여자";
        String actualGender = "";

        try {
            //성별 관련 element 찾기
            WebElement $genderDiv = driver.findElement(By.id("gndrInf"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("./label[normalize-space()='" + expectedGender + "']"));

            //성별 선택
            click($genderLabel);

            //실제 클릭된 성별 읽어오기
            WebElement $genderInput = $genderDiv.findElement(By.tagName("input"));
            String name = $genderInput.getAttribute("name");
            String script = "return $('input[name=" + name  +"]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = $genderDiv.findElement(By.xpath("./label[@for='" + id + "']"));
            actualGender = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setMobileGender(Object... obj) throws SetGenderException {
        String title = "성별";
        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "남" : "여";
        String actualGender = "";
        String script = "return arguments[0].value;";

        try {
            //성별 관련 element 찾기
            WebElement $genderDiv = driver.findElement(By.xpath("(//div[@class='inpWrap w40p'])[1]//div[@class='inpBox sltType']"));
            WebElement $genderInput = $genderDiv.findElement(By.xpath(".//input"));

            //성별 선택
            helper.click($genderDiv);
            WaitUtil.waitFor(1);
            String genderLocation = (gender == MALE) ? "popSlt2" : "popSlt1";
            helper.click(driver.findElement(By.xpath("//label[@for='"+genderLocation+"']")));
            WaitUtil.waitFor(1);

            //실제 클릭된 성별 읽어오기
            actualGender = String.valueOf(helper.executeJavascript(script, $genderInput));

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setJob(Object... obj) throws SetJobException {
        String title = "직업";
        String expectedJob = (String) obj[0];
        String actualJob = "";

        try {
            logger.info("직업 선택 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//button[normalize-space()='직업 선택']"));
            click($button);

            logger.info("직업 입력을 위해 버튼 클릭");
            $button = driver.findElement(By.className("job_btn_click"));
            click($button);
            WaitUtil.waitFor(2);

            logger.info("직업 입력");
            WebElement $jobPop = driver.findElement(By.id("jobPop"));
            WebElement $jobSearchPop = driver.findElement(By.id("jobSerchPop"));
            WebElement $jobInput = $jobSearchPop.findElement(By.id("jobKeyWord"));
            helper.sendKeys4_check($jobInput, expectedJob);
            WaitUtil.waitFor(2);

            logger.info("직업 클릭");
            WebElement $jobSearchList = $jobSearchPop.findElement(By.className("layer_search_list"));
            WebElement $jobButton = $jobSearchList.findElement(By.xpath(".//button[normalize-space()='" + expectedJob + "']"));
            click($jobButton);

            logger.info("직업 입력 팝업을 닫기 위해 완료 버튼 클릭");
            $button = $jobSearchPop.findElement(By.linkText("완료"));
            click($button);

            logger.info("실제 선택된 직업 읽어오기");
            WebElement $jobLabel = driver.findElement(By.xpath("//label[normalize-space()='선택된 직업']"));
            $jobInput = $jobLabel.findElement(By.xpath("./following-sibling::input[1]"));
            actualJob = $jobInput.getAttribute("value");

            //비교
            super.printLogAndCompare(title, expectedJob, actualJob);

            logger.info("직업 유의사항 체크");
            WebElement $label = driver.findElement(By.xpath("//label[@for='temp_c1']"));
            click($label);

            logger.info("직업 선택 팝업을 닫기 위해 완료 버튼 클릭");
            $button = $jobPop.findElements(By.linkText("완료")).get(0);
            click($button);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {
        String title = "운전용도";
        String expectedVehicle = (String) obj[0];
        String actualVehicle = "";

        try {
            //운전용도 관련 element 찾기
            WebElement $vehicleDiv = driver.findElement(By.id("drvUsgInf"));
            WebElement $vehicleLabel = $vehicleDiv.findElement(By.xpath("./label[normalize-space()='" + expectedVehicle + "']"));

            //운전용도 선택
            click($vehicleLabel);

            //실제 클릭된 운전용도 읽어오기
            WebElement $vehicleInput = $vehicleDiv.findElement(By.tagName("input"));
            String name = $vehicleInput.getAttribute("name");
            String script = "return $('input[name=" + name  +"]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $vehicleLabel = $vehicleDiv.findElement(By.xpath("./label[@for='" + id + "']"));
            actualVehicle = $vehicleLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedVehicle, actualVehicle);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간 / 납입기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {
            //보험기간 / 납입기간 관련 element 찾기
            WebElement $insTermSpan = driver.findElement(By.xpath("//span[normalize-space()='" + title + "']"));
            WebElement $insTermDiv = $insTermSpan.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedInsTerm + "']"));

            //보험기간 / 납입기간 선택
            click($insTermLabel);

            //실제 클릭된 보험기간 / 납입기간 읽어오기
            WebElement $insTermInput = $insTermDiv.findElement(By.tagName("input"));
            String name = $insTermInput.getAttribute("name");
            String script = "return $('input[name=" + name  +"]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualInsTerm = $insTermLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "판매플랜";
        String actualPlan = "";

        try {
            //플랜 관련 element 찾기
            WebElement $planDiv = driver.findElement(By.xpath("//div[@class='tab_btn_box']"));
            WebElement $planSpan = $planDiv.findElement(By.xpath(".//span[@class='tit'][normalize-space()='" + expectedPlan + "']"));
            WebElement $planButton = $planSpan.findElement(By.xpath("./parent::button"));

            //플랜 선택
            click($planButton);

            //실제 클릭된 플랜 읽어오기
            $planButton = $planDiv.findElement(By.xpath("./button[@class[contains(., 'btns_active')]]"));
            $planSpan = $planButton.findElement(By.xpath("./span[@class='tit']"));
            actualPlan = $planSpan.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setMobilePlan(String expectedPlan) throws CommonCrawlerException {
        String title = "판매플랜";
        String actualPlan = "";

        logger.info(expectedPlan + "선택해야함");

        try {
            //플랜 관련 element 찾기
            WebElement $planUl = driver.findElement(By.xpath("//ul[@class='calcRadio']"));
            List<WebElement> $planLabel = $planUl.findElements(By.xpath(".//li//label"));

            for(int i = 0; i < $planLabel.size(); i++){
                if($planLabel.get(i).getText().contains(expectedPlan)){
                    //플랜 선택
                    helper.click($planLabel.get(i));
                    break;
                }
            }

            String id = driver.findElement(By.cssSelector("input[name='calcRadio']:checked")).getAttribute("id");
            actualPlan = driver.findElement(By.xpath("//label[@for='"+id+"']")).getText();
            if(actualPlan.contains(expectedPlan)){
                actualPlan = expectedPlan;
            }

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {

            WebElement $treatyUl = driver.findElement(By.xpath("//ul[@class='result_list']"));
            List<WebElement> $treatyLiList = $treatyUl.findElements(By.tagName("li"));

            //원수사에서 가입금액이 존재하는 특약명만 수집
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            for(WebElement $treatyLi : $treatyLiList) {
                WebElement $treatyNameP = $treatyLi.findElement(By.tagName("p"));
                WebElement $treatyAssureMoneySpan = $treatyLi.findElement(By.xpath(".//span[@class[contains(., 'emphasis')]]"));

                String treatyName = $treatyNameP.getText().trim();
                String treatyAssureMoney = $treatyAssureMoneySpan.getText().trim();

                //현재 특약의 가입상태(true : 가입, false : 미가입)
                boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);

                //가입인 경우에만 원수사 특약정보를 적재한다.
                if(isJoin) {
                    treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney2(treatyAssureMoney));

                    CrawlingTreaty treaty = new CrawlingTreaty();
                    treaty.setTreatyName(treatyName);
                    treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
                    targetTreatyList.add(treaty);
                }
            }

            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
            if(result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
                .findFirst()
                .get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 넉넉하게 대기시간을 준다
            WaitUtil.waitFor(3);

            //보험료 관련 element 찾기
            WebElement $premiumP = driver.findElement(By.xpath("//p[@class='result_cost']"));
            WebElement $premiumStrong = $premiumP.findElement(By.tagName("strong"));
            String premium = $premiumStrong.getText().replaceAll("[^0-9]", "");

            //보험료 정보 세팅
            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }
        }  catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//a[contains(., '예상 만기환급률')]"));
            helper.moveToElementByJavascriptExecutor($button);
            click($button);

            //해약환급금 탭 관련 element 찾기
            WebElement $tab = driver.findElement(By.xpath("//div[@class='ca_tabs style2']"));
            List<WebElement> $buttonList = $tab.findElements(By.tagName("button"));

            for(int i = 0; i < $buttonList.size(); i++) {
                $button = $buttonList.get(i);

                //탭 클릭
                String tabTitle = $button.getText();
                logger.info("{} 탭 클릭", tabTitle);
                click($button);

                //클릭한 탭에 따른 활성화된 해약환급금 테이블 정보 읽어오기
                String script = "return $('div.ca_taget div.table_list:visible')[0]";
                WebElement $activedDiv = (WebElement) helper.executeJavascript(script);
                WebElement $tbody = $activedDiv.findElement(By.tagName("tbody"));
                List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

                //해약환급금 크롤링
                for(WebElement $tr : $trList) {
                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                    String term = $tdList.get(0).getText();
                    String premiumSum = $tdList.get(1).getText();
                    String returnMoney = $tdList.get(2).getText();
                    String returnRate = $tdList.get(3).getText();

                    premiumSum = premiumSum.replaceAll("[^0-9]", "");
                    returnMoney = returnMoney.replaceAll("[^0-9]", "");

                    logger.info("{} | 경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                            , tabTitle, term, premiumSum, returnMoney, returnRate);


                    //기존에 적재된 해약환급금이 있는 경우
                    PlanReturnMoney p = planReturnMoneyList
                            .stream()
                            .filter(prm -> prm.getTerm().equals(term))
                            .findFirst()
                            .orElseGet(PlanReturnMoney::new);

                    p.setTerm(term);
                    p.setPremiumSum(premiumSum);

                    if(tabTitle.contains("최저")) {
                        p.setReturnMoneyMin(returnMoney);
                        p.setReturnRateMin(returnRate);
                    } else if(tabTitle.contains("평균")) {
                        p.setReturnMoneyAvg(returnMoney);
                        p.setReturnRateAvg(returnRate);

                        //공시이율에 해당하는 환급금은 제공하지 않기때문에 평균공시이율에 해당하는 정보를 공시정보에도 세팅한다.
                        p.setReturnMoney(returnMoney);
                        p.setReturnRate(returnRate);
                    }

                    if(i == 0) {
                        planReturnMoneyList.add(p);
                    }
                }
            }

            //만기환급금 세팅(해약환급금 표가 아닌 명시되어 있는 예상만기환급금을 크롤링한다)
            WebElement $returnMoneyTh = driver.findElement(By.xpath("//th[normalize-space()='예상만기환급금']"));
            WebElement $returnMoneyTd = $returnMoneyTh.findElement(By.xpath("./following-sibling::td[1]"));
            String returnPremium = $returnMoneyTd.getText();
            returnPremium = returnPremium.replaceAll("[^0-9]", "");
            info.returnPremium = returnPremium;

            logger.info("예상 만기환급금 : {}원", info.returnPremium);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTravelDate() throws SetTravelPeriodException {
        String title = "여행 날짜 선택";
        String actualDepartureDate = "";
        WebElement $button = null;

        try {
            WaitUtil.waitFor(3);
            logger.info("출발일 선택");
            $button = driver.findElement(By.xpath("//label[@for='startDay']//..//div[@class='inpBox dateType']"));
            helper.click($button);
            WaitUtil.waitFor(1);

            String departureDate = plusDateBasedOnToday(7);
            selectDay(departureDate);

            logger.info("출발시간 00시 선택");
            WaitUtil.waitFor(1);
            helper.click(By.xpath("//label[@for='popSlt0']"));
            WaitUtil.waitFor(1);

            logger.info("도착일 선택");
            String arrivalDate = plusDateBasedOnToday(13);
            selectDay(arrivalDate);

            logger.info("도착시간 23시 선택");
            $button = driver.findElement(By.xpath("//label[@for='endDay']//..//div[@class='inpBox sltType']"));
            helper.click($button);
            WaitUtil.waitFor(1);
            helper.click(By.xpath("//label[@for='popSlt23']"));
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    //여행 날짜 계산
    protected String plusDateBasedOnToday(int day) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, day);
        date = sdf.format(cal.getTime());
        return date;
    }

    protected void selectDay(String departureDate) throws Exception {
        int year = Integer.parseInt(departureDate.substring(0,4));
        int month = Integer.parseInt(departureDate.substring(4,6));
        int day = Integer.parseInt(departureDate.substring(6));

        logger.info("연도 선택 :: {} 년",year);
        helper.click(By.xpath("//div[@class='relative-position overflow-hidden flex flex-center']//span[@class='block']"));
        helper.click(By.xpath("//div[@class='q-date__years-content col self-stretch row items-center']//span[text()='"+year+"']"));

        logger.info("월 선택 :: {} 월", month);
        helper.click(By.xpath("//div[@class='relative-position overflow-hidden flex flex-center col']//span[@class='block']"));
        helper.click(By.xpath("//div[@class='q-date__view q-date__months flex flex-center']//span[text()='"+month+"월']"));

        logger.info("일 선택 :: {} 일", day);
        helper.click(By.xpath("//div[@class='q-date__calendar-days fit']//span[text()='"+day+"']"));

    }

    @Override
    public void waitLoadingBar() {
        /**
         * 메리츠 화재 모바일 상품의 로딩바의 경우
         * 로딩중 : body 태그의 aria-busy 속성값이 true
         * 로딩끝 : body 태그의 aria-busy 속성값이 false
         *
         * 따라서 클릭후 aria-busy 속성의 값이 false가 될때까지 기다리게 한다.
         */
        wait.until(ExpectedConditions.attributeToBe(By.tagName("body"), "aria-busy", "false"));
    }

    protected void moveToElement(WebElement location){
        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }

    public void click(WebElement $element) throws Exception {
        helper.waitElementToBeClickable($element).click();
        waitLoadingBar();
        WaitUtil.waitFor(1);
    }

    //TODO 추후에 helper로 뺄것
    public void click(By position) throws Exception {
        WebElement $element = driver.findElement(position);
        click($element);
    }

    protected void popUpAlert() throws Exception {
        WaitUtil.waitFor(2);
        try {
            if (driver.findElement(By.xpath("//div[@class='layer_body typeBot']")).isDisplayed()) {
                logger.debug("알럿표시 확인!!!");
                helper.click(By.xpath("//button[@class='eb_close']"));
            }
        } catch (Exception e) {
            logger.info("알럿표시 없음!!!");
            // TODO: handle exception
        }
    }

}
