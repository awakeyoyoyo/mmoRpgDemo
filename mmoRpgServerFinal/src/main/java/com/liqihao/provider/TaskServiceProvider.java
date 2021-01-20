package com.liqihao.provider;
import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.commons.enums.TaskTypeCode;
import com.liqihao.dao.MmoTaskPOJOMapper;
import com.liqihao.pojo.MmoTaskPOJO;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.TaskBean.ActionDto;
import com.liqihao.pojo.bean.TaskBean.BaseTaskBean;
import com.liqihao.pojo.bean.TaskBean.TaskManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.ScheduledThreadPoolUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务服务提供
 * @author lqhao
 */
@Component
public class TaskServiceProvider {

    private static MmoTaskPOJOMapper mmoTaskPOJOMapper;
    /**
     * 拍卖纪录DB Id
     */
    private static AtomicInteger taskDbId;
    @Autowired
    public void setMmoTaskPOJOMapper(MmoTaskPOJOMapper mmoTaskPOJOMapper) {
        TaskServiceProvider.mmoTaskPOJOMapper = mmoTaskPOJOMapper;
        taskDbId=new AtomicInteger(mmoTaskPOJOMapper.selectNextIndex() - 1);
    }

    /**
     * 接任务
     */
    public static void acceptTask(Integer taskMessageId,MmoSimpleRole role) throws RpgServerException {
        //根据 task
        TaskMessage taskMessage=TaskMessageCache.getInstance().get(taskMessageId);
        if (taskMessage==null){
            throw new RpgServerException(StateCode.FAIL,"不存在该任务");
        }
        if (role.getTaskManager().getTaskIds().contains(taskMessageId)){
            throw new RpgServerException(StateCode.FAIL,"用户已经接收了该任务");
        }
        BaseTaskBean taskBean= reflectionTaskBean(taskMessage.getTargetType());
        taskBean.setTaskDbId(taskDbId.incrementAndGet());
        taskBean.setProgress(0);
        taskBean.setTaskMessageId(taskMessageId);
        taskBean.setStatus(TaskStateCode.ON_DOING.getCode());
        role.getTaskManager().addTask(taskBean);
        //数据库
        Integer roleId=role.getId();
        ScheduledThreadPoolUtil.addTask(() -> insertTaskDb(taskBean,roleId));
    }
    /**
     * 放弃任务
     */
    public static void abandonTask(Integer taskMessageId,MmoSimpleRole role) throws RpgServerException {
        BaseTaskBean taskBean=role.getTaskManager().getTaskBeans().get(taskMessageId);
        if (taskBean==null){
            throw new RpgServerException(StateCode.FAIL,"该角色不存在该任务");
        }
        TaskMessage taskMessage=TaskMessageCache.getInstance().get(taskMessageId);
        if (taskMessage.getType().equals(TaskTypeCode.ACHIEVEMENT.getCode())){
            throw new RpgServerException(StateCode.FAIL,"成就类型的任务不能删除");
        }
        role.getTaskManager().getTaskBeans().remove(taskMessageId);
        Integer taskBeanId=taskBean.getTaskDbId();
        ScheduledThreadPoolUtil.addTask(() -> deleteTaskDb(taskBeanId));
    }


    /**
     * 每次登陆后初始化任务表
     */
    public static void initTask(MmoSimpleRole mmoSimpleRole){
        TaskManager manager=new TaskManager();
        mmoSimpleRole.setTaskManager(manager);
        List<MmoTaskPOJO> mmoTaskPOJOS=mmoTaskPOJOMapper.selectAllByRoleId(mmoSimpleRole.getId());
        for (MmoTaskPOJO mmoTaskPOJO : mmoTaskPOJOS) {
            TaskMessage taskMessage=TaskMessageCache.getInstance().get(mmoTaskPOJO.getTaskMessageId());
            BaseTaskBean taskBean= reflectionTaskBean(taskMessage.getTargetType());
            taskBean.setProgress(mmoTaskPOJO.getProgress());
            taskBean.setCreateTime(mmoTaskPOJO.getCreateTime());
            taskBean.setTaskDbId(mmoTaskPOJO.getId());
            taskBean.setStatus(mmoTaskPOJO.getStatus());
            taskBean.setTaskMessageId(mmoTaskPOJO.getTaskMessageId());
            manager.addTask(taskBean);
        }
    }

    public static void check(MmoSimpleRole role,int progress,int articleType,int targetId,int targetType) {
        ActionDto actionDto=new ActionDto();
        actionDto.setProgress(progress);
        actionDto.setArticleType(articleType);
        actionDto.setTargetId(targetId);
        actionDto.setTargetType(targetType);
        role.getTaskManager().handler(actionDto,role);
    }

    /**
     * 获取用户可接受的任务
     */
    public static List<Integer>  getCanAcceptTasks(MmoSimpleRole role){
        List<Integer> baseTaskBeanList=role.getTaskManager().getTaskIds();
        List<Integer> taskMessageList=new ArrayList<>();
        for (TaskMessage value : TaskMessageCache.getInstance().values()) {
            if (baseTaskBeanList.contains(value.getId())){
                continue;
            }else{
                taskMessageList.add(value.getId());
            }
        }
        return taskMessageList;
    }

    public static void main(String[] args) {
        String className=TaskTargetTypeCode.getClassNameByCode(1);
        Object obj = null;
        try {
            Class<T> clz = (Class<T>) Class.forName(className);
            obj = clz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        BaseTaskBean taskBean= (BaseTaskBean) obj;
//        taskBean.update(null,null);
    }

    public static void insertTaskDb(BaseTaskBean taskBean,Integer roleId){
        MmoTaskPOJO mmoTaskPOJO=new MmoTaskPOJO();
        mmoTaskPOJO.setId(taskBean.getTaskDbId());
        mmoTaskPOJO.setStatus(taskBean.getStatus());
        mmoTaskPOJO.setProgress(taskBean.getProgress());
        mmoTaskPOJO.setRoleId(roleId);
        mmoTaskPOJO.setTaskMessageId(taskBean.getTaskMessageId());
        mmoTaskPOJO.setCreateTime(taskBean.getCreateTime());
        mmoTaskPOJOMapper.insert(mmoTaskPOJO);
    }

    public static void updateTaskDb(BaseTaskBean taskBean,Integer roleId){
        MmoTaskPOJO mmoTaskPOJO=new MmoTaskPOJO();
        mmoTaskPOJO.setId(taskBean.getTaskDbId());
        mmoTaskPOJO.setStatus(taskBean.getStatus());
        mmoTaskPOJO.setProgress(taskBean.getProgress());
        mmoTaskPOJO.setRoleId(roleId);
        mmoTaskPOJO.setTaskMessageId(taskBean.getTaskMessageId());
        mmoTaskPOJO.setCreateTime(taskBean.getCreateTime());
        mmoTaskPOJOMapper.updateByPrimaryKey(mmoTaskPOJO);
    }
    public static BaseTaskBean reflectionTaskBean(Integer targetType){
        String className=TaskTargetTypeCode.getClassNameByCode(targetType);
        Object obj = null;
        //反射减少if else
        try {
            Class<T> clz = (Class<T>) Class.forName(className);
            obj = clz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        BaseTaskBean taskBean= (BaseTaskBean) obj;
        return taskBean;
    }

    public static void deleteTaskDb(Integer taskBeanId) {
        mmoTaskPOJOMapper.deleteByPrimaryKey(taskBeanId);
    }
}
