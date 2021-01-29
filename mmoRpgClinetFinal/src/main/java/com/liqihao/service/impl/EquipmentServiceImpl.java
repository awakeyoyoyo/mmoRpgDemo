package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.PositionCode;
import com.liqihao.commons.enums.StateCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.protobufObject.BackPackModel;
import com.liqihao.protobufObject.EquipmentModel;
import com.liqihao.service.EquipmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService{
    @Override
    public void equipmentMsgResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        List<EquipmentModel.EquipmentDto> dtos=myMessage.getEquipmentMsgResponse().getEquipmentsList();
        for (EquipmentModel.EquipmentDto dto:dtos) {
            EquipmentMessage message= MmoCacheCilent.getInstance().getEquipmentMessageConcurrentHashMap().get(dto.getId());
            System.out.println("[-]");
            System.out.println("[-]装备名字: "+message.getName());
            System.out.println("[-]装备部位: "+ PositionCode.getValue(dto.getPosition())+"装备部位id:"+dto.getPosition());
            System.out.println("[-]装备耐久度: "+dto.getNowDurability()+" 装备星级："+message.getEquipmentLevel());
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void reduceEquipmentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]脱装备成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void addEquipmentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]穿装备成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void fixEquipmentResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        if (nettyResponse.getStateCode()== StateCode.FAIL){
            System.out.println(new String(nettyResponse.getData()));
            return;
        }
        byte[] data=nettyResponse.getData();
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]修复装备成功");
        System.out.println("[-]--------------------------------------------------------");
    }
}
