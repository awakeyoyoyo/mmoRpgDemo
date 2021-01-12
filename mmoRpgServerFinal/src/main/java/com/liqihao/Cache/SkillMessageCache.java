package com.liqihao.Cache;

import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.SceneBean;
import com.liqihao.pojo.bean.SkillBean;
import com.liqihao.util.ExcelReaderUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能的基本信息Cache
 * @author lqhao
 */
@Component
public class SkillMessageCache extends CommonsCache<SkillMessage> {
    private static String excel_file = "classpath:message/skillMessage.xlsx";
    private volatile static SkillMessageCache instance ;
    public static SkillMessageCache getInstance(){
        return instance;
    }
    public SkillMessageCache() {
    }
    @PostConstruct
    public  void init() throws IllegalAccessException, IOException, InstantiationException {
        instance=this;
        super.init(excel_file, SkillMessage.class);
    }
    private SkillMessageCache(ConcurrentHashMap<Integer,SkillMessage> map) {
        super(map);
    }
}
