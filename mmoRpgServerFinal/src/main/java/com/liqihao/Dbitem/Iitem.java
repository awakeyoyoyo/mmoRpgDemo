package com.liqihao.Dbitem;

/**
 * @Classname Iitem
 * @Description 顶层抽象类
 * @Author lqhao
 * @Date 2021/2/7 17:19
 * @Version 1.0
 */
public abstract class Iitem {
    /**
     * 是否更改标志
     */
    private volatile boolean changeFlag;

    public boolean getChangeFlag() {
        return changeFlag;
    }

    public void setChangeFlag(boolean changeFlag) {
        this.changeFlag = changeFlag;
    }

    /**
     * 更新入库
     * @param id
     */
    public abstract void updateItem(Integer id);
}
