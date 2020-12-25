package com.liqihao.commons.enums;

import com.liqihao.commons.ConstantValue;

public enum  CmdCode {
    //
    ASK_CAN_REQUEST_CMD(ConstantValue.ASK_CAN_REQUEST,ConstantValue.ASK_CAN_REQUEST_CMD),
    FIND_ALL_ROLES_REQUEST_CMD(ConstantValue.FIND_ALL_ROLES_REQUEST,ConstantValue.FIND_ALL_ROLES_REQUEST_CMD),
    WENT_REQUEST_CMD(ConstantValue.WENT_REQUEST,ConstantValue.WENT_REQUEST_CMD),
    LOGIN_REQUEST_CMD(ConstantValue.LOGIN_REQUEST,ConstantValue.LOGIN_REQUEST_CMD),
    REGISTER_REQUEST_CMD(ConstantValue.REGISTER_REQUEST,ConstantValue.REGISTER_REQUEST_CMD),
    LOGOUT_REQUEST_CMD(ConstantValue.LOGOUT_REQUEST,ConstantValue.LOGOUT_REQUEST_CMD),
    USE_SKILL_REQUEST_CMD(ConstantValue.USE_SKILL_REQUEST,ConstantValue.USE_SKILL_REQUEST_CMD),
    TALK_NPC_CMD(ConstantValue.TALK_NPC_REQUEST,ConstantValue.TALK_NPC_REQUEST_CMD),
    BACKPACK_MSG_REQUEST_CMD(ConstantValue.BACKPACK_MSG_REQUEST,ConstantValue.BACKPACK_MSG_REQUEST_CMD),
    USE_REQUEST_CMD(ConstantValue.USE_REQUEST,ConstantValue.USE_REQUEST_CMD),
    ADD_ARTICLE_REQUEST_CMD(ConstantValue.ADD_ARTICLE_REQUEST,ConstantValue.ADD_ARTICLE_REQUEST_CMD),
    ADD_EQUIPMENT_REQUEST_CMD(ConstantValue.ADD_EQUIPMENT_REQUEST,ConstantValue.ADD_EQUIPMENT_REQUEST_CMD),
    REDUCE_EQUIPMENT_REQUEST_CMD(ConstantValue.REDUCE_EQUIPMENT_REQUEST,ConstantValue.REDUCE_EQUIPMENT_REQUEST_CMD),
    EQUIPMENT_MSG_REQUEST_CMD(ConstantValue.EQUIPMENT_MSG_REQUEST,ConstantValue.EQUIPMENT_MSG_REQUEST_CMD),
    FIX_EQUIPMENT_REQUEST_CMD(ConstantValue.FIX_EQUIPMENT_REQUEST,ConstantValue.FIX_EQUIPMENT_REQUEST_CMD),
    CREATE_TEAM_REQUEST_CMD(ConstantValue.CREATE_TEAM_REQUEST,ConstantValue.CREATE_TEAM_REQUEST_CMD),
    TEAM_MESSAGE_REQUEST_CMD(ConstantValue.TEAM_MESSAGE_REQUEST,ConstantValue.TEAM_MESSAGE_REQUEST_CMD),
    ABANDON_REQUEST_CMD(ConstantValue.ABANDON_REQUEST,ConstantValue.ABANDON_REQUEST_CMD);
    private  int cmd;
    private  String option;

    CmdCode(int cmd, String option) {
        this.cmd = cmd;
        this.option = option;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }


    public static CmdCode getValue(String option) {
        for (CmdCode ele : values()) {
            if(ele.getOption().equals(option)){
                return ele;
            }
        }
        return null;
    }
}
