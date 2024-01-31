package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.tyl.CrawlingTYL.CrawlingTYLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.List;
import org.openqa.selenium.By;


// 2023.06.21 | 최우진 | 무배당수호천사내가만드는상해보험
// 고개정보 입력시 TYL의 운전자여부설정하는 케이스 (setVehicle()) - 동양생명에 잘 없는 케이스
public class TYL_ACD_F003 extends CrawlingTYLAnnounce {

    public static void main(String[] args) { executeCommand(new TYL_ACD_F003(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempName = PersonNameGenerator.generate();
        String birthYear = info.getFullBirth().substring(0, 4);
        String birthMonth = info.getFullBirth().substring(4, 6);
        String birthDay = info.getFullBirth().substring(6, 8);
        String vehicleOption = "승용차운전";
        String refundOption = "BASE";
        String unitStandLocation = "//*[@id='div_money_psby1_O20121AD0']/div/p/span[1]/b";
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
        setVehicle(
            driver.findElements(By.xpath("//*[@id='layerAgreeBox']/div/div/table/tbody/tr[1]")),
            vehicleOption
        ); // - 운전여부 설정은 동양생명에 잘 없는 내용인데 얘는 있음
        pushButton("주상품 조회", 3);


        logger.info("▉▉▉▉ [02] 주상품을 선택해 주세요 ▉▉▉▉");
        submitMainProduct(driver.findElement(By.id("policycd_l")), arrTextType[0]);
        pushButton("특약 조회", 4);


        logger.info("▉▉▉▉ [03] 특약을 선택해 주세요 ▉▉▉▉");
        submitTreatiesInfo(
            driver.findElements(By.xpath("//*[@id='step3_tbody1']/tr")),
            info.getTreatyList(),
            unitStandLocation // todo 추가 필요
        );
        pushButton("보험료 계산", 5);


        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(null, info);
        pushButton("보장내용상세보기", 5);
        snapPicture(info);
        crawlReturnMoneyList(
            null,
            info,
            refundOption
        );

        return true;

    }

}
