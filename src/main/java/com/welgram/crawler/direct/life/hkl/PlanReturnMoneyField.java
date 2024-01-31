package com.welgram.crawler.direct.life.hkl;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class PlanReturnMoneyField {

    private PlanReturnMoneyFieldEnum fieldEnum;

    private String tdText;

    private int tdIndex;



    public PlanReturnMoneyField(PlanReturnMoneyFieldEnum fieldEnum, String tdText) {
        this.fieldEnum = fieldEnum;
        this.tdText = tdText;
    }



    public PlanReturnMoneyFieldEnum getFieldEnum() {
        return fieldEnum;
    }



    public void setFieldEnum(PlanReturnMoneyFieldEnum fieldEnum) {
        this.fieldEnum = fieldEnum;
    }



    public String getTdText() {
        return tdText;
    }



    public void setTdText(String tdText) {
        this.tdText = tdText;
    }



    public int getTdIndex() {
        return tdIndex;
    }



    public void setTdIndex(int tdIndex) {
        this.tdIndex = tdIndex;
    }
}
