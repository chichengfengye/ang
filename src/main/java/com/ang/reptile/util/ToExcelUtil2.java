package com.ang.reptile.util;

import com.alibaba.fastjson.JSONObject;
import com.ang.reptile.util.excel.XSSFExcelBuilder;
import com.ang.reptile.util.excel.obj.XssFExcelObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToExcelUtil2 {
    private static Logger logger = LoggerFactory.getLogger(ToExcelUtil2.class);
    private static String toPath = "D:/sm_order_time.xlsx";
    // 通过工具类创建writer

    public static XssFExcelObj openFile() {
        try {
            logger.info("读取或者创建excel:{}", toPath);
            XssFExcelObj xssFExcelObj = XSSFExcelBuilder.load(toPath);
            return xssFExcelObj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void toExcel(XssFExcelObj writer, List<String> strings, boolean headers) {
        int index = 0;
        if (!strings.isEmpty()) {
            List<Object> datas = new ArrayList<>();
            //遍历插入每一行数据
            for (String string : strings) {
                JSONObject item = JSONObject.parseObject(string);
                JSONObject data = new JSONObject();
                JSONObject mapper = JSONFieldMapper.getMapper();
                for (Map.Entry<String, Object> stringObjectEntry : mapper.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    String val = item.getString(key);
                    if (StringUtils.isEmpty(val)) {
                        val = "--";
                    }
                    data.put(key, val);
                }
                datas.add(data);
                index++;
            }

            writer.writeRows(datas, 0);
        }
        logger.info("本次插入{}行", index);

    }

    public static void close(XssFExcelObj writer) {
        // 关闭writer，释放内存
        writer.close();
    }

    public static void createHeaders(XssFExcelObj writer) {
        JSONObject headers = new JSONObject();
        JSONObject mapper = JSONFieldMapper.getMapper();
        for (Map.Entry<String, Object> stringObjectEntry : mapper.entrySet()) {
            //自定义标题别名
            String key = stringObjectEntry.getKey();
            String CN = (String) stringObjectEntry.getValue();
            if (StringUtils.isEmpty(CN)) {
                CN = "没有指定映射名称【" + key + "】";
            }
            logger.debug("+++++++++添加标题头: " + key + " = " + CN + "++++++++++");
            headers.put(key, CN);
        }

        writer.headers().addHeaders(headers).finish(0);
    }
}