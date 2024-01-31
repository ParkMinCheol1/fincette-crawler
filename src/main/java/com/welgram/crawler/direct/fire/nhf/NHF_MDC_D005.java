package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

// (무)헤아림다이렉트실손의료비보험2201
public class NHF_MDC_D005 extends CrawlingNHFDirect {



    public static void main(String[] args) {
        executeCommand(new NHF_MDC_D005(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setIniSafe(true);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        boolean _result = false;

        _result = frontPage(info);

        return _result;
    }

    protected boolean frontPage(CrawlingProduct info) throws Exception {

        boolean _result = false;
        String genderOpt = (info.getGender() == 0) ? "sexDcd1" : "sexDcd2";
        String genderText = (info.getGender() == MALE) ? "남" : "여";
        String napCycle = getNapCycleName(info.getNapCycle());
        String vehicleUse = "아니오";

        logger.info("NHF_MDC_D005 :: {}", info.getProductName());
        // 서버에서 모니터링을 돌릴 경우 타임아웃으로 실패가 많아 대기시간을 많이 준다.
        WaitUtil.waitFor(30);
        chkSecurityProgram();

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("iptBirth"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for = '" + genderOpt + "']"), genderText);

        logger.info("직업 선택 :: 중·고등학교 교사");
        setJob();

        logger.info("이륜자동차 운전 여부 :: {}", vehicleUse);
        setVehicle(By.xpath("//label[@for='twvDrvYn0']"), vehicleUse);

        logger.info("의료급여 수급권자 여부 :: [아니오] 선택");
        btnClick(By.xpath("//label[@for='mdctSlryInouRpsYn0']"), 1);

        logger.info("보험료 계산하기 버튼 클릭");
        btnClick(By.id("btnNext"), 5);
        waitHomepageLoadingImg();

        logger.info("납입주기 선택 :: {}", napCycle);
        setNapCycle(By.xpath("//ul[@id='pdtRvcyListArea']//span[text()='" + napCycle + "']/parent::a"), napCycle);

        logger.info("특약 설정");
        setTreaties(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("월 보험료");
        crawlPremium(By.id("sumPremAmt"), info);

        logger.info("해약환급금 조회");
        //최대 60초 기다려보기
        wait = new WebDriverWait(driver, 60);
        waitHomepageLoadingImg();
        crawlReturnMoneyList(By.xpath("//tbody[@id='srdtRfListBody_1']/tr"), info);

        _result = true;
        return _result;
    }

    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();

        try{
            // 하단 고정 nav바 높이 구하기
            int height = getBottomNavHeight(By.cssSelector("#contents > div.btmNav"));

            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String welgramTreatyName = welgramTreaty.getTreatyName();
                String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

                //특약 한줄에서 필요한 요소 찾기
                WebElement $label = driver.findElement(By.xpath("//ul[@id='cvgListArea']//div[@class='inpChkBox']//label[text()='" + welgramTreatyName + "']"));
                WebElement $li = $label.findElement(By.xpath("./ancestor::li[1]"));
                WebElement $input = $li.findElement(By.xpath(".//input[@name='cvgChk']"));
                WebElement $a = $li.findElement(By.xpath(".//a[@class[contains(., 'custom')]]"));
                WebElement $popUp = null;
                boolean isPopUpShow = false;

                //특약이 보이도록 스크롤 이동
                helper.executeJavascript("arguments[0].scrollIntoView(false)", $label);
                height += height;
                helper.executeJavascript("window.scrollBy(0, " + height + ")");

                //특약 체크박스 처리
                if (!$input.isSelected()) {
                    //특약 체크박스 클릭
                    $label.click();
                    WaitUtil.waitFor(2);

                    //특약을 클릭하다가 popup이 뜰 수 있음
                    $popUp = driver.findElement(By.xpath("//*[@id='chkCvgRlpRlePop']/div[1]"));
                    isPopUpShow = $popUp.getAttribute("class").contains("active");

                    //팝업창이 뜬 경우에 확인 버튼 클릭
                    if (isPopUpShow) {
                        checkPopup(By.xpath("//div[@id='okSelect']/a"));
                    }
                }
                //특약 가입금액 펼침 버튼 클릭
                $a.click();
                WaitUtil.waitFor(2);

                //가입금액 선택
                List<WebElement> $liList = $li.findElements(By.xpath(".//ul[@class='stepList between']/li"));
                for (WebElement li : $liList) {
                    String targetAssureMoney = li.findElement(By.xpath("./a/span[1]")).getText();
                    targetAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetAssureMoney));

                    //가입금액 클릭
                    if (welgramTreatyAssureMoney.equals(targetAssureMoney)) {
                        $a = li.findElement(By.xpath("./a"));

                        helper.executeJavascript("arguments[0].scrollIntoView(false)", $a);
                        helper.executeJavascript("window.scrollBy(0, " + height + ")");
                        helper.waitElementToBeClickable($a).click();
                        WaitUtil.waitFor(2);
                    }
                }

                //가입금액을 클릭하다가 popup이 뜰 수 있음
                $popUp = driver.findElement(By.xpath("//*[@id='chkCvgRlpRlePop']//div"));
                isPopUpShow = $popUp.getAttribute("class").contains("active");

                //팝업창이 뜬 경우에 확인 버튼 클릭
                if (isPopUpShow) {
                    checkPopup(By.xpath("//div[@id='okSelect']/a"));
                }

                logger.info("특약명 : {} | 가입금액 : {} 처리 완료", welgramTreatyName, welgramTreatyAssureMoney);
            }

            //원수사에 실제 체크된 특약 정보만 크롤링
            List<WebElement> $inputs = driver.findElements(By.cssSelector("input[name=cvgChk]:checked"));
            targetTreatyList = checkedTreaties($inputs);

            //가입설계 특약정보와 원수사 특약정보 비교
            logger.info("가입하는 특약은 총 {}개입니다.", targetTreatyList.size());
            logger.info("===========================================================");
            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }

    }

}