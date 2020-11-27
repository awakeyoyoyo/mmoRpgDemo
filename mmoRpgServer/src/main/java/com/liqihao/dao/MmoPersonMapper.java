package com.liqihao.dao;

import com.liqihao.entity.MmoPerson;

public interface MmoPersonMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoPerson record);

    int insertSelective(MmoPerson record);

    MmoPerson selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoPerson record);

    int updateByPrimaryKey(MmoPerson record);
}