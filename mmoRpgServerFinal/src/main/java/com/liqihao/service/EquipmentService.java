package com.liqihao.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.RpgServerException;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.EquipmentModel;

/**
 * 装备模块
 * @author lqhao
 */
public interface EquipmentService {
    /**
     * 穿装备
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void addEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;

    /**
     * 背包信息
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void equipmentMasRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException;

    /**
     * 脱装备
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void reduceEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws Exception;

    /**
     * 修复装备
     * @param myMessage
     * @param mmoSimpleRole
     * @throws InvalidProtocolBufferException
     */
    void fixEquipmentRequest(EquipmentModel.EquipmentModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException;
}
