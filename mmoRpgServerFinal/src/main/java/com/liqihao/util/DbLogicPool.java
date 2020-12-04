package com.liqihao.util;

import java.util.concurrent.ExecutorService;

public class DbLogicPool {
    private static ExecutorService pool;

    public static ExecutorService getPool() {
        return pool;
    }

    public static void setPool(ExecutorService pool) {
        DbLogicPool.pool = pool;
    }

    public static void main(String[] args) {

    }
}
