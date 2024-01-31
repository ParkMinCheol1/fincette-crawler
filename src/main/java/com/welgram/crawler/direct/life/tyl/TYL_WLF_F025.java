package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.tyl.CrawlingTYL.CrawlingTYLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.11.07 | 최우진 | (무)수호천사NEW알뜰플러스종신-금리확정형(평준납입형)-(표준형)체증형
public class TYL_WLF_F025 extends CrawlingTYLAnnounce {

    public static void main(String[] args) { executeCommand(new TYL_WLF_F025(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempName = PersonNameGenerator.generate();
        String birthYear = info.getFullBirth().substring(0, 4);
        String birthMonth = info.getFullBirth().substring(4, 6);
        String birthDay = info.getFullBirth().substring(6, 8);
        String unitStandLocation = "//*[@id='div_money_psby1_G6CQ21AA0']/div/p/span[1]/b";

        String refundOption = "WLF";
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
        pushButton("특약 조회", 7);

        logger.info("▉▉▉▉ [03] 특약을 선택해 주세요 ▉▉▉▉");
        submitTreatiesInfo(
            driver.findElements(By.xpath("//*[@id='step3_tbody1']/tr")),
            info.getTreatyList(),
            unitStandLocation // todo 추가 필요
        );
        pushButton("보험료 계산", 5);

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(
            driver.findElement(By.xpath("//*[@id='step5_div']/table/tfoot/tr/td/span")),
            info
        );
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
