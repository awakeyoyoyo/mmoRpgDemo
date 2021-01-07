package com.liqihao.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.service.*;
import com.sun.org.apache.xpath.internal.operations.String;
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
            default:
                System.out.println("handler:收到");
                System.out.println(new java.lang.String(nettyResponse.getData()));
        }


    }
}
