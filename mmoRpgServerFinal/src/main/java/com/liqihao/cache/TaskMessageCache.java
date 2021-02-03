package com.liqihao.cache;

import com.liqihao.cache.base.CommonsCache;
import com.liqihao.pojo.baseMessage.TaskMessage;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务成就基础信息类型
 * @author lqhao
 */
@Service
public class TaskMessageCache extends CommonsCache<TaskMessage> {
    private static String excel_file = "classpath:message/TaskMessage.xlsx";
    private volatile static TaskMessageCache instance ;
    public static TaskMessageCache getInstance(){
        return instance;
    }
    public TaskMessageCache() {
    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, TaskMessage.class);
    }
    private TaskMessageCache(ConcurrentHashMap<Integer,TaskMessage> map) {
        super(map);
    }
}
