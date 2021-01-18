package com.liqihao.dao;

import com.liqihao.pojo.MmoDealBankArticlePOJO;
import com.liqihao.pojo.MmoDealBankAuctionPOJO;

import java.util.List;

public interface MmoDealBankArticlePOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoDealBankArticlePOJO record);

    int insertSelective(MmoDealBankArticlePOJO record);

    MmoDealBankArticlePOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoDealBankArticlePOJO record);

    int updateByPrimaryKey(MmoDealBankArticlePOJO record);

    int selectNextIndex();

    List<MmoDealBankArticlePOJO> selectAll();

}