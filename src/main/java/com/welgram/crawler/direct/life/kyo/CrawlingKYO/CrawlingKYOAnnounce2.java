package com.welgram.crawler.direct.life.kyo.CrawlingKYO;

import com.welgram.common.MoneyUtil;
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
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.InsuranceUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;



// 2023.07.31 | 조하연 | 교보생명 공시실 크롤링 코드
public abstract class CrawlingKYOAnnounce2 extends CrawlingKYONew {

    @Override
    public void waitLoadingBar() {

        try {
            helper.waitForCSSElement(".ui-loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 공시실 상품명 찾기
     * <p>
     * 카테고리를 하나하나 클릭해가며 공시실 상품명과 일치하는 상품을 찾아 보험료 계산 버튼을 클릭한다.
     *
     * @param expectedProductName 공시실 상품명
     * @throws Exception
     */
    public void findProductName(String expectedProductName) throws CommonCrawlerException {

        boolean isFound = false;

        try {
            //메뉴 관련 element 찾기
            WebElement $contentDiv = driver.findElement(By.id("contents"));
            WebElement $menuUl = $contentDiv.findElement(By.xpath(".//ul[@class='menu']"));
            List<WebElement> $menuLiList = $menuUl.findElements(By.tagName("li"));

            //메뉴 내의 카테고리 클릭
            for (WebElement $menuLi : $menuLiList) {
                logger.info("메뉴 카테고리 : {} 클릭", $menuLi.getText().trim());
                $menuLi.click();
                WaitUtil.waitFor(2);

                //상품명 찾기
                boolean isExist = false;
                By position = By.xpath(".//td[normalize-space()='" + expectedProductName + "']");
                WebElement $productsTbody = $contentDiv.findElement(By.id("prodList"));

                isExist = helper.existElement($productsTbody, position);
                if (isExist) {
                    WebElement $productTd = $productsTbody.findElement(position);
                    WebElement $calcButtonTd = $productTd.findElement(By.xpath("./following-sibling::td[1]"));
                    WebElement $calcButton = $calcButtonTd.findElement(By.tagName("button"));

                    logger.info("상품명 : {} 보험료 계산 버튼 클릭", expectedProductName);
                    click($calcButton);

                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                String msg = "상품명 : " + expectedProductName + " 을 찾지 못했습니다.";
                logger.info(msg);
                throw new Exception(msg);
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_PRODUCT_NAME, e.getMessage());
        }
    }



    /**
     * 고객정보 입력
     *
     * @param info
     * @throws CommonCrawlerException
     */
    public void setUserInfo(CrawlingProduct info) throws CommonCrawlerException {

        String category = info.getCategoryName();
        String birth = info.getFullBirth();
        int gender = info.getGender();

        WebElement $userInfoDiv = null;

        try {

            if ("어린이보험".equals(category)) {
                $userInfoDiv = driver.findElement(By.id("userInfoType2"));
                logger.info("주피보험자 생년월일 & 성별 설정");
                setBirthDayAndGender($userInfoDiv, birth, gender);

                $userInfoDiv = driver.findElement(By.id("userInfoType3"));
                logger.info("종피보험자 생년월일 & 성별 설정");
                setBirthDayAndGender($userInfoDiv, info.getParent_FullBirth(), gender);

            } else if ("태아보험".equals(category)) {
                //태아 성별은 남자로 고정
                String dueDate = InsuranceUtil.getDateOfBirth(12);
                $userInfoDiv = driver.findElement(By.id("userInfoType2"));
                logger.info("주피보험자 생년월일 & 성별 설정");
                setBirthDayAndGender($userInfoDiv, dueDate, MALE);

                $userInfoDiv = driver.findElement(By.id("userInfoType3"));
                logger.info("종피보험자 생년월일 & 성별 설정");
                setBirthDayAndGender($userInfoDiv, info.getFullBirth(), gender);

            } else {
                logger.info("생년월일 & 성별 설정");
                $userInfoDiv = driver.findElement(By.id("userInfoType1"));
                setBirthDayAndGender($userInfoDiv, info.getFullBirth(), gender);
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_USER_INFO, e.getMessage());
        }
    }



    /**
     * 주계약 정보 입력
     *
     * @param info
     * @throws CommonCrawlerException
     */
    public void setMainTreatyInfo(CrawlingProduct info) throws CommonCrawlerException {

        By position = null;
        boolean isExistDOM = false;

        try {
            //주계약 설정 영역 element
            WebElement $mainTreatySection = driver.findElement(By.id("areaMain"));

            position = By.xpath(".//label[normalize-space()='보험종류']");
            isExistDOM = helper.existElement($mainTreatySection, position);
            if (isExistDOM) {
                logger.info("보험종류 설정");
                setProductType(info.getTextType());
            }

            position = By.xpath(".//label[normalize-space()='납입주기']");
            isExistDOM = helper.existElement($mainTreatySection, position);
            if (isExistDOM) {
                logger.info("납입주기 설정");
                setNapCycle(info.getNapCycleName());
            }

            position = By.xpath(".//label[normalize-space()='보험기간']");
            isExistDOM = helper.existElement($mainTreatySection, position);
            if (isExistDOM) {
                logger.info("보험기간 설정");
                setInsTerm(info.getInsTerm() + "만기");
            }

            position = By.xpath(".//label[normalize-space()='납입기간']");
            isExistDOM = helper.existElement($mainTreatySection, position);
            if (isExistDOM) {
                logger.info("납입기간 설정");
                String napTerm = info.getInsTerm().equals(info.getNapTerm()) ? "전기납" : info.getNapTerm();

                // 보험기간과 납입기간이 같을 때 상품마다 "전기납"으로 표기하는 경우도 있고, 납입기간 그대로 표기하는 경우도 있음.
                // 따라서 먼저 보험기간과 납입기간이 같아면 "전기납"으로 찾아보고 없다면 납입기간 텍스트로 찾아본다.
                try {
                    setNapTerm(napTerm);
                } catch (SetNapTermException e) {
                    setNapTerm(info.getNapTerm());
                }
            }

            position = By.xpath(".//label[normalize-space()='가입금액']");
            isExistDOM = helper.existElement($mainTreatySection, position);
            if (isExistDOM) {
                logger.info("가입금액 설정");
                setAssureMoney(info.getAssureMoney());
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_MAIN_TERATY, e.getMessage());
        }
    }



    public void setSubTreatiesInfo(CrawlingProduct info) throws CommonCrawlerException {

        //선택특약 추려내기
        List<CrawlingTreaty> subTreatyList
            = info.getTreatyList().stream()
                .filter(t -> t.productGubun == ProductGubun.선택특약)
                .collect(Collectors.toList());

        //가입금액이 연령,성별마다 달라지는 특약 추려내기
        List<CrawlingTreaty> specialTreatyList
            = info.getTreatyList().stream()
                .filter(t -> t.getAssureMoney() == 0)
                .collect(Collectors.toList());

        try {
            //가입설계에 선택특약이 있을 경우에만
            if (subTreatyList.size() > 0 || specialTreatyList.size() > 0) {

                logger.info("가입설계 정보대로 선택특약 세팅하기");
                for (CrawlingTreaty treaty : subTreatyList) {
                    String treatyName = treaty.getTreatyName();
                    WebElement $treatyNameSpan = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']//*[contains(@class,'txt')]"));
//                    WebElement $treatyNameSpan = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
                    WebElement $treatyTr = $treatyNameSpan.findElement(By.xpath("./ancestor::tr[1]"));
                    setTreatyInfoFromTr($treatyTr, treaty);
                }

                logger.info("보험료 계산 버튼 클릭");
                WebElement $button = driver.findElement(By.xpath("//button[normalize-space()='보험료 계산'][@class[contains(., 'md')]]"));
                click($button);

                logger.info("실제 원수사에서 체크된 특약 정보 읽어오기");
                List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
                List<WebElement> $checkedInputs = driver.findElements(By.cssSelector("#areaScn input[name=pdtScnCd]:checked"));
                for (WebElement $checkedInput : $checkedInputs) {
                    WebElement $treatyTr = $checkedInput.findElement(By.xpath("./ancestor::tr[1]"));

                    CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);
                    if (targetTreaty != null) {
                        targetTreatyList.add(targetTreaty);
                    }
                }

                logger.info("가입금액이 매번 달라지는 특약의 가입금액 읽어오기");
                for (CrawlingTreaty specialTreaty : specialTreatyList) {

                    String treatyName = specialTreaty.getTreatyName();
                    WebElement $treatyNameSpan = driver.findElement(By.xpath("//td[normalize-space()='" + treatyName + "']"));
                    WebElement $treatyTr = $treatyNameSpan.findElement(By.xpath("./ancestor::tr[1]"));
                    CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                    //특약 계산테이블에 달라지는 가입금액 세팅
                    PlanCalc planCalc = new PlanCalc();
                    planCalc.setMapperId(Integer.parseInt(specialTreaty.mapperId));
                    planCalc.setInsAge(Integer.parseInt(info.getAge()));
                    planCalc.setGender(info.gender == MALE ? "M" : "F");
                    planCalc.setAssureMoney(String.valueOf(targetTreaty.getAssureMoney()));
                    specialTreaty.setPlanCalc(planCalc);
                }

                //원수사와 가입설계 특약 정보를 비교하기 전에 가입금액이 매번 달라지는 특약 정보는 제외시키고 비교를 진행
                specialTreatyList.forEach(st -> targetTreatyList.removeIf(tt -> tt.getTreatyName().equals(st.getTreatyName())));
                subTreatyList =
                    info.getTreatyList().stream()
                        .filter(t -> t.productGubun == ProductGubun.선택특약 && t.getAssureMoney() != 0)
                        .collect(Collectors.toList());

                logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
                boolean result = advancedCompareTreaties(targetTreatyList, subTreatyList, new CrawlingTreatyEqualStrategy2());
                if (result) {
                    logger.info("특약 정보 모두 일치");

                } else {
                    logger.info("특약 정보 불일치");
                    throw new Exception();
                }

            } else {
                logger.info("가입설계에 선택특약이 존재하지 않습니다.");
                logger.info("보험료 계산 버튼 클릭");
                WebElement $button = driver.findElement(By.xpath("//button[normalize-space()='보험료 계산'][@class[contains(., 'md')]]"));
                click($button);
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_TREATY, e.getMessage());
        }
    }



    /**
     * 특약 tr에 특약정보 세팅 세팅하는 특약정보에는 가입여부, 보험기간, 납입기간, 가입금액이 있다.
     *
     * @param $tr        입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws Exception
     */
    protected void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {

        //가입금액이 달라지는 특약인지 여부(true : 가입금액 매번 달라지는 특약, false : 가입금액 고정인 특약)
        boolean isSpecialTreaty = treatyInfo.getAssureMoney() == 0;

//        int unit = MoneyUnit.만원.getValue();
        String treatyName = treatyInfo.getTreatyName();
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());
        String treatyInsTerm = treatyInfo.getInsTerm();
        String treatyNapTerm = treatyInfo.getNapTerm();
        treatyNapTerm = treatyInsTerm.equals(treatyNapTerm)
                ? "전기납"
                : ("전기납".equals(treatyNapTerm))
                    ?  treatyNapTerm
                    : treatyNapTerm + "납";
        treatyInsTerm = treatyInsTerm + "만기";

        //특약 가입체크 영역
        WebElement $treatyJoinTd = $tr.findElement(By.xpath("./td[1]"));
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input[@name='pdtScnCd']"));

        //특약이 미가입 처리된 경우에만 가입 처리
        if ($treatyJoinInput.isEnabled() && !$treatyJoinInput.isSelected()) {
            logger.info("특약명 : {} 가입체크영역 세팅중...", treatyName);
            $treatyJoinInput.click();
            modalCheck();
        }

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tr.findElement(By.xpath("./td[2]"));
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));
        logger.info("특약명 : {} 보험기간 세팅중...", treatyName);
        helper.selectByText_check($treatyInsTermSelect, treatyInsTerm);

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tr.findElement(By.xpath("./td[3]"));
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));
        logger.info("특약명 : {} 납입기간 세팅중...", treatyName);

        try {
            logger.info("CHECK :: {}", treatyNapTerm);
            //보험기간과 납입기간이 같을 때 납입기간을 "전기납"이라 표현할 때도 있고 그냥 납입기간 그대로 표현하는 경우가 있음.
            helper.selectByText_check($treatyNapTermSelect, treatyNapTerm);

        } catch (NoSuchElementException e) {
            logger.info("CHECK :: {}", treatyNapTerm);
            treatyNapTerm = treatyInsTerm.replace("만기", "");
            treatyNapTerm = treatyNapTerm + "납";
            helper.selectByText_check($treatyNapTermSelect, treatyNapTerm);
        }

        //특약 가입금액 영역
        if (!isSpecialTreaty) {
            //가입금액이 고정인 특약에 대해서만 가입금액 세팅하기
            WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[4]"));
            WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));
            WebElement $treatyAssureMoneyUnitI = $treatyAssureMoneyTd.findElement(By.tagName("i"));

            int unit = MoneyUnit.valueOf($treatyAssureMoneyUnitI.getText().trim()).getValue();
            treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);

            logger.info("특약명 : {} 가입금액 세팅중...", treatyName);
            helper.sendKeys4_check($treatyAssureMoneyInput, treatyAssureMoney);
        }
    }



    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다. 특약정보에는 특약명, 보험기간, 납입기간, 가입금액이 있다.
     * <p>
     * 가입(체크된) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고, 미가입(체크해제된) 특약인 경우 null을 리턴한다.
     *
     * @param $tr
     * @return
     * @throws Exception
     */
    protected CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {

        CrawlingTreaty treaty = null;
        String treatyName = "";
        String treatyAssureMoney = "";
        String treatyInsTerm = "";
        String treatyNapTerm = "";

//        int unit = MoneyUnit.만원.getValue();
        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));
        String script = "return $(arguments[0]).find('option:selected').text();";

        //특약 가입체크 영역
        WebElement $treatyJoinTd = $tdList.get(0);
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input[@name='pdtScnCd']"));
        WebElement $treatyNameSpan = $treatyJoinTd.findElement(By.tagName("span"));
        treatyName = $treatyNameSpan.getText().trim();

        logger.info("특약명 : {} 정보 읽는중...", treatyName);

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tdList.get(1);
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.tagName("select"));
        treatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
        treatyInsTerm = treatyInsTerm.replace("만기", "");

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tdList.get(2);
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.tagName("select"));
        treatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));
        treatyNapTerm = "전기납".equals(treatyNapTerm) ? treatyInsTerm : treatyNapTerm;
        treatyNapTerm = treatyNapTerm.replace("납", "");

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tdList.get(3);
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));
        WebElement $treatyAssureMoneyUnitI = $treatyAssureMoneyTd.findElement(By.tagName("i"));
        int unit = MoneyUnit.valueOf($treatyAssureMoneyUnitI.getText().trim()).getValue();
        treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");
        treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) * unit);

        //특약이 가입인 경우에만 특약 정보 세팅
        if ($treatyJoinInput.isSelected()) {
            treaty = new CrawlingTreaty();
            treaty.setTreatyName(treatyName);
            treaty.setInsTerm(treatyInsTerm);
            treaty.setNapTerm(treatyNapTerm);
            treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
        }

        return treaty;
    }



    public void setBirthDayAndGender(WebElement $userInfoDiv, String birth, int gender) throws Exception {
        logger.info("생년월일 설정");
        setBirthday($userInfoDiv, birth);

        logger.info("성별 설정");
        setGender($userInfoDiv, gender);
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";
        WebElement $userInfoDiv = (WebElement) obj[0];
        String expectedBirth = (String) obj[1];
        String actualBirth = "";

        try {
            //생년월일 관련 element 찾기
            WebElement $birthInput = $userInfoDiv.findElement(By.cssSelector("input[id^=inpBhdt]"));
            String id = $birthInput.getAttribute("id");
            WebElement $birthLabel = driver.findElement(By.xpath(".//label[@for='" + id + "']"));

            //생년월일 입력
            $birthLabel.click();
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

            //비교
            super.printLogAndCompare(title, expectedBirth, actualBirth);

        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        WebElement $userInfoDiv = (WebElement) obj[0];
        int gender = (int) obj[1];
        String expectedGender = (gender == MALE) ? "남성" : "여성";
        String actualGender = "";

        try {
            //성별 관련 element 찾기
//            WebElement $userInfoDiv = driver.findElement(By.id("userInfoType1"));
            WebElement $genderLabel = $userInfoDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedGender + "']"));
            click($genderLabel);

            //실제 클릭된 성별 읽어오기
            WebElement $genderInput = $userInfoDiv.findElement(By.xpath(".//input[@type='radio']"));
            String attrName = $genderInput.getAttribute("name");
            String script = "return $('input[name=" + attrName + "]:checked').attr('id');";
            String attrId = String.valueOf(helper.executeJavascript(script));
            $genderLabel = $userInfoDiv.findElement(By.xpath(".//label[@for='" + attrId + "']"));
            actualGender = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $select = driver.findElement(By.name("pdtScnCd_isPd"));
            actual = helper.selectByText_check($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $select = driver.findElement(By.name("pdtScnCd_paPd"));
            expected = expected.contains("납") ? expected : expected + "납";
            actual = helper.selectByText_check($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입주기";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $select = driver.findElement(By.id("pdtMcrnCd_paCyc"));
            actual = helper.selectByText_check($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        int unit = MoneyUnit.만원.getValue();

        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $element = driver.findElement(By.name("pdtScnCd_sbcAmt"));
            expected = String.valueOf(Integer.parseInt(expected) / unit);

            if ("input".equals($element.getTagName())) {
                //가입금액 세팅란이 input인 경우
                actual = helper.sendKeys4_check($element, expected);
            } else if ("select".equals($element.getTagName())) {
                //가입금액 세팅란이 select인 경우
                actual = helper.selectByValue_check($element, expected);
            }

            expected = String.valueOf(Integer.parseInt(expected) * unit);
            actual = actual.replaceAll("[^0-9]", "");
            actual = String.valueOf(Integer.parseInt(actual) * unit);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        String title = "보험종류";
        String expected = (String) obj[0];
        String actual = "";
        String[] textTypeList = expected.split("#");

        try {
            WebElement $select = driver.findElement(By.id("sel_gdcl"));

            //텍스트 타입을 돌면서
            for (String textType : textTypeList) {
                textType = textType.trim();

                try {
                    helper.selectByText_check($select, textType);
                    expected = textType;
                    break;

                } catch (NoSuchElementException e) {

                }
            }

            //텍스트 타입을 다 돌았음에도 option을 선택못하는 경우도 있기때문에 해당 로직을 추가해줘야 함.
            String script = "return $(arguments[0]).find('option:selected').text();";
            $select = driver.findElement(By.id("sel_gdcl"));
            actual = String.valueOf(helper.executeJavascript(script, $select));

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetProductTypeException(e.getMessage());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        String title = "보험료";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String category = info.getCategoryName();
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.주계약)
            .findFirst()
            .get();

        try {
            WebElement $premiumDiv = driver.findElement(By.id("totPrmTx"));
            WebElement $premiumStrong = null;
            String premium = "";

            if ("태아보험".equals(category)) {
                //출생전 보험료(=주계약 보험료)
                $premiumStrong = $premiumDiv.findElement(By.xpath("./strong[@class='stt'][1]"));
                premium = $premiumStrong.getText().trim();
                premium = premium.replaceAll("[^0-9]", "");
                mainTreaty.monthlyPremium = premium;

                //출생후 보험료(=계속 보험료)
                $premiumStrong = $premiumDiv.findElement(By.xpath("./strong[@class='stt'][2]"));
                premium = $premiumStrong.getText().trim();
                premium = premium.replaceAll("[^0-9]", "");
                info.nextMoney = premium;

                logger.info("출생전 보험료 : {}", mainTreaty.monthlyPremium);
                logger.info("출생후 보험료 : {}", info.nextMoney);

            } else {
                $premiumStrong = $premiumDiv.findElement(By.xpath(".//strong[@class='stt']"));
                premium = $premiumStrong.getText().trim();
                premium = premium.replaceAll("[^0-9]", "");

                mainTreaty.monthlyPremium = premium;
            }

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                String msg = "주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.";
                logger.info(msg);
                throw new Exception(msg);
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

            logger.info("스크린샷 찍기");
            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        String category = info.getCategoryName();
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        PlanReturnMoney lastPlanReturnMoney = null;

        try {
            logger.info("보장내용 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//button[normalize-space()='보장내용']"));
            click($button);

            logger.info("해약환급금 버튼 클릭");
            $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금']"));
            click($button);

            logger.info("해약환급금 크롤링 시작~");
            WebElement $returnMoneyDiv = driver.findElement(By.id("trmRview"));
            WebElement $returnMoneyTbody = $returnMoneyDiv.findElement(By.tagName("tbody"));
            List<WebElement> $trList = $returnMoneyTbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                String term = "";
                String premiumSum = "";
                String returnMoney = "";
                String returnRate = "";
                String returnMoneyAvg = "";
                String returnRateAvg = "";
                String returnMoneyMin = "";
                String returnRateMin = "";

                PlanReturnMoney p = new PlanReturnMoney();

                if ($tdList.size() == 4) {
                    term = $tdList.get(0).getText().trim();
                    premiumSum = $tdList.get(1).getText().trim();
                    returnMoney = $tdList.get(2).getText().trim();
                    returnRate = $tdList.get(3).getText().trim();

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                    logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate);

                } else if ($tdList.size() == 7) {
                    boolean isOnlyMainTreaty = info.getTreatyList().stream()
                        .noneMatch(t -> t.productGubun == ProductGubun.선택특약);

                    term = $tdList.get(0).getText().trim();

                    if (isOnlyMainTreaty) {
                        //주계약만 있는 경우
                        premiumSum = $tdList.get(4).getText().trim();
                        returnMoney = $tdList.get(5).getText().trim();
                        returnRate = $tdList.get(6).getText().trim();

                    } else {
                        //주계약 + 선택특약인 경우
                        premiumSum = $tdList.get(1).getText().trim();
                        returnMoney = $tdList.get(2).getText().trim();
                        returnRate = $tdList.get(3).getText().trim();
                    }

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                    logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate);

                } else if ($tdList.size() == 8) {
                    //최저, 평균, 공시 환급금을 모두 제공하는 경우
                    term = $tdList.get(0).getText().trim();
                    premiumSum = $tdList.get(1).getText().trim();
                    returnMoney = $tdList.get(2).getText().trim();
                    returnRate = $tdList.get(3).getText().trim();
                    returnMoneyAvg = $tdList.get(4).getText().trim();
                    returnRateAvg = $tdList.get(5).getText().trim();
                    returnMoneyMin = $tdList.get(6).getText().trim();
                    returnRateMin = $tdList.get(7).getText().trim();

                    premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                    returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));
                    returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
                    returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));

                    p.setReturnMoneyAvg(returnMoneyAvg);
                    p.setReturnRateAvg(returnRateAvg);
                    p.setReturnMoneyMin(returnMoneyMin);
                    p.setReturnRateMin(returnRateMin);

                    logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {} | 평균환급금 : {} | 평균환급률 : {} | 최저환급금 : {} | 최저환급률 : {}"
                        , term, premiumSum, returnMoney, returnRate, returnMoneyAvg, returnRateAvg, returnMoneyMin, returnRateMin);
                }

                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                lastPlanReturnMoney = p;
                planReturnMoneyList.add(p);

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }

            logger.info("해약환급금 크롤링 끝~");

            //해약환급금 표의 마지막 값이 실제 만기에 해당하는 값인지 체크
            String lastTerm = lastPlanReturnMoney.getTerm().replaceAll(" ", "");
            int lastTermNum = Integer.parseInt(lastTerm.replaceAll("[^0-9]", ""));
            String insTerm = info.getInsTerm();
            int insTermNum = Integer.parseInt(insTerm.replaceAll("[^0-9]", ""));
            int age = "태아보험".equals(category) ? 0 : Integer.parseInt(info.getAge());    //태아보험의 경우 나이를 태아기준으로 계산해야 함.

            if (insTerm.contains("세")) {
                //보험기간이 "N세"인 경우 = 세납인 경우

                /**
                 * 해약환급금 경과기간 = 보험기간 - 실제나이 인 경우에만 만기환급금 세팅
                 * ex)
                 * 보험기간 : 100세
                 * 실제나이 : 1세
                 * 해약환급금 경과기간 : "만기" or "99년"인 경우에만 만기환급금 세팅
                 */
                if (lastTerm.contains("만기") || lastTermNum == insTermNum - age) {
                    info.returnPremium = lastPlanReturnMoney.getReturnMoney();

                } else {
                    info.returnPremium = "0";
                }
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }



    public void modalCheck() throws Exception {

        By position = By.xpath("//article[@role='alertdialog']");
        boolean isExist = helper.existElement(position);

        if (isExist) {
            logger.info("모달 발생!");
            WebElement $modal = driver.findElement(position);
            WebElement $button = $modal.findElement(By.xpath(".//button[normalize-space()='확인']"));

            logger.info("모달 확인 버튼 클릭!");
            click($button);
        }
    }
}