package com.liqihao.dao;

import com.liqihao.pojo.MmoEquipmentPOJO;

import java.util.List;

public interface MmoEquipmentPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoEquipmentPOJO record);

    int insertSelective(MmoEquipmentPOJO record);

    MmoEquipmentPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoEquipmentPOJO record);

    int updateByPrimaryKey(MmoEquipmentPOJO record);

    Integer selectNextIndex();

    List<MmoEquipmentPOJO> selectAll();
}