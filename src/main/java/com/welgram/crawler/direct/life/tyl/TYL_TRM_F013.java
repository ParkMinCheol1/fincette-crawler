package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.tyl.CrawlingTYL.CrawlingTYLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.List;
import org.openqa.selenium.By;


// 2023.03.17 | 최우진 | (무)수호천사경영인정기(보증비용부과형)-(최저해약환급금 보증형-10%체증형)
public class TYL_TRM_F013 extends CrawlingTYLAnnounce {

    public static void main(String[] args) {
        executeCommand(new TYL_TRM_F013(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempName = PersonNameGenerator.generate();
        String birthYear = info.getFullBirth().substring(0, 4);
        String birthMonth = info.getFullBirth().substring(4, 6);
        String birthDay = info.getFullBirth().substring(6, 8);
        List<CrawlingTreaty> treatyList = info.getTreatyList();
        String unitStandLocation = "//*[@id='div_money_psby1_M93821BA2']/div/p/span[2]/b";
        String refundOption = "FULL";
        String[] arrTextType = info.getTextType().split("#");

        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initTYL(info);

        logger.info("▉▉▉▉ [01] 고객정보를 입력해 주세요 ▉▉▉▉");
        setUserName(driver.findElement(By.id("name_21")), tempName);
        setBirthday(
            driver.findElement(By.id("birthday_Y_21")),     birthYear,
            driver.findElement(By.id("birthday_M_21")),     birthMonth,
            driver.findElement(By.id("birthday_D_21")),     birthDay
        );
        setGender(info.getGender());
        pushButton("주상품 조회", 3);

        logger.info("▉▉▉▉ [02] 주상품을 선택해 주세요 ▉▉▉▉");
        submitMainProduct(driver.findElement(By.id("policycd_l")), arrTextType[1]);
        pushButton("특약 조회", 5);

        logger.info("▉▉▉▉ [03] 특약을 선택해 주세요 ▉▉▉▉");
        submitTreatiesInfo(
            driver.findElements(By.xpath("//*[@id='step3_tbody1']//tr")),
            treatyList,
            unitStandLocation // todo 추가 필요
        );
        pushButton("보험료 계산", 5);

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(null, info);
        pushButton("보장내용상세보기", 3);
        snapPicture(info);
        crawlReturnMoneyList(
            driver.findElements(By.xpath("/html/body/div/div/div/div[11]/table/tbody/tr")),
            info,
            refundOption
        );

        return true;

    }

}


//        logger.info("START");       // 대표가설 20% 체증형
//
//            logger.info("공시실에서 상품찾기");
//            findInsuFromAnnounce("무배당수호천사경영인정기보험(보증비용부과형)");       // "무배당수호천사VIP플러스정기보험"
//
//            logger.info("이름 설정");
//            setAnnounceName(PersonNameGenerator.generate());
//
//            logger.info("생년월일 설정 : {}", info.getFullBirth());
//            setAnnounceBirth(info.getFullBirth());
//
//            logger.info("성별 설정");
//            setAnnounceGender(info.getGender());
//
//            logger.info("주상품 조회 버튼 클릭!");
//            announceBtnClick(By.xpath("//span[contains(., '주상품 조회')]"));
//
//            logger.info("주상품 설정");
//            setPlanType("(무)수호천사경영인정기(보증비용부과형)-(최저해약환급금 보증형-20%체증형)");      // todo | 대표가설20%체증형 고정
//
//            logger.info("특약 조회 버튼 클릭!");
//            announceBtnClick(By.xpath("//span[contains(., '특약 조회')]"));
//
//            logger.info("주계약 보험기간 설정");
//            setAnnounceInsTerm(info.getInsTerm());
//
//            logger.info("주계약 납입기간 설정");
//            setAnnounceNapTerm(info.getNapTerm());
//
//            logger.info("주계약 납입주기 설정");
//            setAnnounceNapCycle(info.getNapCycleName());
//
//            CrawlingTreaty mainTreaty = info.getTreatyList().get(0);
//            logger.info("주계약 가입금액 설정 :: {}", mainTreaty.getAssureMoney());
//            setAnnounceAssureMoney(mainTreaty.getAssureMoney());
//
//            logger.info("보험료 계산 버튼 클릭!");
//            announceBtnClick(By.xpath("//span[contains(., '보험료 계산')]"));
//
//            logger.info("주계약 보험료 설정");
//            setAnnounceMonthlyPremium(mainTreaty);
//
//            logger.info("스크린샷 찍기");
//            WebElement element = driver.findElement(By.xpath("//th[text()='합계보험료']/parent::tr//span[@class='point1']"));
//            moveToElement(element);
//            takeScreenShot(info);
//
//            logger.info("보장내용 상세보기 버튼 클릭!");
//            announceBtnClick(By.xpath("//span[contains(., '보장내용상세보기')]"));
//
//            logger.info("해약환급금 조회");
//            int unit = 1;
//            String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();
//
//            if (unitText.contains("만원")) {
//            unit = 10000;
//            }
//
//            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
//    List<WebElement> trList = driver.findElements(By.cssSelector(".tblCol.tableCyber tbody tr"));
//
//    for (WebElement tr : trList) {
//    String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
//    String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "").trim();
//
//    String returnMoneyMin = tr.findElements(By.tagName("td")).get(5).getText().replaceAll("[^0-9]", "").trim();
//    String returnRateMin = tr.findElements(By.tagName("td")).get(6).getText().trim();
//
//    String returnMoneyAvg = tr.findElements(By.tagName("td")).get(9).getText().replaceAll("[^0-9]", "").trim();
//    String returnRateAvg = tr.findElements(By.tagName("td")).get(10).getText().trim();
//
//    String returnMoney = tr.findElements(By.tagName("td")).get(13).getText().replaceAll("[^0-9]", "").trim();
//    String returnRate = tr.findElements(By.tagName("td")).get(14).getText().trim();
//
//    //공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
//    premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
//    returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);
//    returnMoneyMin = String.valueOf(Long.parseLong(returnMoneyMin) * unit);
//    returnMoneyAvg = String.valueOf(Long.parseLong(returnMoneyAvg) * unit);
//
//    logger.info("______해약환급급__________ ");
//    logger.info("|--경과기간: {}", term);
//    logger.info("|--납입보험료: {}", premiumSum);
//    logger.info("|--해약환급금: {}", returnMoney);
//    logger.info("|--최저납입보험료: {}", premiumSum);
//    logger.info("|--최저해약환급금: {}", returnMoneyMin);
//    logger.info("|--최저해약환급률: {}", returnRateMin);
//    logger.info("|--평균해약환급금: {}", returnMoneyAvg);
//    logger.info("|--평균해약환급률: {}", returnRateAvg);
//    logger.info("|--환급률: {}", returnRate);
//    logger.info("|_______________________");
//
//    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//
//    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
//    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
//    planReturnMoney.setInsAge(Integer.parseInt(info.age));
//    planReturnMoney.setTerm(term);
//    planReturnMoney.setPremiumSum(premiumSum);
//    planReturnMoney.setReturnMoneyMin(returnMoneyMin);
//    planReturnMoney.setReturnRateMin(returnRateMin);
//    planReturnMoney.setReturnMoney(returnMoney);
//    planReturnMoney.setReturnRate(returnRate);
//    planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
//    planReturnMoney.setReturnRateAvg(returnRateAvg);
//
//    planReturnMoneyList.add(planReturnMoney);
//
//    info.returnPremium = returnMoney;					// 만기환급금
//    }
//    logger.info("더이상 참조할 차트가 존재하지 않습니다");
//    logger.info("_______________________");
//
//    if(info.getTreatyList().get(0).productKind == ProductKind.순수보장형) {
//    info.returnPremium = "0";
//    logger.info("순수보장형 상품의 경우, 만기환급금이 존재하지 않습니다. 만기환급금을 0원으로 저장합니다.");
//    }
//
//    info.setPlanReturnMoneyList(planReturnMoneyList);		// 해약환급금