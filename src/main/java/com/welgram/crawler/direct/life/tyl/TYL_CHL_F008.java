package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.tyl.CrawlingTYL.CrawlingTYLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.08.27 | 최우진 | (무)꿈나무우리아이보험(만기환급형-종합설계형)
public class TYL_CHL_F008 extends CrawlingTYLAnnounce {

    public static void main(String[] args) { executeCommand(new TYL_CHL_F008(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String childBirth = info.getFullBirth();
        String childBirthYear = childBirth.substring(0, 4);
        String childBirthMonth = childBirth.substring(4, 6);
        String childBirthDay = childBirth.substring(6, 8);
        String childName = PersonNameGenerator.generate();
        String motherName = PersonNameGenerator.generate();
        String motherBirth = info.getParent_FullBirth();
        String birthYear = motherBirth.substring(0, 4);
        String birthMonth = motherBirth.substring(4, 6);
        String birthDay = motherBirth.substring(6, 8);
        String unitStandLocation = "//*[@id='div_money_psby1_9WY921AF7']/div/p/span[1]/b";
        String refundOption = "BASE";
        String[] arrTextType = info.getTextType().split("#");

        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initTYL(info);

        logger.info("▉▉▉▉ [01] 고객정보를 입력해 주세요 ▉▉▉▉");
        setUserName(driver.findElement(By.id("name_21")), childName);
        setCHLBirthday(
            driver.findElement(By.id("birthday_Y_21")),   childBirthYear,
            driver.findElement(By.id("birthday_M_21")),   childBirthMonth,
            driver.findElement(By.id("birthday_D_21")),   childBirthDay
        );
        setCHLGender(info.getGender());
        setUserName(driver.findElement(By.id("name_31")), motherName);
        setBirthday(
            driver.findElement(By.id("birthday_Y_31")),     birthYear,
            driver.findElement(By.id("birthday_M_31")),     birthMonth,
            driver.findElement(By.id("birthday_D_31")),     birthDay
        );
        // setGender(info.getGender());      // default :: 종피보험자로 여성이 선택된 상태
        pushButton("주상품 조회", 5);

        logger.info("▉▉▉▉ [02] 주상품을 선택해 주세요 ▉▉▉▉");
        submitMainProduct(driver.findElement(By.id("policycd_l")), arrTextType[1]);
        pushButton("특약 조회", 7);

        logger.info("▉▉▉▉ [03] 특약을 선택해 주세요 ▉▉▉▉");
        submitTreatiesInfo(
            driver.findElements(By.xpath("//*[@id='step3_tbody1']//tr")),
            info.getTreatyList(),
            unitStandLocation
        );
        pushButton("보험료 계산", 7);

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(null, info);
        pushButton("보장내용상세보기", 5);
        snapPicture(info);
        crawlReturnMoneyList(
            driver.findElements(By.xpath("//caption[text()='해약환급금 예시표']/parent::table//tbody//tr")),
            info,
            refundOption
        );

        return true;

    }

}
