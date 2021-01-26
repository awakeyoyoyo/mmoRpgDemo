package com.liqihao.pojo.bean.taskBean;

import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务管理类
 * @author lqhao
 */
public class TaskManager {
    private HashMap<Integer,BaseTaskBean> taskBeans=new HashMap<>();

    public HashMap<Integer, BaseTaskBean> getTaskBeans() {
        return taskBeans;
    }

    public void setTaskBeans(HashMap<Integer, BaseTaskBean> taskBeans) {
        this.taskBeans = taskBeans;
    }

    /**
     * 增加任务
     * @return
     */
    public void addTask(BaseTaskBean taskBean){
        taskBeans.put(taskBean.getTaskMessageId(),taskBean);
    }

    /**
     * 移除任务
     * @param taskMessageId
     */
    public void removeTask(Integer taskMessageId){
        taskBeans.remove(taskMessageId);
    }

    /**
     * 所有任务更新
     * @param taskAction
     * @param role
     */
    public void handler(BaseTaskAction taskAction, MmoSimpleRole role){
        List<Integer> keys=getTaskIds();
        for (Integer key : keys) {
            BaseTaskBean taskBean=taskBeans.get(key);
            if (taskBean!=null) {
                Integer taskTargetType=taskBean.getTaskTargetTypeId();
                if (taskTargetType.equals(taskAction.getTaskTargetType())&&taskBean.getStatus().equals(TaskStateCode.ON_DOING.getCode())){
                    taskBean.update(taskAction, role);
                }
            }
        }
    }

    /**
     * 返回任务列表
     */
    public List<BaseTaskBean> getTasks(){
        return new ArrayList<>(taskBeans.values());
    }

    /**
     * 返回任务列表Ids
     */
    public List<Integer> getTaskIds(){
        return taskBeans.values().stream().map(BaseTaskBean::getTaskMessageId).collect(Collectors.toList());
    }
}
