package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.09.21 | 최우진 | 무배당 KB 다이렉트자녀보험(23.09)
// 해약환급금 없는 상품
public class KBF_CHL_D013 extends CrawlingKBFDirect {

    public static void main(String[] args) { executeCommand(new KBF_CHL_D013(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String infant = "미취학아동";
        String child = "학생";
        String mz = "학생";
        
        // PROCESS
        waitLoadingBar();
        WaitUtil.waitFor(2);

        logger.info("알럿 표시 여부");
        popUpAlert();

        driver.findElement(By.xpath("//*[@id='evntLayer']/div/div/div/a")).click();
        WaitUtil.waitFor(2);
        logger.info("팝업 끄기");

        // 어린이 선택 | todo 서브카테고리에서 검증할 필요 있음
        driver.findElement(By.xpath("//span[text()='어린이']")).click();
        logger.info("어린이 선택완료");

        setGender(info.getGender());

        setBirthday(info.getFullBirth());

        // '간편하게 보험료 확인' 클릭
        driver.findElement(By.xpath("//a[text()='간편하게 보험료 확인']")).click();
        WaitUtil.waitFor(3);
        logger.info("간편하게 보험료 확인 클릭");

        // 미취학아동 입력 - 선택 -
        element = driver.findElement(By.id("ids_ser1"));
        element.sendKeys(infant);
        WaitUtil.waitFor(4);
        element = driver.findElement(By.xpath("//strong[text()='" + infant + "']/parent::span"));
        element.click();
        logger.info("미취학아동 입력 완료");

        // 완료 버튼
        driver.findElement(By.xpath("//button[text()='선택완료']")).click();
        WaitUtil.waitFor(4);
        logger.info("완료 버튼 클릭");
        WaitUtil.waitFor(5);

        setInsTerm2(info.getInsTerm());

        setNapTerm2(info.getNapTerm());

        WaitUtil.waitFor(4);
        setTreaties2(info);

        logger.info("보험료 크롤링");
        crawlPremium2(info);
//        By monthlyPremium = By.xpath("//span[@class='pc_tot_txt_sum matrixText']");
//        crawlPremium(info);

        logger.info("만기환급금 확인");
        crawlReturnPremium2(info);
//        By premiumLocate = By.xpath("//div[@class='pc_blk_box mt_1 ng-scope']//ul[@class='pc_ver_both_wrap'][contains(., '예상만기환급금')]//li[@class='left pc_w_45 al_right']");
//        By rateLocate = By.xpath("//div[@class='pc_blk_box mt_1 ng-scope']//ul[@class='pc_ver_both_wrap'][contains(., '예상만기환급률')]//li[@class='left pc_w_50 al_right']");
//        crawlReturnPremium(info, premiumLocate, rateLocate, null);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}
