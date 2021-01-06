package com.liqihao.dao;

import com.liqihao.pojo.MmoUserPOJO;
import org.apache.ibatis.annotations.Param;

public interface MmoUserPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoUserPOJO record);

    int insertSelective(MmoUserPOJO record);

    MmoUserPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoUserPOJO record);

    int updateByPrimaryKey(MmoUserPOJO record);
    Integer selectByUsername(String username);

    Integer checkByUernameAndPassword(@Param("username") String username, @Param("password") String password);

}