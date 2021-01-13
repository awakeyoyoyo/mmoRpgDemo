package com.liqihao.dao;

import com.liqihao.pojo.MmoGuildApplyPOJO;

import java.util.List;

public interface MmoGuildApplyPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoGuildApplyPOJO record);

    int insertSelective(MmoGuildApplyPOJO record);

    MmoGuildApplyPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoGuildApplyPOJO record);

    int updateByPrimaryKey(MmoGuildApplyPOJO record);

    List<MmoGuildApplyPOJO> selectByGuildId(Integer id);
}