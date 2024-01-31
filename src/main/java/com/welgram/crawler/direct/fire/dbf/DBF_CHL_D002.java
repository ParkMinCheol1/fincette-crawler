package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DBF_CHL_D002 extends CrawlingDBF {

	

	public static void main(String[] args) {

		executeCommand(new DBF_CHL_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);
		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}


	public void crawlFromHomepage(CrawlingProduct info) throws Exception {

			// 생년월일
			logger.info("생년월일");
			helper.sendKeys3_check(By.id("chdBirth"), info.fullBirth);
			logger.debug("d");

			// 성별
			logger.info("성별");
			List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("sxCd"));
			for (WebElement radioBtn : radioBtns) {
				if (radioBtn.getAttribute("value")
					.equals(Integer.toString(info.getGender() == MALE ? 1 : 2))) {
					radioBtn.findElement(By.xpath("ancestor::label")).click();
					logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
					break;
				}
			}

			// 직업
			logger.info("자녀 취학정보");
			int age = Integer.parseInt(info.age);
			if (age < 8) {    // 미취학 아동
				driver.findElement(By.id("jobCd1")).click();    // 미취학 아동
				logger.info("미취학 아동 선택");
			} else {    // 초/중/고 학생
				driver.findElement(By.id("jobCd2")).click();    // 초/중/고 학생
			}

			// 보험료 확인하기
			logger.info("보험료 확인하기 버튼 클릭");
			clickByLinkText("보험료 확인하기");

			WaitUtil.waitFor(2);
			helper.waitForCSSElement(".loadmask");

			// 가입형태 : 실속형 | 고급형
			logger.info("가입형태");
			String textTypeNumber = "02";
			if (info.textType.equals("실속형")) {
				textTypeNumber = "01";
				driver.findElement(By.id("pdcPanCd1")).sendKeys(Keys.ENTER);    // 실속형
				driver.findElement(By.id("pdcPanCd1")).click();    // 실속형
				logger.info("선택 : " + info.textType);
			} else if (info.textType.equals("고급형")) {
				textTypeNumber = "03";
				driver.findElement(By.id("pdcPanCd3")).sendKeys(Keys.ENTER);    // 고급형
				driver.findElement(By.id("pdcPanCd3")).click();    // 고급형
				logger.info("선택 : " + info.textType);
			}
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(2);


			//특약 체크
			loopTreatyCheck2(info, textTypeNumber);

			//특약 loop
			loopTreatyList(info, textTypeNumber);


			// 보험기간
			logger.info("보험기간");
			setRadioBtnByText(By.name("selArcTrm"), info.insTerm);
			WaitUtil.waitFor(2);


			// 납입기간
			logger.info("납입기간");
			setRadioBtnByText(By.name("selPymTrm"), info.napTerm);
			WaitUtil.waitFor(2);


			//다시계산 버튼이 있는 겨우 클릭
			reCompute();

			// 월 보험료
			logger.info("월 보험료");
			String premium;
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#totPrm")));
			WaitUtil.waitFor(1);
			premium = driver.findElement(By.cssSelector("#totPrm")).getText().replace(",", "").replace("원", "");
			WaitUtil.waitFor(1);
			info.treatyList.get(0).monthlyPremium = premium;
			WaitUtil.waitFor(1);
			logger.info("월 보험료 확인 : " + premium);

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);

			// 예상해약환급금 버튼 클릭
			logger.info("해약환급금");
			getReturnMoney(info, By.linkText("해약환급금 예시"));
	}


	protected void getPremium(CrawlingProduct info) throws Exception {
		String premium; // 산출되었을 때, 크롤링으로 얻어올 '특약별 월 납입료(보장보험료)'
		int sumPremium = 0; // '특약별 월 납입료(보장보험료)'를 더한 값 ---> 임시로 입력한 희망보험료 대신 이 값으로 재입력하게 된다.
		int sumPremiumNew; // '특약별 월 납입료(보장보험료)'를 더한 값 ---> 임시로 입력한 희망보험료 대신 이 값으로 재입력하게 된다.
		String treatyName_ch; // 체크된 특약명
		String treatyName;

		// --------'특약별 월 납입료(보장보험료)'를 api에 저장하고 & 구해진 각각의 값을 더해 합계보험료를 구한다-------------//
		// 보장목록의 각 체크박스 리스트
		elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#tableDamboList input[name='cvr_if__cvr_cd']"));

		for (int i = 0; i < elements.size(); i++) {
			// elements.get(i) 체크박스 input
			if (elements.get(i).isSelected()) {
				element = elements.get(i).findElement(By.xpath("ancestor::tr"));

				// 체크된 체크박스와 같은 행의 '특약별 월 납입료(보장보험료)'
				premium = element.findElement(By.cssSelector("td:last-child")).getText();

				// ------------- '특약별 월 납입료(보장보험료)' 합계 구하기---------------------- //
				sumPremium += Integer.parseInt(premium.replaceAll("[^0-9]", ""));

				// ------------- '특약별 월 납입료(보장보험료)' 저장 ----------------------------------- //
				// 체크된 특약명(담보명)
				treatyName_ch = element.findElement(By.cssSelector("td:nth-child(3)")).getText();

				// API 해당특약마다 산출된 '특약별 월 납입료(보장보험료)' 저장
				for (CrawlingTreaty item : info.treatyList) {
					treatyName = item.treatyName;

					if (treatyName_ch.contains(treatyName)) {
						String premiumVal;
						premiumVal = premium.replaceAll("[^0-9]", "");
						item.monthlyPremium = premiumVal;
						// 금액은 주계에만 세팅 한다 for 할인률 적용
						logger.info(":: 담보별 월 납입료  저장:: " + item.treatyName + ":" + premium);
						break;
					}
				}
			}
		}

		logger.info("담보별 월 납입료 총합 : " + sumPremium);
		// 산출된 '특약별 월 납입료(보장보험료)' 더한 값에서 100의 자리수 올림
		// 10원 단위로 입력해야 하기 때문
		// (121 + 9)/10*10


		switch (info.productCode) {
			case "DBF_MDC_D001": // 보험료를 재입력할 필요가 없음
				break;

			default:
				sumPremiumNew = ((sumPremium + 99) / 100) * 100;
				//info.savePremium = Integer.toString( sumPremiumNew - sumPremium );


				element = helper.waitPresenceOfElementLocated(By.name("rsl_if__sm_prm_input"));
				element.click();
				element.clear();

				// 산출된 '특약별 월 납입료(보장보험료)' 더한 값 입력
				element.sendKeys(Integer.toString(sumPremiumNew));

				logger.info("재입력한 희망 총 보험료 : " + sumPremiumNew);
		}
		clickBtn(By.cssSelector("img[alt^='보험료']"));
		logger.info("보험료 산출버튼 클릭");

	}



