package com.liqihao.dao;

import com.liqihao.pojo.MmoGuildPOJO;

import java.util.List;

public interface MmoGuildPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoGuildPOJO record);

    int insertSelective(MmoGuildPOJO record);

    MmoGuildPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoGuildPOJO record);

    int updateByPrimaryKey(MmoGuildPOJO record);

    List<MmoGuildPOJO> selectAll();
}