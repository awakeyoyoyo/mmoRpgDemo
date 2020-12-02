package com.liqihao.application;


import com.liqihao.commons.CacheUtil;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.MmoSimpleScene;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.protobufObject.SceneModel;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
            System.out.println("请输入module号");
            short module=scanner.nextShort();
            //消除回车
            scanner.nextLine();
            System.out.println("请输入cmd号");
            short cmd=scanner.nextShort();
            //消除回车
            scanner.nextLine();
            handler(scanner,module,cmd);
        }
    }
    public void handler(Scanner scanner,short module,short cmd){
        switch (module){
            case ConstantValue.SCENE_MODULE:
                switch (cmd){
                    case ConstantValue.ASK_CAN_REQUEST:
                        askCanRequest(scanner);
                        break;
                    case ConstantValue.WENT_REQUEST:
                        wentRequest(scanner);
                        break;
                    case ConstantValue.FIND_ALL_ROLES_REQUEST:
                        findAllRolesRequest(scanner);
                        break;
                    default:
                        System.out.println("GameStart-handler:收到错误cmd");
                }
                break;
            case ConstantValue.PLAY_MODULE:
                switch (cmd){
                    case ConstantValue.LOGIN_REQUEST:
                        loginRequest(scanner);
                        break;
                    case ConstantValue.LOGOUT_REQUEST:
                        logoutRequest(scanner);
                        break;
                    case ConstantValue.REGISTER_REQUEST:
                        registerRequest(scanner);
                        break;
                    default:
                        System.out.println("GameStart-handler:收到错误cmd");
                }
                break;
            default:
                System.out.println("GameStart-handler:收到错误module");

        }
    }

    private void registerRequest(Scanner scanner) {
        System.out.println("欢迎来到注册界面~");
        System.out.println("请输入账号");
        String username=scanner.nextLine();
        //消除回车
        scanner.nextLine();
        System.out.println("请输入密码");
        String password=scanner.nextLine();
        //消除回车
        scanner.nextLine();
        System.out.println("请输入游戏角色名称");
        String roleName=scanner.nextLine();
        //消除回车
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REGISTER_REQUEST);
        nettyRequest.setModule(ConstantValue.PLAY_MODULE);
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder().setDataType(PlayModel.PlayModelMessage.DateType.RegisterRequest)
                .setRegisterRequest(
                        PlayModel.RegisterRequest.newBuilder().
                                setPassword(password).setRolename(roleName).
                                setUsername(username).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void logoutRequest(Scanner scanner) {
        Integer rolesId=CacheUtil.getNowRoles().getId();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.LOGOUT_REQUEST);
        nettyRequest.setModule(ConstantValue.PLAY_MODULE);
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder().setDataType(PlayModel.PlayModelMessage.DateType.LogoutRequest)
                .setLogoutRequest(PlayModel.LogoutRequest.newBuilder().setRolesId(rolesId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void loginRequest(Scanner scanner) {
        System.out.println("欢迎来到登录界面~");
        System.out.println("请输入账号");
        String username=scanner.nextLine();
        //消除回车
        scanner.nextLine();
        System.out.println("请输入密码");
        String password=scanner.nextLine();
        //消除回车
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.LOGIN_REQUEST);
        nettyRequest.setModule(ConstantValue.PLAY_MODULE);
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder().setDataType(PlayModel.PlayModelMessage.DateType.LoginRequest)
                .setLoginRequest(PlayModel.LoginRequest.newBuilder().setUsername(username).setPassword(password).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void findAllRolesRequest(Scanner scanner) {
        System.out.println("稍等片刻。等待数据传输");
        Integer sceneId=CacheUtil.getNowScene().getId();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.FIND_ALL_ROLES_REQUEST);
        nettyRequest.setModule(ConstantValue.SCENE_MODULE);
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.newBuilder().setDataType(SceneModel.SceneModelMessage.DateType.FindAllRolesRequest)
                .setFindAllRolesRequest(SceneModel.FindAllRolesRequest.newBuilder().setSceneId(sceneId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void wentRequest(Scanner scanner) {
        System.out.println("请输入你想去的地方");
        String str=scanner.nextLine();
        //消除回车
        scanner.nextLine();
        MmoScene mmoScene=CacheUtil.getNowScene();
        List<MmoSimpleScene> mmoSimpleScenes=mmoScene.getCanScene();
        Integer sceneId=null;
        for(MmoSimpleScene m:mmoSimpleScenes){
            if (str.equals(m.getPalceName())){
                sceneId=m.getId();
            }
        }
        if (sceneId==null){
            System.out.println("该地方无法前往");
        }else{
            NettyRequest nettyRequest=new NettyRequest();
            nettyRequest.setCmd(ConstantValue.WENT_REQUEST);
            nettyRequest.setModule(ConstantValue.SCENE_MODULE);
            SceneModel.SceneModelMessage myMessage;
            myMessage=SceneModel.SceneModelMessage.newBuilder()
                    .setDataType(SceneModel.SceneModelMessage.DateType.WentRequest)
                    .setWentRequest(SceneModel.WentRequest.newBuilder().setSceneId(sceneId).setPlayId(CacheUtil.getNowRoles().getId()).build()).build();
            byte[] data=myMessage.toByteArray();
            nettyRequest.setData(data);
            channel.writeAndFlush(nettyRequest);
        }
    }

    private void askCanRequest(Scanner scanner) {
        System.out.println("稍等片刻。等待数据传输");
        Integer sceneId=CacheUtil.getNowScene().getId();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ASK_CAN_REQUEST);
        nettyRequest.setModule(ConstantValue.SCENE_MODULE);
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.AskCanRequest)
                .setAskCanRequest(SceneModel.AskCanRequest.newBuilder().setSceneId(sceneId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }
}
