package com.welgram.crawler.direct.fire.sfi.common;

public enum PlanState {

    필수가입("필수가입"),    // 주계약
    가입("가입"),          // 가입가능 - 가입된 상태
    미가입("미가입"),       // 가입가능 - 미가입 상태
    가입불가("-");         // 해당 가설에 추가가 불가능한 특약을 뜻함

    final String desc;

    PlanState(String desc) {
        this.desc = desc;
    }
}
