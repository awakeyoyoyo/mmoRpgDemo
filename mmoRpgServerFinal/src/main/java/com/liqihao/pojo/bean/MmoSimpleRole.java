package com.liqihao.pojo.bean;

import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.MmoRolePOJO;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MmoSimpleRole extends MmoRolePOJO {
    private Integer Blood;
    private Integer nowBlood;
    private Integer mp;
    private Integer nowMp;
    private HashMap<Integer,Long> cdMap;
    private List<Integer> skillIdList;
    private List<SkillBean> skillBeans;
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private Integer attack;
    private SceneBean nowScene;
    private BackPackManager backpackManager;
    public BackPackManager getBackpackManager() {
        return backpackManager;
    }

    public void setBackpackManager(BackPackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    public SceneBean getNowScene() {
        return nowScene;
    }

    public void setNowScene(SceneBean nowScene) {
        this.nowScene = nowScene;
    }



    public List<SkillBean> getSkillBeans() {
        return skillBeans;
    }

    public void setSkillBeans(List<SkillBean> skillBeans) {
        this.skillBeans = skillBeans;
    }



    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }
    public CopyOnWriteArrayList<BufferBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BufferBean> bufferBeans) {
        this.bufferBeans = bufferBeans;
    }

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

    public Boolean useArticle(Integer articleId) {
        Article article=backpackManager.getArticleByArticleId(articleId);
        if (article!=null&&article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())){
            //药品
            MedicineBean medicineBean=(MedicineBean) article;
            //删减
            backpackManager.useOrAbandanArticle(articleId,1);
            Boolean flag=medicineBean.useMedicene(getId());
            return flag;
        }else if (article!=null&&article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())){
            //todo 装备
            return false;
        }else{
            return false;
        }

    }
    //使用技能
}
