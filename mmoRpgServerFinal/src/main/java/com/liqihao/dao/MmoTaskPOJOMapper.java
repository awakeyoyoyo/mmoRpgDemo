package com.liqihao.dao;

import com.liqihao.pojo.MmoTaskPOJO;

import java.util.List;

public interface MmoTaskPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoTaskPOJO record);

    int insertSelective(MmoTaskPOJO record);

    MmoTaskPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoTaskPOJO record);

    int updateByPrimaryKey(MmoTaskPOJO record);

    int selectNextIndex();

    List<MmoTaskPOJO> selectAllByRoleId(Integer id);
}