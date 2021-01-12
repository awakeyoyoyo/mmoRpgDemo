package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.GoodsMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商品基本信息缓存类
 * @author lqhao
 */
@Component
public class GoodsMessageCache extends CommonsCache<GoodsMessage>{
    private static String excel_file = "classpath:message/goodsMessage.xlsx";
    private volatile static GoodsMessageCache instance ;
    public static GoodsMessageCache getInstance(){
        return instance;
    }
    public GoodsMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, GoodsMessage.class);
    }
    private GoodsMessageCache(ConcurrentHashMap<Integer,GoodsMessage> map) {
        super(map);
    }
}
