package com.liqihao.util;

/**
 * 自定义线程池接口
 * @author lqhao
 */
public interface ThreadPool<Job extends Runnable> {
    // 执行一个Job，这个Job需要实现Runnable
    void execute(Job job,Integer index);

    // 关闭线程池
    void shutdown();

}

