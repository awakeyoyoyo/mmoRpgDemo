package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.NettyResponse;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.service.TeamService;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl implements TeamService {
    @Override
    public void teamMessageResponse(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data=nettyResponse.getData();
        TeamModel.TeamModelMessage myMessage;
        myMessage=TeamModel.TeamModelMessage.parseFrom(data);
        TeamModel.TeamMessageResponse messageResponse=myMessage.getTeamMessageResponse();
        TeamModel.TeamBeanDto teamBeanDto=messageResponse.getTeamBeanDto();
        System.out.println("--------------------------------------------------------");
        System.out.println("队伍的id： "+teamBeanDto.getTeamId()+" 队伍的名称： "+teamBeanDto.getTeamName());
        System.out.println("队长的id： "+teamBeanDto.getLeaderId());
        for (TeamModel.RoleDto r:teamBeanDto.getRoleDtosList()) {
            System.out.println("角色id： "+r.getId()+"角色名称： "+r.getName()+"  Hp:"+r.getNowHp()+"/"+r.getHp()
                    +"  Mp:"+r.getNowMP()+"/"+r.getMp()+" 所在队伍id： "+r.getTeamId()
            );
        }
        System.out.println("--------------------------------------------------------");
    }
}
