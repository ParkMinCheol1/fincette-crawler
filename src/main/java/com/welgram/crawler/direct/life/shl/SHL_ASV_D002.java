package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLDirect;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.*;


/**
 * 신한생명 - 신한연금저축보험Premium(무배당)
 *
 * @author HyunLae Kim <hyun@welgram.com>
 *     2022.03 - SHL 크롤링 관리자 변경 [김현래 -> 최우진]
 */


// 2022.07.06       | 최우진           | 다이렉트_연금저축
// SHL_ASV_D002     | 신한연금저축보험Premium(무배당)
public class SHL_ASV_D002 extends CrawlingSHLDirect {

    public static void main(String[] args) { executeCommand(new SHL_ASV_D002(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        boolean isAnnuuity = true;
        String birth = info.getFullBirth(); // 생년월일8자리
        String refundOption = "FULL";

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품 검색 ] ▉▉▉▉");
        initSHL(info);

        logger.info("▉▉▉▉ STEP01 [ 기본정보 입력 :: 성별, 생년월일(8) ] ▉▉▉▉");
        setGender(info.getGender());
        setBirthday(driver.findElement(By.id("birymd")), birth);
        pushButton(driver.findElement(By.id("btnCalInpFe")), 3);

        logger.info("▉▉▉▉ STEP02 [ 주계약 내용 입력01 :: 납입주기, 연금개시, 납입기간, 보증지급기간 ] ▉▉▉▉");
        setNapCycle(driver.findElement(By.id("selPamCyclCd")), info.getNapCycleName());
        setAnnuityAge(driver.findElement(By.id("selectMnprIsteCn")), info.getTreatyList().get(0).annAge);
        setNapTerm(driver.findElement(By.id("selectMnprPmpeTc")), info.getNapTerm());
        setAnnuityType(driver.findElement(By.id("selectAnnGutFurPosYyCnCd")), info.getAnnuityType());

        logger.info("▉▉▉▉ STEP03 [ 주계약 내용 입력02 :: 월 보험료] ▉▉▉▉");
        setAssureMoney(
            driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select")),
            info,
            isAnnuuity
        );
        pushReCalc();

        logger.info("▉▉▉▉ STEP04 [ 결과가져오기 :: 보험료, 스크린샷, 해약정보] ▉▉▉▉");
        WaitUtil.waitFor(3);
        pushButton(driver.findElement(By.xpath("//a[text()='연금예시 보기']")), 3);
        crawlAnnuityPremium(
            driver.findElement(By.xpath("//*[@id='tdAnnWhliAnyAm9']")),    // 종신 연금 엘리먼트 위치
            driver.findElement(By.xpath("//*[@id='tdAnnFxtyAnyAm9']")),    // 확정 연금 엘리먼트 위치
            info
        );
        pushButton(driver.findElement(By.xpath("//button[@id='btnConfirm']")), 3);
        snapScreenShot(info);
        pushButton(driver.findElement(By.xpath("//a[text()='해약환급금 예시']")), 3);
        crawlReturnMoneyList(info, refundOption);

        return true;
    }



    @Override
    protected boolean preValidation(CrawlingProduct info) {
        boolean result = true;

        if (info.napTerm.contains("년")) {

            // 최대 가입 연령 = (연금개시나이 - 납입기간)세
            int maxAge = Integer.parseInt(info.annuityAge) - Integer.parseInt(info.napTerm.replaceAll("년", "").trim());
            logger.info("최대 가입 연령 : " + maxAge);
            logger.info("가입 나이 : " + info.age);
//            if(Integer.parseInt(info.age) == maxAge){
//                info.napTerm = "전기납";
//            }
            if (maxAge < Integer.parseInt(info.age)) {
                logger.info("최대 가입 연령 초과");
                return false;
            }
        }

        return result;
    }
}

//        logger.info("연금개시 선택");
//        Select sbAnnAge = new Select(driver.findElement(By.xpath("//select[@id='selectMnprIsteCn']")));
//        try {
//            sbAnnAge.selectByVisibleText(info.getAnnAge() + "세");
//            WaitUtil.waitFor(2);
//        } catch(Exception e) {
//            throw new CommonCrawlerException("연금개시를 선택할 수 없습니다");
//        }
//
//        String strAnnuityType = info.getAnnuityType().replaceAll("[^0-9]", "") + "년";
//        logger.info("보증지급기간 선택 :: " + strAnnuityType);
//        try {
//            Select sbAnnuityType = new Select(driver.findElement(By.xpath("//select[@id='selectAnnGutFurPosYyCnCd']")));
//            sbAnnuityType.selectByVisibleText(strAnnuityType);
//            WaitUtil.waitFor(2);
//        } catch(Exception e) {
//            throw new CommonCrawlerException("보증지금기간 ["+strAnnuityType+"]을 선택할 수 없습니다 ");
//        }
//
//        logger.info("납입기간 선택");
//        try {
//            Select sbNapTerm = new Select(driver.findElement(By.xpath("//select[@id='selectMnprPmpeTc']")));
//            sbNapTerm.selectByVisibleText(info.getNapTerm());
//            WaitUtil.waitFor(2);
//        } catch(Exception e) {
//            throw new CommonCrawlerException("납입기간 선택이 잘못되었습니다 :: [ "+info.getNapTerm()+" ]");
//        }
//
//        logger.info("월 보험료 선택");
//        try {
//            Select sbAssureMoney = new Select(driver.findElement(By.xpath("//select[@title='월 보험료 만단위']")));
//            info.getTreatyList().get(0).monthlyPremium = info.getAssureMoney();       // 월보험료를 가입금액으로 대체
//            sbAssureMoney.selectByValue(info.getAssureMoney());
//            WaitUtil.waitFor(2);
//        } catch(Exception e) {
//            throw new CommonCrawlerException("월 보험료 설정이 잘못되었습니다");
//        }
//
//        logger.info("'다시 계산하기' 클릭");
//        try {
//            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
//            WaitUtil.waitFor(6);
//        } catch(Exception e) {
//            logger.error("[ 다시 계산하기 ] 버튼이 존재하지 않습니다");
//        }

// GET =======================================================================================

//        try {
//            logger.info("연금예시 보기");
//// todo | 종종 에러나는 지점.. 이유불명 , 대기시간때문이 아닐까 추측...
//            helper.doClick(By.xpath("//a[text()='연금예시 보기']"));
//            WaitUtil.waitFor(6);
//
//            if(info.getAnnuityType().equals("종신 10년")) {
//                String whl10Amt = driver.findElement(By.xpath("//td[@id='tdAnnWhliAnyAm9']"))
//                    .getText()
//                    .replaceAll("[^0-9]", "");
//                whl10Amt = whl10Amt + "0000";
//                logger.info("연금확인 [ 종신연금형 10년보증 ] : " + whl10Amt);
//                info.setAnnuityPremium(whl10Amt);
//                info.planAnnuityMoney.setWhl10Y(whl10Amt);
//                WaitUtil.waitFor(1);
//
//            } else if(info.getAnnuityType().equals("종신 20년")) {
//                String whl20Amt = driver.findElement(By.xpath("//td[@id='tdAnnWhliAnyAm9']"))
//                    .getText()
//                    .replaceAll("[^0-9]", "");
//                whl20Amt = whl20Amt + "0000";
//                logger.info("연금확인 [ 종신연금형 20년보증 ] : " + whl20Amt);
//                info.setAnnuityPremium(whl20Amt);
//                info.planAnnuityMoney.setWhl20Y(whl20Amt);
//                WaitUtil.waitFor(1);
//
//            } else if(info.getAnnuityType().equals("확정 10년")) {
//                String fxd10Amt = driver.findElement(By.xpath("//td[@id='tdAnnFxtyAnyAm9']"))
//                    .getText()
//                    .replaceAll("[^0-9]", "");
//                fxd10Amt = fxd10Amt + "0000";
//                logger.info("연금확인 [ 확정연금형 10년보증 ] : " + fxd10Amt);
//                info.fixedAnnuityPremium = fxd10Amt;
//                info.planAnnuityMoney.setFxd10Y(fxd10Amt);
//                WaitUtil.waitFor(1);
//
//            } else if(info.getAnnuityType().equals("확정 20년")) {
//                String fxd20Amt = driver.findElement(By.xpath("//td[@id='tdAnnFxtyAnyAm9']"))
//                    .getText()
//                    .replaceAll("[^0-9]", "");
//                fxd20Amt = fxd20Amt + "0000";
//                logger.info("연금확인 [ 확정연금형 20년보증 ] : " + fxd20Amt);
//                info.fixedAnnuityPremium = fxd20Amt;
//                info.planAnnuityMoney.setFxd20Y(fxd20Amt);
//                WaitUtil.waitFor(1);
//            }
//
//            logger.info("=============================================");
//            logger.info("ANNUITY TYPE :: {}", info.annuityType);
//            logger.info("=============================================");
//            logger.info("WHL 10 :: " + info.planAnnuityMoney.getWhl10Y());
//            logger.info("=============================================");
//            logger.info("WHL 20 :: " + info.planAnnuityMoney.getWhl20Y());
//            logger.info("=============================================");
//            logger.info("FXD 10 :: " + info.planAnnuityMoney.getFxd10Y());
//            logger.info("=============================================");
//            logger.info("FXD 20 :: " + info.planAnnuityMoney.getFxd20Y());
//            logger.info("=============================================");
//
//            logger.info("연금예시 모달 끄기");
//            helper.doClick(By.xpath("//button[@id='btnConfirm']"));
//            WaitUtil.waitFor(1);
//
//        } catch(Exception e) {
//            throw new CommonCrawlerException("연금 내용을 조회할 수 없습니다");
//        }

//        logger.info("스크린샷");
//        takeScreenShot(info);
//        WaitUtil.waitFor(1);
//
//        logger.info("해약환급금 예시 보기");
//        try {
//            helper.doClick(By.xpath("//a[text()='해약환급금 예시']"));
//            WaitUtil.waitFor(2);
//            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
//            int rowIndex = 1;
//            boolean isValubale = true;
//            while (isValubale) {
//                try {
//                    int colIndex = 2;
//                    String term = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/th")).getText();
//                    String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
//                    String minimumReturnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
//                    String minimumReturnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]")).getText();
//                    String averageReturnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
//                    String averageReturnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]")).getText();
//                    String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
//                    String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList02']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]")).getText();
//
//                    rowIndex++;
//                    info.setReturnPremium(returnMoney);
//
//                    logger.info("================================");
//                    logger.info("경과기간 : {}", term);
//                    logger.info("납입보험료 : {}", premiumSum);
//                    logger.info("(최저)해약환급금 : {}", minimumReturnMoney);
//                    logger.info("(최저)환급률 : {}", minimumReturnRate);
//                    logger.info("(평균)해약환급금 : {}", averageReturnMoney);
//                    logger.info("(평균)환급률 : {}", averageReturnRate);
//                    logger.info("해약환급금 : {}", returnMoney);
//                    logger.info("환급률 : {}", returnRate);
//
//                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//                    planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
//                    planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
//                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
//                    planReturnMoney.setTerm(term);
//                    planReturnMoney.setPremiumSum(premiumSum);
//
//                    planReturnMoney.setReturnMoney(returnMoney);
//                    planReturnMoney.setReturnRate(returnRate);
//                    planReturnMoney.setReturnMoneyAvg(minimumReturnMoney);
//                    planReturnMoney.setReturnRateAvg(minimumReturnRate);
//                    planReturnMoney.setReturnMoneyMin(averageReturnMoney);
//                    planReturnMoney.setReturnRateMin(averageReturnRate);
//
//                    planReturnMoneyList.add(planReturnMoney);
//
//                } catch (NoSuchElementException NSEE) {
//                    isValubale = false;
//                    logger.info("=================================");
//                    logger.error("더 이상 참조할 차트가 존재하지 않습니다");
//                    logger.info("=================================");
//                }
//            }
//            info.setPlanReturnMoneyList(planReturnMoneyList);
//
//            logger.info("만기환급금 : {}", info.getReturnPremium());
//            logger.info("=================================");
//
//        } catch (Exception e) {
//            throw new CommonCrawlerException("해약 환급금을 확인할 수 없습니다.");
//        }
//        logger.info(" SCRAP PROCESS END  ");