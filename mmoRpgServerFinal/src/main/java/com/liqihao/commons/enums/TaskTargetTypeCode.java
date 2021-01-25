package com.liqihao.commons.enums;

/**
 * 任务目标类型
 * @author lqhao
 */

public enum TaskTargetTypeCode {
    //
    USE(1,"使用类型","com.liqihao.pojo.bean.TaskBean.UseTask.UseTaskBean"),
//    EQUIPMENT(2,"装备类型","com.liqihao.pojo.bean.TaskBean.EquipTaskBean"),
    TALK(3,"对话类型","com.liqihao.pojo.bean.TaskBean.TalkTaskBean"),
    SCENE(4,"场景类型","com.liqihao.pojo.bean.TaskBean.SceneTaskBean"),
    SKILL(5,"技能类型","com.liqihao.pojo.bean.TaskBean.SkillTaskBean"),
//    Money(6,"金币类型","com.liqihao.pojo.bean.TaskBean.MoneyTaskBean"),
    COPY_SCENE(7,"副本类型","com.liqihao.pojo.bean.TaskBean.CopySceneTaskBean"),
//    KILL(8,"杀怪类型","com.liqihao.pojo.bean.TaskBean.KillTaskBean")
    ;
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
