package com.liqihao.woker;

import io.netty.channel.ChannelFuture;

import java.util.Scanner;

public class ClientWorker {
    public void gameBegim(ChannelFuture channelFuture){
        System.out.println("欢迎来到传奇123，请输入指令啦");
        Scanner scanner=new Scanner(System.in);
        while(true) {
            System.out.println("输入数字：1-注册 2-登陆 3-退出");
            int option = scanner.nextInt();
            if (option == 3) {
                channelFuture.channel().close();
                break;
            } else if (option == 1) {
                System.out.println("输入账号和密码：");
                //注册请求
                break;
            } else if (option == 2) {
                System.out.println("输入账号和密码：");
                //登陆请求
                break;
            } else {
                System.out.println("输入错误数字啦");
            }
        }
    }

    public void playGame(ChannelFuture channelFuture){
        //获取当前场景
        System.out.println("欢迎进入***场景，请输入指令啦");
        Scanner scanner=new Scanner(System.in);
        while(true) {
            System.out.println("输入数字：1-查看可进前往的地图 2-进入某个地图 3-打印当前场景所有角色 4-退出");
            int option = scanner.nextInt();
            if (option == 4) {
                channelFuture.channel().close();
                break;
            } else if (option == 1) {
                System.out.println("输入账号和密码：");
                //注册请求
                break;
            } else if (option == 2) {
                System.out.println("输入账号和密码：");
                //登陆请求
                break;
            }
            else if (option == 3) {
                //查询场景校色
                break;
            } else {
                System.out.println("输入错误数字啦");
            }
        }
    }
}
