package com.liqihao.pojo.bean.dealBean;

import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

/**
 * 交易模块
 * @author lqhao
 */
public class DealBean {
    private Integer id;
    private MmoSimpleRole firstRole;
    private MmoSimpleRole secondRole;
    private DealArticleBean firstDealArticleBean;
    private DealArticleBean secondDealArticleBean;
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MmoSimpleRole getFirstRole() {
        return firstRole;
    }

    public void setFirstRole(MmoSimpleRole firstRole) {
        this.firstRole = firstRole;
    }

    public MmoSimpleRole getSecondRole() {
        return secondRole;
    }

    public void setSecondRole(MmoSimpleRole secondRole) {
        this.secondRole = secondRole;
    }

    public DealArticleBean getFirstDealArticleBean() {
        return firstDealArticleBean;
    }

    public void setFirstDealArticleBean(DealArticleBean firstDealArticleBean) {
        this.firstDealArticleBean = firstDealArticleBean;
    }

    public DealArticleBean getSecondDealArticleBean() {
        return secondDealArticleBean;
    }

    public void setSecondDealArticleBean(DealArticleBean secondDealArticleBean) {
        this.secondDealArticleBean = secondDealArticleBean;
    }
}
