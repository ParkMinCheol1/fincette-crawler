package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class LTF_OST_D002 extends CrawlingLTFDirect {
    public static void main(String[] args) { executeCommand(new LTF_OST_D002(), args); }

    enum DateType {출발, 도착}
    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enter();
        setDateAndTime(DateType.출발);
        setDateAndTime(DateType.도착);
        setTravelType(info.textType.split("#")[0]);
        setGender(info);
        setBirthday(info.getFullBirth());
        helper.click(By.id("btnNext"), "다음 클릭");
        helper.waitForLoading();

        // 단계 전환후
        setPlan(info);
        compareLTFTreaties(info);
        crawlPremium(info);
        crawlReturnPremium(info); // 롯데 다이렉트 상품은 현재까지 만기환급금 정보가 없어서 일괄 "0" 처리함

        return true;
    }

    @Override
    protected void setPlan(CrawlingProduct info) throws CommonCrawlerException {
        try {
            String plan = info.textType.split("#")[1];

            helper.click(By.xpath("//li[contains(@class,'tab-item')]/a[text()='" + plan + "']"),
                plan + "플랜 클릭");

            logger.info("플랜 :: {}", plan);
            WaitUtil.waitFor(2);

        } catch (NoSuchElementException ignored) {
            logger.info("플랜 요소가 존재하지 않습니다.");
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    private void setTravelType(String type) throws CommonCrawlerException {
        try {
            helper.click(By.xpath(
                "//div[@data-label='여행 유형 선택']//span[text()='" + type +"']/ancestor::label"),
                "여행유형 클릭 : " + type);
            WaitUtil.loading(1);
        } catch (Exception e) {
            throw new CommonCrawlerException(e, "여행유형 설정 실패");
        }
    }

    private void setDateAndTime(DateType dateType) throws CommonCrawlerException {

        try {

            // 출발일인 경우에만 요소를 클릭해야함. 도착일은 자동으로 캘린더 팝업이 나타남
            if (dateType.equals(DateType.출발)) {
                logger.info(dateType.name() + " div 요소 클릭하기"); // 출발일, 도착일
                helper.click(
                    By.xpath("//input[@name='"
                        + ( dateType.equals(DateType.출발) ? "insStStr" : "insClstrStr" )
                        + "']/ancestor::div[1]"),
                    dateType.name() + "일 요소 클릭");
                WaitUtil.loading(1);
            }


            // 달력에서 출발일인 경우 오늘 날짜보다 7일 후의 날짜를 선택, 도착일인 경우 6일 후의 날짜를 선택
            LocalDateTime dateTime =
                LocalDateTime.now().plusDays(( dateType.equals(DateType.출발) ? 7 : (7 + 6) ));
            String dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(dateTime);


            // 해당 날짜가 표시될 때까지 다음 월을 클릭
            int attempt = 0;
            while (!helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//button[@aria-label='" + dateFormat + "']"))
                .stream().anyMatch(WebElement::isDisplayed))
            {
                helper.click(By.cssSelector("button.calendar-btn-next"), "다음 월 클릭");
                WaitUtil.loading(1);
                attempt++;

                if (attempt > 3) {
                    throw new CommonCrawlerException("다음 월을 3번 클릭해도 " + dateFormat + " 이 표시되지 않음");
                }
            }


            // 해당 날짜 클릭
            WebElement dateEl = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//button[@aria-label='" + dateFormat + "']"))
                .stream().filter(WebElement::isDisplayed).findFirst().get();
            helper.click(dateEl, "출발일 클릭 : " + dateFormat);
            WaitUtil.loading(2);


            // 시간 선택
            String time = dateType.equals(DateType.출발) ? "00" : "23";
            String name = dateType.equals(DateType.출발) ? "insStHms" : "insClstrHms";
            WebElement timeEl = helper.waitPresenceOfElementLocated(
                By.xpath("//input[@name='" + name + "' and @value='" + time + "']/ancestor::label"));

            helper.moveToElementByJavascriptExecutor(timeEl);
            helper.click(timeEl, dateType.name() + "시간 클릭 : " + time);

            WaitUtil.loading(3);

        } catch (Exception e) {
            throw new CommonCrawlerException(e, dateType.name() + "일 설정 실패");
        }
    }


}
