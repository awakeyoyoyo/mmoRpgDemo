package com.liqihao.util;


import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.cache.ChannelMessageCache;
import com.liqihao.cache.SceneBeanMessageCache;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.SceneModel;
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
     * 发送消息
     * @param channel
     * @param nettyResponse
     * @param builder
     */
    public static void sendMessage(Channel channel, NettyResponse nettyResponse
            , com.google.protobuf.GeneratedMessageV3.Builder builder){
        if (channel!=null) {
            channel.writeAndFlush(nettyResponse);
        }
        //打印日志
        String json= JsonFormat.printToString(builder.build());
        logger.info(json);
    }

    /**
     * 群送角色消息
     * @param nettyResponse
     * @param roles
     * @param builder
     */
    public static void sendRolesMessage(NettyResponse nettyResponse, List<MmoSimpleRole> roles
            ,com.google.protobuf.GeneratedMessageV3.Builder builder){
        String json2= JsonFormat.printToString(builder.build());
        logger.info(json2);
        for (MmoSimpleRole m:roles) {
            Channel c=ChannelMessageCache.getInstance().get(m.getId());
            if (c!=null){
                c.writeAndFlush(nettyResponse);
            }
        }
        //打印日志
    }

    /**
     * 通知当前场景的角色
     * @param nettyResponse
     * @param mmoSimpleRole
     * @param builder
     */
    public static void notificationSceneRole(NettyResponse nettyResponse, Role mmoSimpleRole
            ,com.google.protobuf.GeneratedMessageV3.Builder builder){
        String json2= JsonFormat.printToString(builder.build());
        logger.info(json2);
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
