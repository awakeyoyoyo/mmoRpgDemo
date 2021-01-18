package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Dispatcherservlet {
    @Autowired
    private SceneService sceneService;
    @Autowired
    private PlayService playService;
    @Autowired
    private GameService gameService;
    @Autowired
    private BackPackService backPackService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private CopySceneService copySceneService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private DealService dealService;
    /**
     * 根据model和cmd转发到不同的service
     *
     * @param nettyResponse
     * @return
     */
    public void handler(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        int cmd = nettyResponse.getCmd();

        switch (cmd) {
            case ConstantValue.WENT_RESPONSE:
                sceneService.wentResponse(nettyResponse);
                break;
            case ConstantValue.FIND_ALL_ROLES_RESPONSE:
                sceneService.findAllRolesResponse(nettyResponse);
                break;
            case ConstantValue.ROLE_RESPONSE:
                sceneService.roleResponse(nettyResponse);
                break;
            case ConstantValue.LOGIN_RESPONSE:
                playService.loginResponse(nettyResponse);
                break;
            case ConstantValue.REGISTER_RESPONSE:
                playService.registerResponse(nettyResponse);
                break;
            case ConstantValue.LOGOUT_RESPONSE:
                playService.logoutResponse(nettyResponse);
                break;
            case ConstantValue.OUT_RIME_RESPONSE:
                gameService.outTimeResponse(nettyResponse);
                break;
            case ConstantValue.TALK_NPC_RESPONSE:
                sceneService.talkNPCResponse(nettyResponse);
                break;
            case ConstantValue.USE_SKILL_RSPONSE:
                playService.useSkillResponse(nettyResponse);
                break;
            case ConstantValue.DAMAGES_NOTICE_RESPONSE:
                playService.damagesNoticeResponse(nettyResponse);
                break;
            case ConstantValue.ABANDON_RESPONSE:
                backPackService.abandonResponse(nettyResponse);
                break;
            case ConstantValue.USE_RESPONSE:
                backPackService.useResponse(nettyResponse);
                break;
            case ConstantValue.BACKPACK_MSG_RESPONSE:
                backPackService.backPackMsgResponse(nettyResponse);
                break;
            case ConstantValue.ADD_ARTICLE_RESPONSE:
                backPackService.addArticleResponse(nettyResponse);
                break;
            case ConstantValue.FIND_ALL_CAN_RESPONSE:
                backPackService.findAllCanResponse(nettyResponse);
                break;
            case ConstantValue.GET_ARTICLE_FROM_FLOOR_RESPONSE:
                backPackService.getArticleFromFloorResponse(nettyResponse);
                break;
            case ConstantValue.ADD_EQUIPMENT_RESPONSE:
                equipmentService.addEquipmentResponse(nettyResponse);
                break;
            case ConstantValue.REDUCE_EQUIPMENT_RESPONSE:
                equipmentService.reduceEquipmentResponse(nettyResponse);
                break;
            case ConstantValue.EQUIPMENT_MSG_RESPONSE:
                equipmentService.equipmentMsgResponse(nettyResponse);
                break;
            case ConstantValue.FIX_EQUIPMENT_RESPONSE:
                equipmentService.fixEquipmentResponse(nettyResponse);
                break;
            case ConstantValue.FAIL_RESPONSE:
                System.out.println(new java.lang.String(nettyResponse.getData()));
                break;
            case ConstantValue.TEAM_MESSAGE_RESPONSE:
                teamService.teamMessageResponse(nettyResponse);
                break;
            case ConstantValue.APPLY_FOR_TEAM_RESPONSE:
                teamService.applyForTeamResponse(nettyResponse);
                break;
            case ConstantValue.INVITE_PEOPLE_RESPONSE:
                teamService.invitePeopleResponse(nettyResponse);
                break;
            case ConstantValue.APPLY_MESSAGE_RESPONSE:
                teamService.applyMessageResponse(nettyResponse);
                break;
            case ConstantValue.INVITE_MESSAGE_RESPONSE:
                teamService.inviteMessageResponse(nettyResponse);
                break;
            case ConstantValue.REFUSE_INVITE_RESPONSE:
                teamService.refuseInviteResponse(nettyResponse);
                break;
            case ConstantValue.REFUSE_APPLY_RESPONSE:
                teamService.refuseApplyResponse(nettyResponse);
                break;
            case ConstantValue.ENTRY_PEOPLE_RESPONSE:
                teamService.entryPeopleResponse(nettyResponse);
                break;
            case ConstantValue.EXIT_TEAM_RESPONSE:
                teamService.exitTeamResponse(nettyResponse);
                break;
            case ConstantValue.LEADER_TEAM_RESPONSE:
                teamService.leaderTeamResponse(nettyResponse);
                break;
            case ConstantValue.BAN_PEOPLE_RESPONSE:
                teamService.banPeopleResponse(nettyResponse);
                break;
            case ConstantValue.DELETE_TEAM_RESPONSE:
                teamService.deleteTeamResponse(nettyResponse);
                break;
            case ConstantValue.COPY_SCENE_MESSAGE_RESPONSE:
                copySceneService.copySceneMessageResponse(nettyResponse);
                break;
            case ConstantValue.ENTER_COPY_SCENE_RESPONSE:
                copySceneService.enterCopySceneResponse(nettyResponse);
                break;
            case ConstantValue.EXIT_COPY_SCENE_RESPONSE:
                copySceneService.exitCopySceneResponse(nettyResponse);
                break;
            case ConstantValue.CREATE_COPY_SCENE_RESPONSE:
                copySceneService.createCopySceneResponse(nettyResponse);
                break;
            case ConstantValue.COPY_SCENE_FINISH_RESPONSE:
                copySceneService.copySceneFinishResponse(nettyResponse);
                break;
            case ConstantValue.CHANGE_SUCCESS_RESPONSE:
                copySceneService.changeSuccessResponse(nettyResponse);
                break;
            case ConstantValue.CHANGE_FAIL_RESPONSE:
                copySceneService.changeFailResponse(nettyResponse);
                break;
            case ConstantValue.ACCEPT_MESSAGE_RESPONSE:
                chatService.acceptMessageResopnse(nettyResponse);
                break;
            case ConstantValue.GET_EMAIL_MESSAGE_RESPONSE:
                emailService.getEmailMessageResponse(nettyResponse);
                break;
            case ConstantValue.GET_EMAIL_ARTICLE_RESPONSE:
                emailService.getEmailArticleResponse(nettyResponse);
                break;
            case ConstantValue.ACCEPT_EMAIL_LIST_RESPONSE:
                emailService.acceptEmailListResponse(nettyResponse);
                break;
            case ConstantValue.GET_EMAIL_MONEY_RESPONSE:
                emailService.getEmailMoneyResponse(nettyResponse);
                break;
            case ConstantValue.IS_SEND_EMAIL_LIST_RESPONSE:
                emailService.isSendEmailListResponse(nettyResponse);
                break;
            case ConstantValue.SEND_EMAIL_RESPONSE:
                emailService.sendEmailResponse(nettyResponse);
                break;
            case ConstantValue.DELETE_ACCEPT_EMAIL_RESPONSE:
                emailService.deleteAcceptEmailResponse(nettyResponse);
                break;
            case ConstantValue.DELETE_SEND_EMAIL_RESPONSE:
                emailService.deleteSendEmailResponse(nettyResponse);
                break;
            case ConstantValue.CHECK_MONEY_NUMBER_RESPONSE:
                backPackService.checkMoneyNumberResponse(nettyResponse);
                break;
            case ConstantValue.BUY_GOODS_RESPONSE:
                backPackService.buyGoodsResponse(nettyResponse);
                break;
            case ConstantValue.FIND_ALL_GOODS_RESPONSE:
                backPackService.findAllGoodsResponse(nettyResponse);
                break;
            case ConstantValue.CREATE_GUILD_RESPONSE:
                guildService.createGuild(nettyResponse);
                break;
            case ConstantValue.JOIN_GUILD_RESPONSE:
                guildService.joinGuild(nettyResponse);
                break;
            case ConstantValue.SET_GUILD_POSITION_RESPONSE:
                guildService.setGuildPosition(nettyResponse);
                break;
            case ConstantValue.OUT_GUILD_RESPONSE:
                guildService.outGuild(nettyResponse);
                break;
            case ConstantValue.AGREE_GUILD_APPLY_RESPONSE:
                guildService.agreeGuildApply(nettyResponse);
                break;
            case ConstantValue.REFUSE_GUILD_APPLY_RESPONSE:
                guildService.refuseGuildApply(nettyResponse);
                break;
            case ConstantValue.GET_GUILD_APPLY_LIST_RESPONSE:
                guildService.getGuildApply(nettyResponse);
                break;
            case ConstantValue.GET_GUILD_MESSAGE_RESPONSE:
                guildService.getGuildBean(nettyResponse);
                break;
            case ConstantValue.GUILD_APPLY_RESPONSE:
                guildService.guildApplyResponse(nettyResponse);
                break;
            case ConstantValue.CONTRIBUTE_MONEY_RESPONSE:
                guildService.contributeMoney(nettyResponse);
                break;
            case ConstantValue.CONTRIBUTE_ARTICLE_RESPONSE:
                guildService.contributeArticle(nettyResponse);
                break;
            case ConstantValue.GET_GUILD_MONEY_RESPONSE:
                guildService.getGuildMoney(nettyResponse);
                break;
            case ConstantValue.GET_GUILD_ARTICLE_RESPONSE:
                guildService.getGuildArticle(nettyResponse);
                break;
            case ConstantValue.GET_GUILD_WAREHOUSE_RESPONSE:
                guildService.getGuildWareHouse(nettyResponse);
                break;
            case ConstantValue.ASK_DEAL_RESPONSE:
                dealService.askDealResponse(nettyResponse);
                break;
            case ConstantValue.AGREE_DEAL_RESPONSE:
                dealService.agreeDealResponse(nettyResponse);
                break;
            case ConstantValue.REFUSE_DEAL_RESPONSE:
                dealService.refuseDealResponse(nettyResponse);
                break;
            case ConstantValue.CONFIRM_DEAL_RESPONSE:
                dealService.confirmDealResponse(nettyResponse);
                break;
            case ConstantValue.CANCEL_DEAL_RESPONSE:
                dealService.cancelDealResponse(nettyResponse);
                break;
            case ConstantValue.GET_DEAL_MESSAGE_RESPONSE:
                dealService.getDealMessageResponse(nettyResponse);
                break;
            case ConstantValue.SET_DEAL_MONEY_RESPONSE:
                dealService.setDealMoneyResponse(nettyResponse);
                break;
            case ConstantValue.ADD_DEAL_ARTICLE_RESPONSE:
                dealService.addDealArticleResponse(nettyResponse);
                break;
            case ConstantValue.ABANDON_DEAL_ARTICLE_RESPONSE:
                dealService.abandonDealArticleResponse(nettyResponse);
                break;
            case ConstantValue.DEAL_SUCCESS_RESPONSE:
                dealService.dealSuccessResponse(nettyResponse);
                break;
            default:
                System.out.println("handler:收到");
                System.out.println(new java.lang.String(nettyResponse.getData()));
        }


    }
}
