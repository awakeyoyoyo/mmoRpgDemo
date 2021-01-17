package com.liqihao.util;

import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.dao.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.MmoEmailBean;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.provider.EmailServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库操作util
 * @author lqhao
 */
@Component
public class DbUtil {
    private static MmoBagPOJOMapper mmoBagPOJOMapper;
    private static MmoEquipmentPOJOMapper mmoEquipmentPOJOMapper;
    private static MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;
    private static MmoRolePOJOMapper mmoRolePOJOMapper;
    private static MmoEmailPOJOMapper mmoEmailPOJOMapper;
    private static MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper;
    private static MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper;
    private static MmoWareHousePOJOMapper mmoWareHousePOJOMapper;
    private static  AtomicInteger mmoBagPojoIndex;
    private static  AtomicInteger mmoEquipmentIndex;
    private static  AtomicInteger equipmentBagIndex;
    private static  AtomicInteger mmoEmailPojoIndex;
    private static AtomicInteger mmoWareHouseIndex;


    @Autowired
    public  void initMmoWareHousePOJOMapper(MmoWareHousePOJOMapper mmoWareHousePOJOMapper) {
        DbUtil.mmoWareHousePOJOMapper = mmoWareHousePOJOMapper;
        mmoWareHouseIndex=new AtomicInteger(mmoWareHousePOJOMapper.selectNextIndex());
    }
    @Autowired
    public  void initMmoBagPOJOMapper(MmoBagPOJOMapper mmoBagPOJOMapper) {
        DbUtil.mmoBagPOJOMapper = mmoBagPOJOMapper;
        mmoBagPojoIndex=new AtomicInteger(mmoBagPOJOMapper.selectNextIndex());
    }
    @Autowired
    public  void initMmoEquipmentPOJOMapper(MmoEquipmentPOJOMapper mmoEquipmentPOJOMapper) {
        DbUtil.mmoEquipmentPOJOMapper = mmoEquipmentPOJOMapper;
        mmoEquipmentIndex=new AtomicInteger(mmoEquipmentPOJOMapper.selectNextIndex());
    }

    @Autowired
    public  void initEquipmentBagPOJOMapper(MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper) {
        DbUtil.equipmentBagPOJOMapper = equipmentBagPOJOMapper;
        equipmentBagIndex=new AtomicInteger(equipmentBagPOJOMapper.selectNextIndex());
    }
    @Autowired
    public  void initMmoRolePOJOMapper(MmoRolePOJOMapper mmoRolePOJOMapper) {
        DbUtil.mmoRolePOJOMapper = mmoRolePOJOMapper;
    }
    @Autowired
    public  void initMmoEmailPOJOMapper(MmoEmailPOJOMapper mmoEmailPOJOMapper) {
        DbUtil.mmoEmailPOJOMapper = mmoEmailPOJOMapper;
        mmoEmailPojoIndex=new AtomicInteger(mmoEmailPOJOMapper.selectNextIndex());
    }
    @Autowired
    public  void initMmoGuildApplyPOJOMapper(MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper) {
        DbUtil.mmoGuildApplyPOJOMapper = mmoGuildApplyPOJOMapper;
    }
    @Autowired
    public  void initMmoGuildRolePOJOMapper(MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper) {
        DbUtil.mmoGuildRolePOJOMapper = mmoGuildRolePOJOMapper;
    }

    /**
     * 背包数据库id
     * @return
     */
    public static Integer getBagPojoNextIndex(){
        return mmoBagPojoIndex.incrementAndGet();
    }
    /**
     * 装备数据库id
     * @return
     */
    public static Integer getEquipmentNextIndex(){
        return mmoEquipmentIndex.incrementAndGet();
    }
    /**
     * 装备栏数据库id
     * @return
     */
    public static Integer getEquipmentBagNextIndex(){
        return equipmentBagIndex.incrementAndGet();
    }
    /**
     * 邮件数据库id
     * @return
     */
    public static Integer getEmailPojoNextIndex(){
        return mmoEmailPojoIndex.incrementAndGet();
    }

