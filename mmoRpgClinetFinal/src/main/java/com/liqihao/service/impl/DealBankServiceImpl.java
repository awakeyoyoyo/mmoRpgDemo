package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.DealBankArticleTypeCode;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.DealBankModel;
import com.liqihao.service.DealBankService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class DealBankServiceImpl implements DealBankService {
    @Override
    public void addSellArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]商品上架成功！");
        System.out.println("[-]--------------------------------------------------------");
    }



    @Override
    public void reduceSellArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已下架，请查看邮箱！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void reduceAuctionArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已下架，请查看邮箱！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void buyArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已购买，请查看邮箱！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void auctionArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]已竞价，请注意查看邮箱留意拍卖情况！");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getArticleRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.parseFrom(data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        List<DealBankModel.DealBankArticleDto> dealBankArticleDtos=myMessage.getGetArticleResponse().getDealBankArticleDtosList();
        ConcurrentHashMap<Integer, EquipmentMessage> equipmentMessageConcurrentHashMap=MmoCacheCilent.getInstance().getEquipmentMessageConcurrentHashMap();
        ConcurrentHashMap<Integer, MedicineMessage> medicineMessageConcurrentHashMap= MmoCacheCilent.getInstance().getMedicineMessageConcurrentHashMap();
        System.out.println("[-]--------------------------------------------------------");
        for (DealBankModel.DealBankArticleDto dealBankArticleDto : dealBankArticleDtos) {
            if (dealBankArticleDto.getType()== DealBankArticleTypeCode.AUCTION.getCode()){
                System.out.println("[-][-]");
                System.out.println("[-][-]拍卖类型商品");
                System.out.println("[-][-]商品id:"+dealBankArticleDto.getDealBankArticleBeanId()+"商品类型"+ ArticleTypeCode.getValue(dealBankArticleDto.getArticleType()));
                if (dealBankArticleDto.getArticleType()==ArticleTypeCode.EQUIPMENT.getCode()){
                    EquipmentMessage e=equipmentMessageConcurrentHashMap.get(dealBankArticleDto.getArticleMessageId());
                    System.out.println("[-][-]商品名称："+e.getName()+"商品数量："+dealBankArticleDto.getNum());
                }else{
                    MedicineMessage m=medicineMessageConcurrentHashMap.get(dealBankArticleDto.getArticleMessageId());
                    System.out.println("[-][-]商品名称："+m.getName()+"商品数量："+dealBankArticleDto.getNum());
                }
                System.out.println("[-][-]低价："+dealBankArticleDto.getPrice()+" 目前拍卖价："+dealBankArticleDto.getHighPrice());
                System.out.println("[-][-]开始时间："+sdf.format(dealBankArticleDto.getCreateTime())+" 结束时间:"+sdf.format(dealBankArticleDto.getEndTime()));
                for (DealBankModel.DealBankAuctionDto d:dealBankArticleDto.getDealBankAuctionDtosList()) {
                    System.out.println("[-][-][-]");
                    System.out.println("[-][-][-]拍卖纪录id："+d.getDealBeanAuctionBeanId()+" 拍卖金额："+d.getMoney()+" 拍卖时间"+d.getCreateTime()+" 拍卖玩家id："+d.getFromRoleId());
                    System.out.println("[-][-][-]");
                }
                System.out.println("[-][-]");
            }else{
                System.out.println("[-][-]");
                System.out.println("[-][-]一口价商品");
                System.out.println("[-][-]商品id:"+dealBankArticleDto.getDealBankArticleBeanId()+"商品类型"+ ArticleTypeCode.getValue(dealBankArticleDto.getArticleType()));
                if (dealBankArticleDto.getArticleType()==ArticleTypeCode.EQUIPMENT.getCode()){
                    EquipmentMessage e=equipmentMessageConcurrentHashMap.get(dealBankArticleDto.getArticleMessageId());
                    System.out.println("[-][-]商品名称："+e.getName()+"商品数量："+dealBankArticleDto.getNum());
                }else{
                    MedicineMessage m=medicineMessageConcurrentHashMap.get(dealBankArticleDto.getArticleMessageId());
                    System.out.println("[-][-]商品名称："+m.getName()+"商品数量："+dealBankArticleDto.getNum());
                }
                System.out.println("[-][-]价格是："+dealBankArticleDto.getPrice());
                System.out.println("[-][-]");
            }


        }
        System.out.println("[-]--------------------------------------------------------");
    }
}
