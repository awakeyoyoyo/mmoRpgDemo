package com.liqihao.pojo.bean.TaskBean;

import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

/**
 * 任务实例
 * @author lqhao
 */

public class EatMedicineTaskBean extends BaseTaskBean {


    @Override
    public TaskTargetTypeCode getTaskTargetTypeCode(){
        return TaskTargetTypeCode.MEDICINE;
    }




    @Override
    public void update(ActionDto dto,MmoSimpleRole role) {
        super.update(dto,role);
        // 逻辑


    }





}
