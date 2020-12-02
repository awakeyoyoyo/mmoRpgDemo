package com.liqihao.application;


import com.liqihao.commons.ConstantValue;
import io.netty.channel.Channel;

import java.util.Scanner;

public class GameStart {
    private Channel channel;
    public GameStart() {
    }

    public GameStart(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void play(){
        Scanner scanner=new Scanner(System.in);
        while (true){
            System.out.println("请输入module号和cmd号发送请求");
            short module=scanner.nextShort();
            short cmd=scanner.nextShort();
        }
    }
    public void handler(Scanner scanner,short module,short cmd){
        switch (module){
            case ConstantValue.SCENE_MODULE:
                switch (cmd){
                    case ConstantValue.ASK_CAN_REQUEST:

                        break;
                    default:
                        System.out.println("GameStart-handler:收到错误cmd");
                }
                break;
            case ConstantValue.PLAY_MODULE:
                switch (cmd){
                    case ConstantValue.LOGIN_REQUEST:
                        break;
                    default:
                        System.out.println("GameStart-handler:收到错误cmd");
                }
                break;
            default:
                System.out.println("GameStart-handler:收到错误module");

        }
    }



   \
}
