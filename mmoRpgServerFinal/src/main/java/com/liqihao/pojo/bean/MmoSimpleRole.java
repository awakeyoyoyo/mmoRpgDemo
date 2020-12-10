package com.liqihao.pojo.bean;

import com.liqihao.pojo.MmoRolePOJO;
import java.util.HashMap;
import java.util.List;

public class MmoSimpleRole extends MmoRolePOJO {
    private Integer Blood;
    private Integer nowBlood;
    private Integer mp;
    private Integer nowMp;
    private HashMap<Integer,Long> cdMap;
    private List<Integer> skillIdList;

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }

    public List<Integer> getSkillIdList() {
        return skillIdList;
    }

    public void setSkillIdList(List<Integer> skillIdList) {
        this.skillIdList = skillIdList;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getBlood() {
        return Blood;
    }

    public void setBlood(Integer blood) {
        Blood = blood;
    }

    public Integer getNowBlood() {
        return nowBlood;
    }

    public void setNowBlood(Integer nowBlood) {
        this.nowBlood = nowBlood;
    }



    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }
    //使用技能
}
