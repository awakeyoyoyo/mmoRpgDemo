package com.liqihao.util;


import java.util.concurrent.*;


/**
 * @author awakeyoyoyo
 * @className ScheduledPool
 * @description TODO
 * @date 2020-12-03 22:52
 */
public class ThreadPools {
    private static ExecutorService executorService;
    //线程池初始化大小
    private static int poolSize=8;
    //任务队列长度
    public static void init(){
        /**
         * 核心线程数量大小
         * 最大活跃线程数量大小
         * 空闲线程超时时间
         * 时间单位
         * 有限的阻塞工作队列
         * 丢弃策略
         */
        //创建了一个最大线程数和核心线程数是5的线程池，工作队列长度为100的有限队列，线程池策略采取丢弃抛异常
        executorService = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.AbortPolicy());
    }
    public static void submit(Runnable task){
        executorService.submit(task);
    }
    public static void shutdown(){
        executorService.shutdown();;
    }
}
