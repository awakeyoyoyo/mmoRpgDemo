package com.liqihao.util;


import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.provider.CopySceneProvider;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @Classname NotificationUtil
 * @Description 返回消息工具类
 * @Author lqhao
 * @Date 2021/1/27 17:11
 * @Version 1.0
 */
public class NotificationUtil {
    private static Logger logger = Logger.getLogger(NotificationUtil.class);

    /**
     * 单发
     * @param channel
     * @param nettyResponse
     * @param json
     */
    public static void sendMessage(Channel channel, NettyResponse nettyResponse,String json){
        if (channel!=null) {
            channel.writeAndFlush(nettyResponse);
        }
        //打印日志
        logger.info(json);
    }
    /**
     * 群发指定role
     * @param nettyResponse
     * @param json
     */
    public static void sendRolesMessage(NettyResponse nettyResponse, List<MmoSimpleRole> roles,String json){
        logger.info(json);
        for (MmoSimpleRole m:roles) {
            Channel c=ChannelMessageCache.getInstance().get(m.getId());
            if (c!=null){
                c.writeAndFlush(nettyResponse);
            }
        }
        //打印日志
    }
    /**
     * 群发场景/副本中得角色
     * @param nettyResponse
     * @param mmoSimpleRole
     * @param json
     */
    public static void notificationSceneRole(NettyResponse nettyResponse, Role mmoSimpleRole, String json) {
        logger.info(json);
        List<Integer> players;
        if (mmoSimpleRole.getMmoSceneId()!=null) {
            players = SceneBeanMessageCache.getInstance().get(mmoSimpleRole.getMmoSceneId()).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }
        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(mmoSimpleRole.getCopySceneBeanId()).getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    Channel c= ChannelMessageCache.getInstance().get(role.getId());
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }
            }
        }
    }
}
