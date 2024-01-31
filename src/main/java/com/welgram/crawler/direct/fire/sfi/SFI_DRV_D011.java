package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class SFI_DRV_D011 extends CrawlingSFIDirect {

    public static void main(String[] args) {
        executeCommand(new SFI_DRV_D011(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        waitLoadingBar();

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//span[text()='보험료 계산']"));
        helper.waitElementToBeClickable($button);
        click($button);

        logger.info("모달창이 뜨는지를 확인합니다");
        modalCheck();

        logger.info("가입형태 설정");
        setJoinType("본인");

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth(), By.id("birth-input"));

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("영업용 자동차를 운전하세요? 설정");
        setVehicle("아니요");

        logger.info("보험료 계산하기 버튼 클릭");
        $button = driver.findElement(By.id("js-btn-next"));
        click($button);

        logger.info("모달창이 뜨는지를 확인합니다");
        modalCheck();

//        logger.info("플랜 설정");
//        setPlan(info.planSubName);

        helper.click(By.id("btn-insured-periods"), "조건 변경하기 버튼 클릭");
        WaitUtil.loading(3);

        logger.info("보험기간 설정");
        setInsTerm(info.getInsTerm());

        logger.info("납입기간 설정");
        setNapTerm(info.getNapTerm());

        logger.info("납입방법 설정");
        setNapCycle(info.getNapCycleName());
        helper.click(By.xpath("//button[@class='btn-primary positive js-btn-confirm']"), "변경 버튼 클릭");

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        String expectedGenderText = "";
        String actualGenderText = "";
        int gender = (int) obj[0];

        if (obj.length > 1){
            expectedGenderText = (String) obj[1];
        } else {
            expectedGenderText = (gender == MALE) ? "남성" : "여성";
        }

        try {
            // 성별 element 찾기
            WebElement $genderDiv = driver.findElement(By.id("ui-gender"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedGenderText + "']"));

            // 성별 클릭
            click($genderLabel);

            // 실제 선택된 성별 값 읽어오기
            $genderLabel = $genderDiv.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualGenderText = $genderLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(exceptionEnum.getMsg());
        }
    }



    @Override
    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "플랜";
        String actualPlan = "";
        String[] textTypes = expectedPlan.split("\\|");
        String script = "";

        try {
            // 플랜 관련 element 찾기
            WebElement $planAreaThead = driver.findElement(By.id("header-roll-area"));
            WebElement $planLabel = null;

            // 플랜 선택과 비교를 편하게 하기 위해 불필요한 element 삭제 처리
            script = "$(arguments[0]).find('th > label > span').remove();";
            helper.executeJavascript(script, $planAreaThead);

            for (String textType : textTypes) {
                try {
                    //플랜 클릭
                    textType = textType.trim();

                    $planLabel = $planAreaThead.findElement(By.xpath(".//h2[@class='plan-title'][normalize-space()='" + textType + "']"));
                    click($planLabel);
                    expectedPlan = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            // 플랜 선택과 비교를 편하게 하기 위해 불필요한 element 삭제 처리
            helper.executeJavascript(script, $planAreaThead);

            // 실제 선택된 플랜 값 읽어오기
            $planLabel = $planAreaThead.findElement(By.xpath(".//label[@class[contains(., 'active')]]//h2[@class='plan-title']"));
            actualPlan = $planLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {
            String script = "return $('div[id*=insured-term]:visible')[0]";
            WebElement $insTermAreaDd = (WebElement) helper.executeJavascript(script);
            WebElement $insTermLabel = $insTermAreaDd.findElement(By.xpath(".//label[contains(.,'" + expectedInsTerm + "')]"));
            // 보험기간 라벨 텍스트 예:  "30세 만기(계약 전환시 최대 100세 보장)" -> 시작 텍스트가 보험기간 선택의 기준이 된다.
            click($insTermLabel);

            // 실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
            $insTermLabel = $insTermAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            String activeLabelText = $insTermLabel.getText().trim();
            actualInsTerm
                = activeLabelText.contains("만기") ?
                activeLabelText.substring(0, activeLabelText.indexOf("만기")).trim(): activeLabelText;

            // 비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            // 해약환급금 팝업 오픈 버튼 element 찾기
            logger.info("해약환급금 팝업 오픈 버튼 클릭");
            WebElement $openReturnMoneyPopupButton = driver.findElement(By.xpath("//div[contains(@class, 'select')]//button[@class='btn-rate']"));
            helper.click($openReturnMoneyPopupButton);
            WaitUtil.loading(2);

            //해약환급금 관련 정보 element 찾기
            WebElement $returnMoneyTbody = driver.findElement(By.id("refund-list"));
            List<WebElement> $returnMoneyTrList = $returnMoneyTbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $returnMoneyTrList) {
                // tr이 보이도록 스크롤 조정. 스크롤을 조정하지 않으면 해약환급금 금액을 크롤링 할 수 없음.
                helper.moveToElementByJavascriptExecutor($tr);

                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                String term = $tr.findElement(By.tagName("th")).getText();
                String premiumSum = $tdList.get(0).getText();
                String returnMoneyMin = $tdList.get(1).getText();
                String returnRateMin = $tdList.get(2).getText();
                String returnMoney = $tdList.get(3).getText();
                String returnRate = $tdList.get(4).getText();

                premiumSum = premiumSum.replaceAll("[^0-9]", "");
                returnMoneyMin = returnMoneyMin.replaceAll("[^0-9]", "");
                returnMoney = returnMoney.replaceAll("[^0-9]", "");

                logger.info("경과기간 : {} | 납입보험료 : {} | 최저환급금 : {} | 최저환급률 : {} | 공시환급금 : {} | 공시환급률 : {}", term, premiumSum, returnMoneyMin, returnRateMin, returnMoney, returnRate);

                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoneyMin(returnMoneyMin);
                p.setReturnRateMin(returnRateMin);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);
                info.returnPremium = returnMoney;
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            // 보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumDiv = driver.findElement(By.xpath("//div[contains(@class, 'select')]"));
            WebElement $premiumStrong = $premiumDiv.findElement(By.xpath(".//span[@class='price']"));
            String premium = $premiumStrong.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(exceptionEnum.getMsg());
        }
    }



    /**
     * 삼성화재 다이렉트 특약설정 TYPE2 : 특약 더보기 버튼을 클릭해서 처리
     */
    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        String script = "";
        ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;

        try {
            // 원수사 특약 tbody 영역
            WebElement $treatyTbody = driver.findElement(By.xpath("//div[@class='coverage-contents']"));
            List<WebElement> $treatyList = $treatyTbody.findElements(By.xpath("//button[contains(@class,'select')]"));

            //특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(span 삭제)
            script = "$(arguments[0]).find('span.ne-hidden').remove();";
            helper.executeJavascript(script, $treatyTbody);

            // 특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리
            // (*문자열로 시작하는 span 삭제, NEW/HOT span 삭제)
            script = "$(arguments[0]).find('span.unit-flag-recomm').remove();";
            helper.executeJavascript(script, $treatyTbody);

            //특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(더보기 버튼 삭제)
            script = "$(arguments[0]).find('button.ne-bt-more').remove();";
            helper.executeJavascript(script, $treatyTbody);

            /**
             * ===========================================================================================
             * [STEP 1]
             * 원수사 특약명, 가입설계 특약명 수집 진행하기
             * ===========================================================================================
             */

            List<String> targetTreatyNameList = new ArrayList<>();
            List<String> welgramTreatyNameList = new ArrayList<>();

            // 원수사 특약명 수집
            for (WebElement $treaty : $treatyList) {

                //원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
                helper.moveToElementByJavascriptExecutor($treaty);
                String targetTreatyName = $treaty.getText().trim();
                targetTreatyName
                    = targetTreatyName.substring(0, targetTreatyName.indexOf("\n")).trim()
                    .replaceAll("HOT|NEW|UPGRADE", "");

                targetTreatyNameList.add(targetTreatyName);
            }

            // 가입설계 특약명 수집
            welgramTreatyNameList = welgramTreatyList.stream().map(t -> t.getTreatyName()).collect(Collectors.toList());

            // 원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
            List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
            List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트
            List<String> strangeTreatyNameList = new ArrayList<>();                                //이상 있는 특약명 리스트

            // 원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
            targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
            matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구

            //(원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
            targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
            dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);

            /**
             * ===========================================================================================
             * [STEP 2]
             * 특약 가입/미가입 처리 진행하기
             * ===========================================================================================
             */

            //불일치 특약들에 대해서 원수사에서 미가입 처리 진행
            for (String treatyName : dismatchedTreatyNameList) {
                String treatyAssureMoney = "";

                logger.info("특약명 : {} 미가입 처리 진행중...", treatyName);

                // 특약명, 특약 가입금액 관련 element 찾기
                WebElement $treatyNameTh = $treatyTbody.findElement(By.xpath(".//button[contains(@class,'select') and contains(., '" + treatyName + "')]/div[@class='g-txt']"));
                WebElement $treatyTr = $treatyNameTh.findElement(By.xpath("./parent::button[contains(@class,'select')]"));
                WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./div[@class='g-price']"));

                // 특약이 보이도록 스크롤 조정
                helper.moveToElementByJavascriptExecutor($treatyNameTh);
                treatyAssureMoney = $treatyAssureMoneyTd.getText();

                // 미가입 처리해야하는 특약의 가입 상태가 "가입"인 경우(=가입금액란에 가입금액이 표시된 경우)
                boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
                if (isJoin) {
                    //특약 가입/미가입 팝업창 열기
                    helper.moveToElementByJavascriptExecutor($treatyTbody);
                    click($treatyNameTh);

                    // 특약 팝업 내 가입금액 관련 element 찾기
                    WebElement $treatyPopupDiv = driver.findElement(By.xpath("//div[@class='modal-content']"));
                    WebElement $treatyAssureMoneyArea = $treatyPopupDiv.findElement(By.xpath(".//label[contains(@class, 'active')]"));

                    // 가입금액 선택을 수월하게 하기 위해 불필요한 element 삭제 처리("가입금액" span 삭제)
                    script = "$(arguments[0]).find('span.blind').remove();";
                    helper.executeJavascript(script, $treatyAssureMoneyArea);

                    // 가입금액 선택을 수월하게 하기 위해 불필요한 element 삭제 처리("선택됨" span 삭제)
                    script = "$(arguments[0]).find('span.sr-only').remove();";
                    helper.executeJavascript(script, $treatyAssureMoneyArea);

                    // 미가입 버튼 클릭
                    WebElement $treatyAssureMoneyP = $treatyPopupDiv.findElement(By.xpath(".//span[text()='미가입']"));
                    WebElement $treatyAssureMoneyA = $treatyAssureMoneyP.findElement(By.xpath("./ancestor::label"));
                    click($treatyAssureMoneyA);

                    // 특약 팝업창 닫기 위해 확인 버튼 클릭
                    WebElement $popupCloseButton = driver.findElement(By.id("btn-confirm"));
                    click($popupCloseButton);

                    WaitUtil.loading(1);
                    if ($treatyPopupDiv.isDisplayed()) {
                        click($treatyPopupDiv.findElement(By.xpath("//button[contains(@class, 'btn-confirm')]")));
                    }
                }
            }

            /**
             * TODO 이게 다이렉트 상품에 한해 적합한 프로세스인지 한번 확인해볼 필요 있음
             * 공통된 특약명에 대해서는 사실 가입금액 조정 과정이 필요하다.
             * 하지만 다이렉트 상품의 경우 원수사에서 default로 설정한 특약의 가입금액이 의미있다고 판단된다.
             * 따라서 굳이 원수사가 default로 설정한 가입금액을 우리 가입설계 특약의 금액에 맞게 꾸역꾸역
             * 조정하는 과정이 필요할까?
             * 원수사가 default로 설정한 가입금액이 가입설계 가입금액과 다르면 가입설계의 가입금액을
             * 수정하도록 예외를 발생시키는게 더 의미있다고 판단하기에
             * 공통된 특약들에 대해서는 따로 가입금액 조정 처리를 하지 않도록 하겠다.
             *
             */

            /**
             * ===========================================================================================
             * [STEP 3]
             * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
             * ===========================================================================================
             */

            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            for (WebElement $treaty : $treatyList) {
                // 특약 정보가 보이도록 스크롤 조정
                helper.moveToElementByJavascriptExecutor($treaty);

                // 특약명, 특약가입금액  element 찾기
                String treatyName = $treaty.getText();
                if (treatyName.contains("\n")) {
                    treatyName = treatyName.substring(0, treatyName.indexOf("\n"));
                }
                if (treatyName.contains("HOT") || treatyName.contains("NEW") || treatyName.contains("UPGRADE")) {
                    treatyName = treatyName.replaceAll("HOT|NEW|UPGRADE", "");
                }
                String treatyAssureMoney = "";
                WebElement $treatyNameTh = $treatyTbody.findElement(By.xpath(".//button[contains(@class,'select') and contains(., '" + treatyName + "')]/div[@class='g-txt']"));
                WebElement $treatyAssureMoneyTd = $treatyNameTh.findElement(By.xpath("./following-sibling::div"));

                // 특약명, 특약가입금액 읽어오기
                treatyName = $treatyNameTh.getText();
                if (treatyName.contains("\n")) {
                    treatyName = treatyName.substring(0, treatyName.indexOf("\n"));
                }
                if (treatyName.contains("HOT") || treatyName.contains("NEW") || treatyName.contains("UPGRADE")) {
                    treatyName = treatyName.replaceAll("HOT|NEW|UPGRADE", "");
                }
                treatyAssureMoney = $treatyAssureMoneyTd.getText();
                treatyAssureMoney = treatyAssureMoney.replace("지급", "").replace("한도", "");

                // 가입하는 특약에 대해서만 원수사 특약 정보 적재
                boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
                if (isJoin) {
                    treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    targetTreaty.setTreatyName(treatyName);
                    targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

                    targetTreatyList.add(targetTreaty);
                }
            }

            /**
             * ===========================================================================================
             * [STEP 4]
             * 원수사 특약 정보 vs 가입설계 특약 정보 비교
             * ===========================================================================================
             */

            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
            if (result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            throw new SetTreatyException(exceptionEnum.getMsg());
        }
    }



    // 로딩바 명시적 대기
    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#loading-message-box");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}