    /**
     * 仓库数据库id
     * @return
     */
    public static Integer getWareHouseIndex(){
        return mmoWareHouseIndex.incrementAndGet();
    }

    /**
     * 邮件信息入库
     */
    public static   void mmoEmailPOJOIntoDataBase(MmoEmailBean emailBean){
        MmoEmailPOJO mmoEmailPOJO=new MmoEmailPOJO();
        mmoEmailPOJO.setId(emailBean.getId());
        mmoEmailPOJO.setArticleMessageId(emailBean.getArticleMessageId());
        mmoEmailPOJO.setArticleNum(emailBean.getArticleNum());
        mmoEmailPOJO.setArticleType(emailBean.getArticleType());
        mmoEmailPOJO.setFromRoleId(emailBean.getFromRoleId());
        mmoEmailPOJO.setTitle(emailBean.getTitle());
        mmoEmailPOJO.setToRoleId(emailBean.getToRoleId());
        mmoEmailPOJO.setFromDelete(emailBean.getFromDelete());
        mmoEmailPOJO.setToDelete(emailBean.getToDelete());
        mmoEmailPOJO.setCreateTime(emailBean.getCreateTime());
        mmoEmailPOJO.setChecked(emailBean.getChecked());
        mmoEmailPOJO.setContext(emailBean.getContext());
        mmoEmailPOJO.setIsGet(emailBean.getGet());
        //删除双方都是删除状态的
        if (mmoEmailPOJO.getFromDelete()==true&&mmoEmailPOJO.getToDelete()==true){
            //id小于初始化的id 则代表是旧数据 删除
            if (mmoEmailPOJO.getId()<= EmailServiceProvider.getId()) {
                mmoEmailPOJOMapper.deleteByPrimaryKey(mmoEmailPOJO.getId());
            }
        }else{
            if (mmoEmailPOJO.getId()<=EmailServiceProvider.getId()) {
                mmoEmailPOJOMapper.updateByPrimaryKeySelective(mmoEmailPOJO);
            }else {
                MmoEmailPOJO mmoEmail=mmoEmailPOJOMapper.selectByPrimaryKey(mmoEmailPOJO.getId());
                if (mmoEmail==null) {
                    mmoEmailPOJOMapper.insert(mmoEmailPOJO);
                }else{
                    mmoEmailPOJOMapper.updateByPrimaryKeySelective(mmoEmailPOJO);
                }
            }
        }
    }

    /**
     * 人物信息入库
     */
    public  static void roleInfoIntoDataBase(MmoSimpleRole mmoSimpleRole){
        MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
        mmoRolePOJO.setId(mmoSimpleRole.getId());
        mmoRolePOJO.setStatus(mmoSimpleRole.getStatus());
        mmoRolePOJO.setOnStatus(mmoSimpleRole.getOnStatus());
        mmoRolePOJO.setMmoSceneId(mmoSimpleRole.getMmoSceneId());
        mmoRolePOJO.setName(mmoSimpleRole.getName());
        mmoRolePOJO.setProfessionId(mmoSimpleRole.getProfessionId());
        mmoRolePOJO.setMoney(mmoSimpleRole.getMoney());
        if(mmoSimpleRole.getGuildBean()!=null) {
            mmoRolePOJO.setGuildId(mmoSimpleRole.getGuildBean().getId());
        }else {
            mmoRolePOJO.setGuildId(-1);
        }
//        mmoRolePOJO.setSkillIds(CommonsUtil.listToString(mmoSimpleRole.getSkillIdList()))
        mmoRolePOJO.setType(mmoSimpleRole.getType());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
    }

