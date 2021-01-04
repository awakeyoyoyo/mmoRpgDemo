package com.liqihao.commons.enums;

/**
 * 副本销毁得原因
 * @author lqhao
 */

public enum CopySceneDeleteCauseCode {
    //
    TEAM_END(0,"队伍解散"), NO_PEOPLE(1,"最后一个玩家退出副本")
    ,TIMEOUT(2,"超过规定挑战时间"), PEOPLE_DIE(3,"所有玩家死亡")
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

    public void setValue(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }
    public static String getValue(int code) {
        for (CopySceneDeleteCauseCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }
}
