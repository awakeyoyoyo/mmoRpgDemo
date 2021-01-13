package com.liqihao.pojo.bean.guildBean;

import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.GuildRolePositionCode;
import com.liqihao.provider.GuildServiceProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 公会实例bean
 * @author lqhao
 */
public class GuildBean {
    /**
     * 公会id
     */
    private Integer id;
    /**
     * 公会名称
     */
    private String name;
    /**
     * 会长名称
     */
    private Integer chairmanId;
    /**
     * 公会人数
     */
    private Integer peopleNum;
    /**
     * 公会等级
     */
    private Integer level;
    /**
     * 公会仓库
     */
    private WareHouseManager wareHouseManager;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 人员id集合
     */
    private List<GuildRoleBean> guildRoleBeans=new ArrayList<>();
    /**
     * 申请入公会请求
     */
    private ArrayList<GuildApplyBean> guildApplyBeans=new ArrayList<>();

    public ArrayList<GuildApplyBean> getGuildApplyBeans() {
        return guildApplyBeans;
    }

    public void setGuildApplyBeans(ArrayList<GuildApplyBean> guildApplyBeans) {
        this.guildApplyBeans = guildApplyBeans;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChairmanId() {
        return chairmanId;
    }

    public void setChairmanId(Integer chairmanId) {
        this.chairmanId = chairmanId;
    }

    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public WareHouseManager getWareHouseManager() {
        return wareHouseManager;
    }

    public void setWareHouseManager(WareHouseManager wareHouseManager) {
        this.wareHouseManager = wareHouseManager;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<GuildRoleBean> getGuildRoleBeans() {
        return guildRoleBeans;
    }

    public void setGuildRoleBeans(List<GuildRoleBean> guildRoleBeans) {
        this.guildRoleBeans = guildRoleBeans;
    }

    /**
     * 检查申请是否过时
     */
    private void checkOutTime(){
        Iterator iterator=guildApplyBeans.iterator();
        List<Integer> outTimeApplyIds=new ArrayList<>();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()){
            GuildApplyBean bean= (GuildApplyBean) iterator.next();
            if (bean.getEndTime()<System.currentTimeMillis()){
                guildApplyBeans.remove(bean);
                outTimeApplyIds.add(bean.getId());
            }
        }
        GuildServiceProvider.getInstance().deleteApply(outTimeApplyIds);
    }

    /**
     * 公会申请
     * @param guildApplyBean
     */
    public void addGuildApplyBean(GuildApplyBean guildApplyBean) {
        //每次插入的时候删除过时的
        synchronized (guildApplyBeans) {
            checkOutTime();
            guildApplyBeans.add(guildApplyBean);
        }
    }

    /**
     * 拒绝申请
     */
    public void refuseApply(Integer guildApplyId) throws RpgServerException {
        synchronized (guildApplyBeans) {
            checkOutTime();
            Iterator iterator = guildApplyBeans.iterator();
            //每次插入都删除申请过时或者
            //todo
            while (iterator.hasNext()) {
                GuildApplyBean bean = (GuildApplyBean) iterator.next();
                if (guildApplyId.equals(bean.getId())) {
                    guildApplyBeans.remove(bean);
                    GuildServiceProvider.getInstance().deleteApply(new ArrayList<>(bean.getId()));
                }
            }
        }
    }

    /**
     * 通过申请
     */
    public void agreeApply(Integer guildApplyId) throws RpgServerException {
        synchronized (guildApplyBeans) {
            checkOutTime();
            Iterator iterator = guildApplyBeans.iterator();
            //每次插入都删除申请过时或者
            while (iterator.hasNext()) {
                GuildApplyBean bean = (GuildApplyBean) iterator.next();
                if (guildApplyId.equals(bean.getId())) {
                    peopleNum = peopleNum + 1;
                    //guild成员bean 增加
                    GuildRoleBean guildRoleBean = new GuildRoleBean();
                    guildRoleBean.setRoleId(bean.getRoleId());
                    guildRoleBean.setGuildId(getId());
                    guildRoleBean.setContribution(0);
                    guildRoleBean.setGuildPositionId(GuildRolePositionCode.COMMON_PEOPLE.getCode());
                    Integer id = GuildServiceProvider.getInstance().insertGuildRolePOJO(guildRoleBean);
                    guildRoleBean.setId(id);
                    synchronized (guildRoleBeans) {
                        getGuildRoleBeans().add(guildRoleBean);
                    }
                    //删除申请记录
                    guildApplyBeans.remove(bean);
                    GuildServiceProvider.getInstance().deleteApply(new ArrayList<>(bean.getId()));
                    break;
                }
            }
        }
    }

    /**
     * 离开公会
     */
    public void leaveGuild(Integer roleId) throws RpgServerException {
        synchronized (guildRoleBeans) {
            Iterator iterator = guildRoleBeans.iterator();
            while (iterator.hasNext()) {
                GuildRoleBean roleBean = (GuildRoleBean) iterator.next();
                if (roleId.equals(roleBean.getRoleId())) {
                    peopleNum = peopleNum - 1;
                    guildRoleBeans.remove(roleBean);
                    //数据库删除该人的记录
                    GuildServiceProvider.getInstance().deletePeople(roleBean.getId());
                    break;
                }
            }
        }
    }

    /**
     * 获取公会的信息
     */
    public void guildMessage() throws RpgServerException {
       //todo
    }

    /**
     * 设置公会成员职位
     */
    public void setRolePosition(Integer toRoleId,Integer position) throws RpgServerException {
        Iterator iterator=guildRoleBeans.iterator();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()){
            GuildRoleBean roleBean= (GuildRoleBean) iterator.next();
            if (toRoleId.equals(roleBean.getRoleId())){
                roleBean.setGuildPositionId(position);
                break;
            }
        }
    }

    /**
     * 返回玩家在公会的信息
     * @param roleId
     * @return
     */
    public GuildRoleBean getRoleGuildMsg(Integer roleId){
        Iterator iterator=guildRoleBeans.iterator();
        //每次插入都删除申请过时或者
        while (iterator.hasNext()){
            GuildRoleBean roleBean= (GuildRoleBean) iterator.next();
            if (roleId.equals(roleBean.getRoleId())){
                return roleBean;
            }
        }
        return null;
    }
}
