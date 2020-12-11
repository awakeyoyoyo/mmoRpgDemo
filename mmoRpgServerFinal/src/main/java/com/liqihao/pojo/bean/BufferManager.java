package com.liqihao.pojo.bean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BufferManager {
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private ConcurrentHashMap<Integer,Long> cdMap;

    public BufferManager() {
        bufferBeans=new CopyOnWriteArrayList<>();
        cdMap=new ConcurrentHashMap<>();
    }

    public CopyOnWriteArrayList<BufferBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BufferBean> bufferBeans) {
        this.bufferBeans = bufferBeans;
    }

    public ConcurrentHashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(ConcurrentHashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }
}
