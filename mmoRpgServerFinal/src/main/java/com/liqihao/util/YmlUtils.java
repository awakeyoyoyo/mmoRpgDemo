package com.liqihao.util;

import com.liqihao.pojo.baseMessage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.*;
import java.util.List;

/**
 * 读取yml配置文件工具类
 */
public class YmlUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(YmlUtils.class);

    private static String baseRoleMessage_file = "classpath:message/baseRoleMessage";
    private static String npcMessage_file = "classpath:message/npcMessages";
    private static String sceneMessage_file = "classpath:message/sceneMessages";
    private static String skillMessage_file = "classpath:message/skillMessages";
    private static String bufferMessage_file = "classpath:message/bufferMessage";
    private static String medicineMessage_file = "classpath:message/medicineMessage";
    private static String equipmentMessage_file = "classpath:message/equipmentMessage";
    private static String baseDetailMessage_file = "classpath:message/baseDetailMessage";
    public static BaseRoleMessage getBaseRoleMessage() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(baseRoleMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getBaseRoleMessage();
    }
    public static List<NPCMessage> getNpcMessage() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(npcMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getNpcMessages();
    }
    public static List<SceneMessage> getSceneMessage() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(sceneMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getSceneMessages();
    }
    public static List<SkillMessage> getSkillMessage() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(skillMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getSkillMessages();
    }
    public static List<BufferMessage> getBufferMessage() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(bufferMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getBufferMessage();
    }
    public static List<MedicineMessage> getMedicineMessages() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(medicineMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getMedicineMessages();
    }
    public static List<EquipmentMessage> getEquipmentMessages() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(equipmentMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getEquipmentMessages();
    }

    public static BaseDetailMessage getBaseDetailMessage() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(baseDetailMessage_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj.getBaseDetailMessage();
    }
}
