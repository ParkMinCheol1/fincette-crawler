package com.welgram.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.welgram.common.HostUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.cli.excutor.CommandOptions;
import com.welgram.crawler.cli.excutor.VpnGroup;
import com.welgram.crawler.common.except.NotFoundPlanMasterException;
import com.welgram.crawler.common.except.NotFoundProductData;
import com.welgram.crawler.common.except.ProductCodeEmptyException;
import com.welgram.crawler.common.except.WrongPlanCountException;
import com.welgram.crawler.common.ext.ChromeMudfishProxy;
import com.welgram.crawler.common.ext.ChromeMudfishVpn;
import com.welgram.crawler.common.ext.ChromeSurfSharkVpn;
import com.welgram.crawler.common.ext.Tor;
import com.welgram.crawler.comparer.PlanComparer;
import com.welgram.crawler.comparer.impl.PlanAnnuityMoneyComparer;
import com.welgram.crawler.comparer.impl.PlanReturnMoneyComparer;
import com.welgram.crawler.comparer.impl.PlanReturnMoneyComparer2;
import com.welgram.crawler.comparer.impl.PlanTreatyComparer;
import com.welgram.crawler.general.CrawlScreenShot;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanMonitoringStatus;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.BodabCrawler;
import com.welgram.crawler.scraper.BodabMonitor;
import com.welgram.crawler.scraper.BodabScraper;
import com.welgram.util.Birthday;
import com.welgram.util.InsuranceUtil;
import com.welgram.util.StringUtil;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

public abstract class AbstractCrawler implements Crawler {

//    public final static Logger logger = LoggerFactory.getLogger(AbstractCrawler.class);
    public FinLogger logger = FinLogger.getFinLogger(getProductCodeClass());

    CrawlingApi crawlingApi = new CrawlingApi();

    boolean isStarted = false; // driver 시작여부
    protected String crawlUrl;
    protected final StopWatch stopWatch = new StopWatch();
    protected final StopWatch totalTime = new StopWatch();
    protected final int[] CRAWL_STANDARD_TIME_LIST = {58, 59, 60, 61, 62, 63, 64, 65}; // 크롤링 실행기준시간(60초)
    protected int crawlCount;
    protected CrawlScreenShot crawlScreenShot;
    private CommandOptions commandOptions;
    private String productCode;
    private BodabScraper scraper;

    public CommandOptions getCommandOptions() {
        return commandOptions;
    }

    public void setCommandOptions(CommandOptions commandOptions) {
        this.commandOptions = commandOptions;
    }

    @Override
    public abstract int doCrawlInsurance(CrawlingProduct product) throws Exception;

