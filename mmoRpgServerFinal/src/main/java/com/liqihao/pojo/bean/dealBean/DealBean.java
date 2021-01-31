package com.liqihao.pojo.bean.dealBean;

import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;

import java.util.List;

/**
 * 交易bean
 * @author lqhao
 */
public class DealBean {
    private Integer id;
    private MmoSimpleRole firstRole;
    private MmoSimpleRole secondRole;
    private DealArticleBean firstDealArticleBean;
    private DealArticleBean secondDealArticleBean;
    private volatile Integer status;
    public final Object lock=new Object();
    public long endTime;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

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
