package com.welgram.crawler.direct.life.ail;

import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.HashMap;
import java.util.Map;



// 2023.11.09 | 최우진 | 2형, 30세설계, 10년납, 1억,5%체증
public class AIL_WLF_F008 extends CrawlingAILAnnounce {

    public static void main(String[] args) { executeCommand(new AIL_WLF_F008(), args); }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        return true;
    }
}
