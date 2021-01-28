package com.liqihao.application;


import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.CmdCode;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyRequest;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.SkillAttackTypeCode;
import com.liqihao.commons.enums.SkillDamageTypeCode;
import com.liqihao.pojo.MmoRole;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.protobufObject.*;
import com.liqihao.utils.CommonsUtil;
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
                    case ConstantValue.FIND_ALL_GOODS_REQUEST:
                        findAllGoodsRequest(scanner);
                        break;
                    case ConstantValue.SORT_BACKPACK_REQUEST:
                        sortBackPackRequest(scanner);
                        break;
                    case ConstantValue.CREATE_GUILD_REQUEST:
                        createGuildRequest(scanner);
                        break;
                    case ConstantValue.SET_GUILD_POSITION_REQUEST:
                        setGuildPositionRequest(scanner);
                        break;
                    case ConstantValue.JOIN_GUILD_REQUEST:
                        joinGuildRequest(scanner);
                        break;
                    case ConstantValue.OUT_GUILD_REQUEST:
                        outGuildRequest(scanner);
                        break;
                    case ConstantValue.AGREE_GUILD_APPLY_REQUEST:
                        agreeGuildApplyRequest(scanner);
                        break;
                    case ConstantValue.REFUSE_GUILD_APPLY_REQUEST:
                        refuseGuildApplyRequest(scanner);
                        break;
                    case ConstantValue.GET_GUILD_APPLY_LIST_REQUEST:
                        getGuildApplyListRequest(scanner);
                        break;
                    case ConstantValue.GET_GUILD_MESSAGE_REQUEST:
                        getGuildMessageRequest(scanner);
                        break;
                    case ConstantValue.GET_GUILD_WAREHOUSE_REQUEST:
                        getGuildWareHouseRequest(scanner);
                        break;
                    case ConstantValue.CONTRIBUTE_MONEY_REQUEST:
                        contributeMoneyRequest(scanner);
                        break;
                    case ConstantValue.CONTRIBUTE_ARTICLE_REQUEST:
                        contributeArticleRequest(scanner);
                        break;
                    case ConstantValue.GET_GUILD_MONEY_REQUEST:
                        getGuildMoneyRequest(scanner);
                        break;
                    case ConstantValue.GET_GUILD_ARTICLE_REQUEST:
                        getGuildArticleRequest(scanner);
                        break;
                    case ConstantValue.ASK_DEAL_REQUEST:
                        askDeal(scanner);
                        break;
                    case ConstantValue.AGREE_DEAL_REQUEST:
                        agreeDeal(scanner);
                        break;
                    case ConstantValue.REFUSE_DEAL_REQUEST:
                        refuseDeal(scanner);
                        break;
                    case ConstantValue.CONFIRM_DEAL_REQUEST:
                        confirmDeal(scanner);
                        break;
                    case ConstantValue.CANCEL_DEAL_REQUEST:
                        cancelDeal(scanner);
                        break;
                    case ConstantValue.GET_DEAL_MESSAGE_REQUEST:
                        getDealMessage(scanner);
                        break;
                    case ConstantValue.SET_DEAL_MONEY_REQUEST:
                        setDealMoney(scanner);
                        break;
                    case ConstantValue.ADD_DEAL_ARTICLE_REQUEST:
                        addDealArticle(scanner);
                        break;
                    case ConstantValue.ABANDON_DEAL_ARTICLE_REQUEST:
                        abandonDealArticle(scanner);
                        break;
                    case ConstantValue.ADD_SELL_ARTICLE_REQUEST:
                        addSellArticle(scanner);
                        break;
                    case ConstantValue.REDUCE_SELL_ARTICLE_REQUEST:
                        reduceSellArticle(scanner);
                        break;
                    case ConstantValue.BUY_ARTICLE_REQUEST:
                        buyArticle(scanner);
                        break;
                    case ConstantValue.AUCTION_ARTICLE_REQUEST:
                        auctionArticle(scanner);
                        break;
                    case ConstantValue.GET_ARTICLE_REQUEST:
                        getArticle(scanner);
                        break;
                    case ConstantValue.GET_EMAIL_MONEY_REQUEST:
                        getEmailMoney(scanner);
                        break;
                    case ConstantValue.GET_PEOPLE_TASK_REQUEST:
                        getPeopleTask(scanner);
                        break;
                    case ConstantValue.GET_CAN_ACCEPT_TASK_REQUEST:
                        getCanAcceptTask(scanner);
                        break;
                    case ConstantValue.ACCEPT_TASK_REQUEST:
                        acceptTask(scanner);
                        break;
                    case ConstantValue.ABANDON_TASK_REQUEST:
                        abandonTask(scanner);
                        break;
                        //
                    case ConstantValue.APPLY_FRIEND_REQUEST:
                        applyFriendRequest(scanner);
                        break;
                    case ConstantValue.AGREE_FRIEND_REQUEST:
                        agreeFriendRequest(scanner);
                        break;
                    case ConstantValue.REFUSE_FRIEND_REQUEST:
                        refuseFriendRequest(scanner);
                        break;
                    case ConstantValue.GET_FRIENDS_REQUEST:
                        getFriendsRequest(scanner);
                        break;
                    case ConstantValue.FRIEND_APPLY_LIST_REQUEST:
                        friendApplyListRequest(scanner);
                        break;
                    case ConstantValue.REDUCE_FRIEND_REQUEST:
                        reduceFriendRequest(scanner);
                        break;
                    case ConstantValue.FINISH_TASK_REQUEST:
                        finishTaskRequest(scanner);
                        break;
                    default:
                        System.out.println("GameStart-handler:收到错误cmd");
                }
    }

    private void finishTaskRequest(Scanner scanner) {
        System.out.println("请输入你要完成并领取的任务id：");
        Integer taskId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.FINISH_TASK_REQUEST);
        TaskModel.TaskModelMessage myMessage;
        myMessage= TaskModel.TaskModelMessage.newBuilder()
                .setDataType(TaskModel.TaskModelMessage.DateType.FinishTaskRequest)
                .setFinishTaskRequest(TaskModel.FinishTaskRequest.newBuilder()
                        .setTaskMessageId(taskId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void reduceFriendRequest(Scanner scanner) {
        System.out.println("请输入你要删除的好友的角色id：");
        Integer roleId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REDUCE_FRIEND_REQUEST);
        FriendModel.FriendModelMessage myMessage;
        myMessage= FriendModel.FriendModelMessage.newBuilder()
                .setDataType(FriendModel.FriendModelMessage.DateType.ReduceFriendRequest)
                .setReduceFriendRequest(FriendModel.ReduceFriendRequest.newBuilder()
                        .setRoleId(roleId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void friendApplyListRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.FRIEND_APPLY_LIST_REQUEST);
        FriendModel.FriendModelMessage myMessage;
        myMessage= FriendModel.FriendModelMessage.newBuilder()
                .setDataType(FriendModel.FriendModelMessage.DateType.FriendApplyListRequest)
                .setFriendApplyListRequest(FriendModel.FriendApplyListRequest.newBuilder()
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getFriendsRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_FRIENDS_REQUEST);
        FriendModel.FriendModelMessage myMessage;
        myMessage= FriendModel.FriendModelMessage.newBuilder()
                .setDataType(FriendModel.FriendModelMessage.DateType.GetFriendsRequest)
                .setGetFriendsRequest(FriendModel.GetFriendsRequest.newBuilder()
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void refuseFriendRequest(Scanner scanner) {
        System.out.println("请输入你要拒绝的申请id：");
        Integer applyId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REFUSE_FRIEND_REQUEST);
        FriendModel.FriendModelMessage myMessage;
        myMessage= FriendModel.FriendModelMessage.newBuilder()
                .setDataType(FriendModel.FriendModelMessage.DateType.RefuseFriendRequest)
                .setRefuseFriendRequest(FriendModel.RefuseFriendRequest.newBuilder().setApplyId(applyId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void agreeFriendRequest(Scanner scanner) {
        System.out.println("请输入你要通过的申请id：");
        Integer applyId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.AGREE_FRIEND_REQUEST);
        FriendModel.FriendModelMessage myMessage;
        myMessage= FriendModel.FriendModelMessage.newBuilder()
                .setDataType(FriendModel.FriendModelMessage.DateType.AgreeFriendRequest)
                .setAgreeFriendRequest(FriendModel.AgreeFriendRequest.newBuilder().setApplyId(applyId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void applyFriendRequest(Scanner scanner) {
        System.out.println("请输入你要添加的好友的玩家id：");
        Integer roleId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.APPLY_FRIEND_REQUEST);
        FriendModel.FriendModelMessage myMessage;
        myMessage= FriendModel.FriendModelMessage.newBuilder()
                .setDataType(FriendModel.FriendModelMessage.DateType.ApplyFriendRequest)
                .setApplyFriendRequest(FriendModel.ApplyFriendRequest.newBuilder().setRoleId(roleId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void abandonTask(Scanner scanner) {
        System.out.println("请输入你要放弃的任务id：");
        Integer taskId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ABANDON_TASK_REQUEST);
        TaskModel.TaskModelMessage myMessage;
        myMessage= TaskModel.TaskModelMessage.newBuilder()
                .setDataType(TaskModel.TaskModelMessage.DateType.AbandonTaskRequest)
                .setAbandonTaskRequest(TaskModel.AbandonTaskRequest.newBuilder()
                        .setTaskMessageId(taskId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void acceptTask(Scanner scanner) {
        System.out.println("请输入你要接收的任务id：");
        Integer taskId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ACCEPT_TASK_REQUEST);
        TaskModel.TaskModelMessage myMessage;
        myMessage= TaskModel.TaskModelMessage.newBuilder()
                .setDataType(TaskModel.TaskModelMessage.DateType.AcceptTaskRequest)
                .setAcceptTaskRequest(TaskModel.AcceptTaskRequest.newBuilder()
                        .setTaskMessageId(taskId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getCanAcceptTask(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_CAN_ACCEPT_TASK_REQUEST);
        TaskModel.TaskModelMessage myMessage;
        myMessage= TaskModel.TaskModelMessage.newBuilder()
                .setDataType(TaskModel.TaskModelMessage.DateType.GetCanAcceptTaskRequest)
                .setGetCanAcceptTaskRequest(TaskModel.GetCanAcceptTaskRequest.newBuilder()
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getPeopleTask(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_PEOPLE_TASK_REQUEST);
        TaskModel.TaskModelMessage myMessage;
        myMessage= TaskModel.TaskModelMessage.newBuilder()
                .setDataType(TaskModel.TaskModelMessage.DateType.GetPeopleTaskRequest)
                .setGetPeopleTaskRequest(TaskModel.GetPeopleTaskRequest.newBuilder()
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getArticle(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_ARTICLE_REQUEST);
        DealBankModel.DealBankModelMessage myMessage;
        myMessage= DealBankModel.DealBankModelMessage.newBuilder()
                .setDataType(DealBankModel.DealBankModelMessage.DateType.GetArticleRequest)
                .setGetArticleRequest(DealBankModel.GetArticleRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void auctionArticle(Scanner scanner) {
        System.out.println("请输入你要拍卖的商品id：");
        Integer dealBankArticleId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入你要用多少钱拍卖：");
        Integer money=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.AUCTION_ARTICLE_REQUEST);
        DealBankModel.DealBankModelMessage myMessage;
        myMessage= DealBankModel.DealBankModelMessage.newBuilder()
                .setDataType(DealBankModel.DealBankModelMessage.DateType.AuctionArticleRequest)
                .setAuctionArticleRequest(DealBankModel.AuctionArticleRequest.newBuilder()
                        .setMoney(money).setDealBankArticleId(dealBankArticleId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void buyArticle(Scanner scanner) {
        System.out.println("请输入你要购买的商品id：");
        Integer dealBankArticleId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.BUY_ARTICLE_REQUEST);
        DealBankModel.DealBankModelMessage myMessage;
        myMessage= DealBankModel.DealBankModelMessage.newBuilder()
                .setDataType(DealBankModel.DealBankModelMessage.DateType.BuyArticleRequest)
                .setBuyArticleRequest(DealBankModel.BuyArticleRequest.newBuilder()
                        .setDealBankArticleId(dealBankArticleId)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void reduceAuctionArticle(Scanner scanner) {
        System.out.println("请输入你要下架的拍卖商品id：");
        Integer dealBankArticleId = scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REDUCE_AUCTION_ARTICLE_REQUEST);
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.newBuilder()
                .setDataType(DealBankModel.DealBankModelMessage.DateType.ReduceAuctionArticleRequest)
                .setReduceAuctionArticleRequest(DealBankModel.ReduceAuctionArticleRequest.newBuilder()
                        .setDealBankArticleId(dealBankArticleId)
                        .build()).build();
        byte[] data = myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void reduceSellArticle(Scanner scanner) {
        System.out.println("请输入你要下架的一口价商品id：");
        Integer dealBankArticleId = scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REDUCE_SELL_ARTICLE_REQUEST);
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.newBuilder()
                .setDataType(DealBankModel.DealBankModelMessage.DateType.ReduceSellArticleRequest)
                .setReduceSellArticleRequest(DealBankModel.ReduceSellArticleRequest.newBuilder()
                        .setDealBankArticleId(dealBankArticleId)
                        .build()).build();
        byte[] data = myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }
    

    private void addSellArticle(Scanner scanner) {
        System.out.println("请输入出售物品的背包id：");
        Integer articleId = scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入物品的数量：");
        Integer  num = scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入定价：");
        Integer price = scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入出售物品的方式：0：一口价 1：拍卖");
        Integer type = scanner.nextInt();
        if (type!=0&&type!=1){
            System.out.println("输入错误数字");
            return;
        }
        scanner.nextLine();
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ADD_SELL_ARTICLE_REQUEST);
        DealBankModel.DealBankModelMessage myMessage;
        myMessage = DealBankModel.DealBankModelMessage.newBuilder()
                .setDataType(DealBankModel.DealBankModelMessage.DateType.AddSellArticleRequest)
                .setAddSellArticleRequest(DealBankModel.AddSellArticleRequest.newBuilder()
                        .setArticleId(articleId).setNum(num).setPrice(price).setType(type)
                        .build()).build();
        byte[] data = myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getEmailMoney(Scanner scanner) {
        System.out.println("请输入你要收取金币的emailId：");
        Integer emailId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_EMAIL_MONEY_REQUEST);
        EmailModel.EmailModelMessage myMessage;
        myMessage= EmailModel.EmailModelMessage.newBuilder()
                .setDataType(EmailModel.EmailModelMessage.DateType.GetEmailMoneyRequest)
                .setGetEmailMoneyRequest(EmailModel.GetEmailMoneyRequest.newBuilder().setEmailId(emailId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void abandonDealArticle(Scanner scanner) {
        System.out.println("请输入你要拿回交易栏的物品的交易栏id");
        Integer dealArticleId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入你拿回的数量");
        Integer num=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ABANDON_DEAL_ARTICLE_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.AbandonArticleRequest)
                .setAbandonArticleRequest(DealModel.AbandonArticleRequest.newBuilder().setDealArticleId(dealArticleId).setNum(num).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void addDealArticle(Scanner scanner) {
        System.out.println("请输入你要放入交易栏的物品的背包id");
        Integer articleId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入你要数量");
        Integer num=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ADD_DEAL_ARTICLE_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.AddArticleRequest)
                .setAddArticleRequest(DealModel.AddArticleRequest.newBuilder().setArticleId(articleId).setNum(num).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void setDealMoney(Scanner scanner) {
        System.out.println("请输入你要修改的交易金额");
        Integer money=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SET_DEAL_MONEY_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.SetDealMoneyRequest)
                .setSetDealMoneyRequest(DealModel.SetDealMoneyRequest.newBuilder().setMoney(money).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getDealMessage(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_DEAL_MESSAGE_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.GetDealMessageRequest)
                .setGetDealMessageRequest(DealModel.GetDealMessageRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void cancelDeal(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CANCEL_DEAL_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.CancelDealRequest)
                .setCancelDealRequest(DealModel.CancelDealRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void confirmDeal(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CONFIRM_DEAL_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.ConfirmDealRequest)
                .setConfirmDealRequest(DealModel.ConfirmDealRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void refuseDeal(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REFUSE_DEAL_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.RefuseDealRequest)
                .setRefuseDealRequest(DealModel.RefuseDealRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void agreeDeal(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.AGREE_DEAL_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.AgreeDealRequest)
                .setAgreeDealRequest(DealModel.AgreeDealRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void askDeal(Scanner scanner) {
        System.out.println("请输入你要与之交易的玩家id");
        Integer roleId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.ASK_DEAL_REQUEST);
        DealModel.DealModelMessage myMessage;
        myMessage=DealModel.DealModelMessage.newBuilder()
                .setDataType(DealModel.DealModelMessage.DateType.AskDealRequest)
                .setAskDealRequest(DealModel.AskDealRequest.newBuilder().setRoleId(roleId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getGuildWareHouseRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_GUILD_WAREHOUSE_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.GetGuildWareHouseRequest)
                .setGetGuildWareHouseRequest(GuildModel.GetGuildWareHouseRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);

    }

    private void contributeMoneyRequest(Scanner scanner) {
        System.out.println("请输入你要捐献的金额：");
        Integer money=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CONTRIBUTE_MONEY_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.ContributeMoneyRequest)
                .setContributeMoneyRequest(GuildModel.ContributeMoneyRequest.newBuilder().setMoney(money).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void contributeArticleRequest(Scanner scanner) {
        System.out.println("请输入你要捐献的物品的背包id：");
        Integer articleId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入你要捐献的物品的类型：药品-0 武器-1");
        Integer articleType=scanner.nextInt();
        if (articleType<0||articleType>1){
            System.out.println("输入错误数字");
        }
        Integer number=1;
        if (articleType.equals(1)){
            number=1;
        }else{
            System.out.println("输入放入数量");
            number=scanner.nextInt();
            scanner.nextLine();
        }
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CONTRIBUTE_ARTICLE_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.ContributeArticleRequest)
                .setContributeArticleRequest(GuildModel.ContributeArticleRequest.newBuilder()
                        .setArticleId(articleId).setNumber(number)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getGuildMoneyRequest(Scanner scanner) {
        System.out.println("请输入你要取出的金额：");
        Integer money=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_GUILD_MONEY_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.GetMoneyRequest)
                .setGetMoneyRequest(GuildModel.GetMoneyRequest.newBuilder().setMoney(money).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getGuildArticleRequest(Scanner scanner) {
        System.out.println("请输入你要取出的物品的仓库id：");
        Integer wareHouseId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("请输入你要取出的物品的类型：药品-0 武器-1");
        Integer articleType=scanner.nextInt();
        if (articleType<0||articleType>1){
            System.out.println("输入错误数字");
        }
        Integer number;
        if (articleType.equals(1)){
            number=1;
        }else{
            System.out.println("输入取出数量");
            number=scanner.nextInt();
            scanner.nextLine();
        }
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_GUILD_ARTICLE_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.GetArticleRequest)
                .setGetArticleRequest(GuildModel.GetArticleRequest.newBuilder()
                        .setWarehouseId(wareHouseId).setNumber(number)
                        .build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getGuildMessageRequest(Scanner scanner) {
        System.out.println("请输入你要查看的公会的id：");
        Integer guildBeanId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_GUILD_MESSAGE_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.GetGuildBeanRequest)
                .setGetGuildBeanRequest(GuildModel.GetGuildBeanRequest.newBuilder().setGuildBeanId(guildBeanId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void getGuildApplyListRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.GET_GUILD_APPLY_LIST_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.GetGuildApplyListRequest)
                .setGetGuildApplyListRequest(GuildModel.GetGuildApplyListRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void refuseGuildApplyRequest(Scanner scanner) {
        System.out.println("请输入你要删除的公会申请的id：");
        Integer guildApplyId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REFUSE_GUILD_APPLY_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.RefuseGuildApplyRequest)
                .setRefuseGuildApplyRequest(GuildModel.RefuseGuildApplyRequest.newBuilder().setGuildApplyId(guildApplyId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void agreeGuildApplyRequest(Scanner scanner) {
        System.out.println("请输入你要同意的公会申请的id：");
        Integer guildApplyId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.AGREE_GUILD_APPLY_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.AgreeGuildApplyRequest)
                .setAgreeGuildApplyRequest(GuildModel.AgreeGuildApplyRequest.newBuilder().setGuildApplyId(guildApplyId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void outGuildRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.OUT_GUILD_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.OutGuildRequest)
                .setOutGuildRequest(GuildModel.OutGuildRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void joinGuildRequest(Scanner scanner) {
        System.out.println("请输入你要加入的公会id：");
        Integer guildId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.JOIN_GUILD_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.JoinGuildRequest)
                .setJoinGuildRequest(GuildModel.JoinGuildRequest.newBuilder().setGuildId(guildId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void setGuildPositionRequest(Scanner scanner) {
        System.out.println("请输入你要修改职位的玩家id：");
        Integer roleId=scanner.nextInt();
        scanner.nextLine();
        System.out.println("请选择该玩家的新职位：");
        for (GuildPositionMessage guildPositionMessage:MmoCacheCilent.getInstance().getGuildPositionMessageConcurrentHashMap().values()) {
            System.out.println("职位id："+guildPositionMessage.getId()+" 职位名称："+guildPositionMessage.getName());
        }
        Integer guildPositionId=scanner.nextInt();
        scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SET_GUILD_POSITION_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.SetGuildRequest)
                .setSetGuildRequest(GuildModel.SetGuildRequest.newBuilder().setRoleId(roleId).setGuildPosition(guildPositionId).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void createGuildRequest(Scanner scanner) {
        System.out.println("请输入你要创建的公会名称：");
        String name=scanner.nextLine();
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.CREATE_GUILD_REQUEST);
        GuildModel.GuildModelMessage myMessage;
        myMessage=GuildModel.GuildModelMessage.newBuilder()
                .setDataType(GuildModel.GuildModelMessage.DateType.CreateGuildRequest)
                .setCreateGuildRequest(GuildModel.CreateGuildRequest.newBuilder().setName(name).build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void sortBackPackRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.SORT_BACKPACK_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.SortBackPackRequest)
                .setSortBackPackRequest(BackPackModel.SortBackPackRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
    }

    private void findAllGoodsRequest(Scanner scanner) {
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.FIND_ALL_GOODS_REQUEST);
        BackPackModel.BackPackModelMessage myMessage;
        myMessage=BackPackModel.BackPackModelMessage.newBuilder()
                .setDataType(BackPackModel.BackPackModelMessage.DateType.FindAllGoodsRequest)
                .setFindAllGoodsRequest(BackPackModel.FindAllGoodsRequest.newBuilder().build()).build();
        byte[] data=myMessage.toByteArray();
        nettyRequest.setData(data);
        channel.writeAndFlush(nettyRequest);
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
        if (map.get(skillId)==null){
            System.out.println("没有该技能");
            return;
        }
        if (!map.get(skillId).getSkillAttackType().equals(SkillAttackTypeCode.CALL.getCode())) {
            if (map.get(skillId).getSkillAttackType().equals(SkillAttackTypeCode.SINGLE.getCode())) {
                if (!map.get(skillId).getSkillDamageType().equals(SkillDamageTypeCode.ADD.getCode())) {
                    System.out.println("请输入你的施法目标的类型： 玩家为1 怪物为2");
                    roleType = scanner.nextInt();
                    scanner.nextLine();
                    ;
                    if (roleType != 1 && roleType != 2) {
                        System.out.println("请输入正确数字");
                        return;
                    }
                    System.out.println("请输入你的施法目标的id");
                    roleId = scanner.nextInt();
                    scanner.nextLine();
                } else {
                    roleType = 1;
                    System.out.println("请输入你的施玩家的id");
                    roleId = scanner.nextInt();
                    scanner.nextLine();
                }
            }
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
        System.out.println("请输入你要选择的职业：1-战士 2-牧师 3-法师 4-召唤师");
        Integer professionId=scanner.nextInt();
        scanner.nextLine();
        if (professionId<1||professionId>4){
            System.out.println("输入错误数字");
            return;
        }
        NettyRequest nettyRequest=new NettyRequest();
        nettyRequest.setCmd(ConstantValue.REGISTER_REQUEST);
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.newBuilder().setDataType(PlayModel.PlayModelMessage.DateType.RegisterRequest)
                .setRegisterRequest(
                        PlayModel.RegisterRequest.newBuilder().
                                setPassword(password).setRolename(roleName)
                                .setProfessionId(professionId).
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
