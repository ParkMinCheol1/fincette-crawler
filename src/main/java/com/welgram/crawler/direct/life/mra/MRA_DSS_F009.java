package com.welgram.crawler.direct.life.mra;


import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class MRA_DSS_F009 extends CrawlingMRAAnnounce {     // 미래에셋생명 헬스케어 건강보험(간편고지형(255), 갱신형) 무배당

    public static void main(String[] args) {
        executeCommand(new MRA_DSS_F009(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("공시실 상품명 찾기");
        findProductName(info.getProductNamePublic());

        logger.info("고객정보 입력");
        setUserInfo(info);

        logger.info("가입조건 입력");
        setJoinCondition(info);

        logger.info("주계약 정보 설정");
        setMainTreatyInfo(info);

        logger.info("특약 설정");
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.선택특약)
            .collect(Collectors.toList());
        setTreaties(subTreatyList);

        logger.info("보험료 계산하기 버튼 클릭");
        WebElement $button = driver.findElement(By.id("btnCalc"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        return true;
    }
}