    @Override
    public int execute(CommandOptions commandOptions) {
        this.commandOptions = commandOptions;
        int result = 1;

        try {
            String productCode = commandOptions.getProductCode();

            if (!ObjectUtils.isEmpty(productCode)) {
                List<Integer> planIdList = commandOptions.getPlanIdList();

                if (commandOptions.isMonitoring()) {
                    scraper = new BodabMonitor();
                } else {
                    scraper = new BodabCrawler();
                }

                if (planIdList.size() > 0) {
                    for (int planId : planIdList) {
                        result = execute(productCode, commandOptions.isMonitoring(), planId,
                            commandOptions.getAges(), commandOptions.getGender(),
                            commandOptions.getZero(), commandOptions.getVpnGroup(),
                            commandOptions.getSite(), commandOptions.getScreenShot());
                    }

                } else {
                    result = execute(productCode, commandOptions.isMonitoring(), null,
                        commandOptions.getAges(), commandOptions.getGender(),
                        commandOptions.getZero(), commandOptions.getVpnGroup(),
                        commandOptions.getSite(), commandOptions.getScreenShot());
                }

            } else {
                throw new ProductCodeEmptyException("productCode is empty...");
            }

        } catch (ProductCodeEmptyException e) {
            e.printStackTrace();
            logger.error(e.getMessage());

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    /**
     * 크롤링 구현 클래스의 이름으로부터 상품코드 조회
     */
    protected String getProductCode() {
        return this.productCode;
    }

    protected Class getProductCodeClass() {
        return this.getClass();
    }

    /**
     * 모니터링 시작하기
     *
     * @param productCode 상품코드
     */
    private void startMonitoring(String productCode) {
        crawlingApi.modifyMonitoringStart(productCode);
    }



    /**
     * 비교하기
     *
     * @param info 크롤링상품정보
     * @return boolean
     */
    private boolean comparing(CrawlingProduct info) {

        boolean result = false;
        // 기존 데이터
        // insMoney - 가입금액
        // annPrmium - 연금수령액
        // returnPremium - 만기환급금

        String[] annuityCategories = {"CD00078", "CD00081"};
        String originInsMoney = "";
        String originAnnuityPremium = "";
        String originFixedAnnuityPremium = "";
        String originExpMoney = "";
        String crawlInsMoney = "";
        String crawlAnnuityPremium = "";
        String crawlFixedAnnuityPremium = "";
        String crawlExpMoney = "";

        //디폴트로 가설별 모니터링 데이터의 모든 변동값여부 정상으로 세팅해둠.
        PlanMonitoringStatus planMonitoringStatus = info.getPlanMonitoringStatus();
        planMonitoringStatus.setSiteStatus("Y");                  //가설별 모니터링 데이터 중 사이트 정상
        planMonitoringStatus.setInsMoneyStatus("N");              //가설별 모니터링 데이터 중 보혐료 변동없음
        planMonitoringStatus.setReturnMoneyStatus("N");           //가설별 모니터링 데이터 중 해약환급금 테이블 변동없음
        planMonitoringStatus.setExpMoneyStatus("N");              //가설별 모니터링 데이터 중 만기환급금 변동없음
        planMonitoringStatus.setAnnMoneyStatus("N");              //가설별 모니터링 데이터 중 연금수령액 테이블 변동없음

        try {

            JsonObject planCalcResult = (JsonObject) crawlingApi.getPlanCalc(info.planId, info.caseNumber);
            JsonObject jsonData = planCalcResult.get("data").getAsJsonObject();

            //기존 보험료 정보
            originInsMoney = jsonData.has("insMoneySum") ? jsonData.get("insMoneySum").getAsString() : "0";                             //기존 보험료
            originExpMoney = jsonData.has("expMoneySum") ? jsonData.get("expMoneySum").getAsString() : "0";                             //기존 만기환급금
            originAnnuityPremium = jsonData.has("annMoneySum") ? jsonData.get("annMoneySum").getAsString() : "0";                       //기존 종신연금수령액
            originFixedAnnuityPremium = jsonData.has("fixedAnnMoneySum") ? jsonData.get("fixedAnnMoneySum").getAsString() : "0";        //기존 확정연금수령액

            //크롤링 해온 보험료 정보
            if ("-1".equals(info.totPremium)) {
                crawlInsMoney = "-1";
            } else {
                crawlInsMoney = (info.totPremium.isEmpty()) ? "0": info.totPremium.replaceAll("[^0-9]", "");                                                   //크롤링 해온 보험료
            }

            if ("-1".equals(info.returnPremium)) {
                crawlExpMoney = "-1";                                             //크롤링 해온 만기환급금
            } else {
                crawlExpMoney = (info.returnPremium.isEmpty()) ? "0" : info.returnPremium.replaceAll("[^0-9]", "");                                             //크롤링 해온 만기환급금
            }

            if ("-1".equals(info.annuityPremium)) {
                crawlAnnuityPremium = "-1";                                     //크롤링 해온 종신연금수령액
            } else {
                crawlAnnuityPremium = (info.annuityPremium.isEmpty()) ? "0" : info.annuityPremium.replaceAll("[^0-9]", "");                                     //크롤링 해온 종신연금수령액
            }

            if ("-1".equals(info.fixedAnnuityPremium)) {
                crawlFixedAnnuityPremium = "-1";                      //크롤링 해온 확정연금수령액
            } else {
                crawlFixedAnnuityPremium = (info.fixedAnnuityPremium.isEmpty()) ? "0" : info.fixedAnnuityPremium.replaceAll("[^0-9]", "");                      //크롤링 해온 확정연금수령액
            }

            logger.info("=================================================");
            logger.info("기존 보험료 : {}원", originInsMoney);
            logger.info("기존 만기환급금 : {}원", originExpMoney);
            logger.info("기존 종신연금수령액 : {}원", originAnnuityPremium);
            logger.info("기존 확정연금수령액 : {}원", originFixedAnnuityPremium);
            logger.info("=================================================");
            logger.info("크롤링 해온 보험료 : {}원", crawlInsMoney);
            logger.info("크롤링 해온 만기환급금 : {}원", crawlExpMoney);
            logger.info("크롤링 해온 종신연금수령액 : {}원", crawlAnnuityPremium);
            logger.info("크롤링 해온 확정연금수령액 : {}원", crawlFixedAnnuityPremium);
            logger.info("=================================================");

            planMonitoringStatus.setRecentlyInsMoney(Integer.parseInt(crawlInsMoney));
            planMonitoringStatus.setRecentlyExpMoney(BigInteger.valueOf(Long.parseLong(crawlExpMoney)));

            //기존 보험료와 비교했을 때의 보험료변동 여부값 세팅
            boolean isSameInsMoney = (Integer.parseInt(originInsMoney) == Integer.parseInt(crawlInsMoney));
            boolean isSameExpMoney = (Integer.parseInt(originExpMoney) == Integer.parseInt(crawlExpMoney));
            boolean isSameAnnMoney = true;

            String annuityType = info.annuityType;
            String category = info.category;

            if (Arrays.asList(annuityCategories).contains(category)) {
                //연금 상품인 경우에만
                if (annuityType.contains("종신")) {
                    isSameAnnMoney = (Integer.parseInt(originAnnuityPremium) == Integer.parseInt(crawlAnnuityPremium));
                    planMonitoringStatus.setRecentlyAnnMoney(BigInteger.valueOf(Long.parseLong(crawlAnnuityPremium)));
                } else if (annuityType.contains("확정")) {
                    isSameAnnMoney = (Integer.parseInt(originFixedAnnuityPremium) == Integer.parseInt(crawlFixedAnnuityPremium));
                    planMonitoringStatus.setRecentlyAnnMoney(BigInteger.valueOf(Long.parseLong(crawlFixedAnnuityPremium)));
                }
            }

            if (StringUtils.isEmpty(info.errorMsg)) {
                //TODO 여성전용상품, 특약? 처리해야함.
                //기존의 보험료 정보와 모두 일치하는 경우
                if (isSameInsMoney && isSameExpMoney && isSameAnnMoney) {
                    info.checkPriceYn = "N";            //가격변동 없음 (product.check_price_yn)
                    info.checkSiteYn = "Y";             //사이트 정상 (product.check_site_yn)
                    info.errorMsg = "";

                    logger.info("보험료 변동없음 : {}원 == {}원", originInsMoney, crawlInsMoney);
                    logger.info("만기환급금 변동없음 : {}원 == {}원", originExpMoney, crawlExpMoney);

                    //연금 상품인 경우에만 연금수령액 로그 출력
                    if (Arrays.asList(annuityCategories).contains(category)) {
                        if (annuityType.contains("종신")) {
                            logger.info("종신연금수령액 변동없음 : {}원 == {}원", originAnnuityPremium, crawlAnnuityPremium);
                        } else if (annuityType.contains("확정")) {
                            logger.info("확정연금수령액 변동없음 : {}원 == {}원", originFixedAnnuityPremium, crawlFixedAnnuityPremium);
                        }
                    }

                } else {
                    //기존의 보험료 정보와 하나라도 다른 경우
                    info.checkPriceYn = "Y";           //가격변동 있음 (product.check_price_yn)
                    info.checkSiteYn = "Y";             //사이트 정상 (product.check_site_yn)
                    info.errorMsg = "";

                    if (!isSameInsMoney) {
                        logger.info("보험료 변동 : {}원 >>>>> {}원", originInsMoney, crawlInsMoney);
                        planMonitoringStatus.setInsMoneyStatus("Y");        //보험료 변동 있음 (plan_monitoring_status.ins_money_status)
                    }

                    if (!isSameExpMoney) {
                        logger.info("만기환급금 변동 : {}원 >>>>> {}원", originExpMoney, crawlExpMoney);
                        planMonitoringStatus.setReturnMoneyStatus("Y");     //해약환급금 가격변동 있음 (plan_monitoring_status.return_money_status)
                        planMonitoringStatus.setExpMoneyStatus("Y");        //만기환급금 가격변동 있음 (plan_monitoring_status.exp_money_status)
                    }

                    if (!isSameAnnMoney) {
                        if (annuityType.contains("종신")) {
                            logger.info("종신연금수령액 변동 : {}원 >>>>> {}원", originAnnuityPremium, crawlAnnuityPremium);
                        } else if (annuityType.contains("확정")) {
                            logger.info("확정연금수령액 변동 : {}원 >>>>> {}원", originFixedAnnuityPremium, crawlFixedAnnuityPremium);
                        }
                        planMonitoringStatus.setAnnMoneyStatus("Y");        //연금수령액 가격변동 있음 (plan_monitoring_status.ann_money_status)
                    }

                }

                // 담보 비교
                try {
                    boolean comparePlanTreatyListResult = comparePlanTreatyList(info);
                } catch (Exception e) {
                    logger.error("comparePlanTreatyList");
                }

                //기존 해약환급금 테이블 정보와 원수사 해약환급금 테이블 정보를 비교한다.
                result = comparePlanReturnMoneyList(info);
                if (!result) {
                    info.checkPriceYn = "Y"; // 가격변동

                    info.getPlanMonitoringStatus().setReturnMoneyStatus("Y");         //가설별 모니터링 중 해약환급금 변동있음
                    throw new Exception("[**다시 크롤링 요망**]기존 해약환급금 테이블정보 ≠ 원수사 해약환급금 테이블정보");
                }

            } else {
                info.checkPriceYn = "N"; // 변동없음
                info.checkSiteYn = "N"; // 비정상

                info.getPlanMonitoringStatus().setSiteStatus("N");           //가설별 모니터링 중 사이트 비정상
            }

            result = true;

        } catch (Exception e) {
            e.printStackTrace();

            logger.error(e.getMessage());
        } finally {
            // 결과전송
            sendComparingResult(info);
            info.setErrorMsg("");
        }

        return result;
    }



    /**
     * treaty의 개수와 treatyName으로 비교를 한다.
     */
    private boolean comparePlanTreatyList(CrawlingProduct crawlingProduct) throws Exception {
        PlanTreatyComparer comparer = new PlanTreatyComparer(crawlingProduct);
        return comparer.comparePlanComposition();
    }



    /**
     * TODO 좀 더 테스트해서 갈아끼울 예정...
     *
     * @param originPlanAnnuityMoney 기존 연금수령액 테이블
     * @param crawlPlanAnnuityMoney  크롤링 해온 연금수령액 테이블
     * @return 두 연금수령액 테이블을 비교했을 때 완전히 일치하는지 여부 (true : 일치, false : 불일치)
     * @author 2022.05.03 조하연 기존의 연금수령액 테이블 정보와 크롤링 해온 연금수령액 테이블 정보를 비교한다.
     */
    private boolean comparePlanAnnuityMoney(PlanAnnuityMoney originPlanAnnuityMoney,
        PlanAnnuityMoney crawlPlanAnnuityMoney) {

        PlanComparer comparer = new PlanAnnuityMoneyComparer(originPlanAnnuityMoney, crawlPlanAnnuityMoney);
        return comparer.compare();
    }



    /**
     * TODO 좀 더 테스트해서 갈아끼울 예정
     *
     * @param originPlanReturnMoneyList 기존 해약환급금 테이블
     * @param crawlPlanReturnMoneyList  크롤링 해온 해약환급금 테이블
     * @return 두 해약환급금 테이블을 비교했을 때 완전히 일치하는지 여부 (true : 일치, false : 불일치)
     * @author 2022.05.03 조하연 기존의 해약환급금 테이블 정보와 크롤링 해온 해약환급금 테이블 정보를 비교한다.
     */
    private boolean comparePlanReturnMoneyList(List<PlanReturnMoney> originPlanReturnMoneyList,
        List<PlanReturnMoney> crawlPlanReturnMoneyList) {
        PlanComparer comparer = new PlanReturnMoneyComparer2(originPlanReturnMoneyList, crawlPlanReturnMoneyList);

        return comparer.compare();
    }



    /**
     * 모니터링 시 원수사의 해약환급금 정보와 기존의 해약환급금 정보를 비교한다.
     *
     * @param info
     * @return
     */
    private boolean comparePlanReturnMoneyList(CrawlingProduct info) {
        PlanComparer comparer = new PlanReturnMoneyComparer(info);

        return comparer.compare();
    }



    /**
     * 비교 결과
     *
     * @param result
     */
    protected void sendComparingResult(CrawlingProduct result) {

        // todo VO로 변경가능한지 확인할 것
        JsonObject _params = new JsonObject();
        _params.addProperty("checkSiteYn", result.checkSiteYn);
        _params.addProperty("checkPriceYn", result.checkPriceYn);
        _params.addProperty("checkPrice", result.totPremium);

        //상품 상태값 업데이트 api 호출(product 테이블의 상태값)
        crawlingApi.modifyCrawlingStatus(result.productCode, _params);
        logger.info("################## 상태 값 업데이트 완료 ##################");

        //가설별 모니터링 데이터 업데이트 api 호출(plan_monitoring_status 테이블의 상태값)
        PlanMonitoringStatus planMonitoringStatus = result.getPlanMonitoringStatus();
        planMonitoringStatus.setJobId(BigInteger.valueOf(0));

        boolean _result = false;
        _result = crawlingApi.modifyPlanMonitoringStatus(planMonitoringStatus);

        if (_result) {
            logger.info("################## 가설별 모니터링 데이터 업데이트 완료 ##################");
        }
    }



    /**
     * 상품마스터 비교 카운트 등록
     *
     * @param info
     */
    protected void updateProductMasterCount(CrawlingProduct info) {
        logger.debug("info.ProductMasterVO :: " + info.productMasterVOList);

        JsonObject sendData = new JsonObject();
        sendData.addProperty("checkProductTcount", info.siteProductMasterCount);
        sendData.addProperty("checkProductScount", info.treatyList.size());

        String sendDataString = sendData.toString();
        logger.debug("sendDataString :: " + sendDataString);
        crawlingApi.modifyProductMasterCount(productCode, sendData);
        logger.info("################## 상태 값 업데이트 완료 ##################");

    }



    private CrawlingTreaty getCrawlingTreaty(JsonObject planMapper, JsonObject planCalc) {
        CrawlingTreaty treaty = new CrawlingTreaty(); // caseProduct에 List로 포함되는 특약
        treaty.mapperId = planMapper.get("mapperId").getAsString(); // 특약id
        treaty.treatyName = planMapper.get("productMasterName").getAsString(); // 특약명
        treaty.productMasterId = planMapper.get("productMasterId").getAsString(); // 특약명
        treaty.planCalcId = planCalc.has("planCalcId") ? planCalc.get("planCalcId").getAsInt() : -1;

        treaty.setProductGubun(
            planMapper.get("productGubunNm").getAsString()); // 주계약, 고정부가특약, 선택특약 (mn, ms, sp)
        treaty.setInsTerm(
            planMapper.get("insTermNm").getAsString()); // 보험기간유형 ( 10년, 20년 ) from product(plan)
        treaty.setNapTerm(
            planMapper.get("napTermNm").getAsString()); // 납입기간유형 ( 10년, 전기납 ) from product(plan)
        treaty.setNapCycle(
            planMapper.get("napCycleNm").getAsString()); // 납입주기 (월납, 년납, 일시납)명 from product(plan)
        treaty.setAnnAge(planMapper.get("annuityAgeNm").getAsString()); // 연금개시나이명 from product(plan)
        treaty.setProductKind(
            planMapper.get("productKindNm").getAsString()); // 상품종류 (순수보장, 만기환급형 등) from product(plan)
        treaty.setProductType(
            planMapper.get("productTypeNm").getAsString()); // 상품타입 (갱신형, 비갱신형) from product(plan)
        treaty.assureMoney = Integer.parseInt(
            planMapper.get("assureMoneyNm").getAsString()); // 가입금액 (5000000, 10000000), 연금의 경우 월납입료

        treaty.setPlanCalc(
            new PlanCalc(
                Integer.parseInt(treaty.mapperId),
                planCalc.has("planCalcId") ? planCalc.get("planCalcId").getAsInt() : -1,
                planCalc.get("insAge").getAsInt(),
                planCalc.get("gender").getAsString()
            )
        );

        return treaty;
    }



    private boolean zeroRepair(CrawlingProduct item, int zero) {
        boolean _crawlingYn = true;
        if (zero > 0) {
            String _planIdTmp = item.getPlanId();
            String planCalcIdTmp = String.valueOf(item.getTreatyList().get(0).planCalcId);

            // 보험료 가져오기
            JsonObject planCalcMasterResult = (JsonObject) crawlingApi.getPlanCalc(_planIdTmp,
                planCalcIdTmp);
            JsonObject planCalcMaster = planCalcMasterResult.get("data").getAsJsonObject();

            // 총합 납입보험료
            //String insMoneySum = planCalcMaster.getString("insMoneySum");

            JsonArray planCalcList = planCalcMaster.get("planCalcList").getAsJsonArray();

            // repair1
            if (zero == 1) {
                for (int ri = 0; ri < planCalcList.size(); ri++) {
                    // 모든 특약 중 하나라도 납입보험료가 빈값이거나 "0"이 아닌 경우 크롤링 제외
                    // 모든 특약의 납입보험료가 빈값이거나 "0"이면 크롤링 대상
                    JsonObject planCalcJsonObject = planCalcList.get(ri).getAsJsonObject();
                    if (!("").equals(planCalcJsonObject.get("insMoney").getAsString().trim())
                        && !("0").equals(planCalcJsonObject.get("insMoney").getAsString().trim())) {
                        _crawlingYn = false;
                        break;
                    }
                }
            }

            // repair2
            if (zero == 2) {
                for (int ri = 0; ri < planCalcList.size(); ri++) {
                    // 모든 특약 중 하나라도 납입보험료와 만기 해약환급금이 빈값이거나 "0"이 아닌 경우 크롤링 제외
                    // 모든 특약의 납입보험료와 만기 해약환급금이 빈값이거나 "0"이면 크롤링 대상

                    JsonObject planCalcJsonObject = planCalcList.get(ri).getAsJsonObject();

                    if (!planCalcJsonObject.get("insMoney").getAsString().equals("")
                        && !planCalcJsonObject.get("insMoney").getAsString().equals("0")
                        && !planCalcJsonObject.get("expMoney").getAsString().equals("")
                        && !planCalcJsonObject.get("expMoney").getAsString().equals("0")) {
                        _crawlingYn = false;
                        break;
                    }
                }
            }

        }

        return _crawlingYn;
    }



    private CrawlingProduct getCaseProduct(String productCode, int planCount, int planMasterCount,
        int crawlCount, String categoryName, String productName, String productNamePublic, String saleChannel, int companyId,
        String siteWebUrl, String siteMobileUrl, JsonArray planMasters, JsonObject planMaster,
        JsonArray _planMappers, int _j) {

        CrawlingProduct caseProduct = new CrawlingProduct(productCode);

        for (int _k = 0; _k < _planMappers.size(); _k++) {

            JsonObject planMapper = _planMappers.get(_k).getAsJsonObject();
            JsonObject planCalc = planMapper.get("planCalcs").getAsJsonArray().get(_j).getAsJsonObject();

            int _planCalcId = planCalc.has("planCalcId") ? planCalc.get("planCalcId").getAsInt() : -1;
            int _age = Integer.parseInt(planCalc.get("insAge").getAsString());
            Birthday _birthDay = InsuranceUtil.getBirthday(_age);

            // 주계약인경우 값세팅
            if (_k == 0) {

                caseProduct.setCompanyId(companyId);
                caseProduct.categoryName = categoryName;
                caseProduct.planId = planMaster.get("planId").getAsString(); // by 우정
                caseProduct.planName = planMaster.get("planName").getAsString(); // by 우정
                caseProduct.planSubName = planMaster.get("planSubName").getAsString(); // by 용준
                caseProduct.mainYn = planMaster.get("mainYn").getAsString(); // by 용준
                caseProduct.textType = planMaster.has("textType") ? planMaster.get("textType").getAsString() : "";
                caseProduct.productCode = productCode;
                caseProduct.productName = productName;
                caseProduct.productNamePublic = productNamePublic;
                caseProduct.saleChannel = saleChannel;
                caseProduct.currentCrawlCount = ++crawlCount;
                caseProduct.totalCrawlCount = planCount;
                caseProduct.currentMasterCount = planMasterCount;
                caseProduct.totalMasterCount = planMasters.size();

                caseProduct.setCaseNumber(_planCalcId);
                caseProduct.setGender(planCalc.get("gender").getAsString().trim().equals("M") ? MALE : FEMALE);
                caseProduct.setAge(planCalc.get("insAge").getAsString());

                caseProduct.setInsuName(planMaster.get("planName").getAsString()); // planName과 겹친다.
                caseProduct.setBirth(_birthDay.getYear().substring(2, 4) + _birthDay.getMonth() + _birthDay.getDay());
                caseProduct.setFullBirth(_birthDay.getYear().substring(0, 4) + _birthDay.getMonth() + _birthDay.getDay());
                caseProduct.setParent_FullBirth(40);
                caseProduct.setParent_Birth(40);

                caseProduct.setDiscount(planMaster.get("discount").getAsString()); // 할인정보
                caseProduct.setPregnancyWeek(20);

                caseProduct.setInsTerm(planMapper.get("insTermNm").getAsString()); // 보험기간유형 ( 10년, 20년 ) to treaty
                caseProduct.setNapTerm(planMapper.get("napTermNm").getAsString()); // 납입기간유형 ( 10년, 전기납 ) to treaty
                caseProduct.setNapCycle(planMapper.get("napCycleNm").getAsString()); // 납입주기 (월납, 년납, 일시납)명 to
                caseProduct.setAnnuityAge(planMapper.get("annuityAgeNm").getAsString()); // 연금개시나이명 to treaty 실제사용?
                caseProduct.setAnnuityType(planMapper.get("annuityTypeNm").getAsString()); // 연금수령타입
                caseProduct.setProductKind(planMapper.get("productKindNm").getAsString()); // 상품종류 (순수보장, 만기환급형 등)
                caseProduct.setProductType(planMapper.get("productTypeNm").getAsString()); // 상품타입 (갱신형, 비갱신형) to
                caseProduct.setAssureMoney(planMapper.get("assureMoneyNm").getAsString()); // 가입금액 (5000000,
                // 10000000), 연금의 경우
                // 월납입료 to treaty 실제사용?

                caseProduct.setSiteWebUrl(siteWebUrl);
                caseProduct.setSiteMobileUrl(siteMobileUrl);

                caseProduct.setMinInsAge(planMaster.get("minInsAge").getAsInt());   // 가입설계 에서 정의한 최소 나이
                caseProduct.setMaxInsAge(planMaster.get("maxInsAge").getAsInt());   // 가입설계 에서 정의한 최대 나이
            }

            CrawlingTreaty treaty = getCrawlingTreaty(planMapper, planCalc);

            // 특약별 성별타입세팅
            treaty.setGenderType(planMapper.get("genderType").getAsString());
            caseProduct.treatyList.add(treaty);

        } // end of "for (int k = 0; k < planMappers.size(); k++)"

        return caseProduct;
    }



    /**
     * 크롤링 시작하기
     *
     * @param isMonitoring
     * @param zero
     * @param planCount
     * @param planMasterCount
     * @param item
     * @param vpnGroup
     * @param screenShot
     * @return
     */
    private int crawling(boolean isMonitoring, int zero, int planCount, int planMasterCount,
        CrawlingProduct item, VpnGroup vpnGroup, String screenShot) {

        int _isSuccess = 1;
        try {
            logger.debug(" 나이     : " + item.age + "세");
            logger.debug(" 성별 	  : " + (item.getGender() == MALE ? "남자" : "여자"));
            logger.debug(" 생년월일 : " + item.fullBirth);
            logger.debug(" 총특약수 : " + item.treatyList.size());
            logger.debug(" 가설아이디 : " + item.getPlanId());

            // 성별에 따라 특약을 다시 세팅해야한다.
            logger.debug("성별에 따라 특약을 다시 세팅");
            refineTreaties(item);

            // 특약개수가 너무 많아 상품등록할 때 특약이 중복으로 등록되는 경우가 많음
            logger.debug("특약 중복확인");
            duplicateCheckTreaties(item);

            int _mTotalCount = item.getTotalCrawlCount() * item.getTotalMasterCount();    // 총갯수
            int _mCurrentCount = crawlCount + ((planCount * planMasterCount) - planCount);
            float mRate = (_mCurrentCount * 100) / Float.valueOf(_mTotalCount);
            String _completeRate = String.format("%.2f", mRate);
            item.setCrawCompleteRate(_completeRate);

            logger.debug("###############################");
            logger.debug("총카운트    :: " + _mTotalCount);
            logger.debug("현재카운트 :: " + _mCurrentCount);
            logger.debug("진행률      :: " + _completeRate);
            logger.debug("###############################");

            logger.debug("특약 카운트 :: " + item.getTreatyList().size());

            // 크롤링 실행
            stopWatch.start();

            vpnSettings(item, vpnGroup);

            _isSuccess = doCrawlInsurance(item);

            stopWatch.stop();
            item.crawlingTime = (stopWatch.getTime() / 1000.0) + "초";

            // todo 아래 작업 method로 분리, 정리 필요

            int _monthlyPremium = 0;

            for (CrawlingTreaty _treaty : item.treatyList) {
                logger.info(" 특약이름   : " + _treaty.treatyName);
                logger.info(" 특약보험료 : " + _treaty.monthlyPremium);
                _monthlyPremium = _monthlyPremium + Integer.parseInt(ObjectUtils.isEmpty(_treaty.monthlyPremium) ? "0" : _treaty.monthlyPremium);
            }

            logger.info(" 해약환급금 : " + item.returnPremium);
            logger.info(" 연금수령액 : " + item.annuityPremium);
            logger.info(" 확정연금수령액 : " + item.fixedAnnuityPremium);
            logger.info(" 적립보험료 : " + item.savePremium);
            logger.info(" 보장보험료 : " + _monthlyPremium);

            // annuityType가 null이 아닐 경우 연금성으로 판단하며
            // info.expectSavePremium이 없을 경우 예상적립금을 쌓도록 한다.
            if ("".equals(item.expectSavePremium) && !"".equals(item.annuityType)){
                String expectSaveAge = String.valueOf(Integer.parseInt(item.annuityAge) - Integer.parseInt(item.age));

                for(PlanReturnMoney planReturnMoney : item.planReturnMoneyList) {
                    if(planReturnMoney.getTerm().contains(expectSaveAge+"년") ||planReturnMoney.getTerm().contains(expectSaveAge+"세")){
                        item.expectSavePremium = planReturnMoney.getReturnMoney().replaceAll("[^0-9]", "");
                    }
                }
            }
            logger.info(" 예상적립금 : " + item.expectSavePremium);

            if (!"".equals(item.savePremium)) {
                _monthlyPremium = _monthlyPremium + Integer.parseInt(item.savePremium);
            }

            item.totPremium = String.valueOf(_monthlyPremium);

            logger.info(" 총보험료   : " + _monthlyPremium);
            logger.info(" 소요시간   : " + item.crawlingTime);

            if (isMonitoring && zero == 0) {
                updateProductMasterCount(item);
                comparing(item);
            }

            // 결과 전송
            scraper.sendResult(item);
            String isScreenShot = getCommandOptions().getScreenShot();
            if ("Y".equals(isScreenShot)) { // 화면 스크린샷 설정시
                sendScreenShot(item);
            }


            // driver 시작 되었고 && 크롤링 실행 기준시간 이내였을 때, 지연시킨다.
            // 그리고 옵션설정 확인 (기본값은 true 임)
            // delayTime = false 로 변경 필요한경우 delayTime true 설정
            delayTime(isMonitoring, item.getCrawlingOption().isDelayTime());

        } catch (Exception e) {

            // 오류 정보 전송
            scraper.sendError(e, item);

        } finally {
            stopWatch.reset();
        }

        return _isSuccess;
    }



    private void duplicateCheckTreaties(CrawlingProduct item) {
        for (int _p = 0; _p < item.treatyList.size(); _p++) {
            String _name = item.treatyList.get(_p).treatyName;
            for (int _t = 0; _t < item.treatyList.size(); _t++) {
                if (_p == _t) {
                    continue;
                }
                if (_name.equals(item.treatyList.get(_t).treatyName)) {
                    logger.info("중복으로 등록된 특약명 : " + item.treatyList.get(_t).treatyName);
                }
            }
        }
    }



    private void refineTreaties(CrawlingProduct item) {

        List<CrawlingTreaty> removeList = new ArrayList<>();

        for (CrawlingTreaty treaty : item.treatyList) {
            logger.info(" 특약이름 : " + treaty.treatyName + " | 특약별 성별 :: " + treaty.getGenderType());

            if (treaty.productGubun == CrawlingTreaty.ProductGubun.주계약) {
                logger.info(" - 보험금    : " + treaty.assureMoney);
                logger.info(" - 보험기간 : " + treaty.insTerm);
                logger.info(" - 납입기간 : " + treaty.napTerm);
            }

            // 여기에서 입력받은 성별에 따라 특약을 분리한다.
            if (item.getGender() == MALE) {
                // 남자일경우 > 여자전용특약(F) 제외
                if (treaty.getGenderType().equals("F")) {
                    removeList.add(treaty);
                }
            } else {
                // 여자일 경우 > 남자전용특약(M) 제외
                if (treaty.getGenderType().equals("M")) {
                    removeList.add(treaty);
                }
            }
        }

        // 남녀구분에 따른 필요없는 특약 제거
        item.treatyList.removeAll(removeList);
    }



    /**
     * vpn세팅하기
     *
     * @param item
     * @param vpnGroup
     */
    private void vpnSettings(CrawlingProduct item, VpnGroup vpnGroup) {
        if (vpnGroup != null) {

            logger.debug("location: {}", String.join(",", vpnGroup.section.countries));

            if ("mudfish".equals(vpnGroup.section.vpn)) {
                CrawlingOption crawlingOption = item.getCrawlingOption();
                crawlingOption.setVpn(
                    new ChromeMudfishVpn(HostUtil.getUsername(), vpnGroup.section.countries));
            }

            if ("mudfishproxy".equals(vpnGroup.section.vpn)) {
                CrawlingOption crawlingOption = item.getCrawlingOption();
                crawlingOption.setVpn(
                    new ChromeMudfishProxy(HostUtil.getUsername(), vpnGroup.section.countries));
            }

            if ("shark".equals(vpnGroup.section.vpn)) {
                CrawlingOption crawlingOption = item.getCrawlingOption();
                crawlingOption.setVpn(new ChromeSurfSharkVpn(vpnGroup.section.countries));
            }

            if ("tor".equals(vpnGroup.section.vpn)) {
                CrawlingOption crawlingOption = item.getCrawlingOption();
                crawlingOption.setVpn(new Tor());
            }
        }
    }



    /**
     * 스크린샷 등록하기
     *
     * @param info
     */
    private void sendScreenShot(CrawlingProduct info) {

        int monthlyPremium = Integer.parseInt(info.treatyList.get(0).monthlyPremium);
        int savePremium = StringUtil.isEmpty(info.savePremium) ? 0 : Integer.parseInt(info.savePremium);
        //null 이나 ""이면 false 를 return
        String premium = String.valueOf(monthlyPremium + savePremium);
        crawlScreenShot.setPremium(premium);

        crawlingApi.registProductCrawlingScreenShot(crawlScreenShot);
    }



    private int execute(String productCode, boolean isMonitoring, Integer planId,
        List<Integer> ages, String gender, int zero, VpnGroup vpnGroup, String site,
        String screenShot) {

//        boolean result = false;
        int result = 1;

        JsonObject data = null;

        CrawlingProduct caseProduct = new CrawlingProduct();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = dayTime.format(new Date(System.currentTimeMillis()));

        logger.info("###########################");
        logger.info("########## Start ##########");
        logger.info("###########################");

        try {

            logger.info("크롤링 시작!");
            logger.debug("planId :: {}", planId);
            scraper.start(productCode);

            logger.info("크롤링 상품정보조회!");
            JsonObject _result = scraper.getProductData(isMonitoring, zero, planId, ages,
                gender, productCode,
                screenShot);

            logger.debug("크롤링 상품정보: {}", _result.toString());

            logger.info("크롤링 상품정보 확인!");
            if (!_result.has("data")) {
                throw new NotFoundProductData("해당하는 상품정보(" + productCode + ")가 없습니다.");
            }

            logger.info("크롤링 상품정보 세팅!");
            JsonObject _data = _result.get("data").getAsJsonObject();

            logger.debug("_data :: {}", _data.toString());
            int _totalCalcCount = _data.get("totalCalc").getAsInt();
            int _totalMapperCount = _data.get("totalMapper").getAsInt();

            int _planCount = 0;
            if (_totalMapperCount != 0) {
                _planCount =
                    _totalCalcCount / _totalMapperCount; // 같은 나이와 젠더값을 갖는 가설 row에 해당하는 plan의 갯수
            }
            int _planMasterCount = 1; // 현재 크롤링 진행 중인 설계의 순서
            int _crawlCount = 0;

            JsonObject _productObj = _data.get("product").getAsJsonObject();

            logger.debug("_productObj:: {}" ,_productObj );
            String _categoryName = _productObj.get("categoryName").getAsString();
            String _productName = _productObj.get("productName").getAsString();
            String _productNamePublic = _productObj.get("productNamePublic").getAsString();
            String _saleChannel = _productObj.get("saleChannel").getAsString();
            int _companyId = _productObj.get("company").getAsJsonObject().get("companyId").getAsInt();
            String _siteWebUrl = _productObj.get("siteWebUrl").getAsString();
            String _siteMobileUrl = _productObj.get("siteUrl").getAsString();
            String _category = _productObj.get("category").getAsString();

            JsonArray _planMasters = _data.get("planMasters").getAsJsonArray();

            logger.debug("_planMasters:: {}" ,_planMasters );
            // 가입설계가 없으면 상품마스터 등록이 안되었거나,
            // 상품마스터 등록만하고 가설을 안한경우임
            // 모니터링시 상품마스터를 가져오기위해 임으로 실행가능한 데이터를 세팅한다.
            if (_planMasters.size() == 0) {
                if (isMonitoring) {
                    logger.info("***대표가설이 존재하지 않습니다***");
                }

                throw new NotFoundPlanMasterException("상품의 가설이 존재하지 않습니다.");
            }

            // default
            crawlUrl = _productObj.get("crawUrl").getAsString();

            // 크롤링 URL 종류를 가져온다.
            String crawMethod = _productObj.get("crawMethod").getAsString();
            if (crawMethod.equals("CD00082")) {
                crawlUrl = _productObj.get("siteWebUrl").getAsString();
            } else if (crawMethod.equals("CD00083")) {
                crawlUrl = _productObj.get("siteUrl").getAsString();
            }

            logger.info("크롤링 상품의 가설정보 세팅!");
            for (int i = 0; i < _planMasters.size(); i++) {

                JsonObject _planMaster = _planMasters.get(i).getAsJsonObject();

                logger.info("planName : " + _planMaster.get("planName").getAsString());
                logger.debug("crawUrl : " + crawlUrl);

                // planCalcs를 가져오기 위해 (planCalcs는 planMapper마다 같은 수 할당)
                JsonArray _planMappers = _planMaster.get("planMappers").getAsJsonArray();
                JsonObject _planMapperData = _planMappers.get(0).getAsJsonObject();
                JsonArray _planCalcs = _planMapperData.get("planCalcs").getAsJsonArray();

                logger.debug("_planMappers : " + _planMappers);
                logger.debug("_planCalcs : " + _planCalcs);

                if (isMonitoring) {
                    // 오류 확인
                    if (_totalMapperCount * _planCount != _totalCalcCount) {
                        logger.info("@@@@@@@ totalCount : " + _totalCalcCount);
                        logger.info("@@@@@@@ mapperCount : " + _totalMapperCount);
                        logger.info("@@@@@@@ planCount : " + _planCount);

                        throw new WrongPlanCountException("상품에 대한 설계정보와 총 가입설계의 갯수가 같지 않습니다.");
                    }
                }

                // 가설ID로 모니터링하는 경우, 해당하는 성별만 남긴다.
                // 기본적인 성별은 남자('M')
                logger.debug("_planCalcs size0 :: {}", _planCalcs.size());
                if (isMonitoring && planId != null && ages.size() > 0) {
                    for (int j = 0; j < _planCalcs.size(); j++) {
                        JsonObject _planCalc = (JsonObject) _planCalcs.get(j);
                        if (!gender.equals(_planCalc.get("gender").getAsString())) {
                            _planCalcs.remove(j);
                        }
                    }
                }

                // 가설(planCalcs) 순서로 CrawlingProduct 재배열하기 위한 객체
                List<CrawlingProduct> _caseListPerPlan = new ArrayList<>();

                logger.debug("_planCalcs size1 :: {}", _planCalcs.size());
                for (int j = 0; j < _planCalcs.size(); j++) {

                    caseProduct = getCaseProduct(
                        productCode, _planCount, _planMasterCount,
                        _crawlCount, _categoryName, _productName,
                        _productNamePublic, _saleChannel, _companyId,
                        _siteWebUrl, _siteMobileUrl, _planMasters,
                        _planMaster, _planMappers, j
                    );
                    caseProduct.setCategory(_category);
                    _caseListPerPlan.add(caseProduct);
                }

                logger.info("가설의 케이스리스트 개수 :: " + _caseListPerPlan.size());

                // 크롤링 실행
                for (CrawlingProduct _item : _caseListPerPlan) {
                    boolean _crawlingYn = true;

                    // repair 처리
                    _crawlingYn = zeroRepair(_item, zero);
                    if (_crawlingYn) {
                        result = crawling(isMonitoring, zero, _planCount, _planMasterCount, _item,
                            vpnGroup, screenShot);

                    }

                } // end of "for (CrawlingProduct item : caseListPerPlan)"

                ++_planMasterCount;
            } // end of "for (int i = 0; i < planMasters.size(); i++)"

        } catch (Exception e) {

            result = 1;

            // 크롤링(모니터링) 오류 전송
            scraper.sendError(e, caseProduct);

        } finally {
            // 크롤링 결과 log
            // 크롤링 결과에 상관없이 크롤링 완료 여부는 호출하도록 변경함
            // chunone 2021.5.3
            logger.info("크롤링 완료!");

            // 크롤링(모니터링) 시작
            scraper.finish(productCode);

            logger.info("크롤링시작 시간 :: " + startTime);
            String endTime = dayTime.format(new Date(System.currentTimeMillis()));
            logger.info("크롤링종료 시간 :: " + endTime);
        }

        return result;
    }



    private int getRandomTime() {
        Random rand = new Random();
        return CRAWL_STANDARD_TIME_LIST[(rand.nextInt(CRAWL_STANDARD_TIME_LIST.length))];
    }



    // driver 시작 되었고 && 크롤링 실행 기준시간 이내였을 때, 지연시킨다.
    // 그리고 옵션설정 확인 (기본값은 true 임)
    // delayTime = false 로 변경 필요한경우 delayTime true 설정
    private void delayTime(boolean isMonitoring, boolean isDelayTime) throws InterruptedException {
        if (isDelay(isMonitoring, isDelayTime)) {
            double diff = (60.0 - (stopWatch.getTime() / 1000.0));
            int delayTime = Double.valueOf(diff).intValue();
            logger.info("지연시간 : " + delayTime + " 초");
            WaitUtil.waitFor(delayTime);
        } else {
            logger.info("지연시간 없음");
        }
    }



    /**
     * 크롤링 시간 지연 여부
     *
     * @param isMonitoring
     * @param isDelayTime
     * @return
     */
    private boolean isDelay(boolean isMonitoring, boolean isDelayTime) {
        int randomTime = getRandomTime();
        logger.info("크롤링 실행 기준시간: {} 초", randomTime);

        boolean rtnVal = false;

        rtnVal = (isStarted && (stopWatch.getTime() / 1000.0) < randomTime
            && isDelayTime)
            && !isMonitoring;

        return rtnVal;
    }



    /**
     * 로그 출력 및 비교
     *
     * ex)
     * params
     * (title = 생년월일, expected = 961109, actual = 991212)
     *
     * [출력결과]
     * ==========================================
     * expected 생년월일 : 961109
     * actual 생년월일 : 991212
     * ==========================================
     *
     * @param title 항목명
     * @param expected 세팅할 값
     * @param actual 실제 세팅된 값
     * @throws Exception
     */
    protected void printLogAndCompare(String title, String expected, String actual) throws Exception {
        logger.info("==========================================");
        logger.info("expected {} : {}", title, expected);
        logger.info("actual   {} : {}", title, actual);
        logger.info("==========================================");

        if(expected.equals(actual)) {
            logger.info("result : {} 일치", title);
            logger.info("==========================================");

        } else {
            throw new Exception(title + " 값 불일치");
        }
    }

    public void logging(String msg) {
        logger.info(getProductCode() + "  ::I::  " + msg);
    }

    public void debugging(String msg) {
        logger.debug(getProductCode() + "  ::D::  " + msg);
    }
}
