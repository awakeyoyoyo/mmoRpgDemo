package com.liqihao.util;


import com.liqihao.Cache.MmoCache;
import com.liqihao.pojo.bean.MmoSimpleNPC;
import com.liqihao.pojo.bean.MmoSimpleRole;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字符串处理类
 * @author awakeyoyoyo
 */
public class CommonsUtil {

    public static MmoSimpleRole NpcToMmoSimpleRole(MmoSimpleNPC npc) {
        MmoSimpleRole roleTemp = new MmoSimpleRole();
        roleTemp.setId(npc.getId());
        roleTemp.setName(npc.getName());
        roleTemp.setMmosceneid(npc.getMmosceneid());
        roleTemp.setStatus(npc.getStatus());
        roleTemp.setType(npc.getType());
        roleTemp.setOnstatus(npc.getOnstatus());
        roleTemp.setBlood(npc.getBlood());
        roleTemp.setNowBlood(npc.getNowBlood());
        roleTemp.setMp(npc.getNowBlood());
        return roleTemp;
    }





    /**
     * 根据channel获取Id
     * @param channel
     * @return
     */
    public static Integer getRoleIdByChannel(Channel channel) {
        ConcurrentHashMap<Integer,Channel> channelConcurrentHashMap=MmoCache.getInstance().getChannelConcurrentHashMap();
        Iterator<Integer> ids=channelConcurrentHashMap.keySet().iterator();
        Integer id=null;
        while (ids.hasNext()){
            id=ids.next();
            //加锁 是为了防止 判断成功后，用户断开连接的情况 貌似无所谓
            if (channel.equals(channelConcurrentHashMap.get(id))){
                return id;
            }
        }
        return id;
    }




    /**
     * 分割字符串返回id集合
     * @param str
     * @return
     */
    public  static List<Integer> split(String str) {
        if (null == str || str.trim().length() == 0) {
            return new ArrayList<Integer>();
        }
        String[] strings=str.split(",");
        List<Integer> res=new ArrayList<>();
        for (String s:strings){
            if (s.isEmpty()){
                continue;
            }
            Integer i=Integer.parseInt(s);
            res.add(i);
        }
        return res;
    }

    /**
     * 分割字符串返回id集合
     * @param str
     * @return
     */
    public  static List<String> splitToStringList(String str) {
        if (null == str || str.trim().length() == 0) {
            return new ArrayList<String>();
        }
        String[] strings=str.split("-");
        return Arrays.asList(strings);
    }

    public static String listToString(List<Integer> oldRoles) {
        StringBuffer stringBuffer=new StringBuffer();
        if (oldRoles.size()==0) {
            return "";
        }
        for (int i=0;i<oldRoles.size();i++){
            Integer id=oldRoles.get(i);
            if (i==oldRoles.size()-1){
                stringBuffer.append(id);
            }else {
                stringBuffer.append(id).append(",");
            }
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
//        List<Integer> list=new ArrayList<>();
//        list.add(1);
//        list.add(222);
//        System.out.println(listToString(list));
//        List<Integer> list2=new ArrayList<>();
//        System.out.println(listToString(list2)+"-----");
//        List<Integer> list3=new ArrayList<>();
//        list3.add(1);
//        System.out.println(listToString(list3));
        String str="祝贺吧。-穿越时空，能知过去未来，收集所有蒙面超人的力量。-他就是蒙面超人时王!";
        String str2="";
        String str3="我系时王zio";
        System.out.println(splitToStringList(str).get(0));
        System.out.println(splitToStringList(str).get(1));
        System.out.println(splitToStringList(str).get(2));
        System.out.println(splitToStringList(str2));
        System.out.println(splitToStringList(str3));
    }
}
