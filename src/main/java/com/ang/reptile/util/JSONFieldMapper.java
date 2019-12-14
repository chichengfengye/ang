package com.ang.reptile.util;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONFieldMapper {
    public static String path = "classpath:files\\fields.json";
    private static Logger logger = LoggerFactory.getLogger(JSONFieldMapper.class);

    private static JSONObject mapper;

    static {
        loadFieldsMapper();
    }

    public static JSONObject reloadFieldsMapper() {
        return loadFieldsMapper();
    }

    public static JSONObject loadFieldsMapper() {
//        JSONFieldMapper.class.getClassLoader().get
        FileReader fileReader = new FileReader(path);
        String result = fileReader.readString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        mapper = jsonObject;
        return mapper;
    }

    public static JSONObject getMapper() {
        return mapper;
    }

    public static String getCNCode(String enCode) {
        if (mapper == null) {
            logger.error("======= 获取fields 创建=======");

            return null;
        }
        return mapper.getString(enCode);
    }
}
