package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;


// 2023.03.23               | 최우진                   | 대면_종신
// SHL_WLF_F039             | 신한든든한실속종신보험(무배당, 보증비용부과형)[해약환급금 일부지급형]
public class SHL_WLF_F039 extends CrawlingSHL {

    public static void main(String[] args) {
        executeCommand(new SHL_WLF_F039(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: 신한든든한실속종신보험(무배당, 보증비용부과형)[해약환급금 일부지급형]");
        logger.info("대면보험의 경우 공시실의 정보를 크롤링합니다. [ SHL_WLF_'F'### ]");

        logger.info("검색창에서 상품명 : [ {} ]을 조회합니다", "신한든든한실속종신보험(무배당, 보증비용부과형)[해약환급금 일부지급형]");
        driver.findElement(By.id("meta04")).sendKeys("신한든든한실속종신보험(무배당, 보증비용부과형)");
        WaitUtil.waitFor(1);

        logger.info("검색 버튼 클릭");
        driver.findElement(By.id("btnSearch")).click();
        WaitUtil.waitFor(3);

        logger.info("보험료계산 버튼 클릭");
        driver.findElement(By.id("calc_0")).click();
        WaitUtil.waitFor(3);

        logger.info("▉▉▉ STEP01 ▉▉▉ [ 고객정보(피보험자) 내용 입력 ]  ");
        logger.info("생년월일");
        driver.findElement(By.xpath("//input[@type='text'][@title='생년월일']")).sendKeys(info.getFullBirth());

        logger.info("성별");
        String genderOpt = (info.getGender() == MALE) ? "filt1_1" : "filt1_2";
        helper.click(By.xpath("//input[@id='" + genderOpt + "']//parent::li"));

        logger.info("운전");
        Select select = new Select(driver.findElement(By.id("vhclKdCd")));
        select.selectByVisibleText("승용차(자가용)");

        logger.info("직업 :: 사무직 - 경영지원 사무직 관리자");
        String jobOpt = "경영지원 사무직 관리자";
        helper.click(By.xpath("//span[text()='검색']//parent::button[@class='btn_t m btnJobPop']"));
        helper.sendKeys3_check(By.id("jobNmPop"), jobOpt);
        helper.click(By.id("btnJobSearch"));
        helper.click(By.xpath("//span[@class='infoCell'][text()='" + jobOpt + "']"));

        logger.info("확인 버튼 클릭");
        helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnCstCfn']"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
        WaitUtil.waitFor(5);

        logger.info("▉▉▉ STEP02 ▉▉▉ [ 주계약계산 내용 입력 ]  ");
        logger.info("  [ 주계약계산 내용 입력 ]  ");

        logger.info("▉▉ 보험형태 선택 (1/7) ");
        Select selInsForm = new Select(driver.findElement(By.xpath("//select[@title='보험형태']")));
        selInsForm.selectByVisibleText("일반");                                                                           // todo | 하드코딩????
        WaitUtil.waitFor(2);

        logger.info("▉▉ 보험종류 선택 (2/7) ");
        Select selInsKind = new Select(driver.findElement(By.xpath("//select[@title='보험종류']")));
        selInsKind.selectByVisibleText("일반심사형");
        WaitUtil.waitFor(2);

        logger.info("▉▉ 직종구분 선택 (3/7) ");
        Select selJobDiv = new Select(driver.findElement(By.xpath("//select[@title='직종구분']")));
        selJobDiv.selectByVisibleText("일반");
        WaitUtil.waitFor(2);

        logger.info("▉▉ 납입주기 선택 (4/7) ");
        Select selNapCylce = new Select(driver.findElement(By.xpath("//select[@title='납입주기']")));
        selNapCylce.selectByVisibleText("월납");
        WaitUtil.waitFor(2);

        logger.info("▉▉ 보험기간 선택 (5/7) ");
        Select selInsTerm = new Select(driver.findElement(By.xpath("//select[@title='보험기간']")));
        selInsTerm.selectByVisibleText("종신");
        WaitUtil.waitFor(2);

        logger.info("▉▉ 납입기간 선택 (6/7) ");
        logger.info("납입기간 :: {}", info.getNapTerm());
        Select selNapTerm = new Select(driver.findElement(By.xpath("//select[@title='납입기간']")));
        selNapTerm.selectByVisibleText(info.getNapTerm() + "납");
        WaitUtil.waitFor(2);

        logger.info("▉▉ 가입금액 선택 (7/7) ");
        logger.info("가입금액 :: {}", info.getAssureMoney());
        WebElement inputAssAmt = driver.findElement(By.xpath("//input[@title='가입금액']"));
        inputAssAmt.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        inputAssAmt.sendKeys(String.valueOf(Integer.parseInt(info.getAssureMoney()) / 1_0000));
        WaitUtil.waitFor(2);

        logger.info("확인 버튼 클릭");
        driver.findElement(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnMnpr']")).click();
        WaitUtil.waitFor(1);

        logger.info("▉▉▉ STEP03 ▉▉▉ [ 특약계산 내용 입력 ]");
        logger.info("특약계산 :: 특이사항 없음");

        logger.info("확인 버튼 클릭");
        helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnTrty']"));
        WaitUtil.waitFor(1);

        logger.info("보험료계산 버튼 클릭");
        driver.findElement(By.xpath("//span[text()='보험료계산']//parent::button[@class='btn_p btnInpFeCal']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));

        logger.info("보험료 확인");
        try {
            String monthlyPremium = driver.findElement(By.xpath("//em[@class='rlpaAm']")).getText().replaceAll("[^0-9]", "");
            logger.info("월 보험료 : " + monthlyPremium);
            info.treatyList.get(0).monthlyPremium = monthlyPremium;
            WaitUtil.waitFor(1);
        } catch(Exception e) {
            throw new CommonCrawlerException("보험료 확인중 에러가 발생하였습니다");
        }

        logger.info("스크린샷 찍기");
        try {
            takeScreenShot(info);
            logger.info("찰칵");
        } catch(Exception e) {
            logger.error("스크린 샷을 찍는데 실패하였습니다.");
        }

        logger.info("해약환급금예시 확인");
        helper.click(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
        WaitUtil.waitFor(8);
        driver.findElement(By.xpath("//label[contains(., '(공시이율)가정 시')]")).click();
        WaitUtil.waitFor(8);
        // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
        // 3개월 		- 30세		- 279,000				- 0					- 0.0
        // 6개월 		- 30세 		- 558,000				- 0 				- 0.0
        // 9개월 		- 30세 		- 837,000				- 0 				- 0.0
        // 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
        // 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7
        try {
            List<PlanReturnMoney> pRMList = new ArrayList<>();
            List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
            for(WebElement trMin : trReturnMinInfoList) {
                String term = trMin.findElement(By.xpath("./td[1]")).getText();
                String premiumSum = trMin.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = trMin.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
                String returnRate = trMin.findElement(By.xpath("./td[5]")).getText();

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                pRMList.add(planReturnMoney);

                logger.info("===================================");
                logger.info("기간         : " + term);
                logger.info("납입보험료누계 : " + premiumSum);
                logger.info("해약환급금    : " + returnMoney);
                logger.info("환급률       : " + returnRate);
            }
            info.setPlanReturnMoneyList(pRMList);
            logger.info("===================================");
            logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
//            logger.info("종신보험의 경우 만기환급금이 존재하지 않습니다 (사망보험금 or 해약환급금)");
            logger.info("===================================");

        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급금이 크롤링중 에러가 발생하였습니다.\n" + e.getMessage());
        }

        crawlReturnPremium(info);

        return true;
    }


    private void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            Optional<PlanReturnMoney> returnMoneyOptional = getPlanReturnMoney(info);

            if (returnMoneyOptional.isPresent()) {
                info.returnPremium = returnMoneyOptional.get().getReturnMoney();
            } else {
                info.returnPremium = "-1"; // 만기에 해당하는 중도해약환급금이 없을 경우
            }

//      logger.info("만기환급금 크롤링 :: 만기환급금 :: {}", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }

    /**
     * 기수집한 중도해약환급금 목록에서
     * 경과기간이 만기에 해당하는 환급금 데이터를 찾아 반환
     * @param info
     * @return Optional<PlanReturnMoney>
     */
    static Optional<PlanReturnMoney> getPlanReturnMoney(CrawlingProduct info) {

        Optional<PlanReturnMoney> returnMoneyOptional = Optional.empty();
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        int planCalcAge = info.getCategoryName().equals("태아보험") ? 0
            : Integer.parseInt(info.age.replaceAll("\\D", ""));

        // 만기에 해당하는 환급금이 있는지 확인
        for (int i = planReturnMoneyList.size() - 1; i >= 0; i--) {

            PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);
            String term = planReturnMoney.getTerm();

            // 경과기간이 개월단위인 경우는 일단 제외
            if (term.contains("개월")) continue;

            if (term.equals("만기")) {
                returnMoneyOptional = Optional.of(planReturnMoney);
                break;
            }

            // 해약환급금 행에서 경과기간 추출 (년단위로 변환)
            int annualTerm = getAnnualTerm(term, planCalcAge);

            // 해당 가설(info)의 보험기간 추출 (년단위로 변환)
            int annualInsTerm = getAnnaulInsTerm(info, planCalcAge);

            // 경과기간이 만기에 해당하는지 여부 반환
            if (annualTerm == annualInsTerm) {

//        logger.info("만기환급금 크롤링 :: 카테고리 :: {}", info.categoryName);
//        logger.info("만기환급금 크롤링 :: 가설 케이스 나이 :: {}세", planCalcAge);
//        logger.info("만기환급금 크롤링 :: 가설 보험기간 :: {}", info.insTerm);
//        logger.info("만기환급금 크롤링 :: 가설 납입기간 :: {}", info.napTerm);
//        logger.info("만기환급금 크롤링 :: 해약환급금 해당 경과기간 :: {}", term);

                returnMoneyOptional = Optional.of(planReturnMoney);
            }
        }

        return returnMoneyOptional;
    }

    /**
     * 중도해약환급금 데이터에서 경과기간을 (planReturnMoney.term)
     * 년단위로 변환해서 반환
     */
    static int getAnnualTerm(String term, int planCalcAge) {
        int annualTerm = -1;

        // 경과기관이 예를 들어 "70년 (100세)"와 같이 표기되는 경우 대응 가능 -> termUnit = "년"
        String termUnit;
        if (term.contains("년") && term.contains("세")) {
            termUnit = term.indexOf("년") < term.indexOf("세") ? "년" : "세";
        } else if (term.contains("년")) {
            termUnit = "년";
        } else if (term.contains("세")) {
            termUnit = "세";
        } else {
            throw new RuntimeException("처리할 수 없는 경과기간 단위: " + term );
        }

        int termUnitIndex = term.indexOf(termUnit);
        int termNumberValue = Integer.parseInt(
            term.substring(0, termUnitIndex).replaceAll("\\D", ""));

        switch (termUnit) {
            case "년":
                annualTerm = termNumberValue;
                break;
            case "세":
                annualTerm = termNumberValue - planCalcAge;
                break;
        }

        return annualTerm;
    }

    /**
     * 가설의 보험기간을 (info.insTerm)
     * 년단위로 변환해서 반환
     */
    static int getAnnaulInsTerm(CrawlingProduct info, int planCalcAge) {

        int annaulInsTerm;
        String insTermUnit;
        int insTermNumberValue = -1;

        if (info.categoryName.contains("종신")) {
            String napTermUnit = info.napTerm.replaceAll("[0-9]", "");
            int napTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));
            switch (napTermUnit) {
                case "년":
                    insTermNumberValue = napTerm + 10;
                    break;
                case "세":
                    insTermNumberValue = planCalcAge + napTerm;
            }
            insTermUnit = "년";

        } else if (info.categoryName.contains("연금")) { // 연금보험, 연금저축보험
            insTermUnit = "세"; // 환급금 크롤링 시점은 개시나이
            insTermNumberValue = Integer.parseInt(info.annuityAge.replaceAll("[^0-9]", ""));

        } else {
            insTermUnit = info.insTerm.replaceAll("[0-9]", "");
            insTermNumberValue = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));

        }

        switch (insTermUnit) {
            case "년":
                annaulInsTerm = insTermNumberValue;
                break;
            case "세":
                annaulInsTerm = insTermNumberValue - planCalcAge;
                break;
            default:
                annaulInsTerm = -1;
        }
        return annaulInsTerm;
    }

}
