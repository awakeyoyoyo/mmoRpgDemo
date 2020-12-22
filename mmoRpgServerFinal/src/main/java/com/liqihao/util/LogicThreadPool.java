package com.liqihao.util;

import com.liqihao.netty.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 逻辑线程池的实现
 * @author lqhao
 */
public class LogicThreadPool<Job extends Runnable>implements ThreadPool<Job> {

    private static LogicThreadPool instance;
    private int threadSize;
    public static void init(int num){
        LogicThreadPool logicThreadPool=new LogicThreadPool();
        instance=logicThreadPool;
        instance.initializeWorkers(num);
        instance.threadSize=num;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public static LogicThreadPool getInstance() {
        return instance;
    }
    //工作者列表
    private final CopyOnWriteArrayList<Worker> workers=new CopyOnWriteArrayList<>();
    // 线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    //初始化线程工作者
    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }
    class Worker implements Runnable {
        private final Logger log = LoggerFactory.getLogger(ServerHandler.class);
        private volatile boolean running = true;
        private LinkedList<Job> tasks = new LinkedList<>();

        @Override
        public void run() {
            Job job = null;
            while (running) {
                //如果线程中的工作队列空了，就wait
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException ex) {
                            // 感知到外部对WorkerThread的中断操作，返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // 取出一个Job
                    job = tasks.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception ex) {
                        log.error("System.out...... -> " + ex);
                    }
                }
            }
        }
        public void shutdown() {
            running = false;
        }
    }

    @Override
    public void execute(Job job,Integer index) {
        if (job != null) {
            // 添加一个工作，然后进行通知
           //根据channel的HashCode取余
            Worker worker=workers.get(index);
            synchronized (worker.tasks) {
                worker.tasks.addLast(job);
                //通知线程有任务了
                worker.tasks.notifyAll();
            }
        }
    }
    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

}
