package com.liqihao.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理逻辑代码线程池
 * @author lqhao
 */
public class LogicTreadPoolUtil {
    private static int corePoolSize=4;
    private static int maximumPoolSize=0;
    private static long keepAliveTime=10;
    private static ThreadPoolExecutor logicThreadPool;

    public static  void init(){
            logicThreadPool=new ThreadPoolExecutor(corePoolSize,
                    corePoolSize,
                    keepAliveTime,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>());
    }

    public static ThreadPoolExecutor getLogicThreadPool() {
        return logicThreadPool;
    }

    public static void setLogicThreadPool(ThreadPoolExecutor logicThreadPool) {
        LogicTreadPoolUtil.logicThreadPool = logicThreadPool;
    }
}
