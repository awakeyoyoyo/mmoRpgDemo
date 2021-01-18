package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.DealModel;
import com.liqihao.service.DealService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DealServiceImpl implements DealService {
    @Override
    public void askDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        Integer roleId=myMessage.getAskDealResponse().getRoleId();
        String roleName=myMessage.getAskDealResponse().getRoleName();
        if (roleId.equals(MmoCacheCilent.getInstance().getNowRole().getId())) {
            System.out.println("[-]--------------------------------------------------------");
            System.out.println("[-]已发送交易邀请！");
            System.out.println("[-]--------------------------------------------------------");
        }else{
            System.out.println("[-]--------------------------------------------------------");
            System.out.println("[-]角色id："+roleId+" 角色姓名："+roleName+" 向你发来了交易邀请");
            System.out.println("[-]--------------------------------------------------------");
        }
    }

    @Override
    public void agreeDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]交易开始！请开始你的操作");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void refuseDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]交易已被拒绝，交易结束");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void confirmDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        Integer roleId=myMessage.getConfirmDealResponse().getRoleId();
        String roleName=myMessage.getConfirmDealResponse().getRoleName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]角色id："+roleId+" 角色姓名："+roleName+" 已经确认交易内容");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void cancelDealResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        Integer roleId=myMessage.getCancelDealResponse().getRoleId();
        String roleName=myMessage.getCancelDealResponse().getRoleName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]角色id："+roleId+" 角色姓名："+roleName+" 已经取消了交易，物品已经归还双方");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getDealMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        Integer firstRoleId=myMessage.getGetDealMessageResponse().getFirstRoleId();
        String firstRoleName=myMessage.getGetDealMessageResponse().getFirstRoleName();
        List<DealModel.ArticleDto> firstArticleDtos=myMessage.getGetDealMessageResponse().getFirstArticleDtoList();
        Integer firstMoney=myMessage.getGetDealMessageResponse().getFirstMoney();

        Integer secondRoleId=myMessage.getGetDealMessageResponse().getSecondRoleId();
        String secondRoleName=myMessage.getGetDealMessageResponse().getSecondRoleName();
        List<DealModel.ArticleDto> secondArticleDtos=myMessage.getGetDealMessageResponse().getSecondArticleDtoList();
        Integer secondMoney=myMessage.getGetDealMessageResponse().getSecondMoney();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]角色id："+firstRoleId+" 角色姓名："+firstRoleName+" 交易金额："+firstMoney);
        System.out.println("[-]交易物品:");
        print(firstArticleDtos);
        System.out.println("[-]角色id："+secondRoleId+" 角色姓名："+secondRoleName+" 交易金额："+secondMoney);
        System.out.println("[-]交易物品:");
        print(secondArticleDtos);
        System.out.println("[-]--------------------------------------------------------");
    }
    public void print(List<DealModel.ArticleDto> firstArticleDtos){
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap=MmoCacheCilent.getInstance().getMedicineMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=MmoCacheCilent.getInstance().getEquipmentMessageConcurrentHashMap();
        for (DealModel.ArticleDto firstArticleDto : firstArticleDtos) {
            if (firstArticleDto.getArticleType()== ArticleTypeCode.EQUIPMENT.getCode()){
                //装备
                EquipmentMessage equipmentMessage=equipmentMessageConcurrentHashMap.get(firstArticleDto.getArticleMessageId());
                System.out.println("[-]");
                System.out.println("[-][-]名字: "+equipmentMessage.getName()+" 耐久度： "+firstArticleDto.getNowDurability()+ "描述: "+equipmentMessage.getDescription());
                System.out.println("[-][-]交易栏中的id: "+firstArticleDto.getDealArticleId()+" 装备基本信息id: "+firstArticleDto.getArticleMessageId()+" 装备实例id: "+firstArticleDto.getEquipmentId()+" 物品数量: "+firstArticleDto.getQuantity());
                System.out.println("[-]");
            }else if (firstArticleDto.getArticleType()== ArticleTypeCode.MEDICINE.getCode()){
                //药品
                MedicineMessage medicineMessage=medicineMessageConcurrentHashMap.get(firstArticleDto.getArticleMessageId());
                System.out.println("[-]");
                System.out.println("[-][-]名字: "+medicineMessage.getName()+ "描述: "+medicineMessage.getDescription());
                System.out.println("[-][-]交易栏中的id: "+firstArticleDto.getDealArticleId()+" 药品基本信息id: "+firstArticleDto.getArticleMessageId()+" 物品数量: "+firstArticleDto.getQuantity());
                System.out.println("[-]");
            }else {
                System.out.println("[-]");
                System.out.println("[-]什么鬼东西？？？");
                System.out.println("[-]");
            }
        }
    }
    @Override
    public void setDealMoneyResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        Integer money=myMessage.getSetDealMoneyResponse().getMoney();
        Integer roleId=myMessage.getSetDealMoneyResponse().getRoleId();
        String roleName=myMessage.getSetDealMoneyResponse().getRoleName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]角色id："+roleId+" 角色姓名："+roleName+" 修改交易他付出金额为："+money);
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void addDealArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        Integer roleId=myMessage.getAddArticleResponse().getRoleId();
        String roleName=myMessage.getAddArticleResponse().getRoleName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]角色id："+roleId+" 角色姓名："+roleName+" 往交易栏内放入了物品");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void abandonDealArticleResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        Integer roleId=myMessage.getAbandonArticleResponse().getRoleId();
        String roleName=myMessage.getAbandonArticleResponse().getRoleName();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]角色id："+roleId+" 角色姓名："+roleName+" 拿回了交易栏中的物品");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void dealSuccessResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealModel.DealModelMessage myMessage;
        myMessage = DealModel.DealModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]交易完成！请查看背包！");
        System.out.println("[-]--------------------------------------------------------");
    }
}
