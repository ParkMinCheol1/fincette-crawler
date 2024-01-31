package com.welgram.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Supplier;

public final class RetryUtil {

    public final static Logger logger = LoggerFactory.getLogger(RetryUtil.class);
    /**
     * Retry to run a function a few times, retry if specific exceptions occur.
     *
     * @param timeoutExceptionClasses what exceptions should lead to retry. Default: any exception
     */
    public static <T> T retry(Supplier<T> function, int maxRetries, Class<? extends Exception>... timeoutExceptionClasses) {
        timeoutExceptionClasses = timeoutExceptionClasses.length == 0 ? new Class[]{Exception.class} : timeoutExceptionClasses;
        int retryCounter = 0;
        Exception lastException = null;
        while (retryCounter < maxRetries) {
            try {
                return function.get();
            } catch (Exception e) {
                lastException = e;
                if (Arrays.stream(timeoutExceptionClasses).noneMatch(tClass ->
                        tClass.isAssignableFrom(e.getClass())
                ))
                    throw lastException instanceof RuntimeException ?
                            ((RuntimeException) lastException) :
                            new RuntimeException(lastException);
                else {
                    retryCounter++;
                    System.err.println("FAILED - Command failed on retry " + retryCounter + " of " + maxRetries);
                    e.printStackTrace();
                    if (retryCounter >= maxRetries) {
                        break;
                    }
                }
            }
        }
        throw lastException instanceof RuntimeException ?
                ((RuntimeException) lastException) :
                new RuntimeException(lastException);
    }

    /** Manual test method */
    public static void main(String... args) throws Exception {
        retry(() -> {
            System.out.println((5 / 0));
            return null;
        }, 5, Exception.class);
    }
}