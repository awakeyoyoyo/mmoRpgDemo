package com.liqihao.dao;

import com.liqihao.pojo.MmoEmailPOJO;

import java.util.List;

public interface MmoEmailPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoEmailPOJO record);

    int insertSelective(MmoEmailPOJO record);

    MmoEmailPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoEmailPOJO record);

    int updateByPrimaryKey(MmoEmailPOJO record);

    List<MmoEmailPOJO> selectByToRoleId(Integer toRoleId);

    List<MmoEmailPOJO> selectByFromRoleId(Integer fromRoleId);

    Integer selectNextIndex();
}