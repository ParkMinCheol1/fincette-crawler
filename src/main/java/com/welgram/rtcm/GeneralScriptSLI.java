package com.welgram.rtcm;

import com.welgram.rtcm.enums.AnnuityType;
import com.welgram.rtcm.enums.Category;
import com.welgram.rtcm.enums.ErrorCode;
import com.welgram.rtcm.enums.Gender;
import com.welgram.rtcm.enums.InsType;
import com.welgram.rtcm.enums.PayCycle;
import com.welgram.rtcm.enums.PayType;
import com.welgram.rtcm.enums.TreatyType;
import com.welgram.rtcm.strategy.RtcmTreatyEqualStrategy1;
import com.welgram.rtcm.util.Birthday;
import com.welgram.rtcm.util.MoneyUtil;
import com.welgram.rtcm.util.WaitUtil;
import com.welgram.rtcm.vo.RtcmCrawlingInfo;
import com.welgram.rtcm.vo.RtcmPlanVO;
import com.welgram.rtcm.vo.RtcmPremiumVO;
import com.welgram.rtcm.vo.RtcmReturnVO;
import com.welgram.rtcm.vo.RtcmReturnIdx;
import com.welgram.rtcm.vo.RtcmTreatyVO;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 삼성생명 범용 스크립트
 *
 * - 삼성생명 공시실 전상품에 대응하기 위한 크롤링 스크립트 파일
 */
public class GeneralScriptSLI extends GeneralCrawler {

    public static final Logger logger = LoggerFactory.getLogger(GeneralScriptSLI.class);



    public static void main(String[] args) {
        executeCommand(new GeneralScriptSLI(), args);
    }


