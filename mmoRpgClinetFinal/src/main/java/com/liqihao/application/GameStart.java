package com.liqihao.application;


import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.CmdCode;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.SkillAttackTypeCode;
import com.liqihao.commons.enums.SkillTypeCode;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.pojo.baseMessage.GoodsMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.protobufObject.*;
import com.liqihao.utils.CommonsUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.netty.channel.Channel;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 游戏命令类
 * @author lqhao
 */
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
                    case ConstantValue.FIX_EQUIPMENT_REQUEST:
                        fixEquipmentRequest(scanner);
                        break;
                    case ConstantValue.TEAM_MESSAGE_REQUEST:
                        teamMessageRequest(scanner);
                        break;
                    case ConstantValue.CREATE_TEAM_REQUEST:
                        createTeamRequest(scanner);
                        break;
                    case ConstantValue.APPLY_FOR_TEAM_REQUEST:
                        applyForTeamRequest(scanner);
                        break;
                    case ConstantValue.INVITE_PEOPLE_REQUEST:
                        invitePeopleRequest(scanner);
                        break;
                    case ConstantValue.APPLY_MESSAGE_REQUEST:
                        applyMessageRequest(scanner);
                        break;
                    case ConstantValue.INVITE_MESSAGE_REQUEST:
                        inviteMessageRequest(scanner);
                        break;
                    case ConstantValue.REFUSE_APPLY_REQUEST:
                        refuseApplyRequest(scanner);
                        break;
                    case ConstantValue.REFUSE_INVITE_REQUEST:
                        refuseInviteRequest(scanner);
                        break;
                    case ConstantValue.ENTRY_PEOPLE_REQUEST_APPLY:
                        entryApplyPeopleRequest(scanner);
                        break;
                    case ConstantValue.ENTRY_PEOPLE_REQUEST_INVITE:
                        entryInvitePeopleRequest(scanner);
                        break;
                    case ConstantValue.EXIT_TEAM_REQUEST:
                        exitTeamRequest(scanner);
                        break;
                    case ConstantValue.BAN_PEOPLE_REQUEST:
                        banPeopleRequest(scanner);
                        break;
                    case ConstantValue.DELETE_TEAM_REQUEST:
                        deleteTeamRequest(scanner);
                        break;
                    case ConstantValue.ASK_CAN_COPY_SCENE_REQUEST:
                        askCanCopySceneRequest(scanner);
                        break;
                    case ConstantValue.COPY_SCENE_MESSAGE_REQUEST:
                        copySceneMessageRequest(scanner);
                        break;
                    case ConstantValue.CREATE_COPY_SCENE_REQUEST:
                        createCopySceneRequest(scanner);
                        break;
                    case ConstantValue.ENTER_COPY_SCENE_REQUEST:
                        enterCopySceneRequest(scanner);
                        break;
                    case ConstantValue.EXIT_COPY_SCENE_REQUEST:
                        exitCopySceneRequest(scanner);
                        break;
                    case ConstantValue.SEND_TO_ALL_REQUEST:
                        sendToAllRequest(scanner);
                        break;
                    case ConstantValue.SEND_TO_ONE_REQUEST:
                        sendToOneRequest(scanner);
                        break;
                    case ConstantValue.SEND_TO_TEAM_REQUEST:
                        sendToTeamRequest(scanner);
                        break;
                    case ConstantValue.SEND_TO_SCENE_REQUEST:
                        sendToSceneRequest(scanner);
                        break;
                    case ConstantValue.GET_EMAIL_MESSAGE_REQUEST:
                        getEmailMessageRequest(scanner);
                        break;
                    case ConstantValue.GET_EMAIL_ARTICLE_REQUEST:
                        getEmailArticleRequest(scanner);
                        break;
                    case ConstantValue.ACCEPT_EMAIL_LIST_REQUEST:
                        acceptEmailList(scanner);
                        break;
                    case ConstantValue.IS_SEND_EMAIL_LIST_REQUEST:
                        isSendEmailListRequest(scanner);
                        break;
                    case ConstantValue.SEND_EMAIL_REQUEST:
                        sendEamilRequest(scanner);
                        break;
                    case ConstantValue.DELETE_ACCEPT_EMAIL_REQUEST:
                        deleteAcceptEmailRequest(scanner);
                        break;
                    case ConstantValue.DELETE_SEND_EMAIL_REQUEST:
                        deleteSendEmailRequest(scanner);
                        break;
                    case ConstantValue.FIND_ALL_CAN_REQUEST:
                        findAllCanRequest(scanner);
                        break;
                    case ConstantValue.GET_ARTICLE_FROM_FLOOR_REQUEST:
                        getArticleFromFloor(scanner);
                        break;
                    case ConstantValue.CHECK_MONEY_NUMBER_REQUEST:
                        checkMoneyRequest(scanner);
                        break;
                    case ConstantValue.BUY_GOODS_REQUEST:
                        buyGoodsRequest(scanner);
                        break;
                    default:
                        System.out.println("GameStart-handler:收到错误cmd");
                }
    }

    private void buyGoodsRequest(Scanner scanner) {
        System.out.println("请输入你要购买的商品的Id：");
        Integer goodsId=scanner.nextInt();
        scanner.nextLine();
        GoodsMessage goodsMessage=MmoCacheCilent.getInstance().getGoodsMessageConcurrentHashMap().get(goodsId);
        if (goodsMessage==null){
            System.out.println("没有该商品");
            return;
        }
        Integer num=1;
        if (!goodsMessage.getArticleTypeId().equals(ArticleTypeCode.EQUIPMENT.getCode())){
            System.out.println("请输入要购买的数量");
            num=scanner.nextInt();
            scanner.nextLine();
            if (num<=0){
                System.out.println("输入错误数字");
                return;
            }
        }
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.BUY_GOODS_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.BuyGoodsRequest)
                .setBuyGoodsRequest(BackPackModel.BuyGoodsRequest.newBuilder().setGoodsId(goodsId).setNum(num).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void checkMoneyRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CHECK_MONEY_NUMBER_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.CheckMoneyNumberRequest)
                .setCheckMoneyNumberRequest(BackPackModel.CheckMoneyNumberRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void findAllCanRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.FIND_ALL_CAN_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.FindAllCanGetRequest)
                .setFindAllCanGetRequest(BackPackModel.FindAllCanGetRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getArticleFromFloor(Scanner scanner) {
        System.out.println("请输入你要拾取的物品的floodIndex：");
        Integer floodIndex=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_ARTICLE_FROM_FLOOR_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.GetArticleFromFloorRequest)
                .setGetArticleFromFloorRequest(BackPackModel.GetArticleFromFloorRequest.newBuilder().setIndex(floodIndex).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void deleteSendEmailRequest(Scanner scanner) {
        System.out.println("请输入你要删除的emailId：");
        Integer emailId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.DELETE_SEND_EMAIL_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteSendEmailRequest)
                .setDeleteSendEmailRequest(EmailModel.DeleteSendEmailRequest.newBuilder().setEmailId(emailId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void deleteAcceptEmailRequest(Scanner scanner) {
        System.out.println("请输入你要删除的emailId：");
        Integer emailId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.DELETE_ACCEPT_EMAIL_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.DeleteAcceptEmailRequest)
                .setDeleteAcceptEmailRequest(EmailModel.DeleteAcceptEmailRequest.newBuilder().setEmailId(emailId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void sendEamilRequest(Scanner scanner) {
        MmoRole mmoRole=MmoCacheCilent.getInstance().getNowRole();
        if (mmoRole==null){
            System.out.println("请登陆！");
            return;
        }
        System.out.println("请输入邮件标题：");
        String title=scanner.nextLine();
        System.out.println("请输入邮件内容：");
        String context=scanner.nextLine();
        System.out.println("请输入请问是否携带物品：0-不携带   1-携带");
        Integer flag=scanner.nextInt();
        scanner.nextLine();
        Integer articleId=-1;
        Integer articleNum=-1;
        if (flag==1){
            System.out.println("请输入物品栏id：");
            articleId=scanner.nextInt();
            scanner.nextLine();
            System.out.println("请输入物品数量：");
            articleNum=scanner.nextInt();
            scanner.nextLine();
        }
        System.out.println("请输入收件者的id：");
        Integer toRoleId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SEND_EMAIL_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.SendEmailRequest)
                .setSendEmailRequest(EmailModel.SendEmailRequest.newBuilder()
                        .setArticleId(articleId).setArticleNum(articleNum)
                        .setTitle(title).setContext(context).setToRoleId(toRoleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void isSendEmailListRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.IS_SEND_EMAIL_LIST_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.IsSendEmailListRequest)
                .setIsSendEmailListRequest(EmailModel.IsSendEmailListRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void acceptEmailList(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ACCEPT_EMAIL_LIST_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.AcceptEmailListRequest)
                .setAcceptEmailListRequest(EmailModel.AcceptEmailListRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getEmailArticleRequest(Scanner scanner) {
        System.out.println("请输入你要收取物品的emailId：");
        Integer emailId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_EMAIL_ARTICLE_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailArticleRequest)
                .setGetEmailArticleRequest(EmailModel.GetEmailArticleRequest.newBuilder().setEmailId(emailId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getEmailMessageRequest(Scanner scanner) {
        System.out.println("请输入你要查看的邮件的emailId：");
        Integer emailId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_EMAIL_MESSAGE_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailMessageRequest)
                .setGetEmailMessageRequest(EmailModel.GetEmailMessageRequest.newBuilder().setEmailId(emailId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void sendToSceneRequest(Scanner scanner) {
        System.out.println("请输入你要说的话：");
        String str=scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SEND_TO_SCENE_REQUEST);
        ChatModel.ChatModelMessage myMessage;
        myMessage= ChatModel.ChatModelMessage.newBuilder()
                .setDataType(ChatModel.ChatModelMessage.DateType.SendToSceneRequest)
                .setSendToSceneRequest(ChatModel.SendToSceneRequest.newBuilder().setStr(str).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void sendToTeamRequest(Scanner scanner) {
        System.out.println("请输入你要说的话：");
        String str=scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SEND_TO_TEAM_REQUEST);
        ChatModel.ChatModelMessage myMessage;
        myMessage= ChatModel.ChatModelMessage.newBuilder()
                .setDataType(ChatModel.ChatModelMessage.DateType.SendToTeamRequest)
                .setSendToTeamRequest(ChatModel.SendToTeamRequest.newBuilder().setStr(str).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void sendToAllRequest(Scanner scanner) {
        System.out.println("请输入你要说的话：");
        String str=scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SEND_TO_ALL_REQUEST);
        ChatModel.ChatModelMessage myMessage;
        myMessage= ChatModel.ChatModelMessage.newBuilder()
                .setDataType(ChatModel.ChatModelMessage.DateType.SendToAllRequest)
                .setSendToAllRequest(ChatModel.SendToAllRequest.newBuilder().setStr(str).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void sendToOneRequest(Scanner scanner) {
        System.out.println("请输入对话的目标id：");
        Integer roleId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入你要说的话：");
        String str=scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SEND_TO_ONE_REQUEST);
        ChatModel.ChatModelMessage myMessage;
        myMessage= ChatModel.ChatModelMessage.newBuilder()
                .setDataType(ChatModel.ChatModelMessage.DateType.SendToOneRequest)
                .setSendToOneRequest(ChatModel.SendToOneRequest.newBuilder().setRoleId(roleId).setStr(str).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void askCanCopySceneRequest(Scanner scanner) {
        List<Integer> copySceneIds=new ArrayList<>();
        for (CopySceneMessage cMsg:MmoCacheCilent.getInstance().getCopySceneMessageConcurrentHashMap().values()) {
            copySceneIds.add(cMsg.getId());
        }
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]所能进入的副本：");
        for (Integer id : copySceneIds) {
            CopySceneMessage copySceneMessage = MmoCacheCilent.getInstance().getCopySceneMessageConcurrentHashMap().get(id);
            System.out.println("[-]");
            System.out.println("[-]副本id：" + copySceneMessage.getId());
            System.out.println("[-]副本名称：" + copySceneMessage.getName());
            System.out.println("[-]副本怪物id：" + copySceneMessage.getBossIds());
            System.out.println("[-]副本攻略时间：" + copySceneMessage.getLastTime() + "秒");
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    private void copySceneMessageRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.COPY_SCENE_MESSAGE_REQUEST);
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.CopySceneMessageRequest)
                .setCopySceneMessageRequest(CopySceneModel.CopySceneMessageRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void createCopySceneRequest(Scanner scanner) {
        System.out.println("请输入你想创建的副本的id");
        Integer copySceneId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CREATE_COPY_SCENE_REQUEST);
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.CreateCopySceneRequest)
                .setCreateCopySceneRequest(CopySceneModel.CreateCopySceneRequest.newBuilder().setCopySceneId(copySceneId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void enterCopySceneRequest(Scanner scanner) {
        System.out.println("请输入你想进入的副本的id");
        Integer copySceneId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ENTER_COPY_SCENE_REQUEST);
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.EnterCopySceneRequest)
                .setEnterCopySceneRequest(CopySceneModel.EnterCopySceneRequest.newBuilder().setCopySceneId(copySceneId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void exitCopySceneRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.EXIT_COPY_SCENE_REQUEST);
        CopySceneModel.CopySceneModelMessage myMessage;
        myMessage=CopySceneModel.CopySceneModelMessage.newBuilder()
                .setDataType(CopySceneModel.CopySceneModelMessage.DateType.ExitCopySceneRequest)
                .setExitCopySceneRequest(CopySceneModel.ExitCopySceneRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }


    private void deleteTeamRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.DELETE_TEAM_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.DeleteTeamRequest)
                .setDeleteTeamRequest(TeamModel.DeleteTeamRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }


    private void banPeopleRequest(Scanner scanner) {
        System.out.println("请输入你要踢出队伍的玩家的id");
        Integer roleId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.BAN_PEOPLE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.BanPeopleRequest)
                .setBanPeopleRequest(TeamModel.BanPeopleRequest.newBuilder().setRoleId(roleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void entryInvitePeopleRequest(Scanner scanner) {
        System.out.println("请输入要接收的队伍id");
        Integer teamId=scanner.nextInt();scanner.nextLine();
        Integer roleId=MmoCacheCilent.getInstance().getNowRole().getId();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ENTRY_PEOPLE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.EntryPeopleRequest)
                .setEntryPeopleRequest(TeamModel.EntryPeopleRequest.newBuilder().setRoleId(roleId).setTeamId(teamId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    // 分开来是申请还是邀请
    private void entryApplyPeopleRequest(Scanner scanner) {
        System.out.println("请输入同意入队的用户id");
        Integer roleId=scanner.nextInt();scanner.nextLine();
        Integer teamId=MmoCacheCilent.getInstance().getNowRole().getTeamId();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ENTRY_PEOPLE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.EntryPeopleRequest)
                .setEntryPeopleRequest(TeamModel.EntryPeopleRequest.newBuilder().setRoleId(roleId).setTeamId(teamId==null?-1:teamId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void exitTeamRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.EXIT_TEAM_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.ExitTeamRequest)
                .setExitTeamRequest(TeamModel.ExitTeamRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void refuseInviteRequest(Scanner scanner) {
        System.out.println("请输入你要拒绝的队伍Id");
        Integer teamId=scanner.nextInt();scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REFUSE_INVITE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.RefuseInviteRequest)
                .setRefuseInviteRequest(TeamModel.RefuseInviteRequest.newBuilder().setTeamId(teamId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void refuseApplyRequest(Scanner scanner) {
        System.out.println("请输入你要拒绝的用户id");
        Integer roleId=scanner.nextInt();scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REFUSE_APPLY_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.RefuseApplyRequest)
                .setRefuseApplyRequest(TeamModel.RefuseApplyRequest.newBuilder().setRoleId(roleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void applyForTeamRequest(Scanner scanner) {
        System.out.println("请输入你要加入的队伍的id");
        Integer teamId=scanner.nextInt();scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.APPLY_FOR_TEAM_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.ApplyForTeamRequest)
                .setApplyForTeamRequest(TeamModel.ApplyForTeamRequest.newBuilder().setTeamId(teamId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void applyMessageRequest(Scanner scanner) {

        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.APPLY_MESSAGE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.ApplyMessageRequest)
                .setApplyMessageRequest(TeamModel.ApplyMessageRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void invitePeopleRequest(Scanner scanner) {
        System.out.println("请输入你要邀请的玩家id");
        Integer roleId=scanner.nextInt();scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.INVITE_PEOPLE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.InvitePeopleRequest)
                .setInvitePeopleRequest(TeamModel.InvitePeopleRequest.newBuilder().setRoleId(roleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void inviteMessageRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.INVITE_MESSAGE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.InviteMessageRequest)
                .setInviteMessageRequest(TeamModel.InviteMessageRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);

    }

    private void createTeamRequest(Scanner scanner) {
        System.out.println("请输入创建的队伍名字");
        String teamName=scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CREATE_TEAM_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.CreateTeamRequest)
                .setCreateTeamRequest(TeamModel.CreateTeamRequest.newBuilder().setName(teamName).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void teamMessageRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.TEAM_MESSAGE_REQUEST);
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.newBuilder()
                .setDataType(TeamModel.TeamModelMessage.DateType.TeamMessageRequest)
                .setTeamMessageRequest(TeamModel.TeamMessageRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void fixEquipmentRequest(Scanner scanner) {
        System.out.println("请输入需修复装备的物品栏id");
        Integer articleId=scanner.nextInt();scanner.nextLine();

        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.FIX_EQUIPMENT_REQUEST);
        EquipmentModel.EquipmentModelMessage myMessage;
        myMessage=EquipmentModel.EquipmentModelMessage.newBuilder()
                .setDataType(EquipmentModel.EquipmentModelMessage.DateType.FixEquipmentRequest)
                .setFixEquipmentRequest(EquipmentModel.FixEquipmentRequest.newBuilder().setArticleId(articleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void addEquipmentReuqest(Scanner scanner) {
        System.out.println("请输入装备物品栏id");
        Integer articleId=scanner.nextInt();scanner.nextLine();

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
        Integer position=scanner.nextInt();scanner.nextLine();

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
        Integer id=scanner.nextInt();scanner.nextLine();
        System.out.println("请输入增加物品的类型code");
        Integer articleType=scanner.nextInt();scanner.nextLine();
        System.out.println("请输入增加物品的数量");
        Integer number=scanner.nextInt();scanner.nextLine();
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
        Integer articleId=scanner.nextInt();scanner.nextLine();
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
        Integer articleId=scanner.nextInt();scanner.nextLine();
        System.out.println("请输入丢弃数量");
        Integer number=scanner.nextInt();scanner.nextLine();;
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
            System.out.println("技能Id： "+map.get(sId).getId()+" 技能名称："+map.get(sId).getSkillName()+" 技能类型"+ SkillAttackTypeCode.getValue(map.get(sId).getSkillAttackType()));
        }
        System.out.println("请输入你使用的技能id：");
        Integer skillId=scanner.nextInt();scanner.nextLine();;
        Integer roleId=null;
        Integer roleType=null;
        if (map.get(skillId).getSkillAttackType().equals(SkillAttackTypeCode.SINGLE.getCode())){
            System.out.println("请输入你的施法目标的类型： 玩家为1 怪物为2");
            roleType=scanner.nextInt();scanner.nextLine();;
            System.out.println("请输入你的施法目标的id");
            roleId=scanner.nextInt();scanner.nextLine();;
        }
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.USE_SKILL_REQUEST);
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder()
                .setDataType(PlayModel.PlayModelMessage.DateType.UseSkillRequest)
                .setUseSkillRequest(PlayModel.UseSkillRequest.newBuilder()
                        .setSkillId(skillId).setRoleId(roleId==null?-1:roleId)
                        .setRoleType(roleType==null?-1:roleType).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void talkNpcRequest(Scanner scanner) {
        System.out.println("请输入你想对话的NPC的id");
        Integer npcId=scanner.nextInt();scanner.nextLine();;
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
        System.out.println("请输入密码");
        String password=scanner.nextLine();
        System.out.println("请输入游戏角色名称");
        String roleName=scanner.nextLine();
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
        System.out.println("请输入密码");
        String password=scanner.nextLine();
        //消除回车
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
        Integer sceneId= MmoCacheCilent.getInstance().getNowSceneId();
        if (sceneId==null){
            System.out.println("请先登录");
            return;
        }
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
        Integer mmoSceneId= MmoCacheCilent.getInstance().getNowSceneId();
        ConcurrentHashMap<Integer,SceneMessage> concurrentHashMap=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap();
        SceneMessage sceneMessage=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(mmoSceneId);
        String canScenes=sceneMessage.getCanScene();
        List<Integer> canScenesIds= CommonsUtil.split(canScenes);
        Integer sceneId=null;
        for(Integer m:canScenesIds){
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
        Integer sceneId= MmoCacheCilent.getInstance().getNowRole().getMmosceneid();
        //从缓存中读取
        List<Integer> Scenes= CommonsUtil.split(MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap().get(sceneId).getCanScene());
        ConcurrentHashMap<Integer, SceneMessage> sceneMap=MmoCacheCilent.getInstance().getSceneMessageConcurrentHashMap();
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-][-]当前可以进入的场景：");
        for (Integer id:Scenes){
            System.out.println("[-]");
            System.out.println("[-][-]"+sceneMap.get(id).getPlaceName());
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }
}
