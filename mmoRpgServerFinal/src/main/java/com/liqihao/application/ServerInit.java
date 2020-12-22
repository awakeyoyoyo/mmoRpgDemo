package com.liqihao.application;

import com.liqihao.Cache.*;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ExcelReaderUtil;
import com.liqihao.util.LogicTreadPoolUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
/**
 * 初始化服务器类
 */
public class ServerInit{
    public void init() throws IOException, IllegalAccessException, InstantiationException {
        ScheduledThreadPoolUtil.init();
        LogicTreadPoolUtil.init();
    }


    public static void main(String[] args) {
        String str=null;
        System.out.println(CommonsUtil.split(str));
    }

}
