package com.welgram.crawler.direct.life.pst;

import com.welgram.common.MoneyUtil;
import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public abstract class CrawlingPSTAnnounce extends CrawlingPSTNew {

    public void nProtectModalCheck() throws Exception {

        waitLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("nProtect 보안프로그램 설치 모달창이 뜨는지 검사합니다.");
        By nProtectModalPosition = By.id("npPfsCtrlInstall");
        boolean isExist = helper.existElement(nProtectModalPosition);

        //nProtect 보안프로그램 설치 모달창이 뜰 경우에 x 버튼 클릭(취소 버튼 누르면 안되고 x 버튼 눌러야함)
        if(isExist) {
            logger.info("nProtect 보안프로그램 설치 모달창이 떴습니다!!!");
            WebElement $modal = driver.findElement(nProtectModalPosition);
            WebElement $button = $modal.findElement(By.xpath(".//button[normalize-space()='닫기']"));
            clickByJavascriptExecutor($button);
        }
    }

    public void modalCheck() throws Exception {
        logger.info("모달창이 뜨는지 검사합니다.");
        By modalPosition = By.xpath("//div[@class='popup_inner']");
        boolean isExist = helper.existElement(modalPosition);

        if(isExist) {
            logger.info("모달창이 떴습니다!!!");
            WebElement $modal = driver.findElement(modalPosition);
            WebElement $button = $modal.findElement(By.xpath(".//button[normalize-space()='닫기']"));
            clickByJavascriptExecutor($button);
        }
    }

    public void findProductName(String expectedProductName) throws CommonCrawlerException {
        ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_NAME;

        try {
            nProtectModalCheck();
            WaitUtil.waitFor(3);

            WebElement $accordionDiv = driver.findElement(By.id("ASDSPD0001list"));
            List<WebElement> $categoryDivList = $accordionDiv.findElements(By.xpath("./div[@class='top']"));

            boolean isFoundProduct = false;
            for(WebElement $categoryDiv  : $categoryDivList) {
                //카테고리 아코디언 클릭
                WebElement $button = $categoryDiv.findElement(By.tagName("a"));
                clickByJavascriptExecutor($button);

                //내부에 더보기 버튼이 있는 경우 계속 클릭
                $categoryDiv = $categoryDiv.findElement(By.xpath("./following-sibling::div[1]"));
                By moreButtonPosition = By.xpath(".//a[normalize-space()='더보기']");
                boolean isMoreButton = true;
                while(isMoreButton) {

                    //더보기 버튼을 클릭하면서 상품 존재하는지 확인
                    By productNamePosition = By.xpath(".//a[normalize-space()='" + expectedProductName + "']");
                    isFoundProduct = helper.existElement($categoryDiv, productNamePosition);

                    //상품이 있는 경우 반복문 탈출
                    if(isFoundProduct) {
                        WebElement $productNameA = driver.findElement(By.xpath("//a[normalize-space()='" + expectedProductName + "']"));
                        WebElement $productNameDiv = $productNameA.findElement(By.xpath("./ancestor::div[@class='ch_product']"));
                        WebElement $calcButton = $productNameDiv.findElement(By.xpath(".//a[normalize-space()='계산하기']"));

                        logger.info("상품명 : {} 계산하기 버튼 클릭", $productNameA.getText());
                        clickByJavascriptExecutor($calcButton);

                        break;
                    }

                    //더보기 버튼 클릭
                    isMoreButton = helper.existElement($categoryDiv, moreButtonPosition);

                    if(isMoreButton) {
                        $button = $categoryDiv.findElement(moreButtonPosition);
                        clickByJavascriptExecutor($button);
                    }
                }

                if(isFoundProduct) {
                    break;
                }
            }

            if(!isFoundProduct) {
                throw new Exception(exceptionEnum.getMsg());
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }
    }

    public void setUserInfo(CrawlingProduct info) throws Exception {
        boolean isExist = false;
        By position = null;

        logger.info("피보험자 생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("피보험자 성별 설정");
        setGender(info.getGender());

        position = By.id("momKornEnm");
        isExist = helper.existElement(position);
        if(isExist) {
            WebElement $element = driver.findElement(position);

            if($element.isDisplayed()) {
                logger.info("산모의 이름 설정");
                setUserName(PersonNameGenerator.generate());
            }
        }

        logger.info("납입주기 설정");
        setNapCycle(info.getNapCycleName());

        logger.info("상세정보 입력 버튼 클릭");
        WebElement $button = driver.findElement(By.id("ASDSPDM00APsubmitBtn"));
        clickByJavascriptExecutor($button);
        WaitUtil.waitFor(3);
        modalCheck();
    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {
        String title = "산모의 이름";
        String expectedUserName = (String) obj[0];
        String actualUserName = "";

        try {
            WebElement $userNameInput = driver.findElement(By.id("momKornEnm"));
            actualUserName = setTextToInputBox($userNameInput, expectedUserName);

            super.printLogAndCompare(title, expectedUserName, actualUserName);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_USER_NAME;
            throw new SetUserNameException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {

            WebElement $birthInput = driver.findElement(By.id("birth_input"));

            //생년월일 설정
            actualBirth = setTextToInputBox($birthInput, expectedBirth);

            //비교
            super.printLogAndCompare(title, expectedBirth, actualBirth);

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

            WebElement $genderLabel = driver.findElement(By.xpath(".//label[normalize-space()='" + expectedGender + "']"));

            //성별 클릭
            clickByJavascriptExecutor($genderLabel);

            //실제 클릭된 성별 읽어오기
            String script = "return $('input[name=gender]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualGender = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {

            //납입주기 선택을 위해 버튼 클릭
            WebElement $napCycleTh = driver.findElement(By.xpath("//th[normalize-space()='납입주기']"));
            WebElement $napCycleTd = $napCycleTh.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $napCycleA = $napCycleTd.findElement(By.xpath(".//a[@title]"));
            clickByJavascriptExecutor($napCycleA);

            //납입주기 선택
            WebElement $napCycleUl = $napCycleTd.findElement(By.tagName("ul"));
            $napCycleA = $napCycleUl.findElement(By.xpath(".//a[@data-option-value='" + expectedNapCycle + "']"));
            clickByJavascriptExecutor($napCycleA);

            //실제 선택된 납입주기 읽어오기
            WebElement $napCycleLi = $napCycleUl.findElement(By.xpath("./li[@class[contains(., 'active')]]"));
            $napCycleA = $napCycleLi.findElement(By.tagName("a"));
            actualNapCycle = $napCycleA.getAttribute("data-option-value");

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {

            //보험기간 선택을 위해 버튼 클릭
            WebElement $insTermTh = driver.findElement(By.xpath("//th[normalize-space()='보험기간']"));
            WebElement $insTermTd = $insTermTh.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $insTermA = $insTermTd.findElement(By.xpath(".//a[@title]"));
            clickByJavascriptExecutor($insTermA);

            //보험기간 선택
            WebElement $insTermUl = $insTermTd.findElement(By.tagName("ul"));
            $insTermA = $insTermUl.findElement(By.xpath(".//a[@data-option-value='" + expectedInsTerm + "']"));
            clickByJavascriptExecutor($insTermA);

            //실제 선택된 보험기간 읽어오기
            WebElement $insTermLi = $insTermUl.findElement(By.xpath("./li[@class[contains(., 'active')]]"));
            $insTermA = $insTermLi.findElement(By.tagName("a"));
            actualInsTerm = $insTermA.getAttribute("data-option-value");

            //비교
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

            //납입기간 선택을 위해 버튼 클릭
            WebElement $napTermTh = driver.findElement(By.xpath("//th[normalize-space()='납입기간']"));
            WebElement $napTermTd = $napTermTh.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $napTermA = $napTermTd.findElement(By.xpath(".//a[@title]"));
            clickByJavascriptExecutor($napTermA);

            //납입기간 선택
            WebElement $napTermUl = $napTermTd.findElement(By.tagName("ul"));
            $napTermA = $napTermUl.findElement(By.xpath(".//a[@data-option-value='" + expectedNapTerm + "']"));
            clickByJavascriptExecutor($napTermA);

            //실제 선택된 납입기간 읽어오기
            WebElement $napTermLi = $napTermUl.findElement(By.xpath("./li[@class[contains(., 'active')]]"));
            $napTermA = $napTermLi.findElement(By.tagName("a"));
            actualNapTerm = $napTermA.getAttribute("data-option-value");

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";

        int unit = 10000;
        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";

        try {

            //가입금액 element 찾기
            WebElement $assureMoneyTh = driver.findElement(By.xpath("//th[normalize-space()='가입금액']"));
            WebElement $assureMoneyTd = $assureMoneyTh.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $assureMoneyInput = $assureMoneyTd.findElement(By.id("joinPrium_input"));

            //가입금액 입력
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);
            actualAssureMoney = setTextToInputBox($assureMoneyInput, expectedAssureMoney);
            actualAssureMoney = actualAssureMoney.replaceAll("[^0-9]", "");

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(3);

            WebElement $premiumStrong = driver.findElement(By.id("resultPrice"));
            String premium = $premiumStrong.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
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
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        int unit = ((MoneyUnit) obj[1]).getValue();

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        PlanReturnMoney lastPlanReturnMoney = null;
        try {
            logger.info("해약환급금 예시 버튼 클릭");
            WebElement $button = driver.findElement(By.id("ccltXmpfBtn"));
            clickByJavascriptExecutor($button);

            WebElement $tbody = driver.findElement(By.id("ccltXmpfTbody"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $tdList.get(0).getText();
                String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(2).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(3).getText();

                premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
                returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);
                lastPlanReturnMoney = p;

                logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                    , term, premiumSum, returnMoney, returnRate);

                info.returnPremium = returnMoney;
            }

            //해약환급금 표의 마지막 값이 실제 만기에 해당하는 값인지 체크
            String lastTerm = lastPlanReturnMoney.getTerm().replaceAll(" ", "");
            int lastTermNum = Integer.parseInt(lastTerm.replaceAll("[^0-9]", ""));
            String insTerm = info.getInsTerm();
            int insTermNum = Integer.parseInt(insTerm.replaceAll("[^0-9]", ""));
            int age = Integer.parseInt(info.getAge());

            if(insTerm.contains("세")) {
                //보험기간이 "N세"인 경우

                /**
                 * 해약환급금 경과기간 = 보험기간 - 실제나이 인 경우에만 만기환급금 세팅
                 * ex)
                 * 보험기간 : 100세
                 * 실제나이 : 1세
                 * 해약환급금 경과기간 : "만기" or "99년"인 경우에만 만기환급금 세팅
                 */
                if(lastTerm.contains("만기") || lastTermNum == insTermNum - age) {
                    info.returnPremium = lastPlanReturnMoney.getReturnMoney();
                } else {
                    info.returnPremium = "0";
                }
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {
            WebElement $treatyTbody = driver.findElement(By.id("detailList"));

            logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String treatyName = welgramTreaty.treatyName;

                //원수사에서 해당 특약 tr 얻어오기
                WebElement $treatyNameP = $treatyTbody.findElement(By.xpath(".//p[normalize-space()='" + treatyName + "']"));
                WebElement $treatyTr = $treatyNameP.findElement(By.xpath("./ancestor::tr[1]"));
                $treatyTr = $treatyTr.findElement(By.xpath("./following-sibling::tr[1]"));

                //tr에 가입설계 특약정보 세팅하기
                setTreatyInfoFromTr($treatyTr, welgramTreaty);
            }

            logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
            List<WebElement> $treatyTrList = $treatyTbody.findElements(By.xpath("./tr[@class='bg_lightgray']"));
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            for(WebElement $treatyTr : $treatyTrList) {

                //tr로부터 특약정보 읽어오기
                CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                if(targetTreaty != null) {
                    targetTreatyList.add(targetTreaty);
                }
            }

            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
            if(result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }

            logger.info("상세정보 입력 버튼 클릭");
            WebElement $button = driver.findElement(By.id("ASDSPDM00ASubmitBtn"));
            clickByJavascriptExecutor($button);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 가입여부, 보험기간, 납입기간 이 있다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {
        boolean isMainTreaty = treatyInfo.productGubun == ProductGubun.주계약;
        int unit = 10000;
        String treatyName = treatyInfo.getTreatyName();
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();

        //특약 보험기간 영역
        WebElement $treatyInsTermTh = $tr.findElement(By.xpath("./th[normalize-space()='보험기간']"));
        WebElement $treatyInsTermTd = $treatyInsTermTh.findElement(By.xpath("./following-sibling::td[1]"));
        WebElement $treatyInsTermA = $treatyInsTermTd.findElement(By.xpath(".//a[@title]"));
        clickByJavascriptExecutor($treatyInsTermA);

        //특약 보험기간 선택
        logger.info("특약명 : {} 보험기간 세팅중...", treatyName);
        WebElement $treatyInsTermUl = $treatyInsTermTd.findElement(By.tagName("ul"));
        WebElement $treatyInsTermSpan = $treatyInsTermUl.findElement(By.xpath(".//span[normalize-space()='" + treatyInsTerm + "']"));
        $treatyInsTermA = $treatyInsTermSpan.findElement(By.xpath("./parent::a"));
        clickByJavascriptExecutor($treatyInsTermA);

        //특약 납입기간 영역
        WebElement $treatyNapTermTh = $tr.findElement(By.xpath("./th[normalize-space()='납입기간']"));
        WebElement $treatyNapTermTd = $treatyNapTermTh.findElement(By.xpath("./following-sibling::td[1]"));
        WebElement $treatyNapTermA = $treatyNapTermTd.findElement(By.xpath(".//a[@title]"));
        clickByJavascriptExecutor($treatyNapTermA);

        //특약 납입기간 선택
        logger.info("특약명 : {} 납입기간 세팅중...", treatyName);
        treatyNapTerm = treatyInsTerm.equals(treatyNapTerm) ?
            treatyNapTerm.replaceAll("[^0-9]", "") + "(전기납)" : treatyNapTerm;

        WebElement $treatyNapTermUl = $treatyNapTermTd.findElement(By.tagName("ul"));
        WebElement $treatyNapTermSpan = $treatyNapTermUl.findElement(By.xpath(".//span[normalize-space()='" + treatyNapTerm + "']"));
        $treatyNapTermA = $treatyNapTermSpan.findElement(By.xpath("./parent::a"));
        clickByJavascriptExecutor($treatyNapTermA);

        if(isMainTreaty) {
            //주계약인 경우 => 가입금액 세팅
            WebElement $treatyAssureMoneyTh = $tr.findElement(By.xpath("./th[normalize-space()='가입금액']"));
            WebElement $treatyAssureMoneyTd = $treatyAssureMoneyTh.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

            if($treatyAssureMoneyInput.isEnabled()) {
                logger.info("특약명 : {} 가입금액 세팅중...", treatyName);
                treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);
                setTextToInputBox($treatyAssureMoneyInput, treatyAssureMoney);
            }
        } else {
            //선택특약인 경우

            // CASE 1. 특약 가입 체크박스를 체크해야하는 경우
            By joinPosition = By.xpath("./th[normalize-space()='가입여부']");
            boolean isExist = helper.existElement($tr, joinPosition);

            if(isExist) {
                logger.info("특약명 : {} 가입 체크여부 세팅중...", treatyName);
                WebElement $treatyJoinTh = $tr.findElement(By.xpath("./th[normalize-space()='가입여부']"));
                WebElement $treatyJoinTd = $treatyJoinTh.findElement(By.xpath("./following-sibling::td[1]"));
                WebElement $treatyJoinLabel = $treatyJoinTd.findElement(By.tagName("label"));
                clickByJavascriptExecutor($treatyJoinLabel);
            }

            // CASE 2. 특약 가입금액을 세팅해야하는 경우
            By assureMoneyPosition = By.xpath("./th[normalize-space()='가입금액']");
            isExist = helper.existElement($tr, assureMoneyPosition);

            if(isExist) {
                logger.info("특약명 : {} 가입금액 세팅중...", treatyName);
                WebElement $treatyAssureMoneyTh = $tr.findElement(assureMoneyPosition);
                WebElement $treatyAssureMoneyTd = $treatyAssureMoneyTh.findElement(By.xpath("./following-sibling::td[1]"));
                WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.xpath(".//input[@type='text']"));

                if($treatyAssureMoneyInput.isEnabled()) {
                    treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);
                    setTextToInputBox($treatyAssureMoneyInput, treatyAssureMoney);
                }
            }
        }
    }


    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 세팅하는 특약정보에는 가입여부, 보험기간, 납입기간 이 있다.
     *
     * 가입(체크된) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(체크해제된) 특약인 경우 null을 리턴한다.
     * @param $tr
     * @return
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
        CrawlingTreaty treaty = new CrawlingTreaty();

        int unit = 10000;
        String treatyName = "";
        String treatyAssureMoney = "";
        String treatyInsTerm = "";
        String treatyNapTerm = "";

        //해당 tr이 주계약인지 특약인지 구분하기
        WebElement $treatyNameTr = $tr.findElement(By.xpath("./preceding-sibling::tr[1]"));
        WebElement $treatyNameP = $treatyNameTr.findElement(By.tagName("p"));
        WebElement $treatyTypeSpan = $treatyNameTr.findElement(By.tagName("span"));

        String treatyType = $treatyTypeSpan.getText().trim();
        treatyName = $treatyNameP.getText().trim();
        boolean isMainTreaty = "주계약".equals(treatyType);

        logger.info("특약명 : {} 정보 읽는중...", treatyName);
        treaty.setTreatyName(treatyName);

        //특약 보험기간 영역
        WebElement $treatyInsTermTh = $tr.findElement(By.xpath("./th[normalize-space()='보험기간']"));
        WebElement $treatyInsTermTd = $treatyInsTermTh.findElement(By.xpath("./following-sibling::td[1]"));
        WebElement $treatyInsTermUl = $treatyInsTermTd.findElement(By.tagName("ul"));
        WebElement $treatyInsTermLi = $treatyInsTermUl.findElement(By.xpath("./li[@class[contains(., 'active')]]"));
        WebElement $treatyInsTermA = $treatyInsTermLi.findElement(By.tagName("a"));
        treatyInsTerm = $treatyInsTermA.getAttribute("data-option-value");
        treaty.setInsTerm(treatyInsTerm);

        //특약 납입기간 영역
        WebElement $treatyNapTermTh = $tr.findElement(By.xpath("./th[normalize-space()='납입기간']"));
        WebElement $treatyNapTermTd = $treatyNapTermTh.findElement(By.xpath("./following-sibling::td[1]"));
        WebElement $treatyNapTermUl = $treatyNapTermTd.findElement(By.tagName("ul"));
        WebElement $treatyNapTermLi = $treatyNapTermUl.findElement(By.xpath("./li[@class[contains(., 'active')]]"));
        WebElement $treatyNapTermA = $treatyNapTermLi.findElement(By.tagName("a"));
        treatyNapTerm = $treatyNapTermA.getAttribute("data-option-value");
        treatyNapTerm = treatyNapTerm.contains("전기납") ? treatyInsTerm : treatyNapTerm;
        treaty.setNapTerm(treatyNapTerm);

        if(isMainTreaty) {
            //주계약인 경우
            WebElement $treatyAssureMoneyTh = $tr.findElement(By.xpath("./th[normalize-space()='가입금액']"));
            WebElement $treatyAssureMoneyTd = $treatyAssureMoneyTh.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.xpath(".//input[@type='text']"));

            treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");
            treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
            treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) * unit);
            treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
        } else {
            //특약인 경우

            // CASE 1. 특약 가입 체크박스를 체크해야하는 경우
            By joinPosition = By.xpath("./th[normalize-space()='가입여부']");
            boolean isExist = helper.existElement($tr, joinPosition);

            if(isExist) {
                WebElement $treatyJoinTh = $tr.findElement(By.xpath("./th[normalize-space()='가입여부']"));
                WebElement $treatyJoinTd = $treatyJoinTh.findElement(By.xpath("./following-sibling::td[1]"));
                WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input[@name='joinYn']"));

                if(!$treatyJoinInput.isSelected()) {
                    treaty = null;
                }
            }

            // CASE 2. 특약 가입금액을 세팅해야하는 경우
            By assureMoneyPosition = By.xpath("./th[normalize-space()='가입금액']");
            isExist = helper.existElement($tr, assureMoneyPosition);

            if(isExist) {
                WebElement $treatyAssureMoneyTh = $tr.findElement(assureMoneyPosition);
                WebElement $treatyAssureMoneyTd = $treatyAssureMoneyTh.findElement(By.xpath("./following-sibling::td[1]"));
                WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.xpath(".//input[@type='text']"));
                treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");

                if(StringUtils.isEmpty(treatyAssureMoney)) {
                    treaty = null;
                } else {
                    treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
                    treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) * unit);
                    treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
                }
            }
        }

        return treaty;
    }

    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#body_loading_box");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 우체국 공시실 상품에 대해서는 input에 값을 세팅하기 위해
     * 클릭을 해야하는데 기존의 SeleniumCrawlingHelper 내부의 클릭이 작동하지 않는다.
     * 따라서 우체국 공시실 상품의 input을 세팅할 때는 js 문법으로 클릭을 시켜주도록 한다.
     *
     * input에 text 입력
     * @param $input input element
     * @param text 입력할 텍스트
     * @return 실제 input에 입력된 value
     * @throws Exception
     */
    public String setTextToInputBox(WebElement $input, String text) throws Exception {
        String script = "return $(arguments[0]).val();";
        String actualValue = "";

        if("input".equals($input.getTagName())) {
            //text 입력
            helper.waitElementToBeClickable($input);
            clickByJavascriptExecutor($input);
            $input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            $input.sendKeys(text);

            //실제 input에 입력된 value 읽어오기
            actualValue = String.valueOf(helper.executeJavascript(script, $input));
            logger.info("actual input value :: {}", actualValue);

        } else {
            logger.error("파라미터로 input element를 전달해주세요");
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ELEMENT);
        }

        return actualValue;
    }
}
