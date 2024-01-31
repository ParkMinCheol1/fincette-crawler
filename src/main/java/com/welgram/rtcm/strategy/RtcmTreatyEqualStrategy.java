package com.welgram.rtcm.strategy;

import com.welgram.rtcm.vo.RtcmTreatyVO;

public interface RtcmTreatyEqualStrategy {

    boolean isEqual(RtcmTreatyVO t1, RtcmTreatyVO t2);

    void printInfo(RtcmTreatyVO t);

    void printDifferentInfo(RtcmTreatyVO asIs, RtcmTreatyVO toBe);

}
