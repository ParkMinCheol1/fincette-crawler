package com.welgram.crawler.cli.excutor.config;

import java.util.HashMap;
import java.util.Map;

/**
 * VPN과 관련된 설정
 */
public class VpnConfig {
    int testCode = 0;
    public static final String[] DEFAULT_COUNTRY_CODES = new String[]{"KR"};
    private static final Map<String, String[]> COUNTRY_CODE_BY_PRODUCT_CODES = new HashMap<>();

    /**
     * 상품코드 별로 국가코드를 지정한다.
     *
     * [국가코드]
     *
     * AR	아르헨티나
     * AE	아랍에미리트
     * AU	호주
     * BE	벨기에
     * BG	불가리아
     * BZ	벨리즈
     * CA	캐나다
     * DE	독일
     * ES	스페인
     * FI	핀란드
     * FR	프랑스
     * HK	홍콩
     * ID	인도네시아a
     * IN	인도
     * IT	이탈리아
     * JP	일본
     * KR	대한민국
     * LT	리투아니아
     * LU	룩셈부르크
     * MD	몰도바
     * MY	말레이시아
     * NL	네덜란드
     * PH	필리핀
     * PL	폴란드
     * PT	포르투갈
     * RO	루마니아
     * RU	러시아
     * SE	스웨덴
     * SG	싱가포르
     * TH	태국
     * TR	터키
     * TW	대만
     * UA	우크라이나
     * UK	영국
     * US	미국
     * VN	베트남
     * ZA	남아프리카공화국
     */
    static {

        COUNTRY_CODE_BY_PRODUCT_CODES.put("IBK_ANT_D001", new String[]{"VN","US","UK","UA","TW","TR","TH","SG","SE","RU","PL","PH","NL","MD","LU","LT","IT","ID","HK","FR","ES","DE","CA","BZ","BG","BE","KR","JP","AR","AE"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("IBK_ANT_D004", new String[]{"VN", "TW", "JP", "KR", "HK"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("IBK_ASV_D001", new String[]{"VN","US","UK","UA","TW","TR","TH","SG","SE","RU","PL","PH","NL","MD","LU","LT","IT","ID","HK","FR","ES","DE","CA","BZ","BG","BE","KR","JP","AR","AE"});
// VN TW JP KR SG HK
        // ABL ( ABL생명 )
        // --> ZA :: 접속 불가
        COUNTRY_CODE_BY_PRODUCT_CODES.put("ABL_ASV_D001", new String[]{"VN","US","UK","UA","TW","TR","TH","SG","SE","RU","PL","PH","NL","MD","LU","LT","IT","ID","HK","FR","FI","ES","DE","CA","BZ","BG","BE","AU","KR","JP","AR","AE"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("ABL_DTL_D001", new String[]{"VN","US","UK","UA","TW","TR","TH","SG","SE","RU","PL","PH","NL","MD","LU","LT","IT","ID","HK","FR","FI","ES","DE","CA","BZ","BG","BE","AU","KR","JP","AR","AE"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("ABL_SAV_D003", new String[]{"VN","US","UK","UA","TW","TR","TH","SG","SE","RU","PL","PH","NL","MD","LU","LT","IT","ID","HK","FR","FI","ES","DE","CA","BZ","BG","BE","AU","KR","JP","AR","AE"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("ABL_SAV_D001", new String[]{"VN","US","UK","UA","TW","TR","TH","SG","SE","RU","PL","PH","NL","MD","LU","LT","IT","ID","HK","FR","FI","ES","DE","CA","BZ","BG","BE","AU","KR","JP","AR","AE"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("ABL_TRM_D001", new String[]{"VN","US","UK","UA","TW","TR","TH","SG","SE","RU","PL","PH","NL","MD","LU","LT","IT","ID","HK","FR","FI","ES","DE","CA","BZ","BG","BE","AU","KR","JP","AR","AE"});

        // HWL ( 한화생명 ) - 2021.04.16 :: VPN error로 인해서 KR만 남겨놓았습니다.
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_ANT_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_ASV_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_CCR_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_DSS_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_DSS_D002", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_MCC_D002", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_MDC_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_SAV_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWL_TRM_D001", new String[]{"KR"});

        // LIN ( 라이나생명 )
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_CCR_D001", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_CCR_D002", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_CHL_D001", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_DTL_D001", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_DTL_D002", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_TRM_D001", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_TRM_D002", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LIN_DSS_D001", new String[]{"KR", "JP"});

        // HKL  ( 흥국생명 ) - 2021.04.16 :: VPN error로 인해서 KR만 남겨놓았습니다.
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HKL_ASV_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HKL_ACD_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HKL_CHL_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HKL_CHL_D002", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HKL_MSV_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HKL_TRM_D001", new String[]{"KR"});

        // LTF ( 롯데손해보험 )
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LTF_ASV_D001", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LTF_CCR_D001", new String[]{"KR", "JP"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("LTF_DMT_D001", new String[]{"KR", "JP"});

        //AIG손해보험
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DRV_D004", new String[]{"AR", "AE", "AU", "BE", "BG", "BZ", "CA", "DE", "ES", "FI", "FR", "HK", "ID", "IN", "IT", "JP", "KR", "LU", "MD", "MY", "NL", "PH", "PL", "PT", "RO", "RU", "TR", "TW", "UA", "UK", "US", "VN", "ZA"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D008", new String[]{"AR", "AE", "AU", "BE", "BG", "BZ", "CA", "DE", "ES", "FI", "FR", "HK", "ID", "IN", "IT", "JP", "KR", "LU", "MD", "MY", "NL", "PH", "PL", "PT", "RO", "RU", "TR", "TW", "UA", "UK", "US", "VN", "ZA"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D009", new String[]{"AR", "AE", "AU", "BE", "BG", "BZ", "CA", "DE", "ES", "FI", "FR", "HK", "ID", "IN", "IT", "JP", "KR", "LU", "MD", "MY", "NL", "PH", "PL", "PT", "RO", "RU", "TR", "TW", "UA", "UK", "US", "VN", "ZA"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D011", new String[]{"AR", "AE", "AU", "BE", "BG", "BZ", "CA", "DE", "ES", "FI", "FR", "HK", "ID", "IN", "IT", "JP", "KR", "LU", "MD", "MY", "NL", "PH", "PL", "PT", "RO", "RU", "TR", "TW", "UA", "UK", "US", "VN", "ZA"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D006", new String[]{"AR", "AE", "AU", "BE", "BG", "BZ", "CA", "DE", "ES", "FI", "FR", "HK", "ID", "IN", "IT", "JP", "KR", "LU", "MD", "MY", "NL", "PH", "PL", "PT", "RO", "RU", "TR", "TW", "UA", "UK", "US", "VN", "ZA"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_OST_D001", new String[]{"AR", "AE", "AU", "BE", "BG", "BZ", "CA", "DE", "ES", "FI", "FR", "HK", "ID", "IN", "IT", "JP", "KR", "LU", "MD", "MY", "NL", "PH", "PL", "PT", "RO", "RU", "TR", "TW", "UA", "UK", "US", "VN", "ZA"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DRV_D004", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D008", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D009", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D011", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_DSS_D006", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("AIG_OST_D001", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW"});

        //DGB생명
        COUNTRY_CODE_BY_PRODUCT_CODES.put("DGL_MAC_D001", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("DGL_TRM_D005", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});


        //한화손해보험
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWF_CCR_D001", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWF_CHL_D004", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWF_MDC_D001", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWF_MDC_F001", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("HWF_DRV_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});


        //미래에셋생명
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_CCR_D005", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_DSS_D005", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_DSS_D003", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_CCR_D004", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_CCR_D006", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_MDC_D001", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
//        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_TRM_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_CCR_D005", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_DSS_D005", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_DSS_D003", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_CCR_D004", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_CCR_D006", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_MDC_D001", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("MRA_TRM_D002", new String[]{"KR"});


        //우체국보험
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_ACD_D005", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_ACD_D006", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_ACD_D007", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_ANT_D002", new String[]{"KR"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_ASV_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_BAB_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_CCR_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_DSS_D004", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_CHL_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_DSS_D001", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_DSS_D005", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_DTL_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_SAV_D005", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_SAV_D009", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_SAV_D007", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_SAV_D008", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});
        COUNTRY_CODE_BY_PRODUCT_CODES.put("PST_TRM_D002", new String[]{"HK", "ID", "IN", "JP", "KR", "SG", "TH", "TW", "VN"});


        //동양생명
        COUNTRY_CODE_BY_PRODUCT_CODES.put("TYL_SAV_D001", new String[]{"KR"});

        COUNTRY_CODE_BY_PRODUCT_CODES.put("KDB_CCR_D001", new String[]{"US"});
    }

    /**
     * 상품코드에 해당하는 국가코드리스트를 반환한다.
     * 해당하는 상품코드가 없다면, 기본 KR(대한민국)을 반환한다.
     *
     * @param productCode
     * @return
     */
    public static String[] getVpnCountries(String productCode) {
        return COUNTRY_CODE_BY_PRODUCT_CODES.containsKey(productCode) ? COUNTRY_CODE_BY_PRODUCT_CODES.get(productCode) : DEFAULT_COUNTRY_CODES;
    }
}
