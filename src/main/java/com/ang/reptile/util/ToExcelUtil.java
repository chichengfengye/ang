package com.ang.reptile.util;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ToExcelUtil {
    private static Logger logger = LoggerFactory.getLogger(ToExcelUtil.class);
    private static String toPath = "D:/sm_order_time.xlsx";
    // 通过工具类创建writer

    public static ExcelWriter openFile() {
        ExcelWriter writer;
        // 通过工具类创建writer
//            toPath = toPath.replaceFirst("time", "" + System.currentTimeMillis());
        writer = ExcelUtil.getWriter(toPath);
        return writer;
    }

    public static void toExcel(ExcelWriter writer, List<String> strings, boolean headers) {
        int index = 0;
        if (!strings.isEmpty()) {
            //遍历插入每一行数据
            for (String string : strings) {
                JSONObject item = JSONObject.parseObject(string);
                LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
                JSONObject mapper = JSONFieldMapper.getMapper();
                for (Map.Entry<String, Object> stringObjectEntry : mapper.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    String val = item.getString(key);
                    if (StringUtils.isEmpty(val)) {
                        val = "--";
                    }
                    linkedHashMap.put(key, val);
                }

                // 一次性写出内容，使用默认样式，是否强制输出标题
//                if (index == 0) {//由于是在遍历中依次插入每一条记录，因此客户要求的添加标题这一需求，也只是在第一行之前加入head
//                    writer.writeRow(linkedHashMap, headers);
//                } else {
                writer.writeRow(linkedHashMap, headers);
//                }
                index++;
            }
        }
        logger.info("本次插入{}行", index);

    }

    public static void close(ExcelWriter writer) {
        // 关闭writer，释放内存
        writer.close();
    }

    private static void addHeaders(ExcelWriter writer) {
        JSONObject mapper = JSONFieldMapper.getMapper();
        for (Map.Entry<String, Object> stringObjectEntry : mapper.entrySet()) {
            //自定义标题别名
            String key = stringObjectEntry.getKey();
            String CN = (String) stringObjectEntry.getValue();
            if (StringUtils.isEmpty(CN)) {
                CN = "没有指定映射名称【" + key + "】";
            }
            logger.debug("+++++++++添加标题头: " + key + " = " + CN + "++++++++++");
            writer.addHeaderAlias(key, CN);
        }
    }

    public static void createHeaders(ExcelWriter writer) {
        addHeaders(writer);
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        JSONObject mapper = JSONFieldMapper.getMapper();
        for (Map.Entry<String, Object> stringObjectEntry : mapper.entrySet()) {
            String key = stringObjectEntry.getKey();
            String val = "请无视";
            linkedHashMap.put(key, val);
        }
        writer.writeRow(linkedHashMap, true);
//        writer.close();

    }
}