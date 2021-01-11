package com.liqihao.provider;

import com.liqihao.Cache.EquipmentMessageCache;
import com.liqihao.Cache.GoodsMessageCache;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.GoodsMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.util.CommonsUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 商品服务提供类
 * @author lqhao
 */
@Component
public class GoodsServiceProvider {
    private static final ConcurrentHashMap<Integer, GoodsBean> goodsBeanConcurrentHashMap=new ConcurrentHashMap<>();
    @PostConstruct
    private  void init(){
        for (GoodsMessage g:GoodsMessageCache.getInstance().values()){
            GoodsBean goodsBean= CommonsUtil.goodMessageToGoodBean(g);
            goodsBeanConcurrentHashMap.put(goodsBean.getId(),goodsBean);
        }

    }

    /**
     * 获取所有商品列表
     * @return
     */
    public static List<GoodsBean> getAllArticles(){
        synchronized (goodsBeanConcurrentHashMap) {
            List<GoodsBean> beans = new ArrayList<>(goodsBeanConcurrentHashMap.values());
            return beans;
        }
    }
    /**
     *  对外提供买东西接口
     */
    public static Article sellArticle(Integer goodsId,Integer num, MmoSimpleRole mmoSimpleRole) throws Exception {
        GoodsBean goodsBean=goodsBeanConcurrentHashMap.get(goodsId);
        if (goodsBean == null) {
            throw new Exception("查无该商品");
        }
        GoodsMessage goodsMessage=GoodsMessageCache.getInstance().get(goodsBean.getGoodsMessageId());
        synchronized (goodsBean) {
            if (goodsBean.getNowNum()<=0){
                throw new Exception("该商品数量不足");
            }

            Integer needMoney=num*goodsMessage.getPrice();
            if (mmoSimpleRole.getMoney()<needMoney){
                throw new Exception("用户不够钱");
            }
            mmoSimpleRole.setMoney(mmoSimpleRole.getMoney()-needMoney);
            goodsBean.setNowNum(goodsBean.getNowNum()-num);
        }
        boolean flag=false;
        Article article=null;
        if(goodsMessage.getArticleTypeId().equals(ArticleTypeCode.EQUIPMENT.getCode())){
            //装备
            article= sellEquipment(goodsMessage.getArticleMessageId());
        }else{
            //药品
            article= sellMedicineBean(goodsMessage.getArticleMessageId(),num);
        }
        synchronized (mmoSimpleRole.getBackpackManager()) {
            flag = mmoSimpleRole.getBackpackManager().canPutArticle(article);
            if (!flag) {
                //背包满了
                //恢复
                synchronized (goodsBean) {
                    goodsBean.setNowNum(goodsBean.getNowNum() + num);
                }
                throw new Exception("背包已经满了");
            }
            mmoSimpleRole.getBackpackManager().put(article);
        }
        return  article;
    }

    /**
     * 卖药品
     */
    private static MedicineBean sellMedicineBean(Integer medicineId,Integer num){
        MedicineBean medicineBean=ArticleServiceProvider.productMedicine(medicineId);
        medicineBean.setQuantity(num);
        return medicineBean;
    }

    /**
     * 卖装备
     */
    private static EquipmentBean sellEquipment(Integer equipmentId){
       EquipmentBean equipmentBean= ArticleServiceProvider.productEquipment(equipmentId);
       return equipmentBean;
    }
}
