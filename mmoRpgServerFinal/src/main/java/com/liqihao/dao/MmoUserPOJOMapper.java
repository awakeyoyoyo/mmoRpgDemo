package com.liqihao.dao;

import com.liqihao.pojo.MmoUserPOJO;

public interface MmoUserPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoUserPOJO record);

    int insertSelective(MmoUserPOJO record);

    MmoUserPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoUserPOJO record);

    int updateByPrimaryKey(MmoUserPOJO record);
}