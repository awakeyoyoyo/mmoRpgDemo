package com.liqihao.dao;

import com.liqihao.pojo.MmoEquipmentPOJO;

public interface MmoEquipmentPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoEquipmentPOJO record);

    int insertSelective(MmoEquipmentPOJO record);

    MmoEquipmentPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoEquipmentPOJO record);

    int updateByPrimaryKey(MmoEquipmentPOJO record);
}