package com.liqihao.dao;

import com.liqihao.pojo.MmoDealBankAuctionPOJO;

import java.util.List;

public interface MmoDealBankAuctionPOJOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MmoDealBankAuctionPOJO record);

    int insertSelective(MmoDealBankAuctionPOJO record);

    MmoDealBankAuctionPOJO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MmoDealBankAuctionPOJO record);

    int updateByPrimaryKey(MmoDealBankAuctionPOJO record);

    int selectNextIndex();

    List<MmoDealBankAuctionPOJO> selectAll();
}