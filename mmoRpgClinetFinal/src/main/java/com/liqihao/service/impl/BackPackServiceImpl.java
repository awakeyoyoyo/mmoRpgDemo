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
        System.out.println("[-]--------------------------------------------------------");
        for (BackPackModel.ArticleDto a:bags) {
            if (a.getArticleType()== ArticleTypeCode.EQUIPMENT.getCode()){
                //装备
                EquipmentMessage equipmentMessage=equipmentMessageConcurrentHashMap.get(a.getId());
                System.out.println("[-]");
                System.out.println("[-][-]名字: "+equipmentMessage.getName()+" 装备星级："+equipmentMessage.getEquipmentLevel()+" 耐久度： "+a.getNowDurability()+ "描述: "+equipmentMessage.getDescription());
                System.out.println("[-][-]背包中的id: "+a.getArticleId()+" 装备基本信息id: "+a.getId()+" 装备实例id: "+a.getEquipmentId()+" 物品数量: "+a.getQuantity());
                System.out.println("[-]");
            }else if (a.getArticleType()== ArticleTypeCode.MEDICINE.getCode()){
                //药品
                MedicineMessage medicineMessage=medicineMessageConcurrentHashMap.get(a.getId());
                System.out.println("[-]");
                System.out.println("[-][-]名字: "+medicineMessage.getName()+ "描述: "+medicineMessage.getDescription());
                System.out.println("[-][-]背包中的id: "+a.getArticleId()+" 药品基本信息id: "+a.getId()+" 物品数量: "+a.getQuantity());
                System.out.println("[-]");
            }else {
                System.out.println("[-]");
                System.out.println("[-]什么鬼东西？？？");
                System.out.println("[-]");
            }

        }
        System.out.println("[-]--------------------------------------------------------");
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
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]使用成功");
        System.out.println("[-]--------------------------------------------------------");
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
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]丢弃成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void addArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]增加成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getArticleFromFloorResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]拾取成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void findAllCanResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        List<BackPackModel.ArticleFloorDto> articleFloorDtos=myMessage.getFindAllCanGetResponse().getArticleFloorDtoList();
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap=MmoCacheCilent.getInstance().getMedicineMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=MmoCacheCilent.getInstance().getEquipmentMessageConcurrentHashMap();
        System.out.println("[-]--------------------------------------------------------");
        for (BackPackModel.ArticleFloorDto a:articleFloorDtos) {
            if (a.getArticleType()== ArticleTypeCode.EQUIPMENT.getCode()){
                //装备
                EquipmentMessage equipmentMessage=equipmentMessageConcurrentHashMap.get(a.getId());
                System.out.println("[-]");
                System.out.println("[-][-]名字: "+equipmentMessage.getName()+" 耐久度： "+a.getNowDurability()+ "描述: "+equipmentMessage.getDescription());
                System.out.println("[-][-]装备基本信息id: "+a.getId()+" 装备实例id: "+a.getEquipmentId()+" 装备星级："+equipmentMessage.getEquipmentLevel()+" 物品数量: "+a.getQuantity());
                System.out.println("[-][-]地面物品id："+a.getFloorIndex());
                System.out.println("[-]");
            }else if (a.getArticleType()== ArticleTypeCode.MEDICINE.getCode()){
                //药品
                MedicineMessage medicineMessage=medicineMessageConcurrentHashMap.get(a.getId());
                System.out.println("[-]");
                System.out.println("[-][-]名字: "+medicineMessage.getName()+ "描述: "+medicineMessage.getDescription());
                System.out.println("[-][-]药品基本信息id: "+a.getId()+" 物品数量: "+a.getQuantity());
                System.out.println("[-][-]地面物品id："+a.getFloorIndex());
                System.out.println("[-]");
            }else {
                System.out.println("[-]");
                System.out.println("[-]什么鬼东西？？？");
                System.out.println("[-]");
            }
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void buyGoodsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]购买成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void checkMoneyNumberResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        Integer money=myMessage.getCheckMoneyNumberResponse().getMoney();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]角色的金币数量："+money);
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void findAllGoodsResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.parseFrom(data);
        List<BackPackModel.GoodsDto> goodsDtos=myMessage.getFindAllGoodsResponse().getGoodsDtosList();
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap=MmoCacheCilent.getInstance().getMedicineMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=MmoCacheCilent.getInstance().getEquipmentMessageConcurrentHashMap();
        System.out.println("[-]--------------------------------------------------------");
        for (BackPackModel.GoodsDto a:goodsDtos) {
            if (a.getArticleTypeId()== ArticleTypeCode.EQUIPMENT.getCode()){
                //装备
                EquipmentMessage equipmentMessage=equipmentMessageConcurrentHashMap.get(a.getArticleMessageId());
                System.out.println("[-]");
                System.out.println("[-][-]商品id："+a.getId()+" 名字: "+equipmentMessage.getName()+ "描述: "+equipmentMessage.getDescription());
                System.out.println("[-][-]装备基本信息id: "+a.getId()+" 装备星级："+equipmentMessage.getEquipmentLevel()+" 商品数量: "+a.getNowNum()+"/"+a.getNum());
                System.out.println("[-][-]价格："+a.getPrice());
                System.out.println("[-]");
            }else if (a.getArticleTypeId()== ArticleTypeCode.MEDICINE.getCode()){
                //药品
                MedicineMessage medicineMessage=medicineMessageConcurrentHashMap.get(a.getId());
                System.out.println("[-]");
                System.out.println("[-][-]商品id："+a.getId()+" 名字: "+medicineMessage.getName()+ "描述: "+medicineMessage.getDescription());
                System.out.println("[-][-]药品基本信息id: "+a.getId()+" 商品数量: "+a.getNowNum()+"/"+a.getNum());
                System.out.println("[-][-]价格："+a.getPrice());
                System.out.println("[-]");
            }else {
                System.out.println("[-]");
                System.out.println("[-]什么鬼东西？？？");
                System.out.println("[-]");
            }
        }
        System.out.println("[-]--------------------------------------------------------");
    }
}
