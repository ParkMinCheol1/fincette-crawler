package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.*;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingHNFMobile extends CrawlingHNFNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    // setUserName("","","", "")
    @Override
    public void setUserName(Object... obj) throws SetUserNameException {
        super.setUserName(obj);
    }

    public void setUserInfo(CrawlingProduct info) throws Exception {
        boolean isExist = false;

        logger.info("생년월일 설정");
        setBirthday(info.getBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        By vehiclePosition = By.id("divJobDriverWrapper");
        isExist = helper.existElement(vehiclePosition);
        if(isExist) {
            logger.info("영업용 자동차 운전 여부 설정");
            setVehicle("아니오");
        }



        logger.info("보험료 계산하기 버튼 클릭");
        WebElement $button = driver.findElement(By.id("btnCalcInsurance"));
        click($button);
    }

    /**
     * 가입조건 설정
     *
     * 다이렉트 모든 상품을 대상으로 표준화를 해놓았기 때문에
     * 세팅할 항목이 존재하는지 여부를 먼저 판단한 후에 있을 경우에만 항목을 세팅한다.
     * @param info
     * @throws Exception
     */
    public void setJoinCondition(CrawlingProduct info) throws Exception {
        boolean isExist = false;

//        logger.info("변경 버튼 클릭");
//        WebElement $button = driver.findElement(By.xpath("//a[@title='펼치기']"));
//        click($button);
//        WaitUtil.waitFor(2);

        isExist = helper.existElement(By.xpath("//p[normalize-space()='갱신유형']"));
        if(isExist) {
            logger.info("갱신유형 설정");
            setRenewType(info.productType.toString());
        }

        isExist = helper.existElement(By.xpath("//p[normalize-space()='납입기간 및 보험기간']"));
        if(isExist) {
            logger.info("납입기간 및 보험기간 설정");
            setInsTerm(info.getInsTerm());
        }

    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {
            WebElement $birthInput = driver.findElement(By.id("txtBirth"));

            //생년월일 설정
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

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
        String expectedGender = (gender == MALE) ? "남성" : "여성";
        String actualGender = "";

        try {

            WebElement $genderDiv = driver.findElement(By.id("divSelSex"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("//label[normalize-space()='" + expectedGender + "']"));

            //성별 설정
            click($genderLabel);

            //실제 클릭된 성별 값 읽어오기
            String script = "return $('input[name=rdosSex]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualGender = $genderLabel.getText().trim();

            //성별 비교
            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {
        String title = "영업용 자동차 운전여부";

        String expectedVehicle = (String) obj[0];
        String actualVehicle = "";

        try {

            WebElement $vehicleDiv = driver.findElement(By.id("divJobDriverWrapper"));
            WebElement $vehicleLabel = $vehicleDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedVehicle + "']"));

            //운전여부 설정
            click($vehicleLabel);

            //실제 클릭된 운전여부 값 읽어오기
            String script = "return $('input[name=rdoJobDriver]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $vehicleLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualVehicle = $vehicleLabel.getText().trim();

            //운전여부 비교
            super.printLogAndCompare(title, expectedVehicle, actualVehicle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {
        String title = "갱신유형";
        String expectedRenewType = (String) obj[0];
        String actualRenewType = "";

        try {

            WebElement $renewTypeDiv = driver.findElement(By.xpath("//p[normalize-space()='갱신유형']/following-sibling::div[1]"));
            WebElement $renewTypeLabel = $renewTypeDiv.findElement(By.xpath("//label[normalize-space()='" + expectedRenewType + "']"));

            //갱신유형 선택
            click($renewTypeLabel);

            //실제 클릭된 갱신유형 값 읽어오기
            String script = "return $('input[name=rdoType]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $renewTypeLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualRenewType = $renewTypeLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedRenewType, actualRenewType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RENEW_TYPE;
            throw new SetRenewTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";


        try {

            WebElement $insTermDiv = driver.findElement(By.id("divRenewGigan"));
            WebElement $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedInsTerm + "']"));

            //보험기간 설정
            click($insTermLabel);

            //실제 클릭된 보험기간 값 읽어오기
            String script = "return $('input[name=rdoRenewGigan]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $insTermLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
            actualInsTerm = $insTermLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "플랜명";
        String actualPlan = "";

        try {

            WebElement $planTbody = driver.findElement(By.id("ulPlncodWrapper"));
//            WebElement $planLabel = $planTbody.findElement(By.xpath(".//label[@class='planTitle'][normalize-space()='" + expectedPlan + "']"));
            WebElement $planLabel = driver.findElement(By.xpath("//label[contains(., '" + expectedPlan + "')]"));

            //플랜 선택
            click($planLabel);

            //실제 클릭된 플랜 읽어오기
            WebElement $selectedPlanTh = $planTbody.findElement(By.xpath(".//th[@class[contains(., 'on')]]"));
            $planLabel = $selectedPlanTh.findElement(By.xpath(".//label[@class='planTitle']"));

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {
            WebElement $treatyTbody = driver.findElement(By.id("tbdGuaranteeList"));
            List<WebElement> $treatyTrList = $treatyTbody.findElements(By.xpath(".//td[@class[contains(., 'on')]]/span/ancestor::tr[1]"));

            List<String> targetTreatyNameList = new ArrayList<>();
            List<String> welgramTreatyNameList = new ArrayList<>();

            //원수사 특약명 수집
            for(WebElement $treatyTr : $treatyTrList) {
                WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));
                WebElement $treatyNameSpan = $treatyNameTh.findElement(By.tagName("span"));

                //원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
                helper.moveToElementByJavascriptExecutor($treatyNameSpan);
                String targetTreatyName = $treatyNameSpan.getText().trim();

                targetTreatyNameList.add(targetTreatyName);
            }

            //가입설계 특약명 수집
            welgramTreatyNameList = welgramTreatyList.stream().map(CrawlingTreaty::getTreatyName).collect(Collectors.toList());

            //원수사와 가입설계 특약명을 비교해서 일치, 불일치 특약 추려내기
            List<String> matchedTreatyNameList = getMatchedTreatyNameList(targetTreatyNameList, welgramTreatyNameList);         //원수사에서 가입처리 해야할 특약명 리스트
            List<String> misMatchedTreatyNameList = getMisMatchedTreatyNameList(targetTreatyNameList, welgramTreatyNameList);   //원수사에서 미가입처리 해야할 특약명 리스트

            logger.info("해당 특약들을 가입 처리합니다.");
            for(String treatyName : matchedTreatyNameList) {
                logger.info("특약 : {} 가입 처리중...", treatyName);

                WebElement $treatyNameSpan = $treatyTbody.findElement(By.xpath(".//th[1]/span"));
                WebElement $treatyTr = $treatyNameSpan.findElement(By.xpath("./ancestor::tr[1]"));

                CrawlingTreaty welgramTreaty = welgramTreatyList.stream()
                    .filter(t -> t.getTreatyName().equals(treatyName))
                    .findFirst()
                    .orElseThrow(SetTreatyException::new);

                setTreatyInfoFromTr($treatyTr, String.valueOf(welgramTreaty.getAssureMoney()));
            }

            logger.info("해당 특약들을 미가입 처리합니다.");
            for(String treatyName : misMatchedTreatyNameList) {
                logger.info("특약 : {} 미가입 처리중...", treatyName);

                WebElement $treatyNameSpan = $treatyTbody.findElement(By.xpath(".//th[1]/span"));
                WebElement $treatyTr = $treatyNameSpan.findElement(By.xpath("./ancestor::tr[1]"));

                setTreatyInfoFromTr($treatyTr, null);
            }

            logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
            $treatyTrList = $treatyTbody.findElements(By.xpath(".//td[@class[contains(., 'on')]]/span/ancestor::tr[1]"));
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            for(WebElement $treatyTr : $treatyTrList) {

                //tr로부터 특약정보 읽어오기
                CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                if(targetTreaty != null) {
                    targetTreatyList.add(targetTreaty);
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

    /**
     * 특약을 토글 버튼으로 처리하는 경우 사용한다.
     * @param welgramTreatyList
     * @throws SetTreatyException
     */
    public void setTreatiesToggleType(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {

            String script = "return $(\"span:contains('보장내용 변경'):visible\").parent()[0]";

            logger.info("보장내용 변경 버튼 클릭");
            WebElement $button = (WebElement) helper.executeJavascript(script);
            click($button);

            WebElement $popup = driver.findElement(By.xpath("//div[@class[contains(., 'view') and contains(., 'popup-container')]]"));
            WebElement $treatyUl = $popup.findElement(By.tagName("ul"));
            List<WebElement> $treatyLiList = $treatyUl.findElements(By.xpath("./li[not(@class[contains(., 'line-set')])]"));

            List<String> targetTreatyNameList = new ArrayList<>();
            List<String> welgramTreatyNameList = new ArrayList<>();

            //원수사 특약명 수집
            for(WebElement $treatyLi : $treatyLiList) {
                WebElement $treatyNameP = $treatyLi.findElement(By.tagName("p"));

                //원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
                helper.moveToElementByJavascriptExecutor($treatyNameP);
                String targetTreatyName = $treatyNameP.getText().trim();

                targetTreatyNameList.add(targetTreatyName);
            }

            //가입설계 특약명 수집
            welgramTreatyNameList = welgramTreatyList.stream().map(CrawlingTreaty::getTreatyName).collect(Collectors.toList());

            //원수사와 가입설계 특약명을 비교해서 일치, 불일치 특약 추려내기
            List<String> matchedTreatyNameList = getMatchedTreatyNameList(targetTreatyNameList, welgramTreatyNameList);
            List<String> misMatchedTreatyNameList = getMisMatchedTreatyNameList(targetTreatyNameList, welgramTreatyNameList);


            logger.info("해당 특약들을 모두 가입 상태여야 합니다.");
            for(String treatyName : matchedTreatyNameList) {
                logger.info("특약 : {} 가입인지 확인중...", treatyName);

                WebElement $treatyNameP = $treatyUl.findElement(By.xpath(".//p[normalize-space()='" + treatyName + "']"));
                WebElement $treatyLi = $treatyNameP.findElement(By.xpath("./parent::li"));

                setToggleFromLi($treatyLi, true);
            }


            logger.info("해당 특약들을 모두 미가입 상태여야 합니다.");
            for(String treatyName : misMatchedTreatyNameList) {
                logger.info("특약 : {} 미가입인지 확인중...", treatyName);

                WebElement $treatyNameP = $treatyUl.findElement(By.xpath(".//p[normalize-space()='" + treatyName + "']"));
                WebElement $treatyLi = $treatyNameP.findElement(By.xpath("./parent::li"));

                setToggleFromLi($treatyLi, false);
            }

            logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
            $treatyLiList = $treatyUl.findElements(By.xpath("./li[not(@class[contains(., 'line-set')])]"));
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            for(WebElement $treatyLi : $treatyLiList) {

                //li로부터 특약정보 읽어오기
                CrawlingTreaty targetTreaty = getTreatyInfoFromLi($treatyLi);

                if(targetTreaty != null) {
                    targetTreatyList.add(targetTreaty);
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

            logger.info("확인 버튼 클릭해서 보장내용 변경 창 닫기");
            $button = $popup.findElement(By.xpath(".//span[normalize-space()='확인']/parent::a"));
            click($button);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    /**
     * 원수사 특약명과 가입설계 특약명을 비교해서 일치하는 특약명 리스트를 리턴한다.
     * 기준대상은 원수사 특약명 리스트다.
     * @param targetTreatyNameList
     * @param welgramTreatyNameList
     * @return
     * @throws Exception
     */
    protected List<String> getMatchedTreatyNameList(List<String> targetTreatyNameList, List<String> welgramTreatyNameList) throws Exception {
        List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
        List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기

        System.out.println(System.identityHashCode(targetTreatyNameList));

        //공통된 특약명 찾기
        targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
        matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);

        /**
         * 훼손된 원본리스트를 원복시킬 때
         * targetTreatyNameList = new ArrayList(copiedTargetTreatyNameList);        (X)
         *
         * targetTreatyNameList.clear();
         * targetTreatyNameList.addAll(copiedTargetTreatyNameList);                 (O)
         *
         * 바깥에서 파라미터로 리스트의 참조(주소값)을 전달하고 해당 메서드 내부에서
         * 그 참조를 바꾸면 나는 바깥에 가서도 새로 바뀐 참조를 가지고 있을거라 생각했는데 아님.
         */
        targetTreatyNameList.clear();
        targetTreatyNameList.addAll(copiedTargetTreatyNameList);

        return matchedTreatyNameList;
    }

    /**
     * 원수사 특약명과 가입설계 특약명을 비교해서 불일치하는 특약명 리스트를 리턴한다.
     * 기준대상은 원수사 특약명 리스트다.
     * @param targetTreatyNameList
     * @param welgramTreatyNameList
     * @return
     * @throws Exception
     */
    protected List<String> getMisMatchedTreatyNameList(List<String> targetTreatyNameList, List<String> welgramTreatyNameList) throws Exception {
        List<String> matchedTreatyNameList = getMatchedTreatyNameList(targetTreatyNameList, welgramTreatyNameList);
        List<String> misMatchedTreatyNameList = new ArrayList<>();                              //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
        List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);        //원본 리스트가 훼손되므로 복사본 떠두기

        //불일치 특약명 찾기
        targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
        misMatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);

        /**
         * 훼손된 원본리스트를 원복시킬 때
         * targetTreatyNameList = new ArrayList(copiedTargetTreatyNameList);        (X)
         *
         * targetTreatyNameList.clear();
         * targetTreatyNameList.addAll(copiedTargetTreatyNameList);                 (O)
         *
         * 바깥에서 파라미터로 리스트의 참조(주소값)을 전달하고 해당 메서드 내부에서
         * 그 참조를 바꾸면 나는 바깥에 가서도 새로 바뀐 참조를 가지고 있을거라 생각했는데 아님.
         */
        targetTreatyNameList.clear();
        targetTreatyNameList.addAll(copiedTargetTreatyNameList);

        return misMatchedTreatyNameList;
    }

    /**
     * 특약 tr에 대해 가입금액을 세팅한다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyAssureMoney 세팅할 가입금액(가입인 경우에는 세팅할 가입금액이 넘어오고, 미가입인 경우에는 null이 넘어온다)
     * @throws SetTreatyException
     */
    private void setTreatyInfoFromTr(WebElement $tr, String treatyAssureMoney) throws Exception {
        /**
         * 특약 가입금액 영역
         *
         * 선택된 플랜에서 가입 불가능한 특약의 경우에는 가입금액란이 비어있다.
         * 가입금액이 존재할 때만 가입금액 영역에 span이 존재하고,
         * 가입 불가능한 경우에는 가입금액 영역에 span이 없다.
         * 따라서 span의 존재여부를 판단해서 특약이 가입가능한지 불가능한지를 판단해야 한다.
         */
        WebElement $treatyNameTh = $tr.findElement(By.xpath("./th[1]"));
        WebElement $treatyNameLabel = $treatyNameTh.findElement(By.tagName("label"));

        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[@class[contains(., 'on')]]"));
        WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));
        String targetTreatyAssureMoney = $treatyAssureMoneySpan.getText().trim();

        //특약 가입여부
        boolean toJoin = StringUtils.isNotEmpty(treatyAssureMoney);

        //현재 원수사에서 특약 가입여부 상태
        boolean isJoin = !"미가입".equals(targetTreatyAssureMoney) && !"-".equals(targetTreatyAssureMoney);

        if(toJoin && !isJoin) {
            //가입 해야하는 특약인데 원수사에서 현재 미가입 상태인 경우
            logger.info("특약명 : {} 가입 처리 진행중...");

            click($treatyNameLabel);
            setTreatyAssureMoneyFromModal(treatyAssureMoney);
        }

        if(!toJoin && isJoin) {
            //미가입 해야하는 특약인데 원수사에서 가입 상태인 경우
            logger.info("특약명 : {} 미가입 처리 진행중...");

            click($treatyNameLabel);
            setTreatyAssureMoneyFromModal(treatyAssureMoney);
        }
    }

    /**
     * li element 내부의 토글 상태를 조작한다.
     * @param $li 토글이 존재하는 li element
     * @param toJoin 원하는 특약의 가입상태
     * @throws Exception 현재 특약의 가입상태
     */
    private void setToggleFromLi(WebElement $li, boolean toJoin) throws Exception {
        WebElement $toggleDiv = $li.findElement(By.xpath(".//div[@class[contains(., 'toggle-sw')]]"));
        WebElement $toggleLabel = $toggleDiv.findElement(By.tagName("label"));
//        WebElement $toggleInput = $toggleDiv.findElement(By.tagName("input"));

        String targetTreatyAssureMoney = $toggleLabel.getText();

        //현재 원수사에서 특약 가입여부 상태
        boolean isJoin = !"미가입".equals(targetTreatyAssureMoney)
                && !"-".equals(targetTreatyAssureMoney)
                || "".equals(targetTreatyAssureMoney);
//        boolean isJoin = $toggleInput.isSelected() || !$toggleInput.isEnabled();

        if(toJoin != isJoin) {
            //가입처리해야할 상태와 현재 가입상태의 값이 다를 경우에 토글 클릭
            click($toggleLabel);

            By alertPosition = By.xpath("//div[@class='alert-wrap move']");
            boolean isExist = helper.existElement(alertPosition);
            if(isExist) {
                //특약 처리하면서 알럿창이 발생한 경우
                WebElement $alert = driver.findElement(alertPosition);
                WebElement $button = $alert.findElement(By.xpath(".//span[normalize-space()='확인']/parent::button"));
                click($button);
            }
        }
    }

    /**
     * 특약 li로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 특약가입금액이 있다.
     *
     * 가입(보장금액이 미가입이 아닌경우) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(보장금액이 미가입인 경우) 특약인 경우 null을 리턴한다.
     * @param $li
     * @return
     * @throws Exception
     */
    private CrawlingTreaty getTreatyInfoFromLi(WebElement $li) throws Exception {
        CrawlingTreaty treaty = null;

        //특약명 영역
        WebElement $treatyNameP = $li.findElement(By.tagName("p"));

        //특약 가입금액 영역
        WebElement $toggleDiv = $li.findElement(By.xpath(".//div[@class[contains(., 'toggle-sw')]]"));
        WebElement $treatyAssureMoneyLabel = $toggleDiv.findElement(By.tagName("label"));
        String targetTreatyAssureMoney = $treatyAssureMoneyLabel.getText();

        //현재 원수사에서 특약 가입여부 상태
        boolean isJoin = !"미가입".equals(targetTreatyAssureMoney) && !"-".equals(targetTreatyAssureMoney);

        //특약이 가입인 경우에만 객체 생성
        if(isJoin) {
            String treatyName = $treatyNameP.getText().trim();
            String treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

            treaty = new CrawlingTreaty();
            treaty.setTreatyName(treatyName);
            treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
        }

        return treaty;
    }

    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 특약가입금액이 있다.
     *
     * 가입(보장금액이 미가입이 아닌경우) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(보장금액이 미가입인 경우) 특약인 경우 null을 리턴한다.
     * @param $tr
     * @return
     * @throws Exception
     */
    private CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
        CrawlingTreaty treaty = null;

        //특약명 영역
        WebElement $treatyNameTh = $tr.findElement(By.xpath("./th[1]"));
        WebElement $treatyNameSpan = $treatyNameTh.findElement(By.tagName("span"));

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[@class[contains(., 'on')]]"));
        WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));
        String targetTreatyAssureMoney = $treatyAssureMoneySpan.getText();

        //현재 원수사에서 특약 가입여부 상태
        boolean isJoin = !"미가입".equals(targetTreatyAssureMoney) && !"-".equals(targetTreatyAssureMoney);

        //특약이 가입인 경우에만 객체 생성
        if(isJoin) {
            String treatyName = $treatyNameSpan.getText().trim();
            String treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

            treaty = new CrawlingTreaty();
            treaty.setTreatyName(treatyName);
            treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
        }

        return treaty;
    }

    /**
     * 특약 상세 모달창에서 가입금액을 세팅한다.
     * @param treatyAssureMoney 세팅할 가입금액(미가입인 경우에는 null, 가입인 경우에는 세팅할 가입금액이 넘어온다)
     * @throws Exception
     */
    private void setTreatyAssureMoneyFromModal(String treatyAssureMoney) throws Exception {
        //특약 가입 여부
        boolean toJoin = StringUtils.isNotEmpty(treatyAssureMoney);

        WebElement $treatyAssureMoneyDiv = driver.findElement(By.id("divCoverButtionList"));
        WebElement $treatyAssureMoneyLabel = null;

        //가입금액 선택을 쉽게 하기 위해 불필요한 element 삭제
        String script = "$(arguments[0]).find('span.icon_check').remove();"
            + "$(arguments[0]).find('span.n_count').remove();";
        helper.executeJavascript(script, $treatyAssureMoneyDiv);

        //클릭해야하는 가입금액 label 찾기
        if(toJoin) {
            WebElement $treatyAssureMoneyInput = $treatyAssureMoneyDiv.findElement(By.xpath(".//input[@value='" + treatyAssureMoney + "']"));
            String id = $treatyAssureMoneyInput.getAttribute("id");
            $treatyAssureMoneyLabel = $treatyAssureMoneyDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
        } else {
            WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='back'][normalize-space()='미가입']"));
            $treatyAssureMoneyLabel = $treatyAssureMoneySpan.findElement(By.xpath("./parent::label"));
        }

        //가입금액 클릭
        click($treatyAssureMoneyLabel);

        WebElement $button = $treatyAssureMoneyDiv.findElement(By.xpath(".//button[normalize-space()='확인']"));
        click($button);
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumDiv = driver.findElement(By.id("strSumPrm"));
            String premium = $premiumDiv.getText();
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
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("예상환급률 버튼 클릭");
            WebElement $button = driver.findElement(By.id("popExptEndRtnrt"));
            click($button);

            //해약환급금 탭 관련 element 찾기
            WebElement $tab = driver.findElement(By.xpath("//button[@class='tablinks']/parent::div[@class[contains(., 'tab')]]"));
            List<WebElement> $buttonList = $tab.findElements(By.tagName("button"));

            for(int i = 0; i < $buttonList.size(); i++) {
                $button = $buttonList.get(i);

                //탭 클릭
                String tabTitle = $button.getText();
                logger.info("{} 탭 클릭", tabTitle);
                click($button);


                //클릭한 탭에 따른 활성화된 해약환급금 테이블 정보 읽어오기
                String script = "return $(\"div[id^=Tab][class=tabcontent]:visible\")[0]";
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
                    } else {
                        p.setReturnMoney(returnMoney);
                        p.setReturnRate(returnRate);

                        info.returnPremium = returnMoney;
                    }

                    if(i == 0) {
                        planReturnMoneyList.add(p);
                    }
                }
            }

            logger.info("예상 만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTravelDepartureDate(String expectedDepartureDate) throws SetTravelPeriodException {
        String title = "여행 시작일";
        String expectedDepartureHour = "00";
        String actualDepartureDate = "";
        String actualDepartureHour = "";
        WebElement $button = null;

        //예상 여행시작일 파싱 작업
        String expectedYear = expectedDepartureDate.substring(0, 4);
        String expectedMonth = expectedDepartureDate.substring(4, 6);
        String expectedDate = expectedDepartureDate.substring(6);


        try {
            //여행시작일 선택을 위해 펼치기
            $button = driver.findElement(By.xpath("//div[@id='btn-start']/parent::button"));
            click($button);

            //여행시작일 휠 영역 element
            WebElement $dateWheelArea = driver.findElement(By.xpath("//div[@id='picker']//div[@class[contains(., 'mbsc-datetime-date-wheel')]]"));
            WebElement $hourWheelArea = driver.findElement(By.xpath("//div[@id='picker']//div[@class[contains(., 'mbsc-datetime-hour-wheel')]]"));

            //여행시작날짜 휠 조종
            expectedDepartureDate = expectedYear.substring(2, 4) + "/" + expectedMonth + "/" + expectedDate;
            controlWheel($dateWheelArea, expectedDepartureDate);

            //여행시작시간 휠 조종
            controlWheel($hourWheelArea, expectedDepartureHour);

            //실제 클릭된 여행시작날짜, 여행시작시간 값 읽어오기
            actualDepartureDate = $dateWheelArea.findElement(By.xpath(".//div[@class[contains(., 'mbsc-selected')]]")).getText();
            actualDepartureDate = actualDepartureDate.substring(0, actualDepartureDate.indexOf("("));
            actualDepartureHour = $hourWheelArea.findElement(By.xpath(".//div[@class[contains(., 'mbsc-selected')]]")).getText();

            super.printLogAndCompare("여행시작날짜", expectedDepartureDate, actualDepartureDate);
            super.printLogAndCompare("여행시작시간", expectedDepartureHour, actualDepartureHour);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTravelArrivalDate(String expectedArrivalDate) throws SetTravelPeriodException {
        String title = "여행 종료일";
        String expectedArrivalHour = "23";
        String actualArrivalDate = "";
        String actualArrivalHour = "";
        WebElement $button = null;

        //예상 여행종료일 파싱 작업
        String expectedYear = expectedArrivalDate.substring(0, 4);
        String expectedMonth = expectedArrivalDate.substring(4, 6);
        String expectedDate = expectedArrivalDate.substring(6);


        try {
            //여행종료일 선택을 위해 펼치기
            $button = driver.findElement(By.xpath("//div[@id='btn-end']/parent::button"));
            click($button);

            //여행종료일 휠 영역 element
            WebElement $dateWheelArea = driver.findElement(By.xpath("//div[@id='picker2']//div[@class[contains(., 'mbsc-datetime-date-wheel')]]"));
            WebElement $hourWheelArea = driver.findElement(By.xpath("//div[@id='picker2']//div[@class[contains(., 'mbsc-datetime-hour-wheel')]]"));

            //여행종료날짜 휠 조종
            expectedArrivalDate = expectedYear.substring(2, 4) + "/" + expectedMonth + "/" + expectedDate;
            controlWheel($dateWheelArea, expectedArrivalDate);

            //여행종료시간 휠 조종
            controlWheel($hourWheelArea, expectedArrivalHour);

            //실제 클릭된 여행종료날짜, 여행종료시간 값 읽어오기
            actualArrivalDate = $dateWheelArea.findElement(By.xpath(".//div[@class[contains(., 'mbsc-selected')]]")).getText();
            actualArrivalDate = actualArrivalDate.substring(0, actualArrivalDate.indexOf("("));
            actualArrivalHour = $hourWheelArea.findElement(By.xpath(".//div[@class[contains(., 'mbsc-selected')]]")).getText();

            super.printLogAndCompare("여행종료날짜", expectedArrivalDate, actualArrivalDate);
            super.printLogAndCompare("여행종료시간", expectedArrivalHour, actualArrivalHour);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected void controlWheel(WebElement $wheelArea, String text) throws Exception {
        WebElement $wheel = null;           //휠
        WebElement $wheelCell = null;       //휠 요소 하나
        WebElement $currentCell = null;     //현재 선택된 휠 cell
        WebElement $toClickCell = null;     //클릭해야할 휠 cell
        double cellHeight = 0.0;            //휠 요소의 높이
        double wheelCurrentY = 0.0;         //휠의 현재 y 좌표
        double wheelToY = 0.0;              //휠을 이동시킬 y 좌표
        String script = "";
        long currentCellIdx = 0;             //현재 선택된 cell의 idx값
        long toClickCellIdx = 0;             //클릭해야할 cell의 idx값
        long cellDistance = 0;               //현재 선택된 휠에서부터 클릭해야할 휠 사이의 거리

        //휠, 휠 내부 cell element 찾기
        $wheel = $wheelArea.findElement(By.xpath(".//div[@class=' mbsc-ltr']"));
        $currentCell = $wheelArea.findElement(By.xpath(".//div[@class[contains(., 'mbsc-selected')]]"));

        //현재 선택된 cell의 index값 구하기
        script = "return $(arguments[0]).find('div').index($(arguments[1]));";
        currentCellIdx = (long) helper.executeJavascript(script, $wheel, $currentCell);

        //클릭하고싶은 cell element 찾기
        $toClickCell = $wheel.findElement(By.xpath("./div[contains(., '" + text + "')][last()]"));

        //현재 선택된 cell의 index값 구하기
        script = "return $(arguments[0]).find('div').index($(arguments[1]));";
        toClickCellIdx = (long) helper.executeJavascript(script, $wheel, $toClickCell);

        //cell간의 거리 구하기
        cellDistance = toClickCellIdx - currentCellIdx;

        //cell의 높이 구하기
        script = "return $(arguments[0]).height();";
        cellHeight = (double) helper.executeJavascript(script, $currentCell);

        //휠 조종
        wheelCurrentY = Double.parseDouble($wheel.getCssValue("margin-top").replaceAll("[^-0-9]", ""));
        wheelToY = wheelCurrentY - (cellDistance * cellHeight);
        script = "$(arguments[0]).css('margin-top', '" + wheelToY + "px')";
        helper.executeJavascript(script, $wheel);

        //cell 클릭
        click($toClickCell);
    }

    public void setTravelGoal(String expectedTravelGoal) throws CommonCrawlerException {
        String title = "여행목적";
        String actualTravelGoal = "";

        try {
            WebElement $travelGoalUl = driver.findElement(By.id("purposeTravelList"));
            WebElement $travelGoalInput = $travelGoalUl.findElement(By.xpath(".//input[@data-name='" + expectedTravelGoal + "']"));

            //여행목적 클릭
            clickByJavascriptExecutor($travelGoalInput);

            //실제 선택된 여행목적 값 읽어오기
            String script = "return $('input[name=purposeTravel]:checked')[0]";
            WebElement $selectedInput = (WebElement) helper.executeJavascript(script);
            actualTravelGoal = $selectedInput.getAttribute("data-name");

            //비교
            super.printLogAndCompare(title, expectedTravelGoal, actualTravelGoal);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_GOAL;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
