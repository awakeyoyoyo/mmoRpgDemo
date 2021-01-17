package com.liqihao.dao;

import com.liqihao.pojo.MmoRolePOJO;

import java.util.List;

public interface MmoRolePOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoRolePOJO record);

    int insertSelective(MmoRolePOJO record);

    MmoRolePOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoRolePOJO record);

    int updateByPrimaryKey(MmoRolePOJO record);

    Integer selectByRoleName(String roleName);

    MmoRolePOJO selectByPrimaryKeyAndOnStatus(Integer id);

    List<MmoRolePOJO> selectAll();

    Integer selectNextIndex();
}