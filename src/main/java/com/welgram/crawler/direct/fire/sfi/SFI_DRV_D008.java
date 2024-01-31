package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SFI_DRV_D008 extends CrawlingSFIMobile {

    public static void main(String[] args) {
        executeCommand(new SFI_DRV_D008(), args);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        waitLoadingBar();

        logger.info("모달창이 뜨는지를 확인합니다");
        modalCheck();

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("영업용 자동차를 운전하세요?");
        setVehicle("아니요");

        logger.info("다음 버튼 클릭");
        $button = driver.findElement(By.xpath("//footer[@id='footer']//button[text()='다음']"));
        click($button);

        logger.info("플랜 설정");
        setPlan(info.planSubName);

        logger.info("보험기간 비교");
        setInsTerm(info.getInsTerm());

        logger.info("납입기간 비교");
        setNapTerm(info.getNapTerm());

        logger.info("납입방법 비교");
        setNapCycle(info.getNapCycleName());

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }





    /**
     * 삼성화재 다이렉트 특약설정 TYPE2 : 특약 더보기 버튼을 클릭해서 처리
     */
    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        String script = "";
        ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;

        try {

            //원수사 특약목록 영역
            WebElement $treatyDiv = driver.findElement(By.xpath("//div[@id='calc-dambolist-table']/div[@class='result-list']"));
            List<WebElement> $treatyGroupDivList = $treatyDiv.findElements(By.xpath("./div[@class='row-group']"));

            //특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리 (*문자열로 시작하는 span 삭제)
            script = "$(arguments[0]).find('span.basic-bullet.color-type03').remove();";
            helper.executeJavascript(script, $treatyDiv);

            //특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(UPGRADE/HOT span 삭제)
            script = "$(arguments[0]).find('span.tip-layer').remove();";
            helper.executeJavascript(script, $treatyDiv);


            /**
             * ===========================================================================================
             * [STEP 1]
             * 원수사 특약명, 가입설계 특약명 수집 진행하기
             * ===========================================================================================
             */
            List<String> targetTreatyNameList = new ArrayList<>();
            List<String> welgramTreatyNameList = new ArrayList<>();


            //원수사 특약명 수집
            for(WebElement $treatyGroupDiv : $treatyGroupDivList) {
                //TODO 스크롤 처리?
                WebElement $treatyNameDiv = $treatyGroupDiv.findElement(By.xpath(".//div[@class='tit-name']"));

                String targetTreatyName = $treatyNameDiv.getText().trim();
                targetTreatyNameList.add(targetTreatyName);
            }


            //가입설계 특약명 수집
            welgramTreatyNameList = welgramTreatyList.stream().map(t -> t.getTreatyName()).collect(Collectors.toList());


            //원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
            List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
            List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트
            List<String> strangeTreatyNameList = new ArrayList<>();                                //이상 있는 특약명 리스트


            //(원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
            targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
            matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구


            //(원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
            targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
            dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);



            /**
             * ===========================================================================================
             * [STEP 2]
             * 특약 가입/미가입 처리 진행하기
             * ===========================================================================================
             */


            //불일치 특약들에 대해서 원수사에서 미가입 처리 진행
            for(String treatyName : dismatchedTreatyNameList) {
                String treatyAssureMoney = "";

                logger.info("특약명 : {} 미가입 처리 진행중...", treatyName);


                //특약명, 특약 가입금액 관련 element 찾기
                WebElement $treatyNameDiv = $treatyDiv.findElement(By.xpath(".//div[text()='" + treatyName + "']"));
                WebElement $treatyGroupDiv = $treatyNameDiv.findElement(By.xpath("./ancestor::div[@class='row-group'][1]"));
                WebElement $treatyAssureMoneyDiv = $treatyGroupDiv.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='price']"));
                WebElement $treatyAssureMoneyButton = $treatyAssureMoneySpan.findElement(By.xpath("./parent::button"));

                treatyAssureMoney = $treatyAssureMoneySpan.getText();
                treatyAssureMoney = treatyAssureMoney.replace("한도", "").replace("지급", "");


                //미가입 처리해야하는 특약의 가입 상태가 "가입"인 경우(=가입금액란에 가입금액이 표시된 경우)
                boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
                if(isJoin) {

                    //특약 가입금액 조정 팝업창 열기
                    click($treatyAssureMoneyButton);

                    //미가입 버튼 클릭
                    WebElement $popupSection = driver.findElement(By.id("V2LongEditDambo"));
                    WebElement $popupUl = $popupSection.findElement(By.id("security-list"));
                    WebElement $미가입Div = $popupUl.findElement(By.xpath(".//div[text()='미가입']"));
                    WebElement $미가입Label = $미가입Div.findElement(By.xpath("./parent::label"));
                    click($미가입Label);

                    //확인 버튼 클릭
                    WebElement $confirmButton = $popupSection.findElement(By.xpath(".//button[text()='확인']"));
                    click($confirmButton);
                }
            }



            /**
             * TODO 이게 다이렉트 상품에 한해 적합한 프로세스인지 한번 확인해볼 필요 있음
             * 공통된 특약명에 대해서는 사실 가입금액 조정 과정이 필요하다.
             * 하지만 다이렉트 상품의 경우 원수사에서 default로 설정한 특약의 가입금액이 의미있다고 판단된다.
             * 따라서 굳이 원수사가 default로 설정한 가입금액을 우리 가입설계 특약의 금액에 맞게 꾸역꾸역
             * 조정하는 과정이 필요할까?
             * 원수사가 default로 설정한 가입금액이 가입설계 가입금액과 다르면 가입설계의 가입금액을
             * 수정하도록 예외를 발생시키는게 더 의미있다고 판단하기에
             * 공통된 특약들에 대해서는 따로 가입금액 조정 처리를 하지 않도록 하겠다.
             *
             */



            /**
             * ===========================================================================================
             * [STEP 3]
             * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
             * ===========================================================================================
             */
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            for(WebElement $treatyGroup : $treatyGroupDivList) {

                //TODO 스크롤 조정?

                //특약명, 특약가입금액  element 찾기
                String treatyName = "";
                String treatyAssureMoney = "";
                WebElement $treatyNameDiv = $treatyGroup.findElement(By.xpath(".//div[@class='tit-name']"));
                WebElement $treatyAssureMoneyDiv = $treatyGroup.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='price']"));


                //특약명, 특약가입금액 읽어오기
                treatyName = $treatyNameDiv.getText();
                treatyAssureMoney = $treatyAssureMoneySpan.getText();
                treatyAssureMoney = treatyAssureMoney.replace("한도", "").replace("지급", "");


                //가입하는 특약에 대해서만 원수사 특약 정보 적재
                boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
                if(isJoin) {
                    treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    targetTreaty.setTreatyName(treatyName);
                    targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

                    targetTreatyList.add(targetTreaty);
                }

            }



            /**
             * ===========================================================================================
             * [STEP 4]
             * 원수사 특약 정보 vs 가입설계 특약 정보 비교
             * ===========================================================================================
             */
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
            if(result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }


        } catch (Exception e) {
            throw new SetTreatyException(e, exceptionEnum.getMsg());
        }
    }

}
