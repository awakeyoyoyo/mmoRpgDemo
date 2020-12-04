package com.liqihao.commons;

public enum  CmdCode {
    ASK_CAN_REQUEST_CMD(ConstantValue.SCENE_MODULE,ConstantValue.ASK_CAN_REQUEST,ConstantValue.ASK_CAN_REQUEST_CMD),
    FIND_ALL_ROLES_REQUEST_CMD(ConstantValue.SCENE_MODULE,ConstantValue.FIND_ALL_ROLES_REQUEST,ConstantValue.FIND_ALL_ROLES_REQUEST_CMD),
    WENT_REQUEST_CMD(ConstantValue.SCENE_MODULE,ConstantValue.WENT_REQUEST,ConstantValue.WENT_REQUEST_CMD),
    LOGIN_REQUEST_CMD(ConstantValue.PLAY_MODULE,ConstantValue.LOGIN_REQUEST,ConstantValue.LOGIN_REQUEST_CMD),
    REGISTER_REQUEST_CMD(ConstantValue.PLAY_MODULE,ConstantValue.REGISTER_REQUEST,ConstantValue.REGISTER_REQUEST_CMD),
    LOGOUT_REQUEST_CMD(ConstantValue.PLAY_MODULE,ConstantValue.LOGOUT_REQUEST,ConstantValue.LOGOUT_REQUEST_CMD);
    private  short module;
    private  short cmd;
    private  String option;

    CmdCode(short module, short cmd, String option) {
        this.module = module;
        this.cmd = cmd;
        this.option = option;
    }

    public short getModule() {
        return module;
    }

    public void setModule(short module) {
        this.module = module;
    }

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
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
