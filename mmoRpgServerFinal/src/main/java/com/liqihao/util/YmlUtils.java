package com.liqihao.util;

import com.liqihao.pojo.baseMessage.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.*;

public class YmlUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(YmlUtils.class);

    private static String bootstrap_file = "classpath:baseMessage.yaml";

    public static BaseMessage getBaseMessage() throws FileNotFoundException {
        InputStream in = null;
        File file = ResourceUtils.getFile(bootstrap_file);
        in = new BufferedInputStream(new FileInputStream(file));
        Yaml props = new Yaml(new Constructor(BaseMessage.class));
        BaseMessage obj = props.load(in);
        return obj;
    }
}
