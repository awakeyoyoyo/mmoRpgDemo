package com.liqihao.application;


import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.enums.CmdCode;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.protobufObject.BackPackModel;
import com.liqihao.protobufObject.EquipmentModel;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.utils.CommonsUtil;
import io.netty.channel.Channel;


import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


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
            System.out.println("请输入命令");
            String cmdT=scanner.nextLine();
            scanner.nextLine();
            //消除回车
            CmdCode cmd=CmdCode.getValue(cmdT);
            if (null==cmd){
                System.out.println("输入错误指令");
                continue;
            }
            handler(scanner,cmd.getCmd());

        }
    }
    public void handler(Scanner scanner,int cmd){
                switch (cmd){
                    //
                    case ConstantValue.ASK_CAN_REQUEST:
                        askCanRequest(scanner);
                        break;
                    case ConstantValue.WENT_REQUEST:
                        wentRequest(scanner);
                        break;
                    case ConstantValue.FIND_ALL_ROLES_REQUEST:
                        findAllRolesRequest(scanner);
                        break;
                    case ConstantValue.LOGIN_REQUEST:
                        loginRequest(scanner);
                        break;
                    case ConstantValue.LOGOUT_REQUEST:
                        logoutRequest(scanner);
                        break;
                    case ConstantValue.REGISTER_REQUEST:
                        registerRequest(scanner);
                        break;
                    case ConstantValue.TALK_NPC_REQUEST:
                        talkNpcRequest(scanner);
                        break;
                    case ConstantValue.USE_SKILL_REQUEST:
                        useSkillRequest(scanner);
                        break;
                    case ConstantValue.ABANDON_REQUEST:
                        abandonRequest(scanner);
                        break;
                    case ConstantValue.BACKPACK_MSG_REQUEST:
                        backPackMsgRequest(scanner);
                        break;
                    case ConstantValue.USE_REQUEST:
                        useRequest(scanner);
                        break;
                    case ConstantValue.ADD_ARTICLE_REQUEST:
                        addArticleRquest(scanner);
                        break;
                    case ConstantValue.ADD_EQUIPMENT_REQUEST:
                        addEquipmentReuqest(scanner);
                        break;
                    case ConstantValue.REDUCE_EQUIPMENT_REQUEST:
                        reduceEquipmentRequest(scanner);
                        break;
                    case ConstantValue.EQUIPMENT_MSG_REQUEST:
                        equipmentMsgRequest(scanner);
                        break;
                    default:
                        System.out.println("GameStart-handler:收到错误cmd");
                }
    }

    private void addEquipmentReuqest(Scanner scanner) {
        System.out.println("请输入装备物品栏id");
        Integer articleId=scanner.nextInt();

        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ADD_EQUIPMENT_REQUEST);
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.newBuilder()
                .setDataType(EquipmentModel.EquipmentModelMessage.DateType.AddEquipmentRequest)
                .setAddEquipmentRequest(EquipmentModel.AddEquipmentRequest.newBuilder().setArticleId(articleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void reduceEquipmentRequest(Scanner scanner) {
        System.out.println("请输入脱落部位id");
        Integer position=scanner.nextInt();

        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REDUCE_EQUIPMENT_REQUEST);
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.newBuilder()
                .setDataType(EquipmentModel.EquipmentModelMessage.DateType.ReduceEquipmentRequest)
                .setReduceEquipmentRequest(EquipmentModel.ReduceEquipmentRequest.newBuilder().setPosition(position).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void equipmentMsgRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.EQUIPMENT_MSG_REQUEST);
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.newBuilder()
                .setDataType(EquipmentModel.EquipmentModelMessage.DateType.EquipmentMsgRequest)
                .setEquipmentMsgRequest(EquipmentModel.EquipmentMsgRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void addArticleRquest(Scanner scanner) {
        System.out.println("请输入增加物品的id");
        Integer id=scanner.nextInt();
        System.out.println("请输入增加物品的类型code");
        Integer articleType=scanner.nextInt();
        System.out.println("请输入增加物品的数量");
        Integer number=scanner.nextInt();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ADD_ARTICLE_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.AddArticleRequest)
                .setAddArticleRequest(BackPackModel.AddArticleRequest.newBuilder().setId(id).setArticleType(articleType).setNumber(number).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void backPackMsgRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        BackPackModel.BackPackModelMessage myMessage;
        nettyRequest.setCmd(ConstantValue.BACKPACK_MSG_REQUEST);
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.BackPackRequest)
                .setBackPackRequest(BackPackModel.BackPackRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void useRequest(Scanner scanner) {
        System.out.println("请输入使用物品的背包栏id");
        Integer articleId=scanner.nextInt();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.USE_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.UseRequest)
                .setUseRequest(BackPackModel.UseRequest.newBuilder().setArticleId(articleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void abandonRequest(Scanner scanner) {
        System.out.println("请输入丢弃物品的背包栏id");
        Integer articleId=scanner.nextInt();
        System.out.println("请输入丢弃数量");
        Integer number=scanner.nextInt();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ABANDON_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.AbandonRequest)
                .setAbandonRequest(BackPackModel.AbandonRequest.newBuilder().setNumber(number).setArticleId(articleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void useSkillRequest(Scanner scanner) {
        System.out.println("当前角色可使用的技能：");
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        List<Integer> skills=mmoRole.getSkillIdList();
        ConcurrentHashMap<Integer, SkillMessage> map=MmoCacheCilent.getInstance().getSkillMessageConcurrentHashMap();
        for (Integer sId:skills) {
            System.out.println("技能Id： "+map.get(sId).getId()+" 技能名称："+map.get(sId).getSkillName());
        }
        System.out.println("请输入你使用的技能id");
        Integer skillId=scanner.nextInt();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.USE_SKILL_REQUEST);
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder()
                .setDataType(PlayModel.PlayModelMessage.DateType.UseSkillRequest)
                .setUseSkillRequest(PlayModel.UseSkillRequest.newBuilder().setSkillId(skillId).setSceneId(MmoCacheCilent.getInstance().getNowSceneId()).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void talkNpcRequest(Scanner scanner) {
        System.out.println("请输入你想对话的NPC的id");
        Integer npcId=scanner.nextInt();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.TALK_NPC_REQUEST);
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.TalkNPCRequest)
                .setTalkNPCRequest(SceneModel.TalkNPCRequest.newBuilder().setRoleId(npcId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
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
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.LOGOUT_REQUEST);
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder().setDataType(PlayModel.PlayModelMessage.DateType.LogoutRequest)
                .setLogoutRequest(PlayModel.LogoutRequest.newBuilder().build()).build();
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
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder().setDataType(PlayModel.PlayModelMessage.DateType.LoginRequest)
                .setLoginRequest(PlayModel.LoginRequest.newBuilder().setUsername(username).setPassword(password).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void findAllRolesRequest(Scanner scanner) {
        System.out.println("稍等片刻。等待数据传输");
        Integer sceneId= MmoCacheCilent.getInstance().getNowSceneId();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.FIND_ALL_ROLES_REQUEST);
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
        Integer mmoSceneId= MmoCacheCilent.getInstance().getNowSceneId();
        ConcurrentHashMap<Integer,SceneMessage> concurrentHashMap=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap();
        SceneMessage sceneMessage=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoSceneId);
        String canScenes=sceneMessage.getCanScene();
        List<Integer> canSecnesIds= CommonsUtil.split(canScenes);
        Integer sceneId=null;
        for(Integer m:canSecnesIds){
            SceneMessage temp=concurrentHashMap.get(m);
            if (str.equals(temp.getPlaceName())){
                sceneId=temp.getId();
            }
        }
        if (sceneId==null){
            System.out.println("该地方无法前往");
        }else{
            NettyRequest nettyRequest=new NettyRequest();
            nettyRequest.setCmd(ConstantValue.WENT_REQUEST);
            SceneModel.SceneModelMessage myMessage;
            myMessage=SceneModel.SceneModelMessage.newBuilder()
                    .setDataType(SceneModel.SceneModelMessage.DateType.WentRequest)
                    .setWentRequest(SceneModel.WentRequest.newBuilder().setSceneId(sceneId).build()).build();
            byte[] data=myMessage.toByteArray();
            nettyRequest.setData(data);
            channel.writeAndFlush(nettyRequest);
        }
    }

    private void askCanRequest(Scanner scanner) {
        System.out.println("稍等片刻。等待数据传输");
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ASK_CAN_REQUEST);
        SceneModel.SceneModelMessage myMessage;
        myMessage=SceneModel.SceneModelMessage.newBuilder()
                .setDataType(SceneModel.SceneModelMessage.DateType.AskCanRequest)
                .setAskCanRequest(SceneModel.AskCanRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }
}
