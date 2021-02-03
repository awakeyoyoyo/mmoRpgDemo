package com.liqihao.cache;
import com.liqihao.cache.base.CommonsCache;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 药品基本信息缓存
 * @author lqhao
 */
@Component
public class MedicineMessageCache extends CommonsCache<MedicineMessage> {
    private static String excel_file = "classpath:message/medicineMessage.xlsx";
    private volatile static MedicineMessageCache instance ;
    public static MedicineMessageCache getInstance(){
        return instance;
    }
    public MedicineMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, MedicineMessage.class);
    }

    private MedicineMessageCache(ConcurrentHashMap<Integer,MedicineMessage> map) {
        super(map);
    }
}