    /**
     * 背包入库
     * @param backPackManager
     * @param roleId
     */
    public static  void bagIntoDataBase(BackPackManager backPackManager, Integer roleId){
        List<ArticleDto> articles=backPackManager.getBackpacksMessage();
        //需要修改或者新增的记录
        for (ArticleDto a:articles) {
            MmoBagPOJO mmoBagPOJO=new MmoBagPOJO();
            mmoBagPOJO.setArticleType(a.getArticleType());
            mmoBagPOJO.setNumber(a.getQuantity());
            mmoBagPOJO.setRoleId(roleId);
            if (a.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                mmoBagPOJO.setwId(a.getEquipmentId());
            }else{
                mmoBagPOJO.setwId(a.getId());
            }
            if (a.getBagId()!=null){
                mmoBagPOJO.setBagId(a.getBagId());
                mmoBagPOJOMapper.updateByPrimaryKey(mmoBagPOJO);
            }else{
                //新的
                mmoBagPOJOMapper.insert(mmoBagPOJO);
            }
            /**
             * 新产生的装备以及 更新旧 的装备
             */
            if (a.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                MmoEquipmentPOJO e=mmoEquipmentPOJOMapper.selectByPrimaryKey(mmoBagPOJO.getwId());
                if (e==null){
                    e=new MmoEquipmentPOJO();
                    e.setId(a.getEquipmentId());
                    e.setNowDurability(a.getNowDurability());
                    e.setMessageId(a.getId());
                    mmoEquipmentPOJOMapper.insert(e);
                }else{
                    e.setNowDurability(a.getNowDurability());
                    mmoEquipmentPOJOMapper.updateByPrimaryKey(e);
                }
            }
        }
        //需要删除的记录
        List<Integer> bagIds=backPackManager.getNeedDeleteBagId();
        for (Integer id:bagIds){
            mmoBagPOJOMapper.deleteByPrimaryKey(id);
        }
        backPackManager.setNeedDeleteBagId(new ArrayList<>());
    }

    /**
     * 装备入库
     * @param mmoSimpleRole
     */
    public static void equipmentIntoDataBase(MmoSimpleRole mmoSimpleRole){
        List<EquipmentDto> dtos=mmoSimpleRole.getEquipments();
        for (EquipmentDto e:dtos) {
            MmoEquipmentBagPOJO equipmentBagPOJO = new MmoEquipmentBagPOJO();
            //装备栏
            equipmentBagPOJO.setEquipmentId(e.getEquipmentId());
            equipmentBagPOJO.setRoleId(mmoSimpleRole.getId());
            if (e.getEquipmentBagId()!=null) {
                //主键
                equipmentBagPOJO.setEquipmentBagId(e.getEquipmentBagId());
                equipmentBagPOJOMapper.updateByPrimaryKey(equipmentBagPOJO);
            }else{
                equipmentBagPOJOMapper.insert(equipmentBagPOJO);
            }
            //装备入库
            MmoEquipmentPOJO equipmentPOJO=new MmoEquipmentPOJO();
            equipmentPOJO.setId(e.getEquipmentId());
            equipmentPOJO.setMessageId(e.getId());
            equipmentPOJO.setNowDurability(e.getNowDurability());
            MmoEquipmentPOJO pojo=mmoEquipmentPOJOMapper.selectByPrimaryKey(equipmentPOJO.getId());
            if (pojo==null){
                mmoEquipmentPOJOMapper.insert(equipmentPOJO);
            }else{
                mmoEquipmentPOJOMapper.updateByPrimaryKey(equipmentPOJO);
            }
        }
        //需要删除的记录
        List<Integer> equBagIds=mmoSimpleRole.getNeedDeleteEquipmentIds();
        for (Integer id:equBagIds){
            equipmentBagPOJOMapper.deleteByPrimaryKey(id);
        }
        mmoSimpleRole.setNeedDeleteEquipmentIds(new ArrayList<>());
    }

    public static void deleteBagById(Integer bagId) {
        mmoBagPOJOMapper.deleteByPrimaryKey(bagId);
    }

