package com.ang.reptile.util.excel;

import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class MyExcelUtil {
    public static final String path = "G:\\test.xlsx";

    public static void main(String[] args) {

//        justWriteHeader();
//        writeHeaderThenWriteNullData();
        writeNullThenReopenAndWriteNewData();

    }

    public static void openAndAddRow() {

    }

    public static void justWriteHeader() {
        ExcelWriter writer = MyExcelUtil.newWritter();
        MyExcelUtil.addHeaders(writer);
        MyExcelUtil.close(writer);
    }

    public static void writeHeaderThenWriteNullData() {
        JSONObject user = getUser(true);

        ExcelWriter writer = MyExcelUtil.newWritter();
        MyExcelUtil.addHeaders(writer);
        MyExcelUtil.writeRow(writer, user);
        writer.close();
    }

    public static void writeNullThenReopenAndWriteNewData() {
        writeHeaderThenWriteNullData();
        ExcelWriter writer = newWritter();

        JSONObject user = getUser(false);

        MyExcelUtil.writeRow(writer, user);
        MyExcelUtil.close(writer);
    }

    public static ExcelWriter newWritter() {
        ExcelWriter writer = ExcelUtil.getWriter(path);
        return writer;
    }

    public static JSONObject getUser(boolean isNull) {
        JSONObject o;
        if (isNull) {
            o = new JSONObject();
        } else {
            o = new JSONObject();
            o.put("name", "jinfeng");
            o.put("age", "18");
            o.put("score", "666");
        }
        return o;
    }

    public static ExcelWriter addHeaders(ExcelWriter writer) {
        //自定义标题别名
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("age", "年龄");
        writer.addHeaderAlias("score", "分数");
        return writer;
    }

    public static void writeRow(ExcelWriter writer, JSONObject user) {
        writer.writeRow(user, false);

    }

    public static void close(ExcelWriter writer) {
        writer.close();
    }


}



