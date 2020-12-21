package com.liqihao.pojo.baseMessage;

public class BaseDetailMessage {
    private Integer bagSize;
    private Integer reduceDurability;

    @Override
    public String toString() {
        return "BaseDetailMessage{" +
                "bagSize=" + bagSize +
                ", reduceDurability=" + reduceDurability +
                '}';
    }

    public Integer getBagSize() {
        return bagSize;
    }

    public void setBagSize(Integer bagSize) {
        this.bagSize = bagSize;
    }

    public Integer getReduceDurability() {
        return reduceDurability;
    }

    public void setReduceDurability(Integer reduceDurability) {
        this.reduceDurability = reduceDurability;
    }
}
