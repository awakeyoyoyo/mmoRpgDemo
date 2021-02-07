package com.liqihao.util;

import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.dao.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.BossMessage;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.EmailBean;
import com.liqihao.pojo.bean.articleBean.Article;
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
    /**
     * 更新背包物品
     * @param article
     * @param roleId
     */
    public static void updateBagPojo(Article article,Integer roleId) {
        synchronized (article) {
            MmoBagPOJO mmoBagPOJO = new MmoBagPOJO();
            if (article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                EquipmentBean equipmentBean = (EquipmentBean) article;
                mmoBagPOJO.setwId(equipmentBean.getEquipmentId());
            } else {
                mmoBagPOJO.setwId(article.getArticleMessageId());
            }
            mmoBagPOJO.setNumber(article.getQuantity());
            mmoBagPOJO.setArticleType(article.getArticleTypeCode());
            mmoBagPOJO.setBagId(article.getBagId());
            mmoBagPOJO.setRoleId(roleId);
            article.setChangeFlag(false);
            mmoBagPOJOMapper.updateByPrimaryKey(mmoBagPOJO);
        }
    }

    /**
     * 更新仓库物品
     * @param article
     * @param id
     */
    public static void updateWareHousePojo(Article article, Integer id) {
        MmoWareHousePOJO mmoWareHousePOJO=new MmoWareHousePOJO();
        mmoWareHousePOJO.setId(article.getWareHouseDBId());
        mmoWareHousePOJO.setArticleType(article.getArticleTypeCode());
        mmoWareHousePOJO.setGuildId(id);
        if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            mmoWareHousePOJO.setArticleMessageId(article.getArticleMessageId());
        }else{
            EquipmentBean equipmentBean= (EquipmentBean) article;
            mmoWareHousePOJO.setArticleMessageId(equipmentBean.getEquipmentId());
        }
        mmoWareHousePOJO.setNumber(article.getQuantity());
        ScheduledThreadPoolUtil.addTask(() ->mmoWareHousePOJOMapper.updateByPrimaryKey(mmoWareHousePOJO));
    }

    /**
     * 删除装备
     * @param equipmentId
     */
    public static void deleteEquipmentById(Integer equipmentId) {
        ScheduledThreadPoolUtil.addTask(() ->mmoEquipmentPOJOMapper.deleteByPrimaryKey(equipmentId));
    }

    /**
     * 更新RolePojo
     * @param mmoRolePOJO
     */
    public static void updateRolePOJO(MmoRolePOJO mmoRolePOJO) {
        ScheduledThreadPoolUtil.addTask(()->mmoRolePOJOMapper.updateByPrimaryKey(mmoRolePOJO));
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
    public static   void mmoEmailPOJOIntoDataBase(EmailBean emailBean){
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
        mmoEmailPOJO.setIsGet(emailBean.getGetFlag());
        mmoEmailPOJO.setMoney(emailBean.getMoney());
        mmoEmailPOJO.setEquipmentId(emailBean.getEquipmentId()==null?-1:emailBean.getEquipmentId());
        mmoEmailPOJO.setIsGetMoney(emailBean.getGetMoneyFlag());
        //删除双方都是删除状态的
        if (mmoEmailPOJO.getFromDelete()==true&&mmoEmailPOJO.getToDelete()==true){
            //id小于初始化的id 则代表是旧数据 删除
            if (mmoEmailPOJO.getId()<= EmailServiceProvider.getId()) {
                ScheduledThreadPoolUtil.addTask(() ->mmoEmailPOJOMapper.deleteByPrimaryKey(mmoEmailPOJO.getId()));
            }
        }else{
            if (mmoEmailPOJO.getId()<=EmailServiceProvider.getId()) {
                ScheduledThreadPoolUtil.addTask(() ->mmoEmailPOJOMapper.updateByPrimaryKeySelective(mmoEmailPOJO));
            }else {
                MmoEmailPOJO mmoEmail=mmoEmailPOJOMapper.selectByPrimaryKey(mmoEmailPOJO.getId());
                if (mmoEmail==null) {
                    ScheduledThreadPoolUtil.addTask(() ->mmoEmailPOJOMapper.insert(mmoEmailPOJO));
                }else{
                    ScheduledThreadPoolUtil.addTask(() ->mmoEmailPOJOMapper.updateByPrimaryKeySelective(mmoEmailPOJO));
                }
            }
        }
    }

    /**
     * db删除背包
     * @param bagId
     */
    public static void deleteBagById(Integer bagId) {
        ScheduledThreadPoolUtil.addTask(() -> mmoBagPOJOMapper.deleteByPrimaryKey(bagId));
    }

    /**
     * db删除装备栏
     * @param oldEquipmentBagId
     */
    public static void deleteEquipmentBagById(Integer oldEquipmentBagId) {
        ScheduledThreadPoolUtil.addTask(() ->equipmentBagPOJOMapper.deleteByPrimaryKey(oldEquipmentBagId));
    }

    /**
     * 装备栏插入数据库
     * @param equipmentBean
     * @param roleId
     */
    public static void addEquipmentBagPOJO(EquipmentBean equipmentBean,Integer roleId) {
        MmoEquipmentBagPOJO equipmentBagPOJO = new MmoEquipmentBagPOJO();
        //装备栏
        equipmentBagPOJO.setEquipmentId(equipmentBean.getEquipmentId());
        equipmentBagPOJO.setRoleId(roleId);
        equipmentBagPOJO.setEquipmentBagId(equipmentBean.getEquipmentBagId());
        ScheduledThreadPoolUtil.addTask(() ->equipmentBagPOJOMapper.insert(equipmentBagPOJO));
    }

    /**
     * 更新背包药品
     * @param medicineBean
     * @param roleId
     */
    public static void updateBagMedicine(MedicineBean medicineBean, Integer roleId) {
        MmoBagPOJO mmoBagPOJO=new MmoBagPOJO();
        mmoBagPOJO.setArticleType(medicineBean.getArticleTypeCode());
        mmoBagPOJO.setNumber(medicineBean.getQuantity());
        mmoBagPOJO.setRoleId(roleId);
        mmoBagPOJO.setwId(medicineBean.getArticleMessageId());
        mmoBagPOJO.setBagId(medicineBean.getBagId());
        ScheduledThreadPoolUtil.addTask(() -> mmoBagPOJOMapper.updateByPrimaryKey(mmoBagPOJO));
    }

    /**
     * 插入背包
     * @param a
     * @param roleId
     */
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
        ScheduledThreadPoolUtil.addTask(() -> mmoBagPOJOMapper.insert(mmoBagPOJO));
    }

    /**
     * 更新装备
     * @param e
     */
    public static void updateEquipment(EquipmentBean e) {
        MmoEquipmentPOJO equipmentPOJO=new MmoEquipmentPOJO();
        equipmentPOJO.setId(e.getEquipmentId());
        equipmentPOJO.setMessageId(e.getArticleMessageId());
        equipmentPOJO.setNowDurability(e.getNowDurability());
        ScheduledThreadPoolUtil.addTask(() ->mmoEquipmentPOJOMapper.updateByPrimaryKey(equipmentPOJO));
    }

    /**
     * 更新邮件
     * @param emailBean
     */
    public static void updateEmailBeanDb(EmailBean emailBean) {
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
        mmoEmailPOJO.setIsGet(emailBean.getGetFlag());
        mmoEmailPOJO.setEquipmentId(emailBean.getEquipmentId());
        mmoEmailPOJO.setMoney(emailBean.getMoney());
        mmoEmailPOJO.setIsGetMoney(emailBean.getGetMoneyFlag());
        //删除双方都是删除状态的
        if (mmoEmailPOJO.getFromDelete()==true&&mmoEmailPOJO.getToDelete()==true){
            //id小于初始化的id 则代表是旧数据 删除
            ScheduledThreadPoolUtil.addTask(() ->mmoEmailPOJOMapper.deleteByPrimaryKey(mmoEmailPOJO.getId()));
        }else{
            ScheduledThreadPoolUtil.addTask(() ->mmoEmailPOJOMapper.updateByPrimaryKeySelective(mmoEmailPOJO));
        }
    }

    /**
     * 插入装备
     * @param equipmentBean
     */
    public static void insertEquipment(EquipmentBean equipmentBean) {
        MmoEquipmentPOJO mmoEquipmentPOJO=new MmoEquipmentPOJO();
        mmoEquipmentPOJO.setId(equipmentBean.getEquipmentId());
        mmoEquipmentPOJO.setNowDurability(equipmentBean.getNowDurability());
        mmoEquipmentPOJO.setMessageId(equipmentBean.getArticleMessageId());
        ScheduledThreadPoolUtil.addTask(() ->mmoEquipmentPOJOMapper.insert(mmoEquipmentPOJO));
    }

    /**
     * 更新角色
     * @param mmoSimpleRole
     */
    public static void updateRole(MmoSimpleRole mmoSimpleRole) {
        MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
        mmoRolePOJO.setId(mmoSimpleRole.getId());
        mmoRolePOJO.setStatus(mmoSimpleRole.getStatus());
        mmoRolePOJO.setOnStatus(mmoSimpleRole.getOnStatus());
        mmoRolePOJO.setMmoSceneId(mmoSimpleRole.getMmoSceneId());
        mmoRolePOJO.setName(mmoSimpleRole.getName());
        mmoRolePOJO.setProfessionId(mmoSimpleRole.getProfessionId());
        mmoRolePOJO.setMoney(mmoSimpleRole.getMoney());
        mmoRolePOJO.setExp(mmoSimpleRole.getExp());
        mmoRolePOJO.setFriendIds(CommonsUtil.listToString(mmoSimpleRole.getFriends()));
        if(mmoSimpleRole.getGuildBean()!=null) {
            mmoRolePOJO.setGuildId(mmoSimpleRole.getGuildBean().getId());
        }else {
            mmoRolePOJO.setGuildId(-1);
        }
        mmoRolePOJO.setType(mmoSimpleRole.getType());
        ScheduledThreadPoolUtil.addTask(() -> mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO));
    }

    /**
     * 删除仓库
     * @param wareHouseDBId
     */
    public static void deleteWareHouseById(Integer wareHouseDBId) {
        ScheduledThreadPoolUtil.addTask(() ->mmoWareHousePOJOMapper.deleteByPrimaryKey(wareHouseDBId));
    }

    /**
     * 插入仓库
     * @param articleDto
     * @param guildId
     */
    public static void insertEquipmentWareHouse(ArticleDto articleDto, Integer guildId) {
        MmoWareHousePOJO mmoWareHousePOJO=new MmoWareHousePOJO();
        mmoWareHousePOJO.setId(articleDto.getWareHouseDBId());
        mmoWareHousePOJO.setArticleType(articleDto.getArticleType());
        mmoWareHousePOJO.setGuildId(guildId);
        mmoWareHousePOJO.setArticleMessageId(articleDto.getEquipmentId());
        mmoWareHousePOJO.setNumber(articleDto.getQuantity());
        ScheduledThreadPoolUtil.addTask(() -> mmoWareHousePOJOMapper.insert(mmoWareHousePOJO));
    }

    /**
     * 插入仓库
     * @param articleDto
     * @param guildId
     */
    public static void insertMedicineWareHouse(ArticleDto articleDto, Integer guildId) {
        MmoWareHousePOJO mmoWareHousePOJO=new MmoWareHousePOJO();
        mmoWareHousePOJO.setId(articleDto.getWareHouseDBId());
        mmoWareHousePOJO.setArticleType(articleDto.getArticleType());
        mmoWareHousePOJO.setGuildId(guildId);
        mmoWareHousePOJO.setArticleMessageId(articleDto.getId());
        mmoWareHousePOJO.setNumber(articleDto.getQuantity());
        ScheduledThreadPoolUtil.addTask(() ->mmoWareHousePOJOMapper.insert(mmoWareHousePOJO));
    }

    /**
     * 更新仓库
     * @param temp
     * @param guildId
     */
    public static void updateWareHouseMedicine(MedicineBean temp, Integer guildId) {
        MmoWareHousePOJO mmoWareHousePOJO=new MmoWareHousePOJO();
        mmoWareHousePOJO.setId(temp.getWareHouseDBId());
        mmoWareHousePOJO.setArticleType(temp.getArticleTypeCode());
        mmoWareHousePOJO.setGuildId(guildId);
        mmoWareHousePOJO.setArticleMessageId(temp.getArticleMessageId());
        mmoWareHousePOJO.setNumber(temp.getQuantity());
        ScheduledThreadPoolUtil.addTask(() ->mmoWareHousePOJOMapper.updateByPrimaryKey(mmoWareHousePOJO));
    }
}
