package com.liqihao.pojo.bean.taskBean.moneyNumTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname MoneyTaskAction
 * @Description 金币任务
 * @Author lqhao
 * @Date 2021/1/26 11:36
 * @Version 1.0
 */
public class MoneyTaskAction extends BaseTaskAction {
    private Integer moneyAddNum;

    public Integer getMoneyAddNum() {
        return moneyAddNum;
    }

    public void setMoneyAddNum(Integer moneyAddNum) {
        this.moneyAddNum = moneyAddNum;
    }
}
