package com.liqihao.dao;

import com.liqihao.pojo.MmoWareHousePOJO;

import java.util.List;

public interface MmoWareHousePOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoWareHousePOJO record);

    int insertSelective(MmoWareHousePOJO record);

    MmoWareHousePOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoWareHousePOJO record);

    int updateByPrimaryKey(MmoWareHousePOJO record);

    int selectNextIndex();

    List<MmoWareHousePOJO> selectByGuildId(Integer id);
}