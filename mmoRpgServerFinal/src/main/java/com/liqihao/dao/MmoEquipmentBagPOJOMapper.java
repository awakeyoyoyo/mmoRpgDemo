package com.liqihao.dao;

import com.liqihao.pojo.MmoEquipmentBagPOJO;
import com.liqihao.pojo.MmoEquipmentPOJO;

import java.util.List;

public interface MmoEquipmentBagPOJOMapper {
    int deleteByPrimaryKey(Integer equipmentbagId);

    int insert(MmoEquipmentBagPOJO record);

    int insertSelective(MmoEquipmentBagPOJO record);

    MmoEquipmentBagPOJO selectByPrimaryKey(Integer equipmentbagId);

    int updateByPrimaryKeySelective(MmoEquipmentBagPOJO record);

    int updateByPrimaryKey(MmoEquipmentBagPOJO record);

    List<MmoEquipmentBagPOJO> selectByRoleId(Integer roleId);
}