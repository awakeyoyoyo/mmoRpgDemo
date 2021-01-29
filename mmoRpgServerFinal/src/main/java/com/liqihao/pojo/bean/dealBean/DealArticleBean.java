package com.liqihao.pojo.bean.dealBean;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 交易物品存储类
 * @author lqhao
 */
public class DealArticleBean {
    private MmoSimpleRole role;
    private AtomicInteger dealArticleIdAuto=new AtomicInteger(0);
    private List<Article> articles;
    private Integer size=10;
    private AtomicInteger nowSize=new AtomicInteger(0);
    private Integer money;
    private boolean confirm;

    public int addAndReturnNowSize(){
        return  nowSize.incrementAndGet();
    }
    public int reduceAndReturnNowSize(){
        return  nowSize.decrementAndGet();
    }
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    public int getNowSize(){
        return nowSize.get();
    }
    public MmoSimpleRole getRole() {
        return role;
    }

    public void setRole(MmoSimpleRole role) {
        this.role = role;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public boolean put(Article article) {
        //判断物品类型
        return article.putDealBean(this);
    }
    public synchronized Article abandon(Integer dealArticleId,Integer num) {
        Iterator iterator=articles.iterator();
        while (iterator.hasNext()) {
            Article a= (Article) iterator.next();
            if (a.getDealArticleId().equals(dealArticleId)) {
                return a.abandonDealBean(num,this);
            }
        }
        return null;
    }

    public Integer addAndReturnDealArticleId() {
        return dealArticleIdAuto.incrementAndGet();
    }

    public List<ArticleDto> getArticleDto(){
        List<ArticleDto> articleDtos=new ArrayList<>();
        for (Article article : getArticles()) {
            ArticleDto articleDto=article.getArticleMessage();
            articleDtos.add(articleDto);
        }
        return articleDtos;
    }
}
