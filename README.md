## 基于文字表现的MMORPG游戏
### 配置文件
- medicineMessage.xlsx **药品信息配置**
- npcMessage.xlsx **npc信息配置**
- skillMessage.xlsx **技能信息配置**
- professionMessage.xlsx **职业信息配置**
- taskMessage.xlsx **任务信息配置**
- goodsMessage.xlsx **商品信息配置**
- copySceneMessage.xlsx **副本信息配置**
- guildAuthorityMessage.xlsx **公会权限配置**
- equipmentMessage.xlsx **装备信息配置**
- sceneMessage.xlsx **场景信息配置**
- guildPositionMessage.xlsx **公会职业配置**
- guildBaseMessage.xlsx **公会基本信息配置**
- bufferMessage.xlsx **buff信息配置**
- bossMessage.xlsx **boss信息配置**
- baseRoleMessage.xlsx **人物信息配置**
- baseDetailMessage.xlsx **基础信息配置**
### 游戏指令：
```java
    /**
     * 好友模块
     */
    /** 删除好友*/
    String REDUCE_FRIEND_REQUEST_CMD="reduceFriend";
    /** 申请好友*/
    String APPLY_FRIEND_REQUEST_CMD="applyFriend";
    /** 通过好友申请*/
    String AGREE_FRIEND_REQUEST_CMD="agreeFriendApply";
    /** 拒绝好友申请*/
    String REFUSE_FRIEND_REQUEST_CMD="refuseFriendApply";
    /** 好友列表*/
    String GET_FRIENDS_REQUEST_CMD="friendList";
    /** 好友申请列表*/
    String FRIEND_APPLY_LIST_REQUEST_CMD="friendApplyList";

    /**
     * 任务模块
     */
    /** 完成任务*/
    String FINISH_TASK_REQUEST_CMD="finishTask";
    /** 任务列表*/
    String GET_PEOPLE_TASK_REQUEST_CMD="tasks";
    /** 可接受任务列表*/
    String GET_CAN_ACCEPT_TASK_REQUEST_CMD="getCanAcceptTask";
    /** 接收任务*/
    String ACCEPT_TASK_REQUEST_CMD="acceptTask";
    /** 放弃任务*/
    String ABANDON_TASK_REQUEST_CMD="abandonTask";

    /**
     * 交易行模块
     */
    /** 上架商品*/
    String ADD_SELL_ARTICLE_REQUEST_CMD="addDealBankArticle";
    /** 下架商品列表*/
    String REDUCE_SELL_ARTICLE_REQUEST_CMD="reduceSellArticle";
    /** 购买一口价商品*/
    String BUY_ARTICLE_REQUEST_CMD="buyArticle";
    /** 竞拍商品*/
    String AUCTION_ARTICLE_REQUEST_CMD="auctionArticle";
    /** 拍卖行信息列表*/
    String GET_ARTICLE_REQUEST_CMD="dealBankMsg";

    /**
     * 交易模块
     */
    /** 发送交易申请*/
    String ASK_DEAL_REQUEST_CMD="askDeal";
    /** 同意交易*/
    String AGREE_DEAL_REQUEST_CMD="agreeDeal";
    /** 拒绝交易*/
    String REFUSE_DEAL_REQUEST_CMD="refuseDeal";
    /** 确认交易*/
    String CONFIRM_DEAL_REQUEST_CMD="confirmDeal";
    /** 取消交易*/
    String CANCEL_DEAL_REQUEST_CMD="cancelDeal";
    /** 交易栏信息*/
    String GET_DEAL_MESSAGE_REQUEST_CMD="dealMessage";
    /** 交易栏修改放入金币*/
    String SET_DEAL_MONEY_REQUEST_CMD="setDealMoney";
    /** 交易栏放入物品*/
    String ADD_DEAL_ARTICLE_REQUEST_CMD="addDealArticle";
    /** 拿出交易栏物品*/
    String ABANDON_DEAL_ARTICLE_REQUEST_CMD="abandonDealArticle";

    /**
     * 公会模块
     */
    /** 公会仓库捐赠金币*/
    String CONTRIBUTE_MONEY_REQUEST_CMD="contributeMoney";
    /** 公会仓库捐赠物品*/
    String CONTRIBUTE_ARTICLE_REQUEST_CMD="contributeArticle";
    /** 拿出公会仓库金币*/
    String GET_GUILD_MONEY_REQUEST_CMD="getGuildMoney";
    /** 拿出公会仓库物品*/
    String GET_GUILD_ARTICLE_REQUEST_CMD="getGuildArticle";
    /** 公会仓库信息*/
    String GET_GUILD_WAREHOUSE_REQUEST_CMD="guildWareHouseMsg";
    /** 创建公会*/
    String CREATE_GUILD_REQUEST_CMD="createGuild";
    /** 加入公会*/
    String JOIN_GUILD_REQUEST_CMD="joinGuild";
    /** 设置公会成员职业*/
    String SET_GUILD_POSITION_REQUEST_CMD="setGuildPosition";
    /** 离开公会*/
    String OUT_GUILD_REQUEST_CMD="outGuild";
    /** 同意公会申请*/
    String AGREE_GUILD_APPLY_REQUEST_CMD="agreeGuildApply";
    /** 拒绝公会申请*/
    String REFUSE_GUILD_APPLY_REQUEST_CMD="refuseGuildApply";
    /** 公会信息*/
    String GET_GUILD_MESSAGE_REQUEST_CMD="guildMsg";
    /** 公会申请列表*/
    String GET_GUILD_APPLY_LIST_REQUEST_CMD="getGuildApplyList";

    /**
     * 商店模块
     */
    /** 商店商品*/
    String FIND_ALL_GOODS_REQUEST_CMD="shopGoods";
    /** 人物金币*/
    String CHECK_MONEY_NUMBER_REQUEST_CMD="moneyMsg";
    /** 购买商品*/
    String BUY_GOODS_REQUEST_CMD="buyGoods";

    /**
     * 邮件模块
     */
    /** 获取邮件金币*/
    String GET_EMAIL_MONEY_REQUEST_CMD="getEmailMoney";
    /** 邮件信息*/
    String GET_EMAIL_MESSAGE_REQUEST_CMD="emailMsg";
    /** 获取邮件内物品*/
    String GET_EMAIL_ARTICLE_REQUEST_CMD="getEmailArticle";
    /** 接收邮件列表*/
    String ACCEPT_EMAIL_LIST_REQUEST_CMD="acceptEmailList";
    /** 已发送邮件列表*/
    String IS_SEND_EMAIL_LIST_REQUEST_CMD="isSendEmailList";
    /** 发送邮件*/
    String SEND_EMAIL_REQUEST_CMD="sendEmail";
    /** 删除接收邮件*/
    String DELETE_ACCEPT_EMAIL_REQUEST_CMD="deleteAcceptEmail";
    /** 删除已发送邮件*/
    String DELETE_SEND_EMAIL_REQUEST_CMD="deleteSendEmail";

    /**
     * 队伍模块
     */
    /** 发送队伍信息*/
    String SEND_TO_TEAM_REQUEST_CMD="sendMsgTeam";
    /** 发送场景信息*/
    String SEND_TO_SCENE_REQUEST_CMD="sendMsgScene";
    /** 发送全服信息*/
    String SEND_TO_ALL_REQUEST_CMD="sendMsgAll";
    /** 私聊*/
    String SEND_TO_ONE_REQUEST_CMD="sendMsgOne";

    /**
     * 副本模块
     */
    /** 查看可进入副本*/
    String ASK_CAN_COPY_SCENE_REQUEST_CMD ="askCanCopyScene";
    /** 当前副本信息*/
    String COPY_SCENE_MESSAGE_REQUEST_CMD ="copySceneMsg";
    /** 创建副本*/
    String CREATE_COPY_SCENE_REQUEST_CMD ="createCopyScene";
    /** 进入副本*/
    String ENTER_COPY_SCENE_REQUEST_CMD ="enterCopyScene";
    /** 离开副本*/
    String EXIT_COPY_SCENE_REQUEST_CMD ="exitCopyScene";
    /** 查看副本地面可拾取物品*/
    String FIND_ALL_CAN_REQUEST_CMD="findArticleFromFloor";
    /** 拾取地面物品*/
    String GET_ARTICLE_FROM_FLOOR_REQUEST_CMD="getArticleFromFloor";

    /**
     * 队伍模块
     */
    /** 将人踢除队伍*/
    String BAN_PEOPLE_REQUEST_CMD="banPeople";
    /** 解散队伍*/
    String DELETE_TEAM_REQUEST_CMD="deleteTeam";
    /** 同意队伍邀请*/
    String ENTRY_INVITE_PEOPLE_REQUEST_CMD="agreeTeamInvite";
    /** 同意队伍申请*/
    String ENTRY_APPLY_PEOPLE_REQUEST_CMD="agreeTeamApply";
    /** 离开队伍*/
    String EXIT_TEAM_REQUEST_CMD="exitTeam";
    /** 拒绝申请*/
    String REFUSE_APPLY_REQUEST_CMD="refuseApply";
    /** 拒绝邀请*/
    String REFUSE_INVITE_REQUEST_CMD="refuseInvite";
    /** 申请入队*/
    String APPLY_FOR_TEAM_REQUEST_CMD="applyTeam";
    /** 邀请玩家入队*/
    String INVITE_PEOPLE_REQUEST_CMD="invitePeople";
    /** 队伍申请列表*/
    String APPLY_MESSAGE_REQUEST_CMD="applyMsg";
    /** 队伍邀请列表*/
    String INVITE_MESSAGE_REQUEST_CMD="inviteMsg";
    /** 创建队伍*/
    String CREATE_TEAM_REQUEST_CMD="createTeam";
    /** 队伍信息*/
    String TEAM_MESSAGE_REQUEST_CMD="teamMsg";

    /**
     * 场景模块
     */
    /** 查看可进入场景*/
    String ASK_CAN_REQUEST_CMD="askCan";
    /** 查找当前场景角色*/
    String FIND_ALL_ROLES_REQUEST_CMD="findAllRoles";
    /** 前往场景*/
    String WENT_REQUEST_CMD="move";

    /**
     * 角色模块
     */
    /** 登陆*/
    String LOGIN_REQUEST_CMD="login";
    /** 注册*/
    String REGISTER_REQUEST_CMD="register";
    /** 退出登陆*/
    String LOGOUT_REQUEST_CMD="logout";
    /** 使用技能*/
    String USE_SKILL_REQUEST_CMD="skill";
    /** 与npc聊天*/
    String TALK_NPC_REQUEST_CMD="talk";

    /**
     * 背包模块
     */

    /** 整理背包*/
    String SORT_BACKPACK_REQUEST_CMD="sortBag";
    /** 背包信息*/
    String BACKPACK_MSG_REQUEST_CMD="bag";
    /** 使用物品*/
    String USE_REQUEST_CMD="useArticle";
    /** 丢弃物品*/
    String ABANDON_REQUEST_CMD="abandonArticle";
    /** 增加物品*/
    String ADD_ARTICLE_REQUEST_CMD="addArticle";
    /** 修复装备*/
    String FIX_EQUIPMENT_REQUEST_CMD="fixEquipment";
    /** 穿装备*/
    String ADD_EQUIPMENT_REQUEST_CMD="addEquipment";
    /** 装备信息*/
    String EQUIPMENT_MSG_REQUEST_CMD="equipmentMsg";
    /** 脱装备*/
    String REDUCE_EQUIPMENT_REQUEST_CMD="reduceEquipment";
```