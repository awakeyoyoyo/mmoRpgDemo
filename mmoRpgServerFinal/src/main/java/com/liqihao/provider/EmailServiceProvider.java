package com.liqihao.provider;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.dao.MmoEmailPOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.MmoEmailPOJO;
import com.liqihao.pojo.MmoUserPOJO;
import com.liqihao.pojo.bean.MmoEmailBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.DbUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.rmi.CORBA.Util;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 发送邮件提供类
 * @author lqhao
 */
@Component
public class EmailServiceProvider implements ApplicationContextAware {
    private final Logger log = LoggerFactory.getLogger(EmailServiceProvider.class);
    private static MmoUserPOJOMapper userPOJOMapper;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MmoEmailPOJOMapper mmoEmailPOJOMapper=(MmoEmailPOJOMapper)applicationContext.getBean("mmoEmailPOJOMapper");
        userPOJOMapper=(MmoUserPOJOMapper)applicationContext.getBean("mmoUserPOJOMapper");
        Integer index=mmoEmailPOJOMapper.selectNextIndex();
        emailBeanIdAuto=new AtomicInteger(index);
        id=index-1;
        log.info("EmailServiceProvider：数据库下一个主键index:"+index+" 之前有id："+id);
    }

    /**
     * 自增id
     */
    private static AtomicInteger emailBeanIdAuto;
    private static Integer id;
    private volatile static EmailServiceProvider instance;
    private EmailServiceProvider() {

    }
    public static EmailServiceProvider getInstance() {
        return instance;
    }

    public static Integer getId() {
        return id;
    }

    public static void setId(Integer id) {
        EmailServiceProvider.id = id;
    }

    /**
     * 发送邮件
     */
    public static void sendArticleEmail(MmoSimpleRole fromRole, MmoSimpleRole toRole, MmoEmailBean emailBean) throws RpgServerException {
        emailBean.setId(emailBeanIdAuto.incrementAndGet());
        emailBean.setHasArticle(emailBean.getArticleMessageId()!=-1);
        emailBean.setCreateTime(System.currentTimeMillis());
        emailBean.setChecked(false);
        emailBean.setFromDelete(false);
        emailBean.setToDelete(false);
        emailBean.setIntoDataBase(false);
        emailBean.setGetMoney(false);
        if (fromRole!=null) {
            fromRole.getFromMmoEmailBeanConcurrentHashMap().put(emailBean.getId(), emailBean);
        }
        if (toRole!=null){
            toRole.getToMmoEmailBeanConcurrentHashMap().put(emailBean.getId(),emailBean);
            ScheduledThreadPoolUtil.addTask(() -> DbUtil.mmoEmailPOJOIntoDataBase(emailBean));
        }else{
            // 插入数据库
            //查看是否有该玩家
            MmoUserPOJO userPOJO=userPOJOMapper.selectByPrimaryKey(emailBean.getToRoleId());
            if (userPOJO==null){
                throw new RpgServerException(StateCode.FAIL,"该用户不存在");
            }
            emailBean.setIntoDataBase(true);
            ScheduledThreadPoolUtil.addTask(() -> DbUtil.mmoEmailPOJOIntoDataBase(emailBean));
        }
    }
    /**
     * 获取接收邮件列表
     */
    public static List<MmoEmailBean> getToEmails(MmoSimpleRole role){
        return role.getToMmoEmailBeanConcurrentHashMap().values().stream().filter(e->e.getToDelete().equals(false)).collect(Collectors.toList());
    }
    /**
     * 获取已发送邮件列表
     */
    public static List<MmoEmailBean> getFromEmails(MmoSimpleRole role){
        return role.getFromMmoEmailBeanConcurrentHashMap().values().stream().filter(e->e.getFromDelete().equals(false)).collect(Collectors.toList());
    }
    /**
     * 获取邮件详情
     */
    public static MmoEmailBean getEmailMessage(MmoSimpleRole role, Integer emailId) throws RpgServerException {
        MmoEmailBean mmoEmailBean=role.getToMmoEmailBeanConcurrentHashMap().get(emailId);
        if(mmoEmailBean==null){
            mmoEmailBean=role.getFromMmoEmailBeanConcurrentHashMap().get(emailId);
        }
        if(mmoEmailBean==null) {
            throw new RpgServerException(StateCode.FAIL, "没有该邮件");
        }
        if (role.getId().equals(mmoEmailBean.getToRoleId())) {
            mmoEmailBean.setChecked(true);
        }
        if (mmoEmailBean.getToDelete()){
            return null;
        }
        return mmoEmailBean;
    }

    public static void deleteAcceptEmail(MmoSimpleRole mmoSimpleRole, Integer emailId) {
        ConcurrentHashMap<Integer,MmoEmailBean> map = mmoSimpleRole.getToMmoEmailBeanConcurrentHashMap();
        synchronized (map) {
            Iterator<Integer> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                Integer id=iterator.next();
                if (id.equals(emailId)){
                    MmoEmailBean emailBean=map.get(emailId);
                    emailBean.setToDelete(true);
                    if (emailBean.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())&&!emailBean.getGet()){
                        //消除武器
                        Integer equipmentId=emailBean.getEquipmentId();
                        ScheduledThreadPoolUtil.addTask(() ->DbUtil.deleteEquipmentById(equipmentId));
                    }
                    ScheduledThreadPoolUtil.addTask(() -> DbUtil.updateEmailBeanDb(emailBean));
                    break;
                }
            }
        }
    }

    public static void deleteIsSendEmail(MmoSimpleRole mmoSimpleRole, Integer emailId) {
        ConcurrentHashMap<Integer,MmoEmailBean> map = mmoSimpleRole.getFromMmoEmailBeanConcurrentHashMap();
        synchronized (map) {
            Iterator<Integer> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                Integer id=iterator.next();
                if (id.equals(emailId)){
                    MmoEmailBean emailBean=map.get(emailId);
                    emailBean.setFromDelete(true);
                    ScheduledThreadPoolUtil.addTask(() -> DbUtil.updateEmailBeanDb(emailBean));
                    break;
                }
            }
        }
    }
}
