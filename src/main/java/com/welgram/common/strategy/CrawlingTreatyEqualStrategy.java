package com.welgram.common.strategy;

import com.welgram.crawler.general.CrawlingTreaty;
import java.util.function.Function;

public interface CrawlingTreatyEqualStrategy {

    boolean isEqual(CrawlingTreaty ct1, CrawlingTreaty ct2);

    void printInfo(CrawlingTreaty ct);

    void printDifferentInfo(CrawlingTreaty asIs, CrawlingTreaty toBe);

}