    public static void deleteEquipmentBagById(Integer oldEquipmentBagId) {
        equipmentBagPOJOMapper.deleteByPrimaryKey(oldEquipmentBagId);
    }

    public static void addEquipmentBagPOJO(EquipmentBean equipmentBean,Integer roleId) {
        MmoEquipmentBagPOJO equipmentBagPOJO = new MmoEquipmentBagPOJO();
        //装备栏
        equipmentBagPOJO.setEquipmentId(equipmentBean.getEquipmentId());
        equipmentBagPOJO.setRoleId(roleId);
        equipmentBagPOJO.setEquipmentBagId(equipmentBean.getEquipmentBagId());
        equipmentBagPOJOMapper.insert(equipmentBagPOJO);
    }

    public static void updateBagMedicine(MedicineBean medicineBean, Integer roleId) {
        MmoBagPOJO mmoBagPOJO=new MmoBagPOJO();
        mmoBagPOJO.setArticleType(medicineBean.getArticleTypeCode());
        mmoBagPOJO.setNumber(medicineBean.getQuantity());
        mmoBagPOJO.setRoleId(roleId);
        mmoBagPOJO.setwId(medicineBean.getMedicineMessageId());
        mmoBagPOJO.setBagId(medicineBean.getBagId());
        mmoBagPOJOMapper.updateByPrimaryKey(mmoBagPOJO);
    }

    public static void insertBag(ArticleDto a,Integer roleId) {
        MmoBagPOJO mmoBagPOJO=new MmoBagPOJO();
        mmoBagPOJO.setArticleType(a.getArticleType());
        mmoBagPOJO.setNumber(a.getQuantity());
        mmoBagPOJO.setRoleId(roleId);
        if (a.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
            mmoBagPOJO.setwId(a.getEquipmentId());
        }else{
            mmoBagPOJO.setwId(a.getId());
        }
        mmoBagPOJO.setBagId(a.getBagId());
        mmoBagPOJOMapper.insert(mmoBagPOJO);
    }

    public static void updateEquipment(EquipmentBean e) {
        MmoEquipmentPOJO equipmentPOJO=new MmoEquipmentPOJO();
        equipmentPOJO.setId(e.getEquipmentId());
        equipmentPOJO.setMessageId(e.getEquipmentMessageId());
        equipmentPOJO.setNowDurability(e.getNowDurability());
        mmoEquipmentPOJOMapper.updateByPrimaryKey(equipmentPOJO);
    }

    public static void updateEmailBeanDb(MmoEmailBean emailBean) {
        MmoEmailPOJO mmoEmailPOJO=new MmoEmailPOJO();
        mmoEmailPOJO.setId(emailBean.getId());
        mmoEmailPOJO.setArticleMessageId(emailBean.getArticleMessageId());
        mmoEmailPOJO.setArticleNum(emailBean.getArticleNum());
        mmoEmailPOJO.setArticleType(emailBean.getArticleType());
        mmoEmailPOJO.setFromRoleId(emailBean.getFromRoleId());
        mmoEmailPOJO.setTitle(emailBean.getTitle());
        mmoEmailPOJO.setToRoleId(emailBean.getToRoleId());
        mmoEmailPOJO.setFromDelete(emailBean.getFromDelete());
        mmoEmailPOJO.setToDelete(emailBean.getToDelete());
        mmoEmailPOJO.setCreateTime(emailBean.getCreateTime());
        mmoEmailPOJO.setChecked(emailBean.getChecked());
        mmoEmailPOJO.setContext(emailBean.getContext());
        mmoEmailPOJO.setIsGet(emailBean.getGet());
        //删除双方都是删除状态的
        if (mmoEmailPOJO.getFromDelete()==true&&mmoEmailPOJO.getToDelete()==true){
            //id小于初始化的id 则代表是旧数据 删除
            mmoEmailPOJOMapper.deleteByPrimaryKey(mmoEmailPOJO.getId());
        }else{
            mmoEmailPOJOMapper.updateByPrimaryKeySelective(mmoEmailPOJO);
        }
    }

