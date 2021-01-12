package com.liqihao.dao;

import com.liqihao.pojo.MmoGuildPOJO;

public interface MmoGuildPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoGuildPOJO record);

    int insertSelective(MmoGuildPOJO record);

    MmoGuildPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoGuildPOJO record);

    int updateByPrimaryKey(MmoGuildPOJO record);
}