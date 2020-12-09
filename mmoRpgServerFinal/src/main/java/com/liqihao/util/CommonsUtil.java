package com.liqihao.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串处理类
 * @author awakeyoyoyo
 */
public class CommonsUtil {
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
