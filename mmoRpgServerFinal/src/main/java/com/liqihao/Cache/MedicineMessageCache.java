package com.liqihao.Cache;
import com.liqihao.pojo.baseMessage.GoodsMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 药品基本信息缓存
 * @author lqhao
 */
@Component
public class MediceneMessageCache extends CommonsCache<MedicineMessage>{
    private static String excel_file = "classpath:message/medicineMessage.xlsx";
    private volatile static MediceneMessageCache instance ;
    public static MediceneMessageCache getInstance(){
        return instance;
    }
    public MediceneMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, MedicineMessage.class);
    }
    private MediceneMessageCache(ConcurrentHashMap<Integer,MedicineMessage> map) {
        super(map);
    }
}
