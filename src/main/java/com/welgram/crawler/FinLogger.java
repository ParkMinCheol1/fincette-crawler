package com.welgram.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/** todo | 꼭 읽어주세요!!
 Logger 인스턴스를 호출시 String타입으로 만드는 경우,
 logback에 설정해둔 로그레벨로 적용이 되지 않습니다.
 사용시 인스턴스를 가져올때 "클래스"로 사용해야 합니다.
*/

// 2023.03.17 | 최우진 | 로그확인시 편의성(상품코드확인 용이)위한 양식 통일
public class FinLogger {

    private static FinLogger fLogger;

    private String productCode;
    private Logger logger;

    private FinLogger(Class clazz) {
        this.productCode = clazz.getSimpleName();
        this.logger = LoggerFactory.getLogger(clazz);
    }



// todo | String type 은 에러가 있습니다. 우선은 사용하지 마세요...
//
//    public static FinLogger getFinLogger(String productCode) {
//
//        if(fLogger == null) {
//            System.out.println("NEW FIN_LOGGER BY STRING");
//            fLogger = new FinLogger(productCode);
//        }
//
//        return fLogger;
//    }



    public static FinLogger getFinLogger(Class clazz) {

        if(fLogger == null) {
            System.out.println("NEW FIN_LOGGER BY CLASS");
            fLogger = new FinLogger(clazz);
        }

        return fLogger;
    }



// todo | elastic msg 형식은 테스트 해보고 바꾸기
    public void warn(String msg) { logger.warn("[{}] | {}", productCode, msg); }

    public void warn(String fmt, Object... msg) { logger.warn("[" + productCode + "] | " + fmt, msg); }

    public void info(String msg) { logger.info("[{}] | {}", productCode, msg); }

    public void info(String fmt, Object... msg) { logger.info("[" + productCode + "] | " + fmt, msg); }

    public void debug(String msg) { logger.debug("[{}] | {}", productCode , msg); }

    public void debug(String fmt, Object... msg) { logger.debug("[" + productCode + "] | " + fmt, msg); }

    public void error(String msg) { logger.error("[{}] | {}", productCode , msg); }

    public void error(String fmt, Object... msg) { logger.error("[" + productCode + "] | " + fmt, msg); }
}
