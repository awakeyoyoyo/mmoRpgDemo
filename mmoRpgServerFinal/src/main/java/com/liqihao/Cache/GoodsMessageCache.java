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
 * 商品基本信息缓存类
 * @author lqhao
 */
@Component
public class GoodsMessageCache extends CommonsCache<GoodsMessage>{
    private static String goodsMessage_file = "classpath:message/goodsMessage.xlsx";
    private volatile static GoodsMessageCache instance ;
    public static GoodsMessageCache getInstance(){
        return instance;
    }
    public GoodsMessageCache() {

    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        //药品信息
        List<GoodsMessage> goodsMessages= ExcelReaderUtil.readExcelFromFileName(goodsMessage_file,GoodsMessage.class);
        for (GoodsMessage goodsMessage:goodsMessages) {
            concurrentHashMap.put(goodsMessage.getId(),goodsMessage);
        }
    }
    private GoodsMessageCache(ConcurrentHashMap<Integer,GoodsMessage> map) {
        super(map);
    }
}
