package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.ProfessionMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 职业基础信息缓存类
 * @author lqhao
 */
@Component
public class ProfessionMessageCache extends CommonsCache<ProfessionMessage>{
    private static String professionMessage_file = "classpath:message/professionMessage.xlsx";
    private volatile static ProfessionMessageCache instance ;
    public static ProfessionMessageCache getInstance(){
        return instance;
    }
    public ProfessionMessageCache() {

    }
    @PostConstruct
    public void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<ProfessionMessage> professionMessages= ExcelReaderUtil.readExcelFromFileName(professionMessage_file,ProfessionMessage.class);
        for (ProfessionMessage m:professionMessages){
            instance.put(m.getId(),m);
        }
    }
    private ProfessionMessageCache(ConcurrentHashMap<Integer,ProfessionMessage> map) {
        super(map);
    }
}
