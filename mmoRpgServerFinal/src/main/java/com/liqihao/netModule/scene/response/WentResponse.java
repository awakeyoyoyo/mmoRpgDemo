package com.liqihao.netModule.scene.response;

import com.liqihao.pojo.bean.MmoSimpleRole;

import java.util.List;

public class WentResponse {
    /**
     * 返回前往的下一个场景对象
     */
    private Integer sceneId;
    /**
     * 下一个场景的角色
     */
    private List<MmoSimpleRole> mmoSimpleRoles;
}
