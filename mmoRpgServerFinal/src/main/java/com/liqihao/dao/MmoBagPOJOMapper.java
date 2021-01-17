package com.liqihao.dao;

import com.liqihao.pojo.MmoBagPOJO;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface MmoBagPOJOMapper {
    int deleteByPrimaryKey(Integer bagId);

    int insert(MmoBagPOJO record);

    int insertSelective(MmoBagPOJO record);

    MmoBagPOJO selectByPrimaryKey(Integer bagId);

    int updateByPrimaryKeySelective(MmoBagPOJO record);

    int updateByPrimaryKey(MmoBagPOJO record);

    List<MmoBagPOJO> selectByRoleId(Integer id);

    Integer selectNextIndex();
}