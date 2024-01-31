package com.welgram.common.except;

import com.welgram.crawler.general.CrawlingTreaty;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//특약 정보가 일치하지 않다는 예외 클래스
public class TreatyMisMatchException extends Exception {

    public final static Logger logger = LoggerFactory.getLogger(TreatyMisMatchException.class);

    public TreatyMisMatchException() {
        super("특약이 일치하지 않습니다");
//        api.req(errInfo);
    };

    public TreatyMisMatchException(String msg) {
        super(msg);
    }

    // 2022.06.29 | 최우진 | #idea01 분기처리가 완료된 경우에 예외처리를 진행합니다 (코드가 길어짐, getOrgData 작성필요 x) - 우선은 1안 사용
    // 2022.06.29 | 최우진 | #idea02 예외를 먼저받고 필요한 분기처리를 진행합니다   (코드가 짧아짐, getOrgData 작성필요 o)
    // 2022.07.07 | 최우진 | 일단 중단..
    // 2022.08.24 | 최우진 | 분기처리 말고 패턴으로 바꿀 것

    // 특약 개수 불일치
    public TreatyMisMatchException(Map<String, String> orgData, List<CrawlingTreaty> welgramData) {
        List<String> orTrtNameList = new ArrayList<>(orgData.keySet());
        List<String> welgramTrtNameList = new ArrayList<>();
        welgramData.forEach(trt -> welgramTrtNameList.add(trt.treatyName));

        boolean welInOrg = welgramTrtNameList.containsAll(orTrtNameList);
        boolean orgInWel = orTrtNameList.containsAll(welgramTrtNameList);

//        List<String> beErasedTreatyNameList = new ArrayList<>();
//        List<String> beAddedTreatyNameList = new ArrayList<>();
//        List<String> beMaintainedTreatyNameList = new ArrayList<>();

        // 특약 개수 비교 | 불일치
        if(orgData.size() != welgramData.size()) {
            logger.info("특약의 개수가 일치하지 않습니다.");
            if(orgData.size() > welgramData.size()) {
                orTrtNameList.removeAll(welgramTrtNameList);
                logger.info("===================================");
                logger.info("가입설계에 추가해야할 특약 리스트 입니다");
                orTrtNameList.forEach(trtName -> {
                    logger.info(trtName);
//                    beAddedTreatyNameList.add(trtName);
                });
                logger.info("================================");

            } else {
                welgramTrtNameList.removeAll(orTrtNameList);
                logger.info("================================");
                logger.info("가입설계에서 삭제해야할 리스트 입니다");
                welgramTrtNameList.forEach(trtName -> {
                    logger.info(trtName);
//                    beErasedTreatyNameList.add(trtName);
                });
                logger.info("================================");
            }
        }
//        else {
//            if(welInOrg && orgInWel) {
//                // 특약금액 비교
//                List<CrawlingTreaty> welData = welgramData;
//                Map<String, String> amtUnmatchMap = new HashMap<>();
//                welData.forEach(trt -> {
//                    if (orgData.get(trt.treatyName).equals(String.valueOf(trt.assureMoney))) {
//                        logger.info("특약 [ " + trt.treatyName +  " ]의 특약금액이 일치합니다");
//                        logger.info("원수사 : " + orgData.get(trt.treatyName));
//                        logger.info("웰그램 : " + trt.assureMoney);
//                    } else {
//                        amtUnmatchMap.put(trt.treatyName, String.valueOf(trt.assureMoney));
//                    }
//                });
//            } else if(!welInOrg) {
//                // 웰그램 > 원수사 (삭제리스트)
//                welgramTrtNameList.removeAll(orTrtNameList);
//                logger.info("================================");
//                logger.info("가입설계에서 삭제해야할 리스트 입니다");
//                welgramTrtNameList.forEach(trtName -> {
//                    logger.info(trtName);
////                    beErasedTreatyNameList.add(trtName);
//                });
//                logger.info("================================");
//            } else {
//                // 웰그램 < 원수사 (추가리스트)
//                orTrtNameList.removeAll(welgramTrtNameList);
//                logger.info("===================================");
//                logger.info("가입설계에 추가해야할 특약 리스트 입니다");
//                orTrtNameList.forEach(trtName -> {
//                    logger.info(trtName);
////                    beAddedTreatyNameList.add(trtName);
//                });
//                logger.info("================================");
//            }
//        }
    }

    // 특약명 불일치
    // todo | 전체 특약 리스트 알리는 것이 아닌, 불일치 특약의 이름만 알리는 기능
    public TreatyMisMatchException(List<String> unmatchedNameList) {
        logger.info(unmatchedNameList + "과 같은 특약은 존재하지 않습니다");
    }

    // 특약금액 불일치
    // todo | 전체특약의 금액리스트를 알리는 것이 아닌, 불일치 특약의 금액만 알리는 기능
    public TreatyMisMatchException(Map<String, String> unmatchedAmtMap) {
        logger.info("[ "+ unmatchedAmtMap.keySet().toArray()[0] + " ] 의 특약금액(" + unmatchedAmtMap.get(""+unmatchedAmtMap.keySet().toArray()[0]) + ") 이 일치 하지 않습니다.");
//        unmatchedAmtMap.forEach((trtName, trtAmt) -> logger.info("["+ trtName + "] 의 특약금액(" + trtAmt + ") 이 일치 하지 않습니다."));
    }

}
