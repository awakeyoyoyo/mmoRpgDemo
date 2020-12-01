package com.liqihao.dao;

import com.liqihao.pojo.MmoScenePOJO;

import java.util.List;

public interface MmoScenePOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoScenePOJO record);

    int insertSelective(MmoScenePOJO record);

    MmoScenePOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoScenePOJO record);

    int updateByPrimaryKey(MmoScenePOJO record);

    List<MmoScenePOJO> selectAll();

    String selectCanSceneByPrId(Integer mmosceneid);
}