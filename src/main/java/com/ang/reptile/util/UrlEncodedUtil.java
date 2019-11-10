package com.ang.reptile.util;

import java.lang.reflect.Method;
import java.util.HashMap;

public class UrlEncodedUtil {
    public static HashMap<String, String> getUrlencodedMap(Object object, Class<?> clazz) {
        HashMap<String, String> map = new HashMap<>();
        Method[] methods = clazz.getDeclaredMethods();
        //获取get方法
        try {
            for (Method method : methods) {
                String name = method.getName();
                if (name.startsWith("get")) {
                    name = name.replaceFirst("get","");
                    name = name.substring(0, 1).toLowerCase() + name.substring(1);
                    Object value = method.invoke(object);
                    String valueStr = "";
                    if (value instanceof Number || value instanceof String || value instanceof Boolean) {
                        valueStr = "" + value;
                    } else {
                        valueStr = value.toString();
                    }
                    map.put(name, valueStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
