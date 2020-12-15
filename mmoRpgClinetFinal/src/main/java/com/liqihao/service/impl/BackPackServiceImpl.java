package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.StateCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.protobufObject.BackPackModel;
import com.liqihao.protobufObject.GameSystemModel;
import com.liqihao.service.BackPackService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BackPackServiceImpl implements BackPackService {
    @Override
    public void backPackMsgResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        List<BackPackModel.ArticleDto> bags=myMessage.getBackPackResponse().getArticleDtosList();
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap=MmoCacheCilent.getInstance().getMedicineMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=MmoCacheCilent.getInstance().getEquipmentMessageConcurrentHashMap();
        System.out.println("----------------------------------------------");
        for (BackPackModel.ArticleDto a:bags) {
            if (a.getArticleType()== ArticleTypeCode.EQUIPMENT.getCode()){
                //装备
                EquipmentMessage equipmentMessage=equipmentMessageConcurrentHashMap.get(a.getId());
                System.out.println("背包中的id: "+a.getArticleId()+" 物品id: "+a.getId()+" 物品数量: "+a.getQuantity()
                    +" 名字: "+equipmentMessage.getName()+" 描述: "+equipmentMessage.getDescription()
                );
            }else if (a.getArticleType()== ArticleTypeCode.MEDICINE.getCode()){
                //药品
                MedicineMessage medicineMessage=medicineMessageConcurrentHashMap.get(a.getId());
                System.out.println("背包中的id: "+a.getArticleId()+" 物品id: "+a.getId()+" 物品数量: "+a.getQuantity()
                        +" 名字: "+medicineMessage.getName()+" 描述: "+medicineMessage.getDescription()
                );
            }else {
                System.out.println("什么鬼东西？？？");
            }

        }
        System.out.println("----------------------------------------------");
    }

    @Override
    public void useResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException{
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        System.out.println("使用成功");
    }

    @Override
    public void abandonResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException{
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        System.out.println("丢弃成功");
    }
}
