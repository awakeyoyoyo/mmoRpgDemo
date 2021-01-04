package com.liqihao.provider;

import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.dao.MmoEmailPOJOMapper;
import com.liqihao.pojo.MmoEmailPOJO;
import com.liqihao.pojo.bean.MmoEmailBean;
import com.liqihao.pojo.bean.MmoSimpleRole;
import com.liqihao.util.CommonsUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.print.DocFlavor;
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
    MmoEmailPOJOMapper mmoEmailPOJOMapper;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MmoEmailPOJOMapper mmoEmailPOJOMapper=(MmoEmailPOJOMapper)applicationContext.getBean("mmoEmailPOJOMapper");
        this.mmoEmailPOJOMapper=mmoEmailPOJOMapper;
        Integer index=mmoEmailPOJOMapper.selectNextIndex();
        emailBeanIdAuto=new AtomicInteger(index);
        id=index-1;
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
    public static void sendArticleEmail(MmoSimpleRole fromRole, MmoSimpleRole toRole, MmoEmailBean emailBean){
        emailBean.setId(emailBeanIdAuto.incrementAndGet());
        emailBean.setHasArticle(emailBean.getArticleId()!=-1);
        emailBean.setCreateTime(System.currentTimeMillis());
        emailBean.setChecked(false);
        emailBean.setFromDelete(false);
        emailBean.setToDelete(false);
        emailBean.setIntoDataBase(false);
        fromRole.getFromMmoEmailBeanConcurrentHashMap().put(emailBean.getId(),emailBean);
        if (toRole!=null){
            toRole.getToMmoEmailBeanConcurrentHashMap().put(emailBean.getId(),emailBean);
            return;
        }else{
            // 插入数据库
            emailBean.setIntoDataBase(true);
            CommonsUtil.mmoEmailPOJOIntoDataBase(emailBean);
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
    public static MmoEmailBean getEmailMessage(MmoSimpleRole role, Integer emailId){
        MmoEmailBean mmoEmailBean=role.getToMmoEmailBeanConcurrentHashMap().get(emailId);
        if (mmoEmailBean!=null&&mmoEmailBean.getToDelete()){
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
                    map.remove(emailId);
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
                    map.remove(emailId);
                    break;
                }
            }
        }
    }


}
