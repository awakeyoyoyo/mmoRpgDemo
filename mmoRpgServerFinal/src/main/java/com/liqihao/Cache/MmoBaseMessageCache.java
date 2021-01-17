package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.DetailBaseMessage;
import com.liqihao.pojo.baseMessage.GuildBaseMessage;
import com.liqihao.pojo.baseMessage.RoleBaseMessage;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 基本信息缓存
 * @author lqhao
 */
@Component
public class MmoBaseMessageCache {
    private static String baseRoleMessage_file = "classpath:message/baseRoleMessage.xlsx";
    private static String baseDetailMessage_file = "classpath:message/baseDetailMessage.xlsx";
    private static String baseGuildMessage_file = "classpath:message/guildBaseMessage.xlsx";
    /**
     * 用户角色的基本信息
     */
    private RoleBaseMessage baseRoleMessage;
    /**
     * 基础配置信息
     */
    private DetailBaseMessage baseDetailMessage;
    /**
     * 公会基本信息
     */
    private GuildBaseMessage guildBaseMessage;

    private volatile  static MmoBaseMessageCache instance;

    public static MmoBaseMessageCache getInstance(){
        return instance;
    }
    public MmoBaseMessageCache() {
    }

    public MmoBaseMessageCache(RoleBaseMessage baseRoleMessage, DetailBaseMessage baseDetailMessage) {
        this.baseRoleMessage = baseRoleMessage;
        this.baseDetailMessage = baseDetailMessage;
    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
       instance=this;
       baseRoleMessage= ExcelReaderUtil.readExcelFromFileName(baseRoleMessage_file, RoleBaseMessage.class).get(0);
       baseDetailMessage=ExcelReaderUtil.readExcelFromFileName(baseDetailMessage_file, DetailBaseMessage.class).get(0);
       guildBaseMessage=ExcelReaderUtil.readExcelFromFileName(baseGuildMessage_file, GuildBaseMessage.class).get(0);
    }

    public GuildBaseMessage getGuildBaseMessage() {
        return guildBaseMessage;
    }

    public void setGuildBaseMessage(GuildBaseMessage guildBaseMessage) {
        this.guildBaseMessage = guildBaseMessage;
    }

    public RoleBaseMessage getBaseRoleMessage() {
        return baseRoleMessage;
    }

    public  void setBaseRoleMessage(RoleBaseMessage baseRoleMessage) {
        this.baseRoleMessage = baseRoleMessage;
    }

    public DetailBaseMessage getBaseDetailMessage() {
        return baseDetailMessage;
    }

    public  void setBaseDetailMessage(DetailBaseMessage baseDetailMessage) {
        this.baseDetailMessage = baseDetailMessage;
    }
}
