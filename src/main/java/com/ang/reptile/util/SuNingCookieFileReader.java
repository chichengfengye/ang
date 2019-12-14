package com.ang.reptile.util;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

public class SuNingCookieFileReader {
    private static final String path = "E:\\jinfeng\\suNingCookie.json";


    public static JSONObject getCookies() {
        FileReader fileReader = new FileReader(path);
        String result = fileReader.readString();
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }
}
