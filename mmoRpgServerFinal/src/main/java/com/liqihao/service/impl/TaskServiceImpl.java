package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.bean.TaskBean.BaseTaskBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.EquipmentModel;
import com.liqihao.protobufObject.TaskModel;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.service.TaskService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

/**
 * 任务模块
 * @author lqhao
 */
@Service
@HandlerServiceTag(protobufModel = "TaskModel$TaskModelMessage")
public class TaskServiceImpl implements TaskService {
    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_PEOPLE_TASK_REQUEST,module = ConstantValue.TASK_MODULE)
    public void getPeopleTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        List<BaseTaskBean> taskBeanList=mmoSimpleRole.getTaskManager().getTasks();
        Channel channel=mmoSimpleRole.getChannel();
        List<TaskModel.TaskDto> taskDtos=new ArrayList<>();
        for (BaseTaskBean taskBean : taskBeanList) {
            TaskModel.TaskDto taskDto= CommonsUtil.taskBeanToTaskDto(taskBean);
            taskDtos.add(taskDto);
        }
        //修改
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_PEOPLE_TASK_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf
        TaskModel.TaskModelMessage.Builder messageBuilder = TaskModel.TaskModelMessage.newBuilder();
        messageBuilder.setDataType(TaskModel.TaskModelMessage.DateType.GetPeopleTaskResponse);
        messageBuilder.setGetPeopleTaskResponse(TaskModel.GetPeopleTaskResponse.newBuilder().addAllTaskDtos(taskDtos).build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.GET_CAN_ACCEPT_TASK_REQUEST,module = ConstantValue.TASK_MODULE)
    public void getCanAcceptTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        List<Integer> taskList=TaskServiceProvider.getCanAcceptTasks(mmoSimpleRole);
        Channel channel=mmoSimpleRole.getChannel();
        //修改
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.GET_CAN_ACCEPT_TASK_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf
        TaskModel.TaskModelMessage.Builder messageBuilder = TaskModel.TaskModelMessage.newBuilder();
        messageBuilder.setDataType(TaskModel.TaskModelMessage.DateType.GetCanAcceptTaskResponse);
        messageBuilder.setGetCanAcceptTaskResponse(TaskModel.GetCanAcceptTaskResponse.newBuilder().addAllTaskIds(taskList).build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ACCEPT_TASK_REQUEST,module = ConstantValue.TASK_MODULE)
    public void acceptTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer taskMessageId=myMessage.getAcceptTaskRequest().getTaskMessageId();
        Channel channel=mmoSimpleRole.getChannel();
        TaskServiceProvider.acceptTask(taskMessageId,mmoSimpleRole);
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ACCEPT_TASK_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf
        TaskModel.TaskModelMessage.Builder messageBuilder = TaskModel.TaskModelMessage.newBuilder();
        messageBuilder.setDataType(TaskModel.TaskModelMessage.DateType.AcceptTaskResponse);
        messageBuilder.setAcceptTaskResponse(TaskModel.AcceptTaskResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.ABANDON_TASK_REQUEST,module = ConstantValue.TASK_MODULE)
    public void abandonTaskRequest(TaskModel.TaskModelMessage myMessage, MmoSimpleRole mmoSimpleRole) throws InvalidProtocolBufferException, RpgServerException {
        Integer taskMessageId=myMessage.getAbandonTaskRequest().getTaskMessageId();
        Channel channel=mmoSimpleRole.getChannel();
        TaskServiceProvider.abandonTask(taskMessageId,mmoSimpleRole);
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ABANDON_TASK_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf
        TaskModel.TaskModelMessage.Builder messageBuilder = TaskModel.TaskModelMessage.newBuilder();
        messageBuilder.setDataType(TaskModel.TaskModelMessage.DateType.AbandonTaskResponse);
        messageBuilder.setAbandonTaskResponse(TaskModel.AbandonTaskResponse.newBuilder().build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
}
