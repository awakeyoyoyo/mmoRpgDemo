package com.liqihao.dao;

import com.liqihao.pojo.MmoGuildRolePOJO;

import java.util.List;

public interface MmoGuildRolePOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoGuildRolePOJO record);

    int insertSelective(MmoGuildRolePOJO record);

    MmoGuildRolePOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoGuildRolePOJO record);

    int updateByPrimaryKey(MmoGuildRolePOJO record);

    List<MmoGuildRolePOJO> selectByGuildId(Integer id);
}