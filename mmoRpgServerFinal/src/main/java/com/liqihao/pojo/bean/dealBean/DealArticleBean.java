package com.liqihao.pojo.bean.dealBean;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import org.omg.CORBA.PRIVATE_MEMBER;

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
    private Integer money;
    private boolean confirm;

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
    }

    public boolean abandon(Article article) {
    }

    public boolean abandonMoney(Integer money) throws RpgServerException {
        if (getMoney()<money){
            throw new RpgServerException(StateCode.FAIL,"没有这么多的金币");
        }
        setMoney(getMoney()-money);
        return true;
    }
}
