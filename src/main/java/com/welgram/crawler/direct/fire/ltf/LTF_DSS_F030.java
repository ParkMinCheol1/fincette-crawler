package com.welgram.crawler.direct.fire.ltf;

public class LTF_DSS_F030 extends CrawlingLTFAnnounce {

    public static void main(String[] args) {
        executeCommand(new LTF_DSS_F030(), args);
    }

    /*@Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        disclosureRoomCrawling(info);
        return true;
    }


    private void disclosureRoomCrawling(CrawlingProduct info) throws Exception {
        WaitUtil.loading(4);

        // 공시실
        logger.info("공시실열기");
        openAnnouncePage(info.productName);
        WaitUtil.loading(4);

        elements = driver.findElements(By.cssSelector("#content > div:nth-child(3) > table > thead > tr:nth-child(1) > th"));
        int elementsSize = elements.size();

        for(int i=0; i<elementsSize; i++){

            if(i == 0){
                continue;
            }

            logger.info("id확인 : "+elements.get(i).findElement(By.cssSelector("label > input")).getAttribute("id"));

            if(elements.get(i).findElement(By.cssSelector("label > input")).getAttribute("id").contains(info.textType)){
                elements.get(i).click();
                WaitUtil.waitFor(1);
                break;
            }
        }

        setBirth(info.fullBirth, "#PBirth");

        selectGender(info.gender);

        selectJob();

        logger.info("다음 클릭");
        driver.findElement(By.cssSelector("#btn1_1")).click();
        helper.waitForCSSElement("#loading");
        WaitUtil.waitFor(2);

        setSelectBox(info.napTerm,"pymTrmcd");
        WaitUtil.waitFor(2);

        logger.info("다음 클릭");
        driver.findElement(By.cssSelector("#btn1_1")).click();
        helper.waitForCSSElement("#loading");
        WaitUtil.waitFor(2);


        for (CrawlingTreaty item : info.treatyList) {
            // 특약선택
            setTreaty(item);
        }

        // 보험료 계산
        logger.info("보험료 계산 버튼 누르기");
        driver.findElement(By.cssSelector("#btnCalProc")).click();
        helper.waitForCSSElement("#loading");
        WaitUtil.waitFor(2);


        logger.info("스크린샷!");
        takeScreenShot(info);

        for (CrawlingTreaty item : info.treatyList) {
            getPremium(info,item);
        }

        // 특약개수가 다를경우 result = false 처리
        if(info.treatyList.size() != info.siteProductMasterCount){
            logger.info("특약개수가 다릅니다.");
            logger.info("상품의 특약 개수 :: " + info.treatyList.size());
            logger.info("DB에서 일치하는 특약 개수 :: " + info.siteProductMasterCount);
        } else { // 특약개수가 같아도 상품 특약,DB에서 일치하는 특약 개수를 확인할 수 있는 log 추가
            logger.info("특약개수가 똑같습니다.");
            logger.info("상품의 특약 개수 :: " + info.treatyList.size());
            logger.info("DB에서 일치하는 특약 개수 :: " + info.siteProductMasterCount);
        }

        // 보험료 저장
        String premium = driver.findElement(By.cssSelector("#Adprem1")).getAttribute("value")
            .replaceAll("[^0-9]", "");
        info.getTreatyList().get(0).monthlyPremium = premium;
        logger.info("월 보험료 :: {}원", premium);


        // + 적립보험료
        getSavingPremium(info);

        // 해약환급금
        getReturnPremium(info);

        info.errorMessage = "";

    }

    @Override
    protected void selectJob() throws Exception {
        driver.findElement(By.cssSelector("#Jbnm")).click();


        switchtowindows(3);

        logger.info("직업검색창에 교사입력");
        driver.findElement(By.cssSelector("#content > div.section_udline > p > label > input")).sendKeys("교사");
        WaitUtil.waitFor(1);

        logger.info("검색버튼클릭");
        driver.findElement(By.cssSelector("#content > div.section_udline > p > span > a")).click();
        WaitUtil.waitFor(1);

        logger.info("검색된 직업 선택");
        elements = driver.findElements(By.cssSelector("#addr_list > tr"));
        int elementsSize = elements.size();


        for(int i=0; i<elementsSize; i++){

            if(elements.get(i).findElement(By.cssSelector("td:nth-child(1) > a")).getText().equals("보건 교사")){
                elements.get(i).findElement(By.cssSelector("td:nth-child(1) > a")).click();
                WaitUtil.waitFor(1);
                break;
            }
        }

        switchtowindows(3);
    }

    // 공시실
    @Override
    protected void openAnnouncePage(String productName) {
//		helper.elementWait("tbody#before");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("tbody#before")));
        element = helper.waitVisibilityOfElementLocated((By.cssSelector("tbody#before")));
        elements = element.findElements(By.className("alignL"));

        // 현재 창
        // currentHandle = driver.getWindowHandles().iterator().next();
        // logger.info("윈도우핸들 사이즈 : " + driver.getWindowHandles().size());
        logger.info(productName + " 상품 찾는 중...");
        logger.info("같은 이름의 상품이 2개 올라와 있음 두번째상품이 맞는 상품이라 2번째 선택");
        int count = 0;
        for (WebElement td : elements) {

            // logger.info("td.getText().trim()" + td.getText().trim());
            if (td.getText().trim().contains(productName.trim())) {

                if (count == 1) {
                    element = td.findElement(By.xpath("parent::*")).findElement(By.tagName("span"));
                    element.click();
                    break;
                }
                count++;
            }
        }
        // switchToWindow(currentHandle, driver.getWindowHandles(), true);
        switchtowindows(2);
    }*/

}
