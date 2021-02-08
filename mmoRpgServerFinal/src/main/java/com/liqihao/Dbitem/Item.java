package com.liqihao.Dbitem;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Classname Item
 * @Description 顶层抽象类
 * @Author lqhao
 * @Date 2021/2/7 17:19
 * @Version 1.0
 */
public abstract class Item {
    /**
     * 是否更改标志
     */
    private volatile AtomicBoolean changeFlag=new AtomicBoolean(false);

    public AtomicBoolean getChangeFlag() {
        return changeFlag;
    }

    public void setChangeFlag(AtomicBoolean changeFlag) {
        this.changeFlag = changeFlag;
    }

    /**
     * 更新入库
     * @param id
     */
    public abstract void updateItem(Integer id);
}
