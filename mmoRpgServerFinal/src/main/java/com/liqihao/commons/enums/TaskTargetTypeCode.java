package com.liqihao.commons.enums;

/**
 * 任务目标类型
 * @author lqhao
 */

public enum TaskTargetTypeCode {
    //
    USE(1,"使用类型","com.liqihao.pojo.bean.taskBean.useTask.UseTaskBean"),
    TALK(3,"对话类型","com.liqihao.pojo.bean.taskBean.talkTask.TalkTaskBean"),
    FIRST_TIME_SCENE(4,"第一次进入场景类型","com.liqihao.pojo.bean.taskBean.sceneFirstTask.SceneTaskBean"),
    SKILL(5,"技能类型","com.liqihao.pojo.bean.taskBean.skillTask.SkillTaskBean"),
    Money(6,"金币类型","com.liqihao.pojo.bean.taskBean.moneyNumTask.MoneyTaskBean"),
    COPY_SCENE(7,"副本类型","com.liqihao.pojo.bean.taskBean.copySceneSuccessTask.CopySceneTaskBean"),
    FIRST_TIME_DEAL(9,"第一次交易类型","com.liqihao.pojo.bean.taskBean.dealFirstTask.DealTaskBean"),
    FIRST_TIME_TEAM(10,"第一次组队类型","com.liqihao.pojo.bean.taskBean.teamFirstTask.TeamTaskBean"),
    UP_LEVEL(11,"提升等级类型","com.liqihao.pojo.bean.taskBean.roleLevelTask.RoleLevelTaskBean"),
    EQUIPMENT_LEVEL(12,"总装备星级类型","com.liqihao.pojo.bean.taskBean.equipmentLevelTask.EquipmentLevelTaskBean"),
    FIRST_TIME_GUILD(13,"第一次进入公会类型","com.liqihao.pojo.bean.taskBean.guildFirstTask.GuildFirstTaskBean"),
    FIRST_TIME_PK(14,"第一次pk胜利类型","com.liqihao.pojo.bean.taskBean.pkFirstTask.PkFirstTaskBean"),
    BEST_EQUIPMENT(15,"极品装备类型","com.liqihao.pojo.bean.taskBean.oneBestEquipmentTask.OneBestEquipmentTaskBean"),
    FIRST_TIME_FRIEND(16,"第一次交友类型","com.liqihao.pojo.bean.taskBean.firstFriendTask.FriendFirstTaskBean"),
    KILL(8,"杀怪类型","com.liqihao.pojo.bean.taskBean.killTask.KillTaskBean");

    private  int code;
    private  String value;
    private String className;
    TaskTargetTypeCode(int code, String name,String className)
    {
        this.code=code;
        this.value = name;
        this.className=className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
        for (TaskTargetTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getValue();
            }
        }
        return null;
    }

    public static String getClassNameByCode(int code) {
        for (TaskTargetTypeCode ele : values()) {
            if(ele.getCode()==code){
                return ele.getClassName();
            }
        }
        return null;
    }
}