    @Override
    protected RtcmCrawlingInfo runScript(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {
        Category category = Category.fromAnalysisCode(rtcmCrawlingInfo.getCategory());
        boolean isAnnuityCategory = (category == Category.ANT || category == Category.ASV);
        RtcmPlanVO plan = rtcmCrawlingInfo.getRtcmPlanVO();

        logger.info("공시실 상품명 찾기");
        findProductName(rtcmCrawlingInfo.getProductName());

        logger.info("[group 1] : 가입대상 설정");
        setJoinTargetGroup(rtcmCrawlingInfo);

        logger.info("[group 2] : 고객정보 설정");
        setUserInfoGroup(rtcmCrawlingInfo);

        logger.info("[group 3] : 가입조건 설정");
        setJoinConditionGroup(rtcmCrawlingInfo);

        logger.info("[group 4] : 주계약조건 설정");
        setMainTreatyConditionGroup(rtcmCrawlingInfo);

        logger.info("[group 5] : 특약 설정");
        List<RtcmTreatyVO> subTreatyList = rtcmCrawlingInfo.getRtcmPlanVO().getRtcmTreatyVOList().stream()
            .filter(t -> TreatyType.SUB.getAnalysisCode().equals(t.getTreatyType()))
            .collect(Collectors.toList());

        /**
         * 연금, 연금저축 상품의 경우 보답 상품 상세화면에서 LCC?를 나눠 표기하기 위해
         * 특약을 일부러 2개로 만들어 TND 매핑코드를 따로 매핑 시켜놓았음.
         * 그렇기 때문에 연금성 상품의 경우에는 선택특약이 존재해도 없애고 처리한다.
         *
         */

        if(isAnnuityCategory || category == Category.SAV) {
            subTreatyList = new ArrayList<>();
        }
        setSubTreatyList(subTreatyList);

        logger.info("보험료 크롤링");
        crawlPremiums(rtcmCrawlingInfo);

        if(isAnnuityCategory) {
            logger.info("연금수령액 크롤링");
            crawlAnnuityPremiums(rtcmCrawlingInfo);
        }

        logger.info("해약환급금 크롤링");
        crawlPlanReturnMoneys(rtcmCrawlingInfo);

        return rtcmCrawlingInfo;
    }



    private void crawlAnnuityPremiums(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {
        RtcmPlanVO plan = rtcmCrawlingInfo.getRtcmPlanVO();
        RtcmPremiumVO rtcmPremiumVO = plan.getRtcmPremiumVO();

        try {
            /**
             * 연금수령액의 위치를 함부로 fix할 수 없는 상황(연금 상품마다 제공되는 연금테이블의 정보가 다름)
             * 동적으로 유연하게 처리해야하므로 다음과 같은 로직 추가.
             */
            String script = "var $tr = $(arguments[0]); "
                + "return $tr.nextAll('tr').addBack().slice(0, arguments[1]).get();";
            List<WebElement> $trList;

            AnnuityType annuityType = AnnuityType.fromAnalysisCode(plan.getAnnuityType());
            WebElement $annuityTh = null;
            WebElement $annuityTr = null;
            String rowspan= "";
            String annuityPremium = "";
            By position = By.xpath("./th[contains(., '10년') or contains(., '10회')]");

            String type = (annuityType == AnnuityType.WHOLE) ? "종신" : "확정";
            $annuityTh = driver.findElement(By.xpath("//th[@scope='rowgroup'][contains(., '" + type + "')]"));
            $annuityTr = $annuityTh.findElement(By.xpath("./parent::tr"));
            rowspan = $annuityTh.getAttribute("rowspan");
            $trList = (List<WebElement>) helper.executeJavascript(script, $annuityTr, rowspan);

            for(WebElement $tr : $trList) {
                boolean isExist = helper.existElement($tr, position);

                if(isExist) {
                    WebElement $th = $tr.findElement(position);
                    WebElement $td = $th.findElement(By.xpath("./following-sibling::td[2]"));

                    helper.moveToElementByJavascriptExecutor($td);
                    annuityPremium = $td.getText();
                    annuityPremium = String.valueOf(MoneyUtil.toDigitMoney(annuityPremium));
                }
            }

            logger.info("연금수령액 : {}", annuityPremium);
            rtcmPremiumVO.setAnnuityPremium(new BigInteger(annuityPremium));
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_CRAWL_ANNUITY_PREMIUM;
            throw new Exception(errorCode.getMessage());
        }
    }



    //공시실 상품명 찾기
    private void findProductName(String productName) throws Exception {
        waitLoadingBar();

        boolean isFound = false;

        try {
            WebElement $tabMenuUl = driver.findElement(By.xpath("//ul[@class='tabs-group']"));
            List<WebElement> $tabMenuLiList = $tabMenuUl.findElements(By.tagName("li"));

            //카테고리 탭 순회
            for(WebElement $tabMenuLi : $tabMenuLiList) {
                String categoryText = $tabMenuLi.getText().trim();

                logger.info("공시실 카테고리 탭 : {} 클릭!", categoryText);
                $tabMenuLi.click();

                //카테고리 탭을 클릭할때마다 하단의 보여지는 상품명 목록 ul이 달라진다.
                String script = "return $('ul.disclosure-list:visible')[0]";
                WebElement $productListUl = (WebElement) helper.executeJavascript(script);

                //현재 활성화된 카테고리 탭 내에서 상품명이 존재하는지 확인한다.
                By productPosition = By.xpath(".//p[normalize-space()='" + productName + "']");
                isFound = helper.existElement($productListUl, productPosition);

                if(isFound) {
                    //상품 찾기
                    WebElement $productP = $productListUl.findElement(productPosition);
                    WebElement $productLi = $productP.findElement(By.xpath("./parent::li"));
                    WebElement $calcButton = $productLi.findElement(By.xpath(".//button"));

                    logger.info("상품명 : {}", productName);
                    logger.info("보험료 계산 버튼 클릭");
                    click($calcButton);

                    break;
                }
            }

            //상품명을 찾지못한 경우
            if (!isFound) {
                logger.info("상품명 : {} 을 찾지 못했습니다ㅠㅠ", productName);
                throw new Exception();
            }

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_PRODUCT_NAME;
            throw new Exception(errorCode.getMessage());
        }

    }



    private void setJoinTargetContent(WebElement $section, String expectedJoinTarget) throws Exception {

        String title = "구분";
        By position = By.xpath(".//th[normalize-space()='" + title + "']");

        try {
            //구분에 관련된 element 구하기
            WebElement $th = $section.findElement(position);
            WebElement $td = $th.findElement(By.xpath("./following-sibling::td[1]"));
            WebElement $label = $td.findElement(By.xpath(".//label[normalize-space()='" + expectedJoinTarget + "']"));

            //구분 클릭
            logger.info("[세팅항목 : {}] : {} 클릭", title, $label.getText().trim());
            click($label);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_JOIN_TARGET;
            throw new Exception(errorCode.getMessage());
        }
    }



    //가입대상 그룹 설정
    private void setJoinTargetGroup(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {

        String groupTitle = "가입대상";
        boolean isExistGroup = false;
        boolean isExistContent = false;
        By position = null;

        //가입대상 그룹 영역 element 위치 지정
        position = By.xpath("//h1[@class[contains(., 'head-main-title')]][normalize-space()='" + groupTitle + "']");
        isExistGroup = helper.existElement(position);

        //가입대상 그룹 영역이 존재하는 경우에만 값 세팅
        if(isExistGroup) {
            WebElement $groupSection = driver
                .findElement(position)
                .findElement(By.xpath("./parent::section"));
            WebElement $contentTitleSection = null;
            WebElement $contentValueSection = null;

//            /**
//             * 세팅항목값 "구분"의 경우에는 UI상에서 눈에 보인다고 하더라도 필수로 선택 해야하는 값은 아니다.
//             * 그렇기 때문에 값이 null인지 여부를 판단해서 세팅해야하는 값이 들어있는 경우에만 값을 설정한다.
//             */
//            if(rtcmCrawlingProduct.getJoinTarget() != null) {
//
//                logger.info("세팅항목[구분] UI상에 보여지는지 검사");
//                position = By.xpath(".//th[@scope='row'][normalize-space()='구분']");
//                isExistContent = helper.existElement($groupSection, position);
//
//                if(isExistContent) {
//                    $contentTitleSection = $groupSection.findElement(position);
//                    $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));
//
//                    logger.info("구분 설정하기");
//                    setJoinTargetContent($contentValueSection, rtcmCrawlingProduct.getJoinTarget());
//                }
//            }
        }
    }



    private void setBirthAndGender(WebElement $section, RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {

        By birthPosition = null;
        By genderPosition = null;
        WebElement $birthTitleSection = null;
        WebElement $birthValueSection = null;
        WebElement $genderTitleSection = null;
        WebElement $genderValueSection = null;
        boolean isExist = false;

        logger.info("세팅항목[생년월일] UI상에 보여지는지 검사");
        birthPosition = By.xpath(".//th[@scope='row'][normalize-space()='생년월일']");
        isExist = helper.existElement($section, birthPosition);

        if(isExist) {
            logger.info("생년월일 설정");
            $birthTitleSection = $section.findElement(birthPosition);
            $birthValueSection = $birthTitleSection.findElement(By.xpath("./following-sibling::td[1]"));
            setBirth($birthValueSection, rtcmCrawlingInfo.getBirthday());

            logger.info("성별 설정");
            genderPosition = By.xpath(".//th[@scope='row'][normalize-space()='성별']");
            $genderTitleSection = $section.findElement(genderPosition);
            $genderValueSection = $genderTitleSection.findElement(By.xpath("./following-sibling::td[1]"));
            setGender($genderValueSection, rtcmCrawlingInfo.getGender());
        }

        logger.info("세팅항목[본인(태아)] UI상에 보여지는지 검사");
        birthPosition = By.xpath(".//th[@scope='row'][normalize-space()='본인(태아)']");
        isExist = helper.existElement($section, birthPosition);

        if (isExist) {
            try {
                logger.info("본인(태아) 생년월일 설정");
                $birthTitleSection = $section.findElement(birthPosition);
                $birthValueSection = $birthTitleSection.findElement(By.xpath("./following-sibling::td[1]"));
                setBirth($birthValueSection, rtcmCrawlingInfo.getBabyBirthday());
            } catch (Exception e) {
                ErrorCode errorCode = ErrorCode.ERROR_BY_BABY_BIRTH;
                throw new Exception(errorCode.getMessage());
            }

            try {
                logger.info("본인(태아) 성별 설정(태아 성별 남자로 고정)");
                $genderValueSection = $birthTitleSection.findElement(By.xpath("./following-sibling::td[2]"));
                setGender($genderValueSection, rtcmCrawlingInfo.getBabyGender());
            } catch (Exception e) {
                ErrorCode errorCode = ErrorCode.ERROR_BY_BABY_GENDER;
                throw new Exception(errorCode.getMessage());
            }
        }

        logger.info("세팅항목[종피(임산부)] UI상에 보여지는지 검사");
        birthPosition = By.xpath(".//th[@scope='row'][normalize-space()='종피(임산부)']");
        isExist = helper.existElement($section, birthPosition);

        if (isExist) {
            logger.info("종피(임산부) 생년월일 설정");
            $birthTitleSection = $section.findElement(birthPosition);
            $birthValueSection = $birthTitleSection.findElement(By.xpath("./following-sibling::td[1]"));
            setBirth($birthValueSection, rtcmCrawlingInfo.getBirthday());

            if(rtcmCrawlingInfo.getGender() == Gender.FEMALE) {
                logger.info("종피(임산부) 성별 설정");
                $genderValueSection = $birthTitleSection.findElement(By.xpath("./following-sibling::td[2]"));
                setGender($genderValueSection, rtcmCrawlingInfo.getGender());
            } else {
                logger.error("남자는 가입할 수 없습니다.");
                ErrorCode errorCode = ErrorCode.ERROR_BY_GENDER;
                throw new Exception(errorCode.getMessage());
            }
        }
    }



    private void setJob(WebElement $section, String expectedJob) throws Exception {

        String title = "직업";
        String actualJob = "";

        try {
            logger.info("직업 선택을 위해 돋보기 버튼 클릭");
            WebElement $button = $section.findElement(By.xpath(".//button[@title='검색']"));
            click($button);

            //직업찾기 모달창 section
            $section = driver.findElement(By.xpath("//h1[normalize-space()='직업찾기']/ancestor::section[1]"));
            WebElement $jobInput = $section.findElement(By.id("search_txt"));

            //직업 입력
            helper.setTextToInputBox($jobInput, expectedJob);

            //직업 검색 버튼(돋보기 버튼) 클릭
            $button = $section.findElement(By.xpath(".//button[@title='검색']"));
            click($button);

            //직업 검색 결과 ul
            WebElement $jobUl = $section.findElement(By.xpath(".//ul[@class='result-list']"));
            WebElement $jobA = $jobUl.findElement(By.xpath(".//a[normalize-space()='" + expectedJob + "']"));
            click($jobA);

            //실제 직업이 맞게 입력됐는지 확인
            $jobInput = driver.findElement(By.id("jobNm"));
            actualJob = $jobInput.getAttribute("value");

            super.printLogAndCompare(title, expectedJob, actualJob);
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_JOB;
            throw new Exception(errorCode.getMessage());
        }
    }



    //고객정보 그룹 설정
    private void setUserInfoGroup(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {

        String groupTitle = "고객정보";
        boolean isExistGroup = false;
        boolean isExistContent = false;
        By position = null;

        //고객정보 그룹 영역 element 위치 지정
        position = By.xpath("//h1[@class[contains(., 'head-main-title')]][normalize-space()='" + groupTitle + "']");
        isExistGroup = helper.existElement(position);

        //고객정보 그룹 영역이 존재하는 경우에만 값 세팅
        if (isExistGroup) {
            WebElement $groupSection = driver
                .findElement(position)
                .findElement(By.xpath("./parent::section"));
            WebElement $contentTitleSection = null;
            WebElement $contentValueSection = null;

            logger.info("생년월일 & 성별 설정");
            setBirthAndGender($groupSection, rtcmCrawlingInfo);

            logger.info("세팅항목[직업] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='직업']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("직업 설정");
                setJob($contentValueSection, rtcmCrawlingInfo.getJob());
            }
        }

        /**
         * 가입조건 그룹 영역이 화면에서 보이는지 확인 작업 진행.
         * 고객정보, 가입조건 그룹이 서로 한 step에서 같이 보여지는 경우가 있음.
         * 가입조건 그룹이 보여지면 가입조건 그룹 세팅값을 다 세팅한 후에 다음 버튼을 클릭해야하므로
         * 가입조건 그룹이 화면에 보여지는지 검사할 필요가 있음.
         */
        groupTitle = "가입조건";
        position = By.xpath("//h1[@class[contains(., 'head-main-title')]][normalize-space()='" + groupTitle + "']");
        isExistGroup = helper.existElement(position);

        if (!isExistGroup) {
            WebElement $button = driver.findElement(By.xpath("//button[normalize-space()='다음']"));
            click($button);
        }
    }



    private void setBirth(WebElement $section, Birthday expectedBirth) throws Exception {

        String title = "생년월일";
        String actualBirth = "";

        try {
            //생년월일 관련 element 구하기
            WebElement $yearSelect = $section.findElement(By.cssSelector("select[id^=selYear]"));
            WebElement $monthSelect = $section.findElement(By.cssSelector("select[id^=selMonth]"));
            WebElement $daySelect = $section.findElement(By.cssSelector("select[id^=selDay]"));
            helper.moveToElementByJavascriptExecutor($yearSelect);

            //생년월일 세팅
            String actualYear = helper.selectOptionByText($yearSelect, expectedBirth.getYear());
            waitLoadingBar();

            String actualMonth = helper.selectOptionByText($monthSelect, String.valueOf(Integer.parseInt(expectedBirth.getMonth())));
            waitLoadingBar();

            String actualDay = helper.selectOptionByText($daySelect,String.valueOf(Integer.parseInt(expectedBirth.getDay())));
            waitLoadingBar();

            //생년월일 formatting
            actualBirth = actualYear
                + ((Integer.parseInt(actualMonth) < 10) ? "0" + actualMonth : actualMonth)
                + ((Integer.parseInt(actualDay) < 10) ? "0" + actualDay : actualDay);

            //실제 생년월일 맞게 선택됐는지 검사
            super.printLogAndCompare(title, expectedBirth.getFullBirthday(), actualBirth);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_BIRTH;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setGender(WebElement $section, Gender gender) throws Exception {

        String title = "성별";
        String expectedGender = (gender == Gender.MALE) ? "남자" : "여자";
        String actualGender = "";

        try {
            //성별 관련 element 구하기
            WebElement $genderLabel = $section.findElement(By.xpath(".//label[normalize-space()='" + expectedGender + "']"));
            WebElement $genderInput = $section.findElement(By.xpath(".//input[@type='radio']"));

            //성별란이 활성화된 경우에만 클릭한다.
            if ($genderInput.isEnabled()) {
                //성별 클릭
                click($genderLabel);

                //성별 맞게 클릭됐는지 검사
                String name = $genderInput.getAttribute("name");
                String script = "return $('input[name=" + name + "]:checked').attr('id');";
                String id = String.valueOf(helper.executeJavascript(script));
                $genderLabel = $section.findElement(By.xpath(".//label[@for='" + id + "']"));
                actualGender = $genderLabel.getText().trim();

                super.printLogAndCompare(title, expectedGender, actualGender);

            } else {
                logger.info("성별란이 고정입니다.");
            }

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_GENDER;
            throw new Exception(errorCode.getMessage());
        }
    }



    //가입조건 그룹 설정
    private void setJoinConditionGroup(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {

        String groupTitle = "가입조건";
        boolean isExistGroup = false;
        boolean isExistContent = false;
        By position = null;

        RtcmPlanVO plan = rtcmCrawlingInfo.getRtcmPlanVO();
        RtcmTreatyVO mainTreaty = plan.getMainTreaty();

        //가입조건 그룹 영역 element 위치 지정
        position = By.xpath("//h1[@class[contains(., 'head-main-title')]][normalize-space()='" + groupTitle + "']");
        isExistGroup = helper.existElement(position);

        //가입조건 그룹 영역이 존재하는 경우에만 값 세팅
        if (isExistGroup) {
            WebElement $groupSection = driver
                .findElement(position)
                .findElement(By.xpath("./parent::section"));
            WebElement $contentTitleSection = null;
            WebElement $contentValueSection = null;

            logger.info("세팅항목[보험종류] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='보험종류']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("보험종류 설정");
                setProductKind($contentValueSection, plan.getTextTypeArr());
            }

            logger.info("세팅항목[고액계약] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='고액계약']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("고액계약 설정");
                setLargeAmountContract($contentValueSection, rtcmCrawlingInfo.getLargeAmountContract());
            }

            logger.info("세팅항목[주피건강체] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='주피건강체']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("주피건강체 설정");
                setHealthType($contentValueSection, rtcmCrawlingInfo.getHealthType());
            }

            logger.info("세팅항목[납입주기] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='납입주기']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("납입주기 설정");
                setPayCycle($contentValueSection, PayCycle.fromAnalysisCode(plan.getPayCycle()).getDesc());
            }

            logger.info("세팅항목[의료수급권자] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='의료수급권자']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("의료수급권자 설정");
                setMedicalBeneficiary($contentValueSection, rtcmCrawlingInfo.getMedicalBeneficiary());
            }

            logger.info("세팅항목[임신주수] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='임신주수']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("임신주수 설정");
                setPregnancyWeek($contentValueSection, rtcmCrawlingInfo.getPregnancyWeek());
            }

            logger.info("세팅항목[연금개시연령] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='연금개시연령']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("연금개시연령 설정");
                String annuityAge = String.valueOf(plan.getAnnuityAge());
                setAnnuityAge($contentValueSection, annuityAge);
            }

            logger.info("세팅항목[연금형태] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='연금형태' or normalize-space()='연금유형']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("연금형태 설정");
                String annuityType = "종신형 10회";
//                String annuityType = ";
//                if(mainTreaty.getAnnuityType() == AnnuityType.ANN) {
//                    annuityType = "종신형 10회";
//                } else if(mainTreaty.getAnnuityType() == AnnuityType.FIXED_ANN) {
//                    annuityType = "확정형 10회";
//                }

                setAnnuityType($contentValueSection, annuityType);
            }
        }

        logger.info("다음 버튼 클릭");
        WebElement $button = driver.findElement(By.xpath("//button[normalize-space()='다음']"));
        click($button);
    }



    private void setSubTreatyList(List<RtcmTreatyVO> welgramSubTreatyList) throws Exception {

        try {
            //가입설계에 선택특약이 1개 이상인 경우
            if (welgramSubTreatyList.size() > 0) {
                logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
                for (RtcmTreatyVO welgramSubTreaty : welgramSubTreatyList) {
                    String treatyName = welgramSubTreaty.getTreatyName().trim();

                    WebElement $treatyLabel = driver.findElement(By.xpath("//label[normalize-space()='" + treatyName + "']"));
                    WebElement $treatyTr = $treatyLabel.findElement(By.xpath("./ancestor::tr[1]"));
                    setTreatyInfoFromTr($treatyTr, welgramSubTreaty);
                }

                logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
                List<WebElement> $treatyTrList = driver.findElements(By.xpath("//td[@class='text-l']/parent::tr"));
                List<RtcmTreatyVO> targetTreatyList = new ArrayList<>();
                for (WebElement $treatyTr : $treatyTrList) {
                    RtcmTreatyVO targetTreaty = getTreatyInfoFromTr($treatyTr);

                    if (targetTreaty != null) {
                        targetTreatyList.add(targetTreaty);
                    }
                }

                logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
                boolean result = advancedCompareTreaties(targetTreatyList, welgramSubTreatyList, new RtcmTreatyEqualStrategy1());
                if (result) {
                    logger.info("특약 정보 모두 일치");
                } else {
                    logger.info("특약 정보 불일치");
                    throw new Exception();
                }

            } else {
                logger.info("가입설계에 선택특약이 존재하지 않습니다. 선택특약 세팅을 진행하지 않습니다.");
            }

            logger.info("보험료 계산 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//section[@role='dialog']//button[normalize-space()='보험료 계산']"));
            click($button);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_TREATY;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setTreatyInfoFromTr(WebElement $tr, RtcmTreatyVO subTreaty) throws Exception {

        String treatyName = subTreaty.getTreatyName();
        String insTerm = subTreaty.getInsPeriod() + InsType.fromCode(subTreaty.getInsType()).getDesc();
        String napTerm = subTreaty.getPayPeriod() + PayType.fromCode(subTreaty.getPayType()).getDesc();
        String assureMoney = String.valueOf(subTreaty.getAssureMoney());

        helper.moveToElementByJavascriptExecutor($tr);
        logger.info("특약 : {} 처리중...", treatyName);

        //특약 가입 체크박스 영역
        WebElement $joinTd = $tr.findElement(By.xpath("./td[1]"));
        WebElement $joinInput = $joinTd.findElement(By.tagName("input"));
        WebElement $joinLabel = null;

        //미가입인 경우에만 가입 처리
        if (!$joinInput.isSelected()) {
            //특약 가입 처리
            $joinLabel = $joinTd.findElement(By.tagName("label"));
            $joinLabel.click();
        }

        //특약 보험기간 영역
        WebElement $insTermTd = $tr.findElement(By.xpath("./td[2]"));
        WebElement $insTermSelect = $insTermTd.findElement(By.cssSelector("select[id^=insrPrdTypValCd_]"));
        if ($insTermSelect.isEnabled()) {
            helper.selectOptionByText($insTermSelect, insTerm);
        } else {
            logger.info("보험기간 고정입니다");
        }

        //특약 납입기간 영역
        WebElement $napTermTd = $tr.findElement(By.xpath("./td[3]"));
        WebElement $napTermSelect = $napTermTd.findElement(By.cssSelector("select[id^=padPrd_]"));
        napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";
        if ($napTermSelect.isEnabled()) {
            helper.selectOptionByText($napTermSelect, napTerm);
        } else {
            logger.info("납입기간 고정입니다");
        }

        //특약 가입금액 영역
        WebElement $assureMoneyTd = $tr.findElement(By.xpath("./td[5]"));
        WebElement $assureMoneyInput = $assureMoneyTd.findElement(By.cssSelector("input[id^=price_]"));

        if ($assureMoneyInput.isEnabled()) {
            WebElement $assureMoneyUnitSpan = $assureMoneyTd.findElement(By.xpath(".//span[@class='form-unit']"));
            long unit = getUnitText($assureMoneyUnitSpan.getText());
            assureMoney = String.valueOf(Long.parseLong(assureMoney) / unit);
            helper.setTextToInputBox($assureMoneyInput, assureMoney);
        }
    }



    private RtcmTreatyVO getTreatyInfoFromTr(WebElement $tr) throws Exception {

        RtcmTreatyVO treaty = null;

        helper.moveToElementByJavascriptExecutor($tr);
        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

        //특약 가입 체크박스 영역
        WebElement $joinTd = $tdList.get(0);
        WebElement $joinInput = $joinTd.findElement(By.tagName("input"));

        //특약명 영역
        WebElement $treatyNameLabel = $joinTd.findElement(By.tagName("label"));

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tdList.get(1);
        WebElement $treatyInsTermSelect = $treatyInsTermTd.findElement(By.cssSelector("select[id^=insrPrdTypValCd_]"));

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tdList.get(2);
        WebElement $treatyNapTermSelect = $treatyNapTermTd.findElement(By.cssSelector("select[id^=padPrd_]"));

        //특약 가입금액 영역
        WebElement $treatyAssureMoneyTd = $tdList.get(4);
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.cssSelector("input[id^=price_]"));
        WebElement $treatyAssureMoneyUnitSpan = $treatyAssureMoneyTd.findElement(By.xpath(".//span[@class='form-unit']"));
        long unit = 0;

        //특약이 가입인 경우에만 특약 정보를 객체에 담아준다
        if ($joinInput.isSelected()) {
            String treatyName = "";
            String treatyInsTerm = "";
            String treatyNapTerm = "";
            String treatyAssureMoney = "";

            String script = "return $(arguments[0]).find('option:selected').text();";

            treatyName = $treatyNameLabel.getText().trim();
            treatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
            treatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));
            treatyNapTerm = treatyNapTerm.contains("납") ? treatyNapTerm.replace("납", "") : treatyNapTerm;

            treatyAssureMoney = $treatyAssureMoneyInput.getAttribute("value");
            treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");
            unit = getUnitText($treatyAssureMoneyUnitSpan.getText());
            treatyAssureMoney = String.valueOf(Long.parseLong(treatyAssureMoney) * unit);

            int insPeriod = Integer.parseInt(treatyInsTerm.replaceAll("[^0-9]", ""));
            String insType = treatyInsTerm.replaceAll("[0-9]", "").trim();
            if (insType.contains("종신")) {
                insPeriod = 100;
                insType = InsType.AGE.getDesc();
            }

            int payPeriod = Integer.parseInt(treatyNapTerm.replaceAll("[^0-9]", ""));
            String payType = treatyNapTerm.replaceAll("[0-9]", "").trim();

            treaty = new RtcmTreatyVO();
            treaty.setTreatyName(treatyName);
            treaty.setAssureMoney(new BigInteger(treatyAssureMoney));
            treaty.setInsPeriod(insPeriod);
            treaty.setInsType(InsType.fromDesc(insType).getCode());
            treaty.setPayPeriod(payPeriod);
            treaty.setPayType(PayType.fromDesc(payType).getCode());
        }

        return treaty;
    }



    private void crawlPremiums(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {

        Category category = Category.fromAnalysisCode(rtcmCrawlingInfo.getCategory());
        RtcmPlanVO plan = rtcmCrawlingInfo.getRtcmPlanVO();
        RtcmPremiumVO rtcmPremiumVO = plan.getRtcmPremiumVO();

        try {
            String premium = "";                      //주계약 보험료
            String afterBirthPremium = "";            //출생후 보험료(계속 보험료)

            //합계보험료는 무조건 있는 값
            WebElement $section = driver.findElement(By.xpath("//div[@class='calculate-detail']"));
            WebElement $premiumStrong = $section.findElement(By.xpath(".//strong[normalize-space()='합계보험료']"));
            WebElement $premiumEm = $premiumStrong.findElement(By.xpath("./ancestor::li//em"));

            premium = $premiumEm.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));
            logger.info("보험료 : {}원", premium);

            //출생후 보험료는 태아보험일 경우에만 있는 값
            boolean isExist = false;
            By position = By.xpath(".//strong[normalize-space()='출생예정일 이후 합계보험료']");
            isExist = helper.existElement($section, position);

            if (isExist) {
                $premiumStrong = $section.findElement(position);
                $premiumEm = $premiumStrong.findElement(By.xpath("./ancestor::li//em"));

                afterBirthPremium = $premiumEm.getText();
                afterBirthPremium = String.valueOf(MoneyUtil.toDigitMoney(afterBirthPremium));
                logger.info("출생후 보험료(계속보험료) : {}원", afterBirthPremium);
            }

            if (category == Category.BAB) {
                rtcmPremiumVO.setPreBirthPremium(Integer.parseInt(premium));
                rtcmPremiumVO.setAfterBirthPremium(Integer.parseInt(afterBirthPremium));
            } else {
                rtcmPremiumVO.setPremium(Integer.parseInt(premium));
            }
//            PlanCalc planCalc = mainTreaty.getPlanCalc();
//            planCalc.setInsMoney(premium);
//            planCalc.setNextMoney(nextPremium);
//
//            if(StringUtils.isEmpty(planCalc.getInsMoney())) {
//                logger.error("주계약 보험료는 0원일 수 없습니다.");
//                throw new Exception();
//            }

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_CRAWL_PREMIUM;
            throw new Exception(errorCode.getMessage());
        }
    }



    private RtcmReturnIdx getPlanReturnMoneyIdx() throws Exception {

        RtcmReturnIdx rtcmReturnIdx = new RtcmReturnIdx();

        String script = "return $('div.table-container:visible')[0]";
        WebElement $section = (WebElement) helper.executeJavascript(script);
        WebElement $table = $section.findElement(By.tagName("table"));
        WebElement $thead = $table.findElement(By.tagName("thead"));

        //해약환급금 표의 column size를 알아낸다.
        int colSize = $thead.findElements(By.xpath(".//th[@scope='col']")).size();

        switch(colSize) {
            case 4:
                rtcmReturnIdx.setPremiumSumIdx(1);
                rtcmReturnIdx.setReturnPremiumIdx(2);
                rtcmReturnIdx.setReturnRateIdx(3);
                break;

            case 8:
                rtcmReturnIdx.setPremiumSumIdx(1);
                rtcmReturnIdx.setReturnPremiumMinIdx(2);
                rtcmReturnIdx.setReturnRateMinIdx(3);
                rtcmReturnIdx.setReturnPremiumAvgIdx(4);
                rtcmReturnIdx.setReturnRateAvgIdx(5);
                rtcmReturnIdx.setReturnPremiumIdx(6);
                rtcmReturnIdx.setReturnRateIdx(7);
                break;

            case 11:
                rtcmReturnIdx.setPremiumSumIdx(1);
                rtcmReturnIdx.setReturnPremiumMinIdx(3);
                rtcmReturnIdx.setReturnRateMinIdx(4);
                rtcmReturnIdx.setReturnPremiumAvgIdx(6);
                rtcmReturnIdx.setReturnRateAvgIdx(7);
                rtcmReturnIdx.setReturnPremiumIdx(9);
                rtcmReturnIdx.setReturnRateIdx(10);
                break;

            default:
                logger.error("해약환급금 컬럼 size가 {}개 입니다. 현재 범용스크립트로는 대응할 수 없습니다. 스크립트 수정이 필요합니다.", colSize);
                throw new Exception();
        }

        return rtcmReturnIdx;
    }



    private void crawlPlanReturnMoneys(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {

        RtcmPlanVO plan = rtcmCrawlingInfo.getRtcmPlanVO();
        RtcmPremiumVO rtcmPremiumVO = plan.getRtcmPremiumVO();
        List<RtcmReturnVO> rtcmReturnVOList = plan.getRtcmReturnVOList();

        try {
            logger.info("해약환급금 예시 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금 예시']"));
            helper.moveToElementByJavascriptExecutor($button);
            click($button);

            String script = "return $('div.table-container:visible')[0]";
            WebElement $section = (WebElement) helper.executeJavascript(script);
            WebElement $table = $section.findElement(By.tagName("table"));
            WebElement $tbody = $table.findElement(By.tagName("tbody"));

            //해약환급금 컬럼별 td idx 구해오기
            RtcmReturnIdx rtcmReturnIdx = getPlanReturnMoneyIdx();
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            logger.info("해약환급금 크롤링 시작");
            for (WebElement $tr : $trList) {
                helper.moveToElementByJavascriptExecutor($tr);

                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                String term = $tdList.get(0).getText();
                String premiumSum = $tdList.get(rtcmReturnIdx.getPremiumSumIdx()).getText();
                String returnPremium = $tdList.get(rtcmReturnIdx.getReturnPremiumIdx()).getText();
                String returnRate = $tdList.get(rtcmReturnIdx.getReturnRateIdx()).getText();

                //원수사에서 최저환급금, 평균환급금 정보는 제공할 수도 있고 아닐 수도 있음.
                String returnPremiumMin
                    = (rtcmReturnIdx.getReturnPremiumMinIdx() == null)
                        ? null
                        : $tdList.get(rtcmReturnIdx.getReturnPremiumMinIdx()).getText();
                String returnRateMin
                    = (rtcmReturnIdx.getReturnRateMinIdx() == null)
                        ? null
                        : $tdList.get(rtcmReturnIdx.getReturnRateMinIdx()).getText();
                String returnPremiumAvg
                    = (rtcmReturnIdx.getReturnPremiumAvgIdx() == null)
                        ? null
                        : $tdList.get(rtcmReturnIdx.getReturnPremiumAvgIdx()).getText();
                String returnRateAvg
                    = (rtcmReturnIdx.getReturnRateAvgIdx() == null)
                        ? null
                        : $tdList.get(rtcmReturnIdx.getReturnRateAvgIdx()).getText();

                //해약환급금 정보 적재
                premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                returnPremium = String.valueOf(MoneyUtil.toDigitMoney(returnPremium));
                returnPremiumMin
                    = (rtcmReturnIdx.getReturnPremiumMinIdx() == null)
                        ? null
                        : String.valueOf(MoneyUtil.toDigitMoney(returnPremiumMin));
                returnPremiumAvg
                    = (rtcmReturnIdx.getReturnPremiumAvgIdx() == null)
                        ? null
                        : String.valueOf(MoneyUtil.toDigitMoney(returnPremiumAvg));

                RtcmReturnVO rtcmReturnVO = new RtcmReturnVO();
                rtcmReturnVO.setTerm(term);
                rtcmReturnVO.setPremiumSum(new BigInteger(premiumSum));
                rtcmReturnVO.setReturnPremium(new BigInteger(returnPremium));
                rtcmReturnVO.setReturnRate(returnRate);
                rtcmReturnVO.setReturnPremiumMin(
                    StringUtils.isNotEmpty(returnPremiumMin)
                        ? new BigInteger(returnPremiumMin)
                        : null
                );
                rtcmReturnVO.setReturnRateMin(returnRateMin);
                rtcmReturnVO.setReturnPremiumAvg(
                    StringUtils.isNotEmpty(returnPremiumAvg)
                        ? new BigInteger(returnPremiumAvg)
                        : null
                );
                rtcmReturnVO.setReturnRateAvg(returnRateAvg);

                rtcmReturnVOList.add(rtcmReturnVO);
            }
            logger.info("해약환급금 크롤링 끝");

            BigInteger expirePremium = rtcmReturnVOList.get(rtcmReturnVOList.size() - 1).getReturnPremium();
            rtcmPremiumVO.setReturnPremium(expirePremium);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_CRAWL_RETURN_PREMIUM;
            throw new Exception(errorCode.getMessage());
        }
    }


    private void setProductKind(WebElement $section, String[] textTypeArr) throws Exception {

        String title = "보험종류";
        String expected = "";
        String actual = "";
        String script = "return $(arguments[0]).find('option:selected').text();";

        try {
            WebElement $select = $section.findElement(By.id("hptsLineCd"));
            helper.moveToElementByJavascriptExecutor($select);

            for (String textType : textTypeArr) {
                try {
                    textType = textType.trim();
                    actual = helper.selectOptionByText($select, textType);
                    waitLoadingBar();
                    expected = textType;
                    break;

                } catch (NoSuchElementException e) { }
            }

            $select = $section.findElement(By.id("hptsLineCd"));
            actual = String.valueOf(helper.executeJavascript(script, $select));

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_PRODUCT_KIND;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setHealthType(WebElement $section, String expected) throws Exception {

        String title = "주피건강체";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.id("aisdHlthStatCd"));
            helper.moveToElementByJavascriptExecutor($select);
            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_HEALTH_TYPE;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setPayCycle(WebElement $section, String expected) throws Exception {

        String title = "납입주기";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.id("padCylCd"));
            helper.moveToElementByJavascriptExecutor($select);
            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_PAY_CYCLE;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setMedicalBeneficiary(WebElement $section, String expected) throws Exception {

        String title = "의료수급권자";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.id("mdcrRcbfrYn"));
            helper.moveToElementByJavascriptExecutor($select);
            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_MEDICAL_BENEFICIARY;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setLargeAmountContract(WebElement $section, String expected) throws Exception {

        String title = "고액계약";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.id("lgatContAppnCd"));
            helper.moveToElementByJavascriptExecutor($select);
            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_LARGE_AMOUNT_CONTRACT;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setPregnancyWeek(WebElement $section, String expected) throws Exception {

        String title = "임신주수";
        String actual = "";

        try {
            WebElement $input = $section.findElement(By.id("prgwkFgr"));
            helper.moveToElementByJavascriptExecutor($input);
            actual = helper.setTextToInputBox($input, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_PREGNANCY_WEEK;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setAnnuityAge(WebElement $section, String expected) throws Exception {

        String title = "연금개시연령";
        String actual = "";

        try {
            WebElement $th = $section.findElement(By.xpath("./parent::tr/th[normalize-space()='" + title + "']"));
            WebElement $input = $section.findElement(By.id("anutBgnAge"));
            helper.moveToElementByJavascriptExecutor($input);

            /**
             * 연금개시연령의 경우 잘못된 값이 입력되면 원수사에서 값을 자동세팅하게된다.
             * 따라서 유효성 검사 필요
             */
            expected = expected.replaceAll("[^0-9]", "");
            helper.setTextToInputBox($input, expected);
            click($th);

            $input = $section.findElement(By.id("anutBgnAge"));
            actual = $input.getAttribute("value");

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_ANNUITY_AGE;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setAnnuityType(WebElement $section, String expected) throws Exception {

        String title = "연금유형";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.id("anutPymTypCd"));
            helper.moveToElementByJavascriptExecutor($select);
            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_ANNUITY_TYPE;
            throw new Exception(errorCode.getMessage());
        }
    }


    //주보험조건 그룹 설정
    private void setMainTreatyConditionGroup(RtcmCrawlingInfo rtcmCrawlingInfo) throws Exception {

        String groupTitle = "주보험조건";
        boolean isExistGroup = false;
        boolean isExistContent = false;

        By position = null;

        RtcmPlanVO plan = rtcmCrawlingInfo.getRtcmPlanVO();
        RtcmTreatyVO mainTreaty = plan.getMainTreaty();

        //주보험조건 그룹 영역 element 위치 지정
        position = By.xpath("//h1[@class[contains(., 'head-main-title')]][normalize-space()='" + groupTitle + "']");
        isExistGroup = helper.existElement(position);

        //주보험조건 그룹 영역이 존재하는 경우에만 값 세팅
        if (isExistGroup) {
            WebElement $groupSection = driver
                .findElement(position)
                .findElement(By.xpath("./parent::section"));
            WebElement $contentTitleSection = null;
            WebElement $contentValueSection = null;


            logger.info("세팅항목[보험기간] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='보험기간']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                /**
                 * 연금 상품(연금보험, 연금저축보험)의 경우 보험기간 세팅란에
                 * 연금개시나이를 설정해야 한다.
                 */
                String insTerm = "";
                Category category = Category.fromAnalysisCode(rtcmCrawlingInfo.getCategory());
                boolean isAnnuityProduct = category == Category.ANT || category == Category.ASV;

                insTerm = isAnnuityProduct ?
                    plan.getAnnuityAge() + "세"
                    : mainTreaty.getInsPeriod() + InsType.fromCode(mainTreaty.getInsType()).getDesc();

                //종신보험일 경우에 보험기간을 "종신"으로 세팅한다.
                insTerm = (Category.WLF == category) ? "종신" : insTerm;

                logger.info("보험기간 설정");
                setInsTerm($contentValueSection, insTerm);
            }

            logger.info("세팅항목[납입기간] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='납입기간']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("납입기간 설정");
                PayType payType = PayType.fromCode(mainTreaty.getPayType());
                String napTerm = (payType == PayType.ONCE) ?
                    PayType.ONCE.getDesc()
                    : mainTreaty.getPayPeriod() + payType.getDesc();

                setNapTerm($contentValueSection, napTerm);
            }

            logger.info("세팅항목[보험료(가입금액)] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='보험료' or normalize-space()='가입금액']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("보험료(가입금액) 설정");
                setAssureMoney($contentValueSection, String.valueOf(mainTreaty.getAssureMoney()));
            }

            logger.info("세팅항목[생애설계자금나이] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='생애설계자금나이']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("생애설계자금나이 설정");
                setLifeDesignCostAge($contentValueSection, String.valueOf(rtcmCrawlingInfo.getLifeDesignCostAge()));
            }

            logger.info("세팅항목[생애설계자금지급기간] UI상에 보여지는지 검사");
            position = By.xpath(".//th[@scope='row'][normalize-space()='생애설계자금지급기간']");
            isExistContent = helper.existElement($groupSection, position);

            if (isExistContent) {
                $contentTitleSection = $groupSection.findElement(position);
                $contentValueSection = $contentTitleSection.findElement(By.xpath("./following-sibling::td[1]"));

                logger.info("생애설계자금지급기간 설정");
                setLifeDesignCostPeriod($contentValueSection, rtcmCrawlingInfo.getLifeDesignCostPeriod());
            }
        }
    }



    private void setInsTerm(WebElement $section, String expected) throws Exception {

        String title = "보험기간";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.cssSelector("select[id^=insrPrdTypValCd_]"));
            helper.moveToElementByJavascriptExecutor($select);

            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_INS_TERM;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setNapTerm(WebElement $section, String expected) throws Exception {

        String title = "납입기간";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.cssSelector("select[id^=padPrd_]"));
            helper.moveToElementByJavascriptExecutor($select);

            expected = expected.contains("납") ? expected : expected + "납";
            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_NAP_TERM;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setAssureMoney(WebElement $section, String expected) throws Exception {

        String title = "보험료(가입금액)";
        String actual = "";
        long unit = 1;

        try {
            WebElement $input = $section.findElement(By.cssSelector("input[id^=price_]"));
            helper.moveToElementByJavascriptExecutor($input);

            //보험료(가입금액) 단위에 맞게 세팅값 치환
            WebElement $unitSpan = $section.findElement(By.xpath(".//span[@class='form-unit']"));
            unit = getUnitText($unitSpan.getText());

            //보험료(가입금액) 입력
            expected = String.valueOf(Long.parseLong(expected) / unit);
            helper.setTextToInputBox($input, expected);

            //실제 입력된 보험료(가입금액) 값 읽어오기
            $input = $section.findElement(By.cssSelector("input[id^=price_]"));

            //비교를 위해 원래 값의 형태로 치환
            expected = String.valueOf(Long.parseLong(expected) * unit);
            actual = $input.getAttribute("value").replaceAll("[^0-9]", "");
            actual = String.valueOf(Long.parseLong(actual) * unit);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_ASSURE_MONEY;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setLifeDesignCostAge(WebElement $section, String expected) throws Exception {

        String title = "생애설계자금나이";
        String actual = "";

        try {
            WebElement $input = $section.findElement(By.id("exptRtmAge"));
            helper.moveToElementByJavascriptExecutor($input);
            actual = helper.setTextToInputBox($input, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_LIFE_DESIGN_COST_AGE;
            throw new Exception(errorCode.getMessage());
        }
    }



    private void setLifeDesignCostPeriod(WebElement $section, String expected) throws Exception {

        String title = "생애설계자금지급기간";
        String actual = "";

        try {
            WebElement $select = $section.findElement(By.id("lvngFdPymPrd"));
            helper.moveToElementByJavascriptExecutor($select);
            actual = helper.selectOptionByText($select, expected);

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.ERROR_BY_LIFE_DESIGN_COST_PERIOD;
            throw new Exception(errorCode.getMessage());
        }
    }



    private long getUnitText(String unitStr) {

        long result = 0L;
        unitStr = unitStr.trim();

        switch (unitStr) {
            case "억원" :
                result = 100000000;
                break;
            case "천만원" :
                result = 10000000;
                break;
            case "백만원" :
                result = 1000000;
                break;
            case "십만원" :
                result = 100000;
                break;
            case "만원" :
                result = 10000;
                break;
            case "천원" :
                result = 1000;
                break;
            case "원" :
                result = 1;
                break;
        }

        return result;
    }



    public void waitLoadingBar() {

        try {
            helper.waitForCSSElement("#loadingId");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void click(WebElement $element) throws Exception {

        helper.waitElementToBeClickable($element).click();
        waitLoadingBar();
        WaitUtil.waitFor(1);

        //클릭시 삼성생명 알럿창 발생여부 확인
        alertCheck();
    }



    public void click(By position) throws Exception {
        WebElement $element = driver.findElement(position);
        click($element);
    }



    protected void clickByJavascriptExecutor(WebElement element) {

        String script = "arguments[0].click();";

        try {
            helper.executeJavascript(script, element);
            waitLoadingBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private boolean alertCheck() throws Exception {

        By alertPosition = By.xpath("//div[@class='sl-modal samsunglife-alert']");
        boolean isAlert = helper.existElement(alertPosition);

        if (isAlert) {
            WebElement $alert = driver.findElement(alertPosition);
            WebElement $alertBodyDiv = $alert.findElement(By.xpath(".//div[@class='modal-contents-body']"));
            String alertText = $alertBodyDiv.getText().trim();

            logger.error("[삼성생명 알럿] : {}", alertText);

            ErrorCode errorCode = ErrorCode.ERROR_BY_ALERT;
            throw new Exception(errorCode.getMessage());
        }

        return isAlert;
    }

}
