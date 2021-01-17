package com.liqihao.dao;

import com.liqihao.pojo.MmoEquipmentBagPOJO;

import java.util.List;

public interface MmoEquipmentBagPOJOMapper {
    int deleteByPrimaryKey(Integer equipmentBagId);

    int insert(MmoEquipmentBagPOJO record);

    int insertSelective(MmoEquipmentBagPOJO record);

    MmoEquipmentBagPOJO selectByPrimaryKey(Integer equipmentBagId);

    int updateByPrimaryKeySelective(MmoEquipmentBagPOJO record);

    int updateByPrimaryKey(MmoEquipmentBagPOJO record);

    List<MmoEquipmentBagPOJO> selectByRoleId(Integer roleId);

    int selectNextIndex();
}