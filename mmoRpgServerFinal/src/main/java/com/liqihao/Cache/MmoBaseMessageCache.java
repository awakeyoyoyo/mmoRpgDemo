package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.BaseDetailMessage;
import com.liqihao.pojo.baseMessage.BaseRoleMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基本信息缓存
 * @author lqhao
 */
@Component
public class MmoBaseMessageCache {
    private static String baseRoleMessage_file = "classpath:message/baseRoleMessage.xlsx";
    private static String baseDetailMessage_file = "classpath:message/baseDetailMessage.xlsx";
    //用户角色的基本信息
    private BaseRoleMessage baseRoleMessage;
    //基础配置信息
    private BaseDetailMessage baseDetailMessage;

    private volatile  static MmoBaseMessageCache instance;

    public static MmoBaseMessageCache getInstance(){
        return instance;
    }
    public MmoBaseMessageCache() {
    }

    public MmoBaseMessageCache(BaseRoleMessage baseRoleMessage, BaseDetailMessage baseDetailMessage) {
        this.baseRoleMessage = baseRoleMessage;
        this.baseDetailMessage = baseDetailMessage;
    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
       instance=this;
       baseRoleMessage= ExcelReaderUtil.readExcelFromFileName(baseRoleMessage_file,BaseRoleMessage.class).get(0);
       baseDetailMessage=ExcelReaderUtil.readExcelFromFileName(baseDetailMessage_file,BaseDetailMessage.class).get(0);
    }
    public BaseRoleMessage getBaseRoleMessage() {
        return baseRoleMessage;
    }

    public  void setBaseRoleMessage(BaseRoleMessage baseRoleMessage) {
        this.baseRoleMessage = baseRoleMessage;
    }

    public  BaseDetailMessage getBaseDetailMessage() {
        return baseDetailMessage;
    }

    public  void setBaseDetailMessage(BaseDetailMessage baseDetailMessage) {
        this.baseDetailMessage = baseDetailMessage;
    }
}