/*    //상품마스터의 특약이 전부 존재하는제 체크
    protected boolean loopTreatyCheck1(CrawlingProduct info,String textTypeNumber){

        boolean result;
        int pmTreatySize = info.treatyList.size();
        List<String> treatySave = new ArrayList<>();

        elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.plan-fix > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));
        int elementsSize = elements.size();

        for (int i=0; i<info.treatyList.size(); i++) {

            for(int j=0; j<elementsSize; j++){

                if(elements.get(j).findElement(By.cssSelector("span")).getText().equals(info.treatyList.get(i).treatyName)){
                    logger.info("존재 확인 : "+info.treatyList.get(i).treatyName);
                    pmTreatySize --;
                    break;
                }

                if(j == elementsSize-1){
                    treatySave.add(info.treatyList.get(i).treatyName);
                }
            }
        }
        if(pmTreatySize == 0) {
            logger.info("모든특약이 존재함");
            result = true;
        }else {
            logger.info("존재하지 않는 특약의 수 : "+pmTreatySize+"개");
            for(int i=0; i<treatySave.size(); i++){
                logger.info("존재하지 않는 특약 : "+treatySave.get(i));
            }
            result = false;
        }
        return result;
    }*/






	//상품마스터의 특약이 전부 존재하는제 체크
	protected void loopTreatyCheck2(CrawlingProduct info, String textTypeNumber) throws Exception {

		int pmTreatySize = info.treatyList.size();

		logger.info("나이 확인 (info.age) : "+info.age);
		List<String> treatyListSave = new ArrayList<>();
		List<String> treatyListCount = new ArrayList<>();
		List<String> userPageTreatyMoney = new ArrayList<>();
		List<Integer> productMasterTreatyMoneyList = new ArrayList<>();
		HashMap<String, Integer> productMasterList = new HashMap<String, Integer>();


		for(int i=0; i<pmTreatySize; i++){
			String tName = info.treatyList.get(i).treatyName;
			int tMoney = info.treatyList.get(i).assureMoney;

			productMasterList.put(tName, tMoney);
			//productMasterTreatyMoneyList.add(tMoney);
			//treatyListSave.add(tName);
		}

		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap.t1 > div.plan-fix > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));
		int elementsSize = elements.size();
		DecimalFormat decFormat = new DecimalFormat("###,###");

		//logger.info("특약명 size : "+treatyListSave.size());
		//logger.info("특약가격 size : "+productMasterTreatyMoneyList.size());
		logger.info("상품마스터 size : "+productMasterList.size());
		logger.info("페이지 size : "+elementsSize);



		for(int i=0; i<elementsSize; i++){
			int treatyCount = 0;

			Set set2 = productMasterList.entrySet();
			Iterator iterator2 = set2.iterator();


			while(iterator2.hasNext()){

				Entry<String,Integer> entry = (Entry)iterator2.next();
				String key = (String)entry.getKey();
				int value = (Integer)entry.getValue();

				if(productMasterList.size() == 0){
					break;
				}

				/*if(elements.get(i).findElement(By.cssSelector("td")).getAttribute("class").equals("title-group")){
					logger.info("클레스명 확인 : "+elements.get(i).findElement(By.cssSelector("td")).getAttribute("class")+ " / 제목");
					break;
				}*/


				if(elements.get(i).findElement(By.cssSelector("span")).getText().contains(entry.getKey())){

					//골전 진단비만 특이케이스로 contains를 사용할 수 없음
					if(entry.getKey().equals("골절 진단비")) {
						if(!entry.getKey().equals(elements.get(i).findElement(By.cssSelector("span")).getText())){
							continue;
						}
					}

					//logger.info("특약 확인 : "+elements.get(i).findElement(By.cssSelector("span")).getText()+" == "+entry.getKey());

					//가입금액 형식수정
					String formatMoney;
					formatMoney = Integer.toString(entry.getValue());
					formatMoney = formatMoney.replaceFirst("0000", "");

					// 1억이 넘는 경우, 처리
					if(Integer.parseInt(formatMoney) >= 10000){

						formatMoney = decFormat.format(Integer.parseInt(formatMoney));
						String million100 = formatMoney;
						formatMoney = formatMoney.substring(0, 1) + "억" + formatMoney.substring(1, million100.length());
						formatMoney = formatMoney+"만 원";

						// 1억원 또는 2억원 등등 억원으로 표시되는 경우를 처리
						if(formatMoney.contains("0,000만 원")){
							formatMoney = formatMoney.replaceFirst("0,000만 원", " 원");
						}
						// 1억 미만인 경우, 처리
					}else{
						formatMoney = decFormat.format(Integer.parseInt(formatMoney));
						formatMoney = formatMoney+"만 원";
					}


					if(!elements.get(i).findElement(By.cssSelector("ul > li.pmoney > span:nth-child(1)")).getText().contains(formatMoney)){
						logger.info("-------------------------------------------------------------------------------------------");
						logger.info("가격 다름");
						logger.info("페이지 이름 : "+elements.get(i).findElement(By.cssSelector("span")).getText());
						logger.info("페이지에 금액확인 : "+elements.get(i).findElement(By.cssSelector("ul > li.pmoney > span:nth-child(1)")).getText());
						logger.info("상품마스터 이름 : "+entry.getKey());
						logger.info("상품에 등록된 금액확인 : "+formatMoney);
						logger.info("-------------------------------------------------------------------------------------------");
					}


					productMasterList.remove(entry.getKey());
					//treatyListSave.remove(treatyListSave.get(j));
					//productMasterTreatyMoneyList.remove(productMasterTreatyMoneyList.get(j));

					//logger.info("상품마스터 수 : "+productMasterList.size();
					treatyCount++;
					break;
				}
			}

			if(treatyCount == 0){
				treatyListCount.add(elements.get(i).findElement(By.cssSelector("span")).getText());
			}

			if((i+1) < elementsSize){
				WebElement element = elements.get(i+1);
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			}
		}


		try {
			if (productMasterList.size() != 0) {
				String noneText = "";
				for(Entry<String, Integer> elem : productMasterList.entrySet()){
					noneText += elem.getKey() + System.lineSeparator();
				}

				throw new Exception("존재하지 않는 가설 수 : " + productMasterList.size()+"개" + System.lineSeparator() + noneText);
			}

			if(treatyListCount.size() != 0){

				for(int i=0; i<treatyListCount.size(); i++){
					logger.info("웹페이지에만 존재하는 특약 목록 : "+treatyListCount.get(i));
				}
			}

		}catch (Exception e){
			throw e;
		}
		if(treatyListSave.size() == 0){
			logger.info("상품마스터에 모든 특약이 존재");
		}
	}


}