    public static void insertEquipment(EquipmentBean equipmentBean) {
        MmoEquipmentPOJO mmoEquipmentPOJO=new MmoEquipmentPOJO();
        mmoEquipmentPOJO.setId(equipmentBean.getEquipmentId());
        mmoEquipmentPOJO.setNowDurability(equipmentBean.getNowDurability());
        mmoEquipmentPOJO.setMessageId(equipmentBean.getEquipmentMessageId());
        mmoEquipmentPOJOMapper.insert(mmoEquipmentPOJO);
    }

    public static void updateRole(MmoSimpleRole mmoSimpleRole) {
        MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
        mmoRolePOJO.setId(mmoSimpleRole.getId());
        mmoRolePOJO.setStatus(mmoSimpleRole.getStatus());
        mmoRolePOJO.setOnStatus(mmoSimpleRole.getOnStatus());
        mmoRolePOJO.setMmoSceneId(mmoSimpleRole.getMmoSceneId());
        mmoRolePOJO.setName(mmoSimpleRole.getName());
        mmoRolePOJO.setProfessionId(mmoSimpleRole.getProfessionId());
        mmoRolePOJO.setMoney(mmoSimpleRole.getMoney());
        if(mmoSimpleRole.getGuildBean()!=null) {
            mmoRolePOJO.setGuildId(mmoSimpleRole.getGuildBean().getId());
        }else {
            mmoRolePOJO.setGuildId(-1);
        }
        mmoRolePOJO.setType(mmoSimpleRole.getType());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
    }

    public static void deleteWareHouseById(Integer wareHouseDBId) {
        mmoWareHousePOJOMapper.deleteByPrimaryKey(wareHouseDBId);
    }

    public static void insertEquipmentWareHouse(ArticleDto articleDto, Integer guildId) {
        MmoWareHousePOJO mmoWareHousePOJO=new MmoWareHousePOJO();
        mmoWareHousePOJO.setId(articleDto.getWareHouseDBId());
        mmoWareHousePOJO.setArticleType(articleDto.getArticleType());
        mmoWareHousePOJO.setGuildId(guildId);
        mmoWareHousePOJO.setArticleMessageId(articleDto.getEquipmentId());
        mmoWareHousePOJO.setNumber(articleDto.getQuantity());
        mmoWareHousePOJOMapper.insert(mmoWareHousePOJO);
    }
    public static void insertMedicineWareHouse(ArticleDto articleDto, Integer guildId) {
        MmoWareHousePOJO mmoWareHousePOJO=new MmoWareHousePOJO();
        mmoWareHousePOJO.setId(articleDto.getWareHouseDBId());
        mmoWareHousePOJO.setArticleType(articleDto.getArticleType());
        mmoWareHousePOJO.setGuildId(guildId);
        mmoWareHousePOJO.setArticleMessageId(articleDto.getId());
        mmoWareHousePOJO.setNumber(articleDto.getQuantity());
        mmoWareHousePOJOMapper.insert(mmoWareHousePOJO);
    }

    public static void updateWareHouseMedicine(MedicineBean temp, Integer guildId) {
        MmoWareHousePOJO mmoWareHousePOJO=new MmoWareHousePOJO();
        mmoWareHousePOJO.setId(temp.getWareHouseDBId());
        mmoWareHousePOJO.setArticleType(temp.getArticleTypeCode());
        mmoWareHousePOJO.setGuildId(guildId);
        mmoWareHousePOJO.setArticleMessageId(temp.getMedicineMessageId());
        mmoWareHousePOJO.setNumber(temp.getQuantity());
        mmoWareHousePOJOMapper.updateByPrimaryKey(mmoWareHousePOJO);
    }
}
