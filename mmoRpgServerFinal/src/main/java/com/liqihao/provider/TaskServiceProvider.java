package com.liqihao.provider;
import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.RpgServerException;
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
            BaseTaskBean taskBean= reflectionTaskBean(taskMessage.getTargetType());
            taskBean.setTaskDbId(taskDbId.incrementAndGet());
            taskBean.setProgress(0);
            taskBean.setTaskMessageId(taskMessage.getId());
            taskBean.setTaskTargetTypeId(taskMessage.getTargetType());
            taskBean.setStatus(TaskStateCode.ON_DOING.getCode());
            taskBean.setCreateTime(System.currentTimeMillis());
            //数据库
            ScheduledThreadPoolUtil.addTask(() -> insertTaskDb(taskBean,roleId));
        }
    }

    @Autowired
    public void setMmoTaskPOJOMapper(MmoTaskPOJOMapper mmoTaskPOJOMapper) {
        TaskServiceProvider.mmoTaskPOJOMapper = mmoTaskPOJOMapper;
        taskDbId=new AtomicInteger(mmoTaskPOJOMapper.selectNextIndex() - 1);
    }

    /**
     * 接任务
     */
    public static void acceptTask(TaskMessage taskMessage,MmoSimpleRole role){
        //根据 task
        BaseTaskBean taskBean= reflectionTaskBean(taskMessage.getTargetType());
        taskBean.setTaskDbId(taskDbId.incrementAndGet());
        taskBean.setProgress(0);
        taskBean.setTaskMessageId(taskMessage.getId());
        taskBean.setTaskTargetTypeId(taskMessage.getTargetType());
        taskBean.setStatus(TaskStateCode.ON_DOING.getCode());
        taskBean.setCreateTime(System.currentTimeMillis());
        role.getTaskManager().addTask(taskBean);
        //数据库
        Integer roleId=role.getId();
        ScheduledThreadPoolUtil.addTask(() -> insertTaskDb(taskBean,roleId));
    }

    /**
     * 放弃任务
     */
    public static void abandonTask(BaseTaskBean taskBean,MmoSimpleRole role) throws RpgServerException {
        role.getTaskManager().getTaskBeans().remove(taskBean.getTaskMessageId());
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
            taskBean.setTaskTargetTypeId(taskMessage.getTargetType());
            taskBean.setStatus(mmoTaskPOJO.getStatus());
            taskBean.setTaskMessageId(mmoTaskPOJO.getTaskMessageId());
            manager.addTask(taskBean);
        }
    }



    /**
     * 获取用户可接受的任务
     */
    public static List<Integer>  getCanAcceptTasks(MmoSimpleRole role){
        List<Integer> baseTaskBeanList=role.getTaskManager().getTaskIds();
        List<Integer> taskMessageList=new ArrayList<>();
        for (TaskMessage value : TaskMessageCache.getInstance().values()) {
            if (baseTaskBeanList.contains(value.getId())||value.getType().equals(TaskTypeCode.ACHIEVEMENT.getCode())){
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
