package com.liqihao.commons.enums;

/**
 * 副本销毁得原因
 * @author lqhao
 */

public enum CopySceneDeleteCauseCode {
    //
    TEAMEND(0,"队伍解散"),NOPEOPLE(1,"最后一个玩家退出副本")
    ,TIMEOUT(2,"超过规定挑战时间"),PEOPLEDIE(3,"所有玩家死亡")
    ,SUCCESS(4,"挑战成功");
    private  int code;
    private  String value;
    CopySceneDeleteCauseCode(int code,String name)
    {
        this.code=code;
        this.value = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setvalue(String name) {
        this.value = name;
    }

    public String getvalue() {
        return value;
    }
    public static String getValue(int code) {
        for (CopySceneDeleteCauseCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getvalue();
            }
        }
        return null;
    }
}
