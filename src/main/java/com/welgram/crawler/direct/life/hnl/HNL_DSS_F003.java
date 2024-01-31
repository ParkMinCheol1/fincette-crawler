package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HNL_DSS_F003 extends CrawlingHNLAnnounce {

    //(무)하나로 연결된 든든한 건강보험 - 일반심사형, 100세 20년, 3대
    public static void main(String[] args) {
        executeCommand(new HNL_DSS_F003(), args);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        boolean result = false;
        WebElement $button = null;

        //step1-1 : 상품명 검색
        WebElement $productName = driver.findElement(By.xpath("//td[contains(.,'" + info.getProductNamePublic() + "')]"));
        WebElement $productNameBtn = $productName.findElement(By.xpath("./following-sibling::td/span"));
        $productNameBtn.click();

        //step1-2 : 상품 검색(상품의 종/형 세팅)
        setProductType(info.getTextType());
        /**
         * 간혹 고객정보를 먼저 세팅하고 주계약 정보를 세팅하게 되면
         * 실제로 가입가능함에도 가입연령 제한 알럿이 발생하는 경우가 있다.
         * 주계약 정보를 먼저 세팅한 후에 고객정보를 세팅해야 한다.
         */

        //step2 : 주계약 정보 세팅
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.주계약)
            .findFirst()
            .get();
        setMainTreatyInfo(mainTreaty);

        //step3 : 고객정보 세팅
        setUserInfo(info);

        //고객정보를 세팅하면서 또 주계약 정보가 초기화되는 경우가 있음. 다시 한번 세팅해줌
        setMainTreatyInfo(mainTreaty);

        //step4 : 특약 세팅
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.선택특약)
            .collect(Collectors.toList());
        setSubTreatyInfo(subTreatyList);

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.id("calPrmBtn"));
        click($button);

        //최저보험료를 충족해야만
        if(!checkAlert()) {
            logger.info("보험료 크롤링");
            crawlPremium(info);

            logger.info("해약환급금 크롤링");
            ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
            returnMoneyIdx.setPremiumSumIdx(2);
            returnMoneyIdx.setReturnMoneyIdx(3);
            returnMoneyIdx.setReturnRateIdx(4);
            crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.원);

            result = true;
        }

        return result;
    }


    public void setProductType(String expectedTextType) throws CommonCrawlerException {
        String title = "심사유형";

        try {
            waitLoadingBar();
            WaitUtil.waitFor(5);

            Set<String> windowId = driver.getWindowHandles();
            Iterator<String> handles = windowId.iterator();
            // 메인 윈도우 창 확인
            subHandle = null;
            while (handles.hasNext()) {
                subHandle = handles.next();
                logger.debug(subHandle);
                WaitUtil.loading(3);
            }
            driver.switchTo().window(subHandle);

            //심사유형 설정
            WebElement $productTypeDiv = driver.findElement(By.id("grp_241"));
            WebElement $productTypeSpan = $productTypeDiv.findElement(By.xpath("//span[text()='" + expectedTextType + "']"));
            WebElement $productTypeBtn = $productTypeSpan.findElement(By.xpath("./following-sibling::button"));
            click($productTypeBtn);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new CommonCrawlerException(exceptionEnum, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setSubTreatyInfo(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
        WebElement $subTreatyTbody = driver.findElement(By.id("scnNodeList"));

        try {
            logger.info("특약 전체 해제");
            List<WebElement> $checkedInputBox = driver.findElements(By.cssSelector("input[type=checkbox][id^=scnChk_]:checked"));

            for(WebElement $checkInput : $checkedInputBox){
                String id = $checkInput.getAttribute("id");
                WebElement $label = driver.findElement(By.xpath("//label[@for='" + id + "']"));
                if ($checkInput.isSelected()) {
                    helper.waitElementToBeClickable($label).click();
                }
            }
            driver.findElement(By.xpath("//*[@id=\"globalAlert0\"]/div/div/div[2]/button/span")).click();

            WaitUtil.waitFor(3);

            if(welgramTreatyList.size() > 0) {
                //가입설계 정보대로 선택특약 세팅
                for(CrawlingTreaty treaty : welgramTreatyList) {
                    String treatyName = treaty.getTreatyName();
                    WebElement $treatyDiv = $subTreatyTbody.findElement(By.xpath(".//div[normalize-space()='" + treatyName + "']"));
                    WebElement $treatyTr = $treatyDiv.findElement(By.xpath("./ancestor::tr[1]"));
                    setTreatyInfoFromTr($treatyTr, treaty);
                }

                //실제 입력된 선택특약 정보 읽어오기
                List<WebElement> $checkedInputs = $subTreatyTbody.findElements(By.cssSelector("input[name='scnChk']:checked"));
                for(WebElement $input : $checkedInputs) {
                    WebElement $treatyTr = $input.findElement(By.xpath("./ancestor::tr[1]"));
                    CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                    if(targetTreaty != null) {
                        targetTreatyList.add(targetTreaty);
                    }
                }

                boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
                if(result) {
                    logger.info("선택특약 정보 일치");
                } else {
                    logger.info("선택특약 정보 불일치");
                    throw new Exception();
                }

            } else {
                logger.info("가입설계에 선택특약이 존재하지 않습니다.");
            }

        }catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



}

