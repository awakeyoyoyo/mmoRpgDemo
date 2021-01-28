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
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.taskBean.TaskManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.util.ScheduledThreadPoolUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务服务提供
 * @author lqhao
 */
@Component
public class TaskServiceProvider {

    private static MmoTaskPOJOMapper mmoTaskPOJOMapper;
    /**
     * 任务纪录DB Id
     */
    private static AtomicInteger taskDbId;

    /**
     * description 插入所有的成就
     * @param roleId
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/26 16:41
     */
    public static void insertAllAchievements(Integer roleId) {
        for (TaskMessage taskMessage : TaskMessageCache.getInstance().values()) {
            if (!taskMessage.getType().equals(TaskTypeCode.ACHIEVEMENT.getCode())){
                continue;
            }
            BaseTaskBean taskBean=createTaskBean(taskMessage);
            //数据库
            insertTaskDb(taskBean,roleId);
        }
    }

    @Autowired
    public void setMmoTaskPOJOMapper(MmoTaskPOJOMapper mmoTaskPOJOMapper) {
        TaskServiceProvider.mmoTaskPOJOMapper = mmoTaskPOJOMapper;
        taskDbId=new AtomicInteger(mmoTaskPOJOMapper.selectNextIndex() - 1);
    }

    /**
     * 接任务
     * @param taskMessage
     * @param role
     */
    public static void acceptTask(TaskMessage taskMessage,MmoSimpleRole role){
        //根据 task
        BaseTaskBean taskBean=createTaskBean(taskMessage);
        role.getTaskManager().addTask(taskBean);
        //数据库
        Integer roleId=role.getId();
        insertTaskDb(taskBean,roleId);
    }

    /**
     * description 根据taskMessage创建任务实体
     * @param taskMessage
     * @return {@link BaseTaskBean }
     * @author lqhao
     * @createTime 2021/1/28 11:41
     */
    private static BaseTaskBean createTaskBean(TaskMessage taskMessage) {
        BaseTaskBean taskBean= reflectionTaskBean(taskMessage.getTargetType());
        taskBean.setTaskDbId(taskDbId.incrementAndGet());
        taskBean.setProgress(0);
        taskBean.setTaskMessageId(taskMessage.getId());
        taskBean.setTaskTargetTypeId(taskMessage.getTargetType());
        taskBean.setStatus(TaskStateCode.ON_DOING.getCode());
        taskBean.setCreateTime(System.currentTimeMillis());
        return taskBean;
    }

    /**
     * 放弃任务
     */
    public static void abandonTask(BaseTaskBean taskBean,MmoSimpleRole role) throws RpgServerException {
        role.getTaskManager().getTaskBeans().remove(taskBean.getTaskMessageId());
        Integer taskBeanId=taskBean.getTaskDbId();
        deleteTaskDb(taskBeanId);
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
            taskBean.setTaskTargetTypeId(taskMessage.getTargetType());
            taskBean.setStatus(mmoTaskPOJO.getStatus());
            taskBean.setTaskMessageId(mmoTaskPOJO.getTaskMessageId());
            manager.addTask(taskBean);
        }
    }

    /**
     * 完成任务
     * @param taskMessageId
     * @param role
     */
    public static void finish(Integer taskMessageId,MmoSimpleRole role) throws RpgServerException {
        BaseTaskBean taskBean=role.getTaskManager().getTaskBeans().get(taskMessageId);
        if (taskBean==null){
            throw new RpgServerException(StateCode.FAIL,"没有该任务");
        }
        TaskMessage taskMessage=TaskMessageCache.getInstance().get(taskMessageId);
        if (!taskBean.getStatus().equals(TaskStateCode.FINISH.getCode())){
            throw new RpgServerException(StateCode.FAIL,"该任务未达到条件");
        }
        //删除
        role.getTaskManager().getTaskBeans().remove(taskMessageId);
        Integer taskBeanId=taskBean.getTaskDbId();
        deleteTaskDb(taskBeanId);
        //奖励
        taskBean.reward(taskMessage,role);
        taskBean.sendFinishTask(role);
        if (taskMessage.getNextTaskId()!=-1){
            //还有后续任务
            TaskMessage nextTaskMessage= TaskMessageCache.getInstance().get(taskMessage.getNextTaskId());
            TaskServiceProvider.acceptTask(nextTaskMessage,role);
        }
    }


    /**
     * 获取用户可接受的任务
     */
    public static List<Integer>  getCanAcceptTasks(MmoSimpleRole role){
        List<Integer> baseTaskBeanList=role.getTaskManager().getTaskIds();
        List<Integer> taskMessageList=new ArrayList<>();
        for (TaskMessage value : TaskMessageCache.getInstance().values()) {
            if (baseTaskBeanList.contains(value.getId())
                    ||value.getType().equals(TaskTypeCode.ACHIEVEMENT.getCode())
            ||value.getPreTaskId()!=-1){
                continue;
            }
            taskMessageList.add(value.getId());

        }
        return taskMessageList;
    }

    /**
     * 插入任务实体数据库
     * @param taskBean
     * @param roleId
     */
    public static void insertTaskDb(BaseTaskBean taskBean,Integer roleId){
        MmoTaskPOJO mmoTaskPOJO=new MmoTaskPOJO();
        mmoTaskPOJO.setId(taskBean.getTaskDbId());
        mmoTaskPOJO.setStatus(taskBean.getStatus());
        mmoTaskPOJO.setProgress(taskBean.getProgress());
        mmoTaskPOJO.setRoleId(roleId);
        mmoTaskPOJO.setTaskMessageId(taskBean.getTaskMessageId());
        mmoTaskPOJO.setCreateTime(taskBean.getCreateTime());
        ScheduledThreadPoolUtil.addTask(()->mmoTaskPOJOMapper.insert(mmoTaskPOJO));
    }

    /**
     * 更新任务实体数据库
     * @param taskBean
     * @param roleId
     */
    public static void updateTaskDb(BaseTaskBean taskBean,Integer roleId){
        MmoTaskPOJO mmoTaskPOJO=new MmoTaskPOJO();
        mmoTaskPOJO.setId(taskBean.getTaskDbId());
        mmoTaskPOJO.setStatus(taskBean.getStatus());
        mmoTaskPOJO.setProgress(taskBean.getProgress());
        mmoTaskPOJO.setRoleId(roleId);
        mmoTaskPOJO.setTaskMessageId(taskBean.getTaskMessageId());
        mmoTaskPOJO.setCreateTime(taskBean.getCreateTime());
        ScheduledThreadPoolUtil.addTask(() -> mmoTaskPOJOMapper.updateByPrimaryKey(mmoTaskPOJO));
    }

    /**
     * 反射得到TaskBean
     * @param targetType
     * @return
     */
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

    /**
     * 从数据库删除
     * @param taskBeanId
     */
    public static void deleteTaskDb(Integer taskBeanId) {
        ScheduledThreadPoolUtil.addTask(() -> mmoTaskPOJOMapper.deleteByPrimaryKey(taskBeanId));
    }
}
