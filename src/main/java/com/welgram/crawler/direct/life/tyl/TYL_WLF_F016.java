package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;


// 2023.04.23           | 최우진           | 대면_종신
// TYL_WLF_F016         | 무배당수호천사유니버셜더확실한종신보험(간편가입,기본형)
public class TYL_WLF_F016 extends CrawlingTYL {

    public static void main(String[] args) { executeCommand(new TYL_WLF_F016(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // FIELDS
        CrawlingTreaty mainTreaty = info.treatyList.get(0);
        String tempName = PersonNameGenerator.generate();

        // PROCESS
        // SETUP) CRAWLING OPTIONS
        logger.info("START :: TYL_WLF_F016 :: 무배당수호천사유니버셜더확실한종신보험(간편가입,기본형)");
        logger.info("대면보험의 경우 공시실의 정보를 크롤링합니다. [ TYL_WLF_'F'### ]");

        logger.info("공시실에서 상품찾기");
        findInsuFromAnnounce("무배당수호천사유니버셜더확실한종신보험");

        // 창 전환) STEP1 - 고객정보를 입력해주세요
        logger.info("이름 설정 : {}", tempName);
        setAnnounceName(tempName);
        logger.info("생년월일 설정 : {}", info.getFullBirth());
        setAnnounceBirth(info.getFullBirth());
        logger.info("성별 설정 : {}", info.getGender());
        setAnnounceGender(info.getGender());
        logger.info("주상품 조회 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '주상품 조회')]"));

        // 화면전환) STEP2 - 주상품을 선택해주세요
        logger.info("주상품 설정");
        setPlanType("(무)유니버셜더확실한종신(보장형)-간편심사형-기본형");
        logger.info("특약 조회 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '특약 조회')]"));

        // 현재 종신보험은 특약1개인 상태
        logger.info("주계약 보험기간 설정");
        setAnnounceInsTerm("99년");              // 종신보험의 보험기간은 정해진 형식이 없습니다. TYL에서는 99년을 사용중입니다
        logger.info("주계약 납입기간 설정");
        setAnnounceNapTerm(info.getNapTerm());
        logger.info("주계약 납입주기 설정");
        setAnnounceNapCycle(info.getNapCycleName());
        logger.info("주계약 가입금액 설정");
        setAnnounceAssureMoney(mainTreaty.assureMoney);
        logger.info("보험료 계산 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '보험료 계산')]"));

        // 화면전환) 보험료계산 결과
        logger.info("주계약 보험료 설정");
        setAnnounceMonthlyPremium(mainTreaty);
        logger.info("스크린샷 찍기");
        WebElement element = driver.findElement(By.xpath("//th[text()='합계보험료']/parent::tr//span[@class='point1']"));
        moveToElement(element);
        takeScreenShot(info);
        logger.info("보장내용 상세보기 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '보장내용상세보기')]"));
        logger.info("해약환급금 조회");

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        List<WebElement> trList = driver.findElements(By.xpath("//caption[text()='해약환급금 예시표']/parent::table//tbody//tr"));
        for (WebElement tr : trList) {

            PlanReturnMoney prm = new PlanReturnMoney();

            String term = tr.findElement(By.xpath(".//td[1]")).getText();
            String premiumSum = String.valueOf(Integer.parseInt(tr.findElement(By.xpath(".//td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * 1_0000);
            String returnAmount = String.valueOf(Integer.parseInt(tr.findElement(By.xpath(".//td[7]"))
                    .getText()
                    .replaceAll("[^0-9]", "")) * 1_0000);
            String returnRate = tr.findElement(By.xpath(".//td[8]")).getText();

            logger.info("");
            logger.info("==========  REFUND INFO  =========");
            logger.info("기  간 :: {}", term);
            logger.info("납입보험료 :: {}", premiumSum);
            logger.info("해약환급금 :: {}", returnAmount);
            logger.info("해약환급율 :: {}", returnRate);
            logger.info("==============================");

            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoney(returnAmount);
            prm.setReturnRate(returnRate);

            planReturnMoneyList.add(prm);
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.info("==============================");

        return true;

    }

}
