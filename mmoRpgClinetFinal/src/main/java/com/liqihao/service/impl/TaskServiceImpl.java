package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.MmoCacheCilent;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.commons.enums.TaskTypeCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.protobufObject.TaskModel;
import com.liqihao.protobufObject.TeamModel;
import com.liqihao.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TaskServiceImpl implements TaskService {
    @Override
    public void getPeopleTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TaskModel.TaskModelMessage myMessage;
        myMessage =TaskModel.TaskModelMessage.parseFrom(data);
        List<TaskModel.TaskDto> taskDtos=myMessage.getGetPeopleTaskResponse().getTaskDtosList();
        System.out.println("[-]--------------------------------------------------------");
        for (TaskModel.TaskDto taskDto : taskDtos) {
            TaskMessage taskMessage= MmoCacheCilent.getInstance().getTaskMessageConcurrentHashMap().get(taskDto.getTaskMessageId());
            System.out.println("[-]");
            System.out.println("[-]任务id："+taskMessage.getId()+" 任务名称: "+taskMessage.getName());
            System.out.println("[-]任务描述："+taskMessage.getDescription());
            System.out.println("[-]任务进度："+taskDto.getProgress()+"/"+taskMessage.getTargetProgress());
            System.out.println("[-]任务类型："+ TaskTypeCode.getValue(taskMessage.getType())+" 任务状态："+ TaskStateCode.getValue(taskDto.getStatus()));
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void getCanAcceptTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TaskModel.TaskModelMessage myMessage;
        myMessage =TaskModel.TaskModelMessage.parseFrom(data);
        List<Integer> taskIds=myMessage.getGetCanAcceptTaskResponse().getTaskIdsList();
        System.out.println("[-]--------------------------------------------------------");
        for (Integer taskMessageId : taskIds) {
            TaskMessage taskMessage= MmoCacheCilent.getInstance().getTaskMessageConcurrentHashMap().get(taskMessageId);
            System.out.println("[-]");
            System.out.println("[-]任务id："+taskMessage.getId()+" 任务名称: "+taskMessage.getName());
            System.out.println("[-]任务描述："+taskMessage.getDescription());
            System.out.println("[-]任务目标进度："+taskMessage.getTargetProgress());
            System.out.println("[-]任务类型："+ TaskTypeCode.getValue(taskMessage.getType()));
            System.out.println("[-]");
        }
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void acceptTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TaskModel.TaskModelMessage myMessage;
        myMessage =TaskModel.TaskModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]接收任务成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void abandonTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TaskModel.TaskModelMessage myMessage;
        myMessage =TaskModel.TaskModelMessage.parseFrom(data);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]放弃任务成功");
        System.out.println("[-]--------------------------------------------------------");
    }

    @Override
    public void finishTaskRequest(NettyResponse nettyResponse) throws InvalidProtocolBufferException {
        byte[] data = nettyResponse.getData();
        TaskModel.TaskModelMessage myMessage;
        myMessage =TaskModel.TaskModelMessage.parseFrom(data);
        int taskMessageId=myMessage.getFinishTaskResponse().getTaskMessageId();
        TaskMessage taskMessage=MmoCacheCilent.getInstance().getTaskMessageConcurrentHashMap().get(taskMessageId);
        System.out.println("[-]--------------------------------------------------------");
        System.out.println("[-]任务名:"+taskMessage.getName());
        System.out.println("[-]任务描述:"+taskMessage.getDescription());
        System.out.println("[-]任务类型:"+TaskTypeCode.getValue(taskMessage.getType()));
        System.out.println("[-]该任务已经完成");
        System.out.println("[-]--------------------------------------------------------");
    }
}
