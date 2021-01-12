package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ExcelReaderUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景实例Cache
 * @author lqhao
 */
@Component
public class SceneBeanMessageCache extends CommonsBeanCache<SceneBean>{
    private static String sceneMessage_file = "classpath:message/sceneMessage.xlsx";
    private volatile static SceneBeanMessageCache instance ;
    @Autowired
    private NpcMessageCache npcMessageCache;
    public static SceneBeanMessageCache getInstance(){
        return instance;
    }
    public SceneBeanMessageCache() {

    }
    @PostConstruct
    public void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        this.concurrentHashMap=new ConcurrentHashMap<>();
        List<SceneMessage> sceneMessages= ExcelReaderUtil.readExcelFromFileName(sceneMessage_file,SceneMessage.class);
        for (SceneMessage m:sceneMessages){
            SceneBean sceneBean;
            sceneBean= CommonsUtil.sceneMessageToSceneBean(m);
            instance.put(sceneBean.getId(),sceneBean);
        }
    }
    private SceneBeanMessageCache(ConcurrentHashMap<Integer,SceneBean> map) {
        super(map);
    }
}